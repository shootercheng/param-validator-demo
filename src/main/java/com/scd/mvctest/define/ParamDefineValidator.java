package com.scd.mvctest.define;

import com.alibaba.fastjson.JSON;
import org.catdou.validate.model.InputParam;
import org.catdou.validate.model.ValidateResult;
import org.catdou.validate.model.config.CheckRule;
import org.catdou.validate.type.ParamValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author James
 */
public class ParamDefineValidator implements ParamValidator {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamDefineValidator.class);

    @Override
    public ValidateResult validate(InputParam inputParam, CheckRule checkRule) {
        LOGGER.info(JSON.toJSONString(inputParam));
        ValidateResult validateResult = new ValidateResult();
        validateResult.setSuccess(true);
        return validateResult;
    }
}
