@echo off
rem This is the automatically built startup script for the DomibusStandaloneConnector.
rem To be able to run the JAVA_HOME system environment variable must be set properly.

if exist "%JAVA_HOME%" goto okJava
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okJava

set "PATH=%PATH%;%JAVA_HOME%\bin"
set "CURRENT_DIR=%cd%"

set "CLASSPATH=%CURRENT_DIR%\bin\*;%CURRENT_DIR%\lib\*"
echo %CLASSPATH%

:loop
IF NOT "%1"=="" (
    IF "%1"=="-connector-client.properties" (
        SET CONNECTOR_PROPERTIES=%2
        SHIFT
    )
    IF "%1"=="-logging.properties" (
        SET LOGGING_PROPERTIES=%2
        SHIFT
    )
    SHIFT
    GOTO :loop
)

rem set "CONNECTOR_PROPERTIES=%connector-client.properties%"
if exist "%CONNECTOR_PROPERTIES%" goto okConnProps
set "CONNECTOR_PROPERTIES=conf\connector-client.properties
:okConnProps
set "connector-client.properties=%CONNECTOR_PROPERTIES%"
rem echo connector-client.properties set to "%CONNECTOR_PROPERTIES%"

rem set "LOGGING_PROPERTIES=%logging.properties%"
if exist "%LOGGING_PROPERTIES%" goto okLogProps
set "LOGGING_PROPERTIES=conf\log4j.properties
:okLogProps
set "logging.properties=%LOGGING_PROPERTIES%"
rem echo LOGGING_PROPERTIES set to "%LOGGING_PROPERTIES%"

title "DomibusConnectorClient"

java -cp %CLASSPATH% eu.domibus.connector.client.runnable.DomibusConnector

:end