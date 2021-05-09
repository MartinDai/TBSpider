package com.martin.product.controller;

import com.google.common.collect.Maps;
import com.martin.product.response.BaseResponse;
import com.martin.product.spider.TaoBaoSpider;
import com.martin.product.tuple.Tuple2;
import com.martin.product.util.FileUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@RestController
public class IndexController {

    private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

    private static final Map<String, Tuple2<Integer, Integer>> PROCESS_MAP = Maps.newHashMap();

    /**
     * 上传excel文件
     */
    @PostMapping(value = "/upload")
    public BaseResponse<Map<String, String>> upload(@RequestParam MultipartFile file) {
        Assert.notNull(file, "请上传要分析的文件");
        String fileName = file.getOriginalFilename();
        String fileSuffix = fileName.substring(fileName.lastIndexOf("."));
        if (!".xls".equals(fileSuffix) && !".xlsx".equals(fileSuffix)) {
            throw new IllegalArgumentException("不支持的文件格式");
        }

        final String randomFileName = String.valueOf(System.currentTimeMillis());
        FileUtil.saveTempFile(file, randomFileName + fileSuffix);
        PROCESS_MAP.put(fileName, new Tuple2<>(0, 0));
        new Thread(() -> analyzeExcel(randomFileName, fileSuffix)).start();
        Map<String, String> result = Maps.newHashMap();
        result.put("key", randomFileName);
        return BaseResponse.success(result);
    }

    /**
     * 获取进度
     */
    @GetMapping(value = "/getProgress/{fileKey}")
    public BaseResponse<Map<String, Object>> getProgress(@PathVariable("fileKey") String fileKey) {
        Map<String, Object> result = Maps.newHashMap();
        if (PROCESS_MAP.containsKey(fileKey)) {
            Tuple2<Integer, Integer> progressTuple = PROCESS_MAP.get(fileKey);
            result.put("status", "processing");
            result.put("percent", progressTuple.getP1() * 100 / progressTuple.getP2());
        } else {
            result.put("status", "OK");
        }
        return BaseResponse.success(result);
    }

    /**
     * 下载分析结果
     */
    @GetMapping(value = "/download/{fileKey}")
    public void download(@PathVariable("fileKey") String fileKey, HttpServletResponse response) throws IOException {
        String path = FileUtil.getResultPath();
        File file = new File(path + File.separator + fileKey + ".xls");
        Assert.isTrue(file.exists(), "文件不存在");

        try (BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
             BufferedOutputStream bos = new BufferedOutputStream(response.getOutputStream())) {
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileKey + ".xls");
            byte[] b = new byte[1024];
            int length;
            while ((length = bis.read(b)) > 0) {
                bos.write(b, 0, length);
            }
            bos.flush();
        }
    }

    private static void analyzeExcel(String fileName, String suffix) {
        File file = new File(FileUtil.getTmpPath() + File.separator + fileName + suffix);
        InputStream is;
        Workbook readWB;
        Workbook writeWB;
        try {
            is = new FileInputStream(file);
            switch (suffix) {
                case ".xls":
                    readWB = new HSSFWorkbook(is);
                    break;
                case ".xlsx":
                    readWB = new XSSFWorkbook(is);
                    break;
                default:
                    return;
            }
            Sheet sheet = readWB.getSheetAt(0);
            int total = sheet.getPhysicalNumberOfRows();
            writeWB = new HSSFWorkbook();
            writeWB.createCellStyle();
            CellStyle style = writeWB.createCellStyle(); // 样式对象
            Sheet writeSheet = writeWB.createSheet();
            int writeRowNum = 0;
            Row readRow;
            Row writeRow;
            Cell cell;
            String tbUrl;
            boolean isOnSale;
            for (int i = 0; i < total; i++) {
                readRow = sheet.getRow(i);
                cell = readRow.getCell(0);
                tbUrl = cell.getStringCellValue();
                updateProgress(fileName, i + 1, total);
                if (StringUtils.isBlank(tbUrl)) {
                    break;
                }
                isOnSale = TaoBaoSpider.checkItemIsOnSale(tbUrl);
                if (isOnSale) {
                    writeRow = writeSheet.createRow(writeRowNum);
                    createAndFillCell(writeSheet, writeRow, style, 0, tbUrl);
                    writeRowNum++;
                }
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) {
                }
            }
        } catch (Exception e) {
            logger.error("分析Excel异常", e);
            return;
        } finally {
            PROCESS_MAP.remove(fileName);
        }

        // 写入文件
        FileOutputStream fileOut = null;
        try {
            File folder = new File(FileUtil.getResultPath());
            if (!folder.exists()) {
                folder.mkdirs();
            }
            File resultFile = new File(folder, fileName + ".xls");
            resultFile.deleteOnExit();
            resultFile.createNewFile();
            fileOut = new FileOutputStream(resultFile);
            writeWB.write(fileOut);
            fileOut.flush();
        } catch (Exception ignored) {
        } finally {
            if (fileOut != null) {
                try {
                    fileOut.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    /**
     * 更新进度
     */
    private static void updateProgress(String key, int current, int total) {
        Tuple2<Integer, Integer> processTuple;
        if (PROCESS_MAP.containsKey(key)) {
            processTuple = PROCESS_MAP.get(key);
            processTuple.setP1(current);
            processTuple.setP2(total);
        } else {
            processTuple = new Tuple2<>(current, total);
            PROCESS_MAP.put(key, processTuple);
        }
    }

    private static void createAndFillCell(Sheet sheet, Row row, CellStyle style, int colIdx, String value) {
        Cell cell = row.createCell(colIdx);
        cell.setCellStyle(style);
        cell.setCellValue(value);
        sheet.autoSizeColumn(colIdx, true);
        if (value != null) {
            int width = value.getBytes().length * 256;
            sheet.setColumnWidth(colIdx, width);
        }
    }

}
