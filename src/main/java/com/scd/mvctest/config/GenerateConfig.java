package com.scd.mvctest.config;

import org.catdou.param.generate.config.RequestMethodProvider;
import org.catdou.param.generate.core.json.JsonGenerator;
import org.catdou.param.generate.model.GenerateParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.io.File;
import java.util.List;

/**
 * @author James
 */
@Configuration
public class GenerateConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateConfig.class);

    public GenerateConfig(List<RequestMappingInfoHandlerMapping> handlerMappings) {
        RequestMethodProvider requestMethodProvider = new RequestMethodProvider(handlerMappings);
        LOGGER.info("generate config success {}", requestMethodProvider);
        JsonGenerator jsonGenerator = new JsonGenerator();
        GenerateParam generateParam = new GenerateParam();
        String path = "file";
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        generateParam.setParentPath(path);
        jsonGenerator.generate(generateParam);
    }
}
