package com.example.webservice.controllers;

import com.example.webservice.cache.TriangleCache;
import com.example.webservice.constants.ValidationConstants;
import com.example.webservice.entities.TriangleDatabase;
import com.example.webservice.entities.TriangleEntity;
import com.example.webservice.interfaces.TriangleRepository;
import com.example.webservice.models.*;
import com.example.webservice.operations.CalculatingOperation;
import com.example.webservice.operations.CounterOperation;
import com.example.webservice.operations.TriangleResultAggregatorOperation;
import com.example.webservice.repositories.DbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;


@RestController
public class TriangleController {

    @Autowired
    private CalculatingOperation calculatingOperation;

    @Autowired
    private TriangleCache<Triangle, CalculatingResultModel> cache;

    @Autowired
    private CounterOperation counterOperation;

    @Autowired
    private TriangleRepository triangleRepository;

    @Autowired
    DbContext dbContext;

    Logger logger = LoggerFactory.getLogger(TriangleController.class);
    Lock lock = new ReentrantLock();


    @GetMapping("/calculating")
    public CalculatingResultModel calculating(@ModelAttribute("triangle") Triangle triangle){

        lock.lock();
        counterOperation.Add();
        lock.unlock();

        var cacheValue = cache.Get(triangle);
        if(cacheValue != null) return cacheValue;

        double A = triangle.getA();
        double B = triangle.getB();
        double C = triangle.getC();
        var entity = new TriangleEntity(A, B, C);

        if(A + B <= C || A + C <= B || B + C <= A)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationConstants.ArgumentInvalidMessage);
        else if (A <= 0 || B <= 0 || C <= 0)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationConstants.ArgumentInvalidMessage);

        try
        {
            double area = calculatingOperation.ComputeArea(entity);
            double perimeter = calculatingOperation.ComputePerimeter(entity);
            logger.info(String.format("(%f %f %f) Area = %f; Perimeter = %f", A, B, C, area, perimeter));

            var result = new CalculatingResultModel(perimeter, area);
            cache.Push(triangle, result);

            if(!triangleRepository.exists(Example.of(new TriangleDatabase(A, B, C)))){
                triangleRepository.save(new TriangleDatabase(A, B, C));
            }

            return result;
        }
        catch (Exception ex){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, ValidationConstants.ServerErrorMessage);
        }
    }

    @PostMapping(value = "/calculating/collection", consumes = "application/json", produces = "application/json")
    public TriangleCollectionResultModel calculatingCollection(@RequestBody TriangleCollectionModel triangles){
        var entities = triangles.collection.stream().parallel().map(triangle -> new TriangleEntity(triangle.getA(), triangle.getB(), triangle.getC())).collect(Collectors.toCollection(ArrayList::new));
        var results = triangles.collection.stream().parallel().map(this::calculating).collect(Collectors.toCollection(ArrayList::new));
        var stats = TriangleResultAggregatorOperation.Aggregate(entities, results);
        return new TriangleCollectionResultModel(results, stats);
    }

    @GetMapping("/statistic")
    public StatisticModel statistic(){
        var model = new StatisticModel();
        model.count = counterOperation.GetCount();
        return model;
    }

    @PostMapping(value = "/calculatingAsync", consumes = "application/json", produces = "application/json")
    public TriangleIdModel computeCollection(@RequestBody Triangle triangle){
        var entity = new TriangleEntity(triangle.getA(), triangle.getB(), triangle.getC());
        triangleRepository.save(new TriangleDatabase(triangle.getA(), triangle.getB(), triangle.getC()));
        TriangleDatabase data = triangleRepository.findFirstByOrderByIdDesc();

        calculatingOperation.ComputeAsync(entity, data.getId());
        return new TriangleIdModel(data.getId());
    }

    @GetMapping("/asyncResult")
    public CalculatingResultModel asyncResult(@ModelAttribute TriangleIdModel model)
    {
        try
        {
            Statement statement = dbContext.conn.createStatement();

            var sql = String.format("SELECT * FROM results where id = '%d'", model.getId());
            ResultSet resultSet = statement.executeQuery(sql);
            resultSet.next();

            return new CalculatingResultModel(resultSet.getDouble("perimeter"), resultSet.getDouble("area"));
        }
        catch (Exception ex)
        {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ValidationConstants.ArgumentInvalidMessage);
        }
    }
}
