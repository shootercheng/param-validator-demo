package com.scd.mvctest.business.model;

import io.swagger.models.Path;
import io.swagger.models.parameters.AbstractParameter;
import io.swagger.models.parameters.BodyParameter;
import io.swagger.models.parameters.FormParameter;
import io.swagger.models.parameters.Parameter;
import io.swagger.models.parameters.PathParameter;
import io.swagger.models.parameters.QueryParameter;

import java.util.List;

/**
 * @author James
 */
public class UrlPath {
    private String url;

    private String method;

    private List<Parameter> parameterList;

    public UrlPath() {}

    public UrlPath(String url, String method) {
        this.url = url;
        this.method = method;
    }

    public UrlPath(String url, String method, List<Parameter> parameterList) {
        this.url = url;
        this.method = method;
        this.parameterList = parameterList;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public List<Parameter> getParameterList() {
        return parameterList;
    }

    public void setParameterList(List<Parameter> parameterList) {
        this.parameterList = parameterList;
    }
}
