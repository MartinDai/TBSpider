package com.martin.product.view;

import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class StringView extends AbstractView {

    private static final String CONTENT_TYPE = "text/html";

    private static final String DEFAULT_CHARSET = CharEncoding.UTF_8;

    private String content;

    public StringView(String content) {
        super();
        this.content = content;
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        if (content == null || StringUtils.isEmpty(content)) {
            return;
        }

        byte[] bytes = content.getBytes(DEFAULT_CHARSET);
        response.setContentType(CONTENT_TYPE);
        response.setContentLength(bytes.length);
        response.setCharacterEncoding(DEFAULT_CHARSET);
        response.getWriter().write(content);
    }
}
