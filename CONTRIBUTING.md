# Contribuir al cuaderno

## Criterios para aceptar código

- Java 17, sin dependencias externas de ejecución.
- Nombres de métodos y tipos que expresen el contrato; índices desde cero y rangos semiabiertos por defecto.
- Javadoc con propósito, complejidad, precondiciones, casos especiales y límites numéricos importantes.
- Pruebas de regresión; para algoritmos delicados, comparación aleatoria con una solución simple.
- Compilación y pruebas con `scripts/check.sh`.
- Registro en `notebook.json` si hay un archivo nuevo o debe cambiar su selección en el PDF.
- Declaración de dependencias mediante `requires` en `notebook.json` cuando el archivo usa otra clase del cuaderno; comprobar también `scripts/export_main.py`.
- Para código basado en una fuente externa, enlace, autor y licencia compatibles. No pegues código sin comprobar permisos.

## Exportación

`notebook.json` contiene los capítulos y perfiles. `scripts/generate_notebook.py` inserta el contenido actual de cada archivo Java en un `.tex`; nunca mantiene una segunda copia del algoritmo. Los perfiles son selecciones iniciales, no límites de páginas garantizados. Para un concurso concreto, selecciona las entradas necesarias, genera el PDF y comprueba el límite y la legibilidad conforme a sus reglas.

## Casos que requieren atención especial

- Desbordamiento de `int` o `long`, incluso en resultados intermedios.
- Grafos vacíos, desconectados, con ciclos o aristas de peso negativo.
- Cadenas vacías y caracteres fuera de ASCII.
- Puntos duplicados, colineales y error de coma flotante.
- La complejidad real en Java, incluidas asignaciones y recursión profunda.
