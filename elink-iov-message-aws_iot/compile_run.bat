@echo off
setlocal enabledelayedexpansion

REM Stop script on error
set "errorlevel=0"

REM Set Maven path
set MAVEN_PATH=D:\sonasid_3.2024\project\ig_iot_tracker\apache-maven-3.9.9\bin\mvn.cmd

REM Check to see if root CA file exists, download if not
if not exist "root-CA.crt" (
    echo.
    echo Downloading AWS IoT Root CA certificate from AWS...
    curl https://www.amazontrust.com/repository/AmazonRootCA1.pem -o root-CA.crt
)

REM Install AWS Device SDK for Java if not already installed
if not exist "aws-iot-device-sdk-java-v2" (
    echo.
    echo Installing AWS SDK...
	git config --global core.longpaths true
    git clone https://github.com/aws/aws-iot-device-sdk-java-v2.git --recursive
    cd aws-iot-device-sdk-java-v2
    "%MAVEN_PATH%" versions:use-latest-versions -Dincludes="software.amazon.awssdk.crt*"
    "%MAVEN_PATH%" clean install -Dmaven.test.skip=true
    cd ..
)

REM Run pub/sub sample app using certificates downloaded in package
echo.
echo Running lkpofjezfkeznf sample application...

"%MAVEN_PATH%" package appassembler:assemble -Dexec.args="--endpoint a13p3ird4zt8ai-ats.iot.eu-central-1.amazonaws.com --client_id sdk-java --topic sdk/test/java --ca_file root-CA.crt --cert gateway.cert.pem --key gateway.private.key"

endlocal
