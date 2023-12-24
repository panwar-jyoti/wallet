package com.example.wallet.util;

import freemarker.cache.ClassTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import java.io.IOException;
import java.util.Map;

public class NotificationTemplateUtil {

    private static final String TEMPLATE_DIRECTORY = "/templates"; // Change this to your actual template directory

    private static Configuration freemarkerConfig;

    static {
        freemarkerConfig = new Configuration();
        freemarkerConfig.setTemplateLoader(new ClassTemplateLoader(NotificationTemplateUtil.class, TEMPLATE_DIRECTORY));
    }

    public static String generateNotification(Map<String, Object> model, String templateName) {
        try {
            Template template = freemarkerConfig.getTemplate(templateName);
            return FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (IOException | TemplateException e) {
            // Handle exceptions or log errors
            e.printStackTrace();
            return null;
        }
    }
}
