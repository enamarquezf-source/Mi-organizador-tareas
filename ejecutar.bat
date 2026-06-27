@echo off
setlocal

cd /d "%~dp0"

if not exist "target\mi-organizador-tareas.jar" (
    echo No existe target\mi-organizador-tareas.jar
    echo Compilando la aplicacion...
    call mvn clean package
    if errorlevel 1 (
        echo.
        echo No se pudo compilar la aplicacion.
        pause
        exit /b 1
    )
)

start "Mi Organizador de Tareas" javaw -jar "target\mi-organizador-tareas.jar"
