#!/bin/bash

clear

echo "Version to load $1"

echo $1 > current-version 
cat current-version

cp ./prod/$1/GlucoMonStationary.jad .
cp ./prod/$1/GlucoMonStationary.jar .


cat GlucoMonStationary.jad 


diff -s ./prod/$1/GlucoMonStationary.jad GlucoMonStationary.jad
diff -s ./prod/$1/GlucoMonStationary.jar GlucoMonStationary.jar

grep $1 prod/glucomon.prod.deployments
