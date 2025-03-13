@echo off
set "sccPath=C:\Users\nico.pellegrinelli\Desktop\Eclipse\itemis_CREATE\scc.bat"
powershell -Command "& { $env:sccPath='%sccPath%'; Get-ChildItem benchmark\*.ysc | Sort-Object Name | ForEach-Object { java -jar CREATest-0.0.3.jar -s $env:sccPath -y ('benchmark\' + $_.BaseName + '.ysc') -g -e | Tee-Object -FilePath ($_.BaseName + '.txt') } }"