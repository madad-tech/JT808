#!/bin/bash

# Stop script on error
set -e

# Check to see if root CA file exists, download if not
if [ ! -f "root-CA.crt" ]; then
    echo "Downloading AWS IoT Root CA certificate from AWS..."
    curl -o root-CA.crt https://www.amazontrust.com/repository/AmazonRootCA1.pem
fi

# Install AWS Device SDK for Java if not already installed
if [ ! -d "aws-iot-device-sdk-java-v2" ]; then
    echo "Installing AWS SDK..."
    git config --global core.longpaths true
    git clone --recursive https://github.com/aws/aws-iot-device-sdk-java-v2.git
    cd aws-iot-device-sdk-java-v2
    mvn versions:use-latest-versions -Dincludes="software.amazon.awssdk.crt*"
    mvn clean install -Dmaven.test.skip=true
    cd ..
fi

# Run pub/sub sample app using certificates downloaded in package
echo "Running lkpofjezfkeznf sample application..."
mvn package appassembler:assemble -Dexec.args="--endpoint a13p3ird4zt8ai-ats.iot.eu-central-1.amazonaws.com --client_id sdk-java --topic sdk/test/java --ca_file root-CA.crt --cert gateway.cert.pem --key gateway.private.key"
