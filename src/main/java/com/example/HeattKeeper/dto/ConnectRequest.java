package com.example.HeattKeeper.dto;

import lombok.Data;

@Data
public class ConnectRequest {
    private String code;

    public ConnectRequest(String code) {
        this.code = code;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}