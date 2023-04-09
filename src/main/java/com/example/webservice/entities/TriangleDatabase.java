package com.example.webservice.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "triangles")
@Data
public class TriangleDatabase {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    private double A;
    private double B;
    private double C;


    public TriangleDatabase(double a, double b, double c) {
        A = a;
        B = b;
        C = c;
    }

    public TriangleDatabase() {
    }
}
