package com.example.deber1camachosantiago.controller;

import com.example.deber1camachosantiago.model.Medicion;
import com.example.deber1camachosantiago.service.HistorialService;
import com.example.deber1camachosantiago.service.ReporteService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/historial")
public class HistorialController {

    private final HistorialService historialService;
    private final ReporteService reporteService;

    public HistorialController(HistorialService historialService, ReporteService reporteService) {
        this.historialService = historialService;
        this.reporteService = reporteService;
    }

    @GetMapping
    public List<Medicion> listar() {
        return historialService.listar();
    }

    @DeleteMapping
    public void borrar() {
        historialService.limpiar();
    }

    @GetMapping("/excel")
    public ResponseEntity<byte[]> descargarExcel() throws Exception {
        byte[] archivo = reporteService.generarExcel();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comparacion-cache.xlsx")
                .header(HttpHeaders.CONTENT_TYPE, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .body(archivo);
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> descargarPdf() {
        byte[] archivo = reporteService.generarPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=comparacion-cache.pdf")
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                .body(archivo);
    }
}
