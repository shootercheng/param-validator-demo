package com.scd.mvctest.business.controller;

import io.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author James
 */
@RestController
@RequestMapping(value = "/file")
//@Api(tags = "File Upload Download")
public class FileController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    @PostMapping("/upload")
    public String upload(HttpServletRequest httpServletRequest,
                         @RequestPart(value = "business") MultipartFile multipartFile,
                         @RequestParam(value = "sequence") Integer sequence) throws IOException {
        String fileName = multipartFile.getOriginalFilename();
        byte[] bytes = multipartFile.getBytes();
        LOGGER.info("file name {} sequence {}", fileName, sequence);
        return "success";
    }
}
