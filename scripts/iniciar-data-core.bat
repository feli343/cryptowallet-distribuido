@echo off
echo Iniciando DATA-CORE...
cd /d %~dp0..\data-core
call mvn clean package
java -jar target\data-core-1.0-SNAPSHOT.jar
pause
