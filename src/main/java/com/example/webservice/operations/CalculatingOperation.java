package com.example.webservice.operations;

import com.example.webservice.controllers.TriangleController;
import com.example.webservice.entities.TriangleEntity;
import com.example.webservice.interfaces.ICalculatingOperation;
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
    private DbContext dbContext;

    Logger logger = LoggerFactory.getLogger(TriangleController.class);

    @Override
    public double computeArea(TriangleEntity triangle) {
        double sp = computePerimeter(triangle) / 2;
        return Math.sqrt(sp * (sp - triangle.A) * (sp - triangle.B) * (sp - triangle.C));
    }

    @Override
    public double computePerimeter(TriangleEntity triangle) {
        return triangle.A + triangle.B + triangle.C;
    }

    @Override
    public void computeAsync(TriangleEntity entity, Long id) {
        CompletableFuture.supplyAsync(() -> {
            try {
                var resultArea = computeArea(entity);
                var resultPerimeter = computePerimeter(entity);
                Thread.sleep(30000);

                var sql = "INSERT INTO results (id, perimeter, area) VALUES(?, ?, ?)";
                PreparedStatement st = dbContext.conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                st.setLong(1, id);
                st.setDouble(2, resultPerimeter);
                st.setDouble(3, resultArea);
                st.executeUpdate();

                logger.info("task completed");
                return null;
            } catch (InterruptedException | SQLException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
