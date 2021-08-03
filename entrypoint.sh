#!/bin/bash

JAVA_ARGS="${JAVA_ARGS} -Djavax.net.ssl.trustStore=/opt/app/cacerts -Djavax.net.ssl.trustStorePassword=changeit"

java  ${JAVA_ARGS} -jar /opt/app/*.jar
