package com.example.webservice.repositories;

import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;

@Component
public class DbContext {
    public Connection conn;

    public DbContext()
    {
        try
        {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/triangles", "Admin", "123");
        }
        catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
