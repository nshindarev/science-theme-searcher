#!/usr/bin/env bash
export JAVA_PROGRAM_ARGS=`echo "$@"`
mvn exec:java -Dexec.mainClass="main.Main" -Dexec.args="$JAVA_PROGRAM_ARGS"