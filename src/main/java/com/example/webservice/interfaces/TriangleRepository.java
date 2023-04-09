package com.example.webservice.interfaces;

import com.example.webservice.entities.TriangleDatabase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TriangleRepository extends JpaRepository<TriangleDatabase, Long> {
    TriangleDatabase findFirstByOrderByIdDesc();
}
