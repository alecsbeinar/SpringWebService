package com.example.webservice.operations;

import com.example.webservice.controllers.TriangleController;
import com.example.webservice.entities.TriangleEntity;
import com.example.webservice.interfaces.ICalculatingOperation;
import com.example.webservice.interfaces.TriangleRepository;
import com.example.webservice.repositories.DbContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.sql.*;
import java.util.concurrent.CompletableFuture;


@Component
public class CalculatingOperation implements ICalculatingOperation {

    @Autowired
    TriangleRepository triangleRepository;

    Logger logger = LoggerFactory.getLogger(TriangleController.class);

    @Autowired
    DbContext dbContext;

    @Override
    public double ComputeArea(TriangleEntity triangle) {
        double sp = ComputePerimeter(triangle) / 2;
        return Math.sqrt(sp * (sp - triangle.A) * (sp - triangle.B) * (sp - triangle.C));
    }

    @Override
    public double ComputePerimeter(TriangleEntity triangle) {
        return triangle.A + triangle.B + triangle.C;
    }

    @Override
    public void ComputeAsync(TriangleEntity entity, Long id) {
        CompletableFuture.supplyAsync(() -> {
            try{
                var resultArea = ComputeArea(entity);
                var resultPerimeter = ComputePerimeter(entity);
                Thread.sleep(30000);

                var sql = "INSERT INTO results (id, perimeter, area) VALUES(?, ?, ?)";
                PreparedStatement st = dbContext.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                st.setLong(1, id);
                st.setDouble(2, resultPerimeter);
                st.setDouble(3, resultArea);
                st.executeUpdate();

                logger.info("task completed");
                return null;
            } catch (InterruptedException | SQLException e){
                throw new RuntimeException(e);
            }
        });
    }
}
