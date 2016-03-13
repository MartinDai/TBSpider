package com.martin.product.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 文件工具类
 */
public class FileUtil {

    private static String LINUX_BASE_PATH = "/data";
    private static String WINDOWS_BASE_PATH = "D:\\data";

    /**
     * 获取临时文件夹目录
     */
    public static String getTmpPath() {
        return WINDOWS_BASE_PATH + File.separator + "tmp";
    }

    /**
     * 获取真实路径
     */
    public static String getRealPath(String relativePath) {
        return WINDOWS_BASE_PATH + File.separator + relativePath;
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
