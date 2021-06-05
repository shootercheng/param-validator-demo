package com.scd.mvctest.business.model;

import lombok.Data;

import java.util.List;

/**
 * @author James
 */
@Data
public class ParamVO {
    private Long id;

    private String name;

    private List<DataVO> list;
}
