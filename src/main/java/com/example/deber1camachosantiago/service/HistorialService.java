package com.example.deber1camachosantiago.service;

import com.example.deber1camachosantiago.model.Medicion;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Va guardando en memoria cada carga que se hace para mostrarla en el historial y en el reporte
@Service
public class HistorialService {

    private final List<Medicion> mediciones = new ArrayList<>();

    public synchronized Medicion registrar(String tipo, int registros, double tiempoMs) {
        String hora = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        Medicion medicion = new Medicion(mediciones.size() + 1, tipo, registros, tiempoMs, hora);
        mediciones.add(medicion);
        return medicion;
    }

    public synchronized List<Medicion> listar() {
        return new ArrayList<>(mediciones);
    }

    // devuelve las ultimas "cantidad" cargas de un tipo ("Sin caché" o "Con caché")
    public synchronized List<Medicion> ultimas(String tipo, int cantidad) {
        List<Medicion> delTipo = new ArrayList<>();
        for (Medicion m : mediciones) {
            if (m.getTipo().equals(tipo)) {
                delTipo.add(m);
            }
        }
        int desde = Math.max(0, delTipo.size() - cantidad);
        return delTipo.subList(desde, delTipo.size());
    }

    public synchronized void limpiar() {
        mediciones.clear();
    }
}
