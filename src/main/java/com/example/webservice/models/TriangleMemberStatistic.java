package com.example.webservice.models;

public class TriangleMemberStatistic {
    public String name;
    public double min;
    public double middle;
    public double max;

    public TriangleMemberStatistic(String name, double min, double middle, double max) {
        this.name = name;
        this.min = min;
        this.middle = middle;
        this.max = max;
    }
}
