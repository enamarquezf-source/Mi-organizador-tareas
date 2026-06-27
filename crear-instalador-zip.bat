@echo off
setlocal

set "ROOT=%~dp0"
set "ROOT=%ROOT:~0,-1%"
set "DIST=%ROOT%\dist"
set "PACKAGE=%DIST%\MiOrganizadorTareas-Instalador"
set "ZIP=%DIST%\MiOrganizadorTareas-Instalador.zip"

if not exist "%DIST%" mkdir "%DIST%"
if exist "%PACKAGE%" rmdir /S /Q "%PACKAGE%"
if exist "%ZIP%" del /F /Q "%ZIP%"
mkdir "%PACKAGE%"

echo Compilando aplicacion...
call mvn package
if errorlevel 1 (
    echo No se pudo compilar la aplicacion.
    pause
    exit /b 1
)

copy /Y "%ROOT%\installer\Instalar.bat" "%PACKAGE%\Instalar.bat" >nul
copy /Y "%ROOT%\target\mi-organizador-tareas.jar" "%PACKAGE%\mi-organizador-tareas.jar" >nul
copy /Y "%ROOT%\src\main\resources\icon.ico" "%PACKAGE%\icon.ico" >nul

powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "Compress-Archive -Path '%PACKAGE%\*' -DestinationPath '%ZIP%' -Force"
if not exist "%ZIP%" (
    echo No se pudo crear el ZIP instalable.
    pause
    exit /b 1
)

echo Instalador ZIP creado:
echo %ZIP%
pause
exit /b 0
