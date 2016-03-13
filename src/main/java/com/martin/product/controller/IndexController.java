package com.martin.product.controller;

import com.alibaba.fastjson.support.spring.FastJsonJsonView;
import com.martin.product.spider.TaoBaoSpider;
import com.martin.product.tuple.Tuple2Unit;
import com.martin.product.util.FileUtil;
import com.martin.product.view.ExcelView;
import com.martin.product.view.StringView;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

@Controller
public class IndexController {

    private static Map<String, Tuple2Unit<Integer, Integer>> PROCESS_MAP = new HashMap<>();

    private static String EXCEL_PATH = "excel";

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletResponse response) {
        response.setHeader("Content-Type", "text/html;charset=" + CharEncoding.UTF_8);
        return "index";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView upload(HttpServletRequest request, Map<String, Object> model) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile uploadFile;
        String fileName;
        final String fileSufix;
        uploadFile = (CommonsMultipartFile) multipartRequest.getFile("urls");
        if (uploadFile == null) {
            return new ModelAndView(new StringView("未找到文件"));
        }
        fileName = uploadFile.getOriginalFilename();
        fileSufix = fileName.substring(fileName.lastIndexOf("."));
        if (!fileSufix.equals(".xls") && !fileSufix.equals(".xlsx")) {
            return new ModelAndView(new StringView("文件格式不对"));
        }

        final String radomFileName = String.valueOf(System.currentTimeMillis());
        FileUtil.saveTempFile(uploadFile, radomFileName + fileSufix);
        PROCESS_MAP.put(fileName, new Tuple2Unit<>(0, 0));
        new Thread() {
            @Override
            public void run() {
                analyzeExcel(radomFileName, fileSufix);
            }
        }.start();
        model.put("key", radomFileName);
        return new ModelAndView(new FastJsonJsonView());
    }

    private static void analyzeExcel(String fileName, String sufix) {
        File file = new File(FileUtil.getTmpPath() + File.separator + fileName + sufix);
        InputStream is;
        Workbook readWB;
        Workbook writeWB;
        try {
            is = new FileInputStream(file);
            switch (sufix) {
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
            style.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);// 垂直
            style.setAlignment(HSSFCellStyle.ALIGN_LEFT);// 水平
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
            return;
        } finally {
            PROCESS_MAP.remove(fileName);
        }

        // 写入文件
        FileOutputStream fileOut = null;
        try {
            File folder = new File(FileUtil.getRealPath(EXCEL_PATH));
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
        Tuple2Unit<Integer, Integer> processTuple;
        if (PROCESS_MAP.containsKey(key)) {
            processTuple = PROCESS_MAP.get(key);
            processTuple.setP1(current);
            processTuple.setP2(total);
        } else {
            processTuple = new Tuple2Unit<>(current, total);
            PROCESS_MAP.put(key, processTuple);
        }
    }

    private static Cell createAndFillCell(Sheet sheet, Row row, CellStyle style, int colIdx, String value) {
        Cell cell = row.createCell(colIdx);
        cell.setCellStyle(style);
        cell.setCellValue(value);
        sheet.autoSizeColumn(colIdx, true);
        if (value != null) {
            int width = value.getBytes().length * 256;
            sheet.setColumnWidth(colIdx, width);
        }
        return cell;
    }

    @RequestMapping(value = "/getProgress/{fileKey}", method = RequestMethod.GET)
    public ModelAndView getProgress(@PathVariable("fileKey") String fileKey, Map<String, Object> model) {
        if (PROCESS_MAP.containsKey(fileKey)) {
            Tuple2Unit<Integer, Integer> progressTuple = PROCESS_MAP.get(fileKey);
            model.put("status", "processing");
            model.put("precent", progressTuple.getP1() * 100 / progressTuple.getP2());
        } else {
            model.put("status", "OK");
        }
        return new ModelAndView(new FastJsonJsonView());
    }

    @RequestMapping(value = "/download/{fileKey}", method = RequestMethod.GET)
    public ModelAndView download(@PathVariable("fileKey") String fileKey, Map<String, Object> model) {
        String path = FileUtil.getRealPath(EXCEL_PATH);
        File file = new File(path + File.separator + fileKey + ".xls");
        if (file.exists()) {
            try {
                InputStream returnStream = new FileInputStream(file);
                model.put("fileName", fileKey);
                return new ModelAndView(new ExcelView(returnStream));
            } catch (FileNotFoundException ignored) {
            }
        }
        return new ModelAndView(new StringView("文件不存在"));
    }

}
