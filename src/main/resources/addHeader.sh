#! /bin/bash
cat ./src/main/resources/header.txt ${1} > tmp.txt
cat tmp.txt
cp tmp.txt ${1}
