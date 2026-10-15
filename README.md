# Deber 1 - Caché con Spring Boot

**Autor:** Santiago Camacho

En este deber se hizo una aplicación en Java con Spring Boot que lee 100.000 productos de una base de datos, primero sin caché y después con caché, para ver cuánto cambia el tiempo de respuesta. También tiene una página web sencilla para hacer las pruebas y ver los resultados.

## Qué se usó

- Java 25 y Spring Boot
- Base de datos H2 (vive en memoria, no hay que instalar nada)
- La caché que ya trae Spring
- HTML, CSS y JavaScript para la página

## Cómo correrlo

1. Abrir el proyecto en IntelliJ y esperar a que cargue Gradle.
2. Ejecutar la clase `Deber1CamachoSantiagoApplication`.
3. Esperar a que en la consola salga el mensaje `Se insertaron 100000 productos`.
4. Abrir `frontend/index.html` desde IntelliJ con el ícono del navegador.

## Cómo probarlo

En la página hay dos botones: **Leer sin caché** y **Leer con caché**. Lo mejor es darle unas cuantas veces a cada uno y ver cómo cambian los tiempos. Abajo se va llenando el historial con cada carga.

También se pueden descargar los resultados en **Excel** o **PDF**. El archivo muestra las últimas 5 cargas de cada tipo, el promedio y una pequeña explicación de por qué hay tanta diferencia.

## Por qué la caché es más rápida

Sin caché, cada vez que se pide la información el programa va a la base de datos, hace la consulta y arma los 100.000 productos otra vez. Eso toma varios cientos de milisegundos.

Con caché, la primera vez sí va a la base de datos, pero guarda el resultado en memoria. Las siguientes veces ya no consulta nada y devuelve lo que tenía guardado, por eso tarda menos de un milisegundo. Por eso la primera carga con caché siempre sale un poco lenta.

En mis pruebas sin caché tardaba alrededor de 280 ms y con caché menos de 1 ms.

## El problema de CORS

La página se abre en un puerto (el 63342 de IntelliJ) y el backend está en otro (el 8080). El navegador ve que son direcciones distintas y bloquea las peticiones por seguridad. Para solucionarlo se agregó la clase `CorsConfig`, que le dice al backend que acepte peticiones que vengan de `localhost`. Esto está pensado solo para trabajar en la computadora; si se publicara la página habría que poner su dirección real.
