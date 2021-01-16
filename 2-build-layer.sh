#!/bin/bash
set -eo pipefail
gradle -q packageLibs
mv build/distributions/fhir-java-lambda.zip build/fhir-java-lambda-lib.zip