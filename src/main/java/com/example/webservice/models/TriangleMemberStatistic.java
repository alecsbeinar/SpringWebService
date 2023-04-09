package com.example.webservice.models;

public class TriangleMemberStatistic {
    private String name;
    private double min;
    private double middle;
    private double max;

    public TriangleMemberStatistic(String name, double min, double middle, double max) {
        this.name = name;
        this.min = min;
        this.middle = middle;
        this.max = max;
    }
}
