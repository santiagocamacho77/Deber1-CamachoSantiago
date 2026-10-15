package com.example.deber1camachosantiago.service;

import com.example.deber1camachosantiago.model.Producto;
import com.example.deber1camachosantiago.repository.ProductoRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {
        this.productoRepository = productoRepository;
    }

    // siempre consulta la base de datos
    public List<Producto> listarSinCache() {
        return productoRepository.findAll();
    }

    // la primera vez consulta la base y guarda el resultado en la cache "productos",
    // las siguientes veces Spring devuelve lo guardado y ni entra al metodo
    @Cacheable("productos")
    public List<Producto> listarConCache() {
        System.out.println("Consultando la base de datos para llenar la cache...");
        return productoRepository.findAll();
    }

    @CacheEvict(value = "productos", allEntries = true)
    public void limpiarCache() {
        System.out.println("Cache vaciada");
    }
}
