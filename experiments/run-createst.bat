@echo off

:: Set Java home to a Java JDK 9 instalaltion
set "JAVA_HOME=C:\Program Files\Java\jdk-9.0.4"
set "Path=%JAVA_HOME%\bin;%Path%"

:: Set path to scc.bat
set "sccPath=C:\Users\nico.pellegrinelli\Desktop\Eclipse\itemis_CREATE\scc.bat"

:: Execute the PowerShell command that runs CREATest
powershell -Command "& { $env:sccPath='%sccPath%'; Get-ChildItem benchmark\*.ysc | Sort-Object Name | ForEach-Object { java -jar CREATest-0.0.3.jar -s $env:sccPath -y ('benchmark\' + $_.BaseName + '.ysc') -g -e 2>&1 | Tee-Object -FilePath ($_.BaseName + '_exec.txt') } }"