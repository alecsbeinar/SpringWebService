package com.example.webservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TriangleIdModel {
    @JsonProperty("id")
    private Long id;

    public TriangleIdModel(Long id) {
        this.id = id;
    }
}
