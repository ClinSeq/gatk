#!/bin/bash

mvn verify -P\!queue

JAR=GenomeAnalysisTK-Klevebring.jar
cp ./public/external-example/target/external-example-1.0-SNAPSHOT.jar $JAR

