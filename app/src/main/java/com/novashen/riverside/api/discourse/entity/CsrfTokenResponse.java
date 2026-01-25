package com.novashen.riverside.api.discourse.entity;

import com.google.gson.annotations.SerializedName;

/**
 * CSRF Token 响应
 */
public class CsrfTokenResponse {
    @SerializedName("csrf")
    private String csrf;

    public String getCsrf() {
        return csrf;
    }

    public void setCsrf(String csrf) {
        this.csrf = csrf;
    }

    @Override
    public String toString() {
        return "CsrfTokenResponse{" +
                "csrf='" + csrf + '\'' +
                '}';
    }
}
