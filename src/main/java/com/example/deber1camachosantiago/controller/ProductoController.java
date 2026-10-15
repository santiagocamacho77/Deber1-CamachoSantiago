package com.example.deber1camachosantiago.controller;

import com.example.deber1camachosantiago.model.Medicion;
import com.example.deber1camachosantiago.model.Producto;
import com.example.deber1camachosantiago.service.HistorialService;
import com.example.deber1camachosantiago.service.ProductoService;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final HistorialService historialService;

    public ProductoController(ProductoService productoService, HistorialService historialService) {
        this.productoService = productoService;
        this.historialService = historialService;
    }

    @GetMapping("/sin-cache")
    public ResultadoConsulta leerSinCache() {
        long inicio = System.nanoTime();
        List<Producto> productos = productoService.listarSinCache();
        double tiempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

        Medicion medicion = historialService.registrar("Sin caché", productos.size(), tiempoMs);
        return new ResultadoConsulta(medicion, productos.subList(0, 10));
    }

    @GetMapping("/con-cache")
    public ResultadoConsulta leerConCache() {
        long inicio = System.nanoTime();
        List<Producto> productos = productoService.listarConCache();
        double tiempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

        Medicion medicion = historialService.registrar("Con caché", productos.size(), tiempoMs);
        return new ResultadoConsulta(medicion, productos.subList(0, 10));
    }

    @DeleteMapping("/cache")
    public void vaciarCache() {
        productoService.limpiarCache();
    }
}
