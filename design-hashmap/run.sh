#!/bin/bash
# run.sh

javac -d out -cp out src/**/*.java

# From project root, run the application
java -cp out MainApplication