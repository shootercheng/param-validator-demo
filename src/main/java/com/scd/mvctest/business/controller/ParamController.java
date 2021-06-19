package com.scd.mvctest.business.controller;

import com.scd.mvctest.business.model.ParamVO;
import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author James
 */
@RestController
//@Api(tags = "param controller")
@RequestMapping("/param")
public class ParamController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ParamController.class);

    @PostMapping("/body")
    public String checkParam(@RequestBody ParamVO paramVO) {
        LOGGER.info("param vo {}", paramVO);
        return "success";
    }

    @PostMapping("/body/{key}")
    public String checkBodyUrl(@RequestBody ParamVO paramVO, @PathVariable(value = "key") String key, String taskId) {
        LOGGER.info("param vo {}, key {}, url param {}", paramVO, key, taskId);
        return "success";
    }

    @PostMapping("/form")
    public String checkFormData(ParamVO paramVO) {
        LOGGER.info("param vo {}", paramVO);
        return "success";
    }

    @GetMapping("/url")
    public String checkGetData(ParamVO paramVO, String id, String name) {
        LOGGER.info("param vo {} id {} name {}", paramVO, id, name);
        return "success";
    }
}
