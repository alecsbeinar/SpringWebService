package com.example.webservice.cache;

import com.example.webservice.models.CalculatingResultModel;
import com.example.webservice.models.Triangle;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class TriangleCache {
    private final Map<Triangle, CalculatingResultModel> triangleMap = Collections.synchronizedMap(new HashMap<>());

    public void push(Triangle key, CalculatingResultModel value) {
        triangleMap.putIfAbsent(key, value);
    }

    public CalculatingResultModel get(Triangle key) {
        return triangleMap.get(key);
    }
}
