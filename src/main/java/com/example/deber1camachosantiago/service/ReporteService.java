package com.example.deber1camachosantiago.service;

import com.example.deber1camachosantiago.model.Medicion;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openpdf.text.Document;
import org.openpdf.text.Font;
import org.openpdf.text.FontFactory;
import org.openpdf.text.PageSize;
import org.openpdf.text.Paragraph;
import org.openpdf.text.Phrase;
import org.openpdf.text.pdf.PdfPCell;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

// Arma el reporte con las ultimas 5 cargas sin cache y con cache, en Excel o en PDF
@Service
public class ReporteService {

    private static final String TITULO = "Comparación de tiempos de carga: sin caché vs con caché";

    private static final String RESUMEN =
            "Sin caché, cada carga va a la base de datos: ejecuta el SELECT y convierte cada fila en un objeto "
            + "Producto, y eso se repite en todas las peticiones. Con caché (@Cacheable) solo la primera carga va a "
            + "la base de datos y la lista queda guardada en memoria; las siguientes devuelven esa misma lista sin "
            + "consultar la base, por eso tardan casi nada. Si alguna carga con caché salió lenta, es la que llenó "
            + "la caché (la primera, o la que vino después de vaciarla).";

    private final HistorialService historialService;

    @Value("${app.cantidad-registros}")
    private int cantidadRegistros;

    public ReporteService(HistorialService historialService) {
        this.historialService = historialService;
    }

    public byte[] generarExcel() throws IOException {
        List<Medicion> sinCache = historialService.ultimas("Sin caché", 5);
        List<Medicion> conCache = historialService.ultimas("Con caché", 5);

        XSSFWorkbook libro = new XSSFWorkbook();
        Sheet hoja = libro.createSheet("Comparación");

        CellStyle negrita = libro.createCellStyle();
        org.apache.poi.ss.usermodel.Font fuenteNegrita = libro.createFont();
        fuenteNegrita.setBold(true);
        negrita.setFont(fuenteNegrita);

        CellStyle textoLargo = libro.createCellStyle();
        textoLargo.setWrapText(true);

        hoja.createRow(0).createCell(0).setCellValue(TITULO);
        hoja.getRow(0).getCell(0).setCellStyle(negrita);
        hoja.createRow(1).createCell(0).setCellValue(
                "Cada carga lee " + String.format("%,d", cantidadRegistros) + " productos. Se muestran las últimas 5 de cada tipo.");

        Row encabezado = hoja.createRow(3);
        String[] columnas = {"Carga", "Sin caché (ms)", "Con caché (ms)"};
        for (int i = 0; i < columnas.length; i++) {
            encabezado.createCell(i).setCellValue(columnas[i]);
            encabezado.getCell(i).setCellStyle(negrita);
        }

        for (int i = 0; i < 5; i++) {
            Row fila = hoja.createRow(4 + i);
            fila.createCell(0).setCellValue(i + 1);
            ponerTiempo(fila, 1, sinCache, i);
            ponerTiempo(fila, 2, conCache, i);
        }

        Row filaPromedio = hoja.createRow(9);
        filaPromedio.createCell(0).setCellValue("Promedio");
        filaPromedio.createCell(1).setCellValue(redondear(promedio(sinCache)));
        filaPromedio.createCell(2).setCellValue(redondear(promedio(conCache)));
        filaPromedio.getCell(0).setCellStyle(negrita);

        Row filaDiferencia = hoja.createRow(10);
        filaDiferencia.createCell(0).setCellValue("Diferencia");
        filaDiferencia.createCell(1).setCellValue(diferencia(sinCache, conCache));
        filaDiferencia.getCell(0).setCellStyle(negrita);

        hoja.createRow(12).createCell(0).setCellValue("¿Por qué pasa esto?");
        hoja.getRow(12).getCell(0).setCellStyle(negrita);

        // el resumen va en una celda combinada de A14 a F14 para que se lea bien
        Row filaResumen = hoja.createRow(13);
        filaResumen.createCell(0).setCellValue(RESUMEN);
        filaResumen.getCell(0).setCellStyle(textoLargo);
        filaResumen.setHeightInPoints(95);
        hoja.addMergedRegion(new CellRangeAddress(13, 13, 0, 5));

        hoja.setColumnWidth(0, 14 * 256);
        hoja.setColumnWidth(1, 18 * 256);
        hoja.setColumnWidth(2, 18 * 256);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        libro.write(salida);
        libro.close();
        return salida.toByteArray();
    }

    public byte[] generarPdf() {
        List<Medicion> sinCache = historialService.ultimas("Sin caché", 5);
        List<Medicion> conCache = historialService.ultimas("Con caché", 5);

        Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 15);
        Font fuenteNegrita = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11);
        Font fuenteNormal = FontFactory.getFont(FontFactory.HELVETICA, 11);

        ByteArrayOutputStream salida = new ByteArrayOutputStream();
        Document documento = new Document(PageSize.A4, 50, 50, 50, 50);
        PdfWriter.getInstance(documento, salida);
        documento.open();

        documento.add(new Paragraph(TITULO, fuenteTitulo));
        documento.add(new Paragraph("Cada carga lee " + String.format("%,d", cantidadRegistros)
                + " productos. Se muestran las últimas 5 de cada tipo.", fuenteNormal));

        PdfPTable tabla = new PdfPTable(3);
        tabla.setWidthPercentage(100);
        tabla.setSpacingBefore(15);
        tabla.setSpacingAfter(10);

        celda(tabla, "Carga", fuenteNegrita, true);
        celda(tabla, "Sin caché (ms)", fuenteNegrita, true);
        celda(tabla, "Con caché (ms)", fuenteNegrita, true);

        for (int i = 0; i < 5; i++) {
            celda(tabla, String.valueOf(i + 1), fuenteNormal, false);
            celda(tabla, tiempo(sinCache, i), fuenteNormal, false);
            celda(tabla, tiempo(conCache, i), fuenteNormal, false);
        }

        celda(tabla, "Promedio", fuenteNegrita, true);
        celda(tabla, formato(promedio(sinCache)), fuenteNegrita, true);
        celda(tabla, formato(promedio(conCache)), fuenteNegrita, true);
        documento.add(tabla);

        documento.add(new Paragraph("Diferencia: " + diferencia(sinCache, conCache), fuenteNegrita));

        Paragraph subtitulo = new Paragraph("¿Por qué pasa esto?", fuenteNegrita);
        subtitulo.setSpacingBefore(15);
        documento.add(subtitulo);
        documento.add(new Paragraph(RESUMEN, fuenteNormal));

        documento.close();
        return salida.toByteArray();
    }

    private void celda(PdfPTable tabla, String texto, Font fuente, boolean resaltada) {
        PdfPCell celda = new PdfPCell(new Phrase(texto, fuente));
        celda.setPadding(6);
        if (resaltada) {
            celda.setBackgroundColor(new Color(240, 241, 244));
        }
        tabla.addCell(celda);
    }

    // en el Excel los tiempos van como numero para poder hacer calculos con ellos
    private void ponerTiempo(Row fila, int columna, List<Medicion> lista, int i) {
        if (i < lista.size()) {
            fila.createCell(columna).setCellValue(redondear(lista.get(i).getTiempoMs()));
        } else {
            fila.createCell(columna).setCellValue("-");
        }
    }

    private double redondear(double valor) {
        return Math.round(valor * 100) / 100.0;
    }

    // tiempo de la carga en la posicion i, o "-" si todavia no hay tantas cargas
    private String tiempo(List<Medicion> lista, int i) {
        if (i < lista.size()) {
            return formato(lista.get(i).getTiempoMs());
        }
        return "-";
    }

    private double promedio(List<Medicion> lista) {
        if (lista.isEmpty()) {
            return 0;
        }
        double suma = 0;
        for (Medicion m : lista) {
            suma += m.getTiempoMs();
        }
        return suma / lista.size();
    }

    private String diferencia(List<Medicion> sinCache, List<Medicion> conCache) {
        double promedioSin = promedio(sinCache);
        double promedioCon = promedio(conCache);
        if (promedioSin == 0 || promedioCon == 0) {
            return "Faltan cargas de algún tipo para comparar";
        }
        long veces = Math.round(promedioSin / promedioCon);
        return "Con caché fue " + veces + " veces más rápido";
    }

    private String formato(double valor) {
        if (valor == 0) {
            return "-";
        }
        return String.format("%.2f", valor);
    }
}
