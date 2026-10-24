<div align="center">

<img src="ICPC%20Notebook%20Java%20Team%20Reference%20Logo.png" alt="ICPC Notebook Java Team Reference" width="760">

# ICPC Notebook Java

**Algoritmos de programación competitiva en Java 17, comprobados y exportables a LaTeX/PDF.**

[![Verify notebook](https://github.com/Alejo2075/icpc-notebook-java/actions/workflows/verify.yml/badge.svg)](https://github.com/Alejo2075/icpc-notebook-java/actions/workflows/verify.yml)

</div>

Este repositorio reúne implementaciones para estudiar, practicar y preparar un documento de referencia de equipo. El código Java de `src/main/java/notebook` es la fuente única: las pruebas lo compilan, el generador lo inserta en el cuaderno y el exportador lo combina en un único `Main.java`. También hay una [plantilla](src/template/Main.java) independiente para empezar una solución.

## Contenido

| Tema | Implementaciones |
| --- | --- |
| Entrada y plantilla | lector rápido de tokens ASCII, `Main.java` autónomo |
| Estructuras de datos | DSU, Fenwick, segment tree de sumas |
| Búsqueda y ordenamiento | binaria, ternaria, `lowerBound`, counting sort para `char` |
| Programación dinámica | cambio de monedas, escalones, distancia de edición, mochila 0/1, LCS, LIS, corte de producto, partición mínima, subset sum |
| Cadenas | KMP, función prefijo, Manacher, Boyer-Moore, función Z, trie, rolling hash |
| Matemáticas | MCD/MCM, Euclides extendido, aritmética modular, Miller-Rabin determinista para `long`, criba, divisores, factorial, CRT generalizado, Newton-Raphson |
| Combinatoria | binomiales y Catalan exactos con `BigInteger` |
| Geometría | distancia, área y perímetro, envolvente convexa, par cercano y par lejano |
| Grafos | lista de adyacencia, BFS, DFS, orden topológico, Dijkstra, Kruskal, SCC, Bellman-Ford, LCA, flujo máximo, puentes y puntos de articulación |
| Teoría de juegos | Nim normal, mex, Grundy en DAG, minimax |

Las clases documentan precondiciones y complejidades junto a cada método. En general, los vértices y arreglos se indexan desde cero y los rangos son semiabiertos `[inicio, fin)`.

## Empezar

Requisitos: **JDK 17 o superior** y **Python 3**. Para PDF se necesita además `latexmk` con TeX Live, `pdflatex` o Tectonic. `pdfinfo` de Poppler permite imponer un límite de páginas. En macOS, si el JDK no está en el PATH, el script detecta `~/.jdks/jdk17`; también acepta `JDK_HOME`.

```sh
scripts/check.sh
scripts/build-notebook.sh full
scripts/build-notebook.sh contest
```

Los fuentes LaTeX quedan en `build/full/notebook.tex` y `build/contest/notebook.tex`. Los PDF listos para compartir son el [cuaderno completo](output/pdf/icpc-notebook-java-full.pdf) y la [selección de concurso](output/pdf/icpc-notebook-java-contest.pdf). El perfil `full` contiene todas las entradas; `contest` es una selección inicial más corta. **La cantidad de páginas depende de las herramientas y la tipografía instaladas**: para exigir un máximo, pásalo como segundo argumento, por ejemplo `scripts/build-notebook.sh contest 25`. Si se supera, el comando falla. Comprueba las reglas del concurso antes de imprimir o enviar el PDF.

También puedes generar solo el LaTeX, sin instalar un compilador TeX:

```sh
python3 scripts/generate_notebook.py --profile full --output build/notebook.tex
python3 scripts/generate_notebook.py --include template,structures,graphs --output build/custom/notebook.tex
```

El archivo `.tex` contiene el código Java completo y el generador copia el logo desde la raíz junto al resultado. Compílalo desde la carpeta de salida para que encuentre `logo.png`.

## Exportar un `Main.java` para el juez

Escribe **solo las instrucciones del método `main`** en un archivo `.solve`. El exportador inserta esas instrucciones en la plantilla, añade las clases elegidas y resuelve sus dependencias. Por ejemplo:

```sh
python3 scripts/export_main.py --include structures,graphs \
  --solution tests/judges/atcoder-dsu.solve --output build/Main.java
javac --release 17 build/Main.java
```

`--include` acepta los identificadores de [`notebook.json`](notebook.json). Si se omite, se usa el perfil `contest`; `--profile full` añade toda la biblioteca. La salida tiene una sola clase pública `Main` y no requiere el paquete `notebook`. Para iniciar un problema desde cero, genera el archivo sin `--solution` y rellena el cuerpo de `main`.

## Uso del código

Para usar las clases como biblioteca durante práctica local, compila `src/main/java/notebook`. Para enviar un único archivo a un juez, usa el exportador anterior.

Ejemplo local:

```java
import notebook.DataStructures;

class Example {
    public static void main(String[] args) {
        DataStructures.DSU dsu = new DataStructures.DSU(5);
        dsu.union(0, 1);
        System.out.println(dsu.find(0) == dsu.find(1)); // true
    }
}
```

## Verificación

`scripts/check.sh` compila la biblioteca, la plantilla y dos exportaciones autónomas; ejecuta pruebas de regresión y comparaciones aleatorias reproducibles con una semilla fija; valida los generadores; y prueba archivos `Main.java` contra [ejemplos públicos de AtCoder](tests/judges/cases.json). [GitHub Actions](.github/workflows/verify.yml) repite la comprobación y genera ambos PDF en cada cambio. Entre las comparaciones se incluyen búsqueda de patrones frente a fuerza bruta, palíndromos, partición y subset sum frente a enumeración, geometría frente a todos los pares y multiplicación modular frente a `BigInteger`.

Las pruebas elevan la confianza, pero los ejemplos públicos y las pruebas locales **no equivalen a una aceptación del juez**. Cada concurso puede exigir otros límites numéricos y de tiempo. Revisa las precondiciones del método que vayas a usar.

Para repetir una prueba local con los tamaños máximos de las tareas públicas de DSU y Fenwick, ejecuta `python3 scripts/benchmark_judges.py`. Los tiempos de ese comando corresponden al equipo donde se ejecuta y no se comparan directamente con los límites del juez.

## Añadir o mejorar un algoritmo

1. Añade el código a la clase temática correspondiente, con propósito, complejidad, índices y límites claros.
2. Agrega casos límite y, cuando sea posible, una comparación contra una solución simple en `NotebookTest`.
3. Ejecuta `scripts/check.sh`.
4. Si creas un archivo nuevo, agrégalo a `notebook.json` con título, descripción y perfiles de exportación.
5. Genera los dos cuadernos y revisa el PDF visualmente.

Consulta [CONTRIBUTING.md](CONTRIBUTING.md) para los criterios de aceptación y las notas de procedencia.

## Diseño y referencias

Las implementaciones y API se adaptaron a Java. [CodeLibrary](https://github.com/indy256/codelibrary) y [AtCoderLibraryForJava](https://github.com/NASU41/AtCoderLibraryForJava) sirvieron como referencias para la cobertura temática. [KACTL](https://github.com/kth-competitive-programming/kactl) inspiró el proceso de compilar, probar y exportar un cuaderno compacto. Estas referencias no sustituyen las pruebas locales ni implican que su código se haya copiado al repositorio.

El código y la documentación originales de este repositorio se ofrecen bajo la [licencia MIT](LICENSE). El logo aportado al proyecto permanece en la raíz y se utiliza en esta cabecera y en la portada del PDF; su uso no está cubierto por MIT. Los ejemplos de entrada y salida de AtCoder conservan sus propios términos. Consulta [THIRD_PARTY.md](THIRD_PARTY.md) para la procedencia y el alcance.
