package common.module.thymeleaf;

import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Locale;
import java.util.Map;

@Component
public class AppTemplateUtil {

    private TemplateEngine templateEngine;

    public AppTemplateUtil(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }

    public String process(String content, Map<String, Object> params) {
        Context context = new Context(Locale.CHINA, params);
        return templateEngine.process(content, context);
    }
}
