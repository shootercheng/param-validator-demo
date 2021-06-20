package com.scd.mvctest.config;

import org.catdou.param.generate.config.RequestMethodProvider;
import org.catdou.param.generate.constant.GenTypeEnum;
import org.catdou.param.generate.core.BaseGenerator;
import org.catdou.param.generate.factory.GenerateFactory;
import org.catdou.param.generate.model.GenerateParam;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.mvc.method.RequestMappingInfoHandlerMapping;

import java.util.List;

/**
 * @author James
 */
//@Configuration
public class SimpleDocConfig {

    public SimpleDocConfig(List<RequestMappingInfoHandlerMapping> handlerMappings) {
        RequestMethodProvider requestMethodProvider = new RequestMethodProvider(handlerMappings);
        requestMethodProvider.initHandlerMapping();
        GenerateParam generateParam = new GenerateParam();
        generateParam.setParentPath("file");
        BaseGenerator baseGenerator = GenerateFactory.createGenerator(GenTypeEnum.JSON);
        baseGenerator.generate(generateParam);
    }
}
