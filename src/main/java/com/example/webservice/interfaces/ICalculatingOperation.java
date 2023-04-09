package com.example.webservice.interfaces;

import com.example.webservice.entities.TriangleEntity;

public interface ICalculatingOperation {
    double ComputePerimeter(TriangleEntity entity);
    double ComputeArea(TriangleEntity entity);

    void ComputeAsync(TriangleEntity entity, Long id);

}
