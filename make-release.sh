#!/bin/bash

VERSION=$1

if [[ -z "$VERSION" ]]; then
  echo "usage: make-release.sh VERSION"
  exit 1
fi

JAR=GenomeAnalysisTK-Klevebring.jar
cp ./public/external-example/target/external-example-1.0-SNAPSHOT.jar $JAR

zip GenomeAnalysisTK-Klevebring-${VERSION}.zip $JAR

rm $JAR