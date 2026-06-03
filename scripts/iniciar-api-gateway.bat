@echo off
echo Iniciando API GATEWAY...
cd /d %~dp0..\api-gateway
call mvn clean package
java -jar target\api-gateway-1.0-SNAPSHOT.jar
pause
