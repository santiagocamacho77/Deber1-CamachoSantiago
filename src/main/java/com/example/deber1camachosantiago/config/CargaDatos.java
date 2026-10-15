package com.example.deber1camachosantiago.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// Cuando arranca la app llena la tabla productos con muchos registros
@Component
public class CargaDatos implements CommandLineRunner {

    private final JdbcTemplate jdbcTemplate;

    @Value("${app.cantidad-registros}")
    private int cantidad;

    public CargaDatos(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(String... args) {
        String[] categorias = {"Laptops", "Monitores", "Teclados", "Mouses", "Audifonos",
                "Impresoras", "Cables", "Almacenamiento", "Redes", "Accesorios"};
        String sql = "INSERT INTO productos (id, nombre, categoria, precio, stock) VALUES (?, ?, ?, ?, ?)";
        Random random = new Random();
        List<Object[]> filas = new ArrayList<>();

        for (int i = 1; i <= cantidad; i++) {
            String categoria = categorias[random.nextInt(categorias.length)];
            double precio = Math.round((5 + random.nextDouble() * 1500) * 100) / 100.0;
            int stock = random.nextInt(500);
            filas.add(new Object[]{i, "Producto " + i, categoria, precio, stock});

            // se insertan de 5000 en 5000 para que no se demore tanto
            if (filas.size() == 5000) {
                jdbcTemplate.batchUpdate(sql, filas);
                filas.clear();
            }
        }
        if (!filas.isEmpty()) {
            jdbcTemplate.batchUpdate(sql, filas);
        }

        System.out.println("Se insertaron " + cantidad + " productos");
    }
}
