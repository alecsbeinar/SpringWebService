package com.example.webservice.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class Triangle {

    @Positive
    @JsonProperty("A")
    private double A;


    @Positive
    @JsonProperty("B")
    private double B;


    @Positive
    @JsonProperty("C")
    private double C;


    public Triangle(double a, double b, double c) {
        A = a;
        B = b;
        C = c;
    }

    public Triangle() {
    }
}
