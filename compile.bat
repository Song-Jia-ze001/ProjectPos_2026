@echo off
cd /d %~dp0
if not exist bin mkdir bin
javac -encoding UTF-8 -d bin src/pos/domain/*.java src/pos/patterns/*.java src/pos/ui/*.java src/pos/Main.java src/pos/test/*.java
if errorlevel 1 exit /b 1
echo Compile OK
java -cp bin pos.test.SaleTest
echo Run GUI:  java -cp bin pos.Main
echo Screenshots: java -cp bin pos.Main --screenshots
