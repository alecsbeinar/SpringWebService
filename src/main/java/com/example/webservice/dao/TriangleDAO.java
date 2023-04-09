package com.example.webservice.dao;

import com.example.webservice.models.Triangle;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class TriangleDAO {
    private final List<Triangle> triangles;
    {
        triangles = new ArrayList<>();
        triangles.add(new Triangle());
    }
    public void addTriangle(Triangle triangle){triangles.add(triangle);}

    public Triangle getLastTriangle(){return triangles.get(triangles.size() - 1);}
}
