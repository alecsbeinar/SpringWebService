package com.example.webservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CalculatingResultModel {

    @JsonProperty("perimeter")
    public double perimeter;

    @JsonProperty("area")
    public double area;

    public CalculatingResultModel(double perimeter, double area) {
        this.perimeter = perimeter;
        this.area = area;
    }

    public CalculatingResultModel() {
    }

}
