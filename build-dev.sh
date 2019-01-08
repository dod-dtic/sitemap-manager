#!/usr/bin/bash

echo Building sitemap-manager
mvn clean package $1 -f "./pom.xml"
echo
echo

cp ./target/sitemap-manager-0.0.1-SNAPSHOT.war ./target/sitemap-manager.war
