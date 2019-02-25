@echo off
rem This is the automatically built startup script for the DomibusStandaloneConnector.
rem To be able to run the JAVA_HOME system environment variable must be set properly.

if exist "%JAVA_HOME%" goto okJava
call setenv.bat
if exist "%JAVA_HOME%" goto okJava
echo The JAVA_HOME environment variable is not defined correctly
echo This environment variable is needed to run this program
goto end
:okJava

set "CURRENT_DIR=%cd%"

set "CLASSPATH=%CURRENT_DIR%\bin\*"
echo %CLASSPATH%


set "CONFIG_FOLDER=%CURRENT_DIR%\config\"


title "DomibusConnectorClient"

rem -D"spring.config.location=%CONFIG_FOLDER%" -D"spring.config.name=connector" -D"spring.cloud.bootstrap.location=%CONFIG_FOLDER%bootstrap.properties" -D"loader.path=%LIB_FOLDER%"


@echo on
"%JAVA_HOME%\bin\java" -D"loader.path=./lib" -cp "%CLASSPATH%" "org.springframework.boot.loader.PropertiesLauncher"

:end

