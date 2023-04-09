package com.example.webservice.interfaces;

import com.example.webservice.entities.TriangleEntity;

public interface ICalculatingOperation {
    double computePerimeter(TriangleEntity entity);

    double computeArea(TriangleEntity entity);

    void computeAsync(TriangleEntity entity, Long id);

}
