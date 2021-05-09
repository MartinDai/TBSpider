package com.martin.product.listener;


import com.martin.product.constants.WebConstants;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.File;

/**
 * 初始化监听
 */
@WebListener
public class InitListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext servletContext = sce.getServletContext();
        WebConstants.ROOT_PATH = servletContext.getRealPath(File.separator);
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
    }
}
