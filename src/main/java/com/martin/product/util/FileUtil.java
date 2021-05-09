package com.martin.product.util;

import com.martin.product.constants.WebConstants;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 */
public class FileUtil {

    /**
     * 获取临时文件夹目录
     */
    public static String getTmpPath() {
        return WebConstants.ROOT_PATH + File.separator + "tmp";
    }

    /**
     * 获取结果目录
     */
    public static String getResultPath() {
        return WebConstants.ROOT_PATH + File.separator + "excel";
    }

    public static void saveTempFile(MultipartFile file, String targetFileName) {
        File folder = new File(getTmpPath());
        if (!folder.exists()) {
            folder.mkdirs();
        }
        File targetFile = new File(folder, targetFileName);
        try {
            file.transferTo(targetFile);
        } catch (IOException ignored) {
        }
    }
}
