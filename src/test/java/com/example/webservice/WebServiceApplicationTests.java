package com.example.webservice;

import com.example.webservice.entities.TriangleEntity;
import com.example.webservice.operations.CalculatingOperation;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.stream.Stream;

import static org.junit.jupiter.params.provider.Arguments.arguments;

@SpringBootTest
class WebServiceApplicationTests {

    @Autowired
    CalculatingOperation calculatingOperation;

    @Test
    void contextLoads() {
    }


    @ParameterizedTest
    @MethodSource("TriangleCompute")
    void contextLoads(double a, double b, double c){
        var entity = new TriangleEntity(a, b, c);
        var area = calculatingOperation.ComputeArea(entity);
        var perimeter = calculatingOperation.ComputePerimeter(entity);

        double sp = perimeter / 2;

        if(perimeter == a + b + c)
            Assertions.assertEquals(Math.sqrt(sp * (sp - a) * (sp - b) * (sp - c)), area);

    }

    private static Stream<Arguments> TriangleCompute(){
        return Stream.of(
                arguments(3, 4, 5),
                arguments(5, 6, 7)
        );
    }

}
