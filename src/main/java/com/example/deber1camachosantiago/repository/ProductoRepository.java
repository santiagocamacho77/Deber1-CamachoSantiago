package com.example.deber1camachosantiago.repository;

import com.example.deber1camachosantiago.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductoRepository extends JpaRepository<Producto, Long> {
}
