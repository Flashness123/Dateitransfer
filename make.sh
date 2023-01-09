#!/bin/bash

if [[ $1 = "clean" ]]
then
  rm -rf bin/*
else
  mkdir -p bin/
  javac src/*java src/Packets/*.java -d ./bin/
fi
