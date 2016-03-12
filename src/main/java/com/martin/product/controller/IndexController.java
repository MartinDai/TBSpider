package com.martin.product.controller;

import com.martin.product.spider.TaoBaoSpider;
import com.martin.product.view.ExcelView;
import com.martin.product.view.StringView;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Map;

@Controller
public class IndexController {

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String index(HttpServletResponse response) {
        response.setHeader("Content-Type", "text/html;charset=" + CharEncoding.UTF_8);
        return "index";
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ModelAndView upload(HttpServletRequest request, HttpServletResponse response, Map<String, Object> model) {
        MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        CommonsMultipartFile uploadFile;
        Workbook readWB;
        Workbook writeWB;
        InputStream uploadIs = null;
        String fileName;
        try {
            uploadFile = (CommonsMultipartFile) multipartRequest.getFile("urls");
            if (uploadFile == null) {
                return new ModelAndView(new StringView("未找到文件"));
            }
            fileName = uploadFile.getOriginalFilename();
            uploadIs = uploadFile.getInputStream();
            if (fileName.endsWith(".xls")) {
                readWB = new HSSFWorkbook(uploadIs);
            } else if (fileName.endsWith(".xlsx")) {
                readWB = new XSSFWorkbook(uploadIs);
            } else {
                return new ModelAndView(new StringView("文件格式不对"));
            }
            Sheet sheet = readWB.getSheetAt(0);
            int total = sheet.getPhysicalNumberOfRows();
            String tempName = String.valueOf(System.currentTimeMillis());
            String path = request.getSession().getServletContext().getRealPath("upload");
            File tempFile = new File(path + File.separator + tempName + ".xls");
            tempFile.deleteOnExit();
            tempFile.createNewFile();
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
                if (StringUtils.isBlank(tbUrl)) {
                    break;
                }
                isOnSale = TaoBaoSpider.checkItemIsOnSale(tbUrl);
                if (isOnSale) {
                    writeRow = writeSheet.createRow(writeRowNum);
                    createAndFillCell(writeSheet, writeRow, style, 0, tbUrl);
                    writeRowNum++;
                }
            }

            // 写入文件
            FileOutputStream fileOut = null;
            try {
                fileOut = new FileOutputStream(tempFile);
                writeWB.write(fileOut);
                fileOut.flush();
            } catch (Exception e) {
                return null;
            } finally {
                if (fileOut != null) {
                    try {
                        fileOut.close();
                    } catch (IOException ignored) {
                    }
                }
            }
            InputStream returnStream = new FileInputStream(tempFile);
            model.put("fileName", "down");
            return new ModelAndView(new ExcelView(returnStream));
        } catch (IOException e) {
            return new ModelAndView(new StringView("出错了"));
        } finally {
            if (uploadIs != null) {
                try {
                    uploadIs.close();
                } catch (IOException ignored) {
                }
            }
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

}
