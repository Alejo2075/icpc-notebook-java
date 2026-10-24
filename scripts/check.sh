#!/bin/sh
set -eu
cd "$(dirname "$0")/.."

if [ -n "${JDK_HOME:-}" ]; then
    JAVAC="$JDK_HOME/bin/javac"
    JAVA="$JDK_HOME/bin/java"
elif [ -x "$HOME/.jdks/jdk17/bin/javac" ]; then
    JAVAC="$HOME/.jdks/jdk17/bin/javac"
    JAVA="$HOME/.jdks/jdk17/bin/java"
else
    JAVAC=javac
    JAVA=java
fi

mkdir -p build/classes
"$JAVAC" --release 17 -d build/classes src/main/java/notebook/*.java src/test/java/notebook/*.java
"$JAVA" -ea -cp build/classes notebook.NotebookTest
"$JAVAC" --release 17 -d build/classes src/template/Main.java
python3 -m unittest discover -s scripts/tests -v
python3 scripts/export_main.py --profile full --output build/export-full/Main.java
"$JAVAC" --release 17 -d build/export-full build/export-full/Main.java
python3 scripts/export_main.py --include advanced-graphs --output build/export-advanced/Main.java
"$JAVAC" --release 17 -d build/export-advanced build/export-advanced/Main.java
python3 scripts/smoke_judges.py
