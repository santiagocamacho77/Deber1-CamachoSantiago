package com.example.deber1camachosantiago.controller;

import com.example.deber1camachosantiago.model.Medicion;
import com.example.deber1camachosantiago.model.Producto;

import java.util.List;

// lo que se le responde al front: el tiempo de la carga y 10 productos de muestra
// (no se mandan los 100.000 para no medir lo que tarda la red)
public record ResultadoConsulta(Medicion medicion, List<Producto> muestra) {
}
