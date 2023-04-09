package com.example.webservice.models;

public class ErrorModel {
    private String message;

    public ErrorModel(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
