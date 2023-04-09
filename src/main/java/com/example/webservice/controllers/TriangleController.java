package com.example.webservice.controllers;

import com.example.webservice.cache.TriangleCache;
import com.example.webservice.constants.ValidationConstants;
import com.example.webservice.entities.TriangleDatabase;
import com.example.webservice.entities.TriangleEntity;
import com.example.webservice.interfaces.TriangleRepository;
import com.example.webservice.models.CalculatingResultModel;
import com.example.webservice.models.StatisticModel;
import com.example.webservice.models.Triangle;
import com.example.webservice.models.TriangleCollectionModel;
import com.example.webservice.models.TriangleCollectionResultModel;
import com.example.webservice.models.TriangleIdModel;
import com.example.webservice.operations.CalculatingOperation;
import com.example.webservice.operations.CounterOperation;
import com.example.webservice.operations.TriangleResultAggregatorOperation;
import com.example.webservice.repositories.DbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.stream.Collectors;


@RestController
public class TriangleController {

    @Autowired
    private CalculatingOperation calculatingOperation;

    @Autowired
    private TriangleCache cache;

    @Autowired
    private CounterOperation counterOperation;

    @Autowired
    private TriangleRepository triangleRepository;

    @Autowired
    private DbContext dbContext;

    private Logger logger = LoggerFactory.getLogger(TriangleController.class);

    @GetMapping("/calculating")
    public CalculatingResultModel calculating(@ModelAttribute("triangle") Triangle triangle) {

        counterOperation.add();

        var cacheValue = cache.get(triangle);
        if (cacheValue != null) {
            return cacheValue;
        }

        double A = triangle.getA();
        double B = triangle.getB();
        double C = triangle.getC();
        var entity = new TriangleEntity(A, B, C);

        if (A + B <= C || A + C <= B || B + C <= A) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationConstants.ARGUMENT_INVALID_MESSAGE);
        } else if (A <= 0 || B <= 0 || C <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationConstants.ARGUMENT_INVALID_MESSAGE);
        }

        try {
            double area = calculatingOperation.ComputeArea(entity);
            double perimeter = calculatingOperation.ComputePerimeter(entity);
            logger.info(String.format("(%f %f %f) Area = %f; Perimeter = %f", A, B, C, area, perimeter));

            var result = new CalculatingResultModel(perimeter, area);
            cache.push(triangle, result);

            if (!triangleRepository.exists(Example.of(new TriangleDatabase(A, B, C)))) {
                triangleRepository.save(new TriangleDatabase(A, B, C));
            }

            return result;
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ValidationConstants.SERVER_ERROR_MESSAGE);
        }
    }

    @PostMapping(value = "/calculating/collection", consumes = "application/json", produces = "application/json")
    public TriangleCollectionResultModel calculatingCollection(@RequestBody TriangleCollectionModel triangles) {
        var entities = triangles.collection.stream().map(triangle -> new TriangleEntity(triangle.getA(), triangle.getB(), triangle.getC()))
                .collect(Collectors.toCollection(ArrayList::new));
        var results = triangles.collection.stream().map(this::calculating).collect(Collectors.toCollection(ArrayList::new));
        var stats = TriangleResultAggregatorOperation.Aggregate(entities, results);
        return new TriangleCollectionResultModel(results, stats);
    }

    @GetMapping("/statistic")
    public StatisticModel statistic() {
        var model = new StatisticModel();
        model.count = counterOperation.getCount();
        return model;
    }

    @PostMapping(value = "/calculatingAsync", consumes = "application/json", produces = "application/json")
    public TriangleIdModel computeCollection(@RequestBody Triangle triangle) {
        var entity = new TriangleEntity(triangle.getA(), triangle.getB(), triangle.getC());
        triangleRepository.save(new TriangleDatabase(triangle.getA(), triangle.getB(), triangle.getC()));
        TriangleDatabase data = triangleRepository.findFirstByOrderByIdDesc();

        calculatingOperation.ComputeAsync(entity, data.getId());
        return new TriangleIdModel(data.getId());
    }

    @GetMapping("/asyncResult")
    public CalculatingResultModel asyncResult(@ModelAttribute TriangleIdModel model) {
        try {
            Statement statement = dbContext.conn.createStatement();

            var sql = String.format("SELECT * FROM results where id = '%d'", model.getId());
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();

            return new CalculatingResultModel(resultSet.getDouble("perimeter"), resultSet.getDouble("area"));
        } catch (Exception ex) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationConstants.ARGUMENT_INVALID_MESSAGE);
        }
    }
}
