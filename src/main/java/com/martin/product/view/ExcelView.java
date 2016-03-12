package com.martin.product.view;

import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public class ExcelView extends AbstractView {

    private InputStream inputStream;

    public ExcelView(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
                                           HttpServletResponse response) throws Exception {
        OutputStream os = response.getOutputStream();
        response.setCharacterEncoding("utf-8");
        Object fileName = model.get("fileName");
        if (fileName == null) {
            fileName = "download";
        }
        if (inputStream != null) {
            try {
                byte[] b = new byte[1024];
                int length;
                while ((length = inputStream.read(b)) > 0) {
                    os.write(b, 0, length);
                }
                os.flush();
                //
                response.setContentType("application/octet-stream");

                response.setHeader("Content-Disposition", "attachment; filename=" + fileName + ".xls");
            } catch (Exception ignored) {
            } finally {
                inputStream.close();
            }
        } else {
            os.write(encode("no data to export"));
        }
    }

    private byte[] encode(String content) {
        try {
            return content.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return content.getBytes();
        }
    }

}
