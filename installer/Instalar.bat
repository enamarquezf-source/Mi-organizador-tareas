@echo off
setlocal

set "APP_NAME=Mi Organizador de Tareas"
set "INSTALL_DIR=%LOCALAPPDATA%\MiOrganizadorTareas"
set "JAR_NAME=mi-organizador-tareas.jar"
set "ICON_NAME=icon.ico"

where javaw.exe >nul 2>nul
if errorlevel 1 (
    where java.exe >nul 2>nul
    if errorlevel 1 (
        echo No se encontro Java en este equipo.
        echo Instale Java 17 o superior y vuelva a ejecutar este instalador.
        pause
        exit /b 1
    )
)

if not exist "%INSTALL_DIR%" mkdir "%INSTALL_DIR%"
copy /Y "%~dp0%JAR_NAME%" "%INSTALL_DIR%\%JAR_NAME%" >nul
copy /Y "%~dp0%ICON_NAME%" "%INSTALL_DIR%\%ICON_NAME%" >nul

powershell.exe -NoProfile -ExecutionPolicy Bypass -Command "$javaw=(Get-Command javaw.exe -ErrorAction SilentlyContinue).Source; if (-not $javaw) { $javaw=(Get-Command java.exe).Source }; $desktop=[Environment]::GetFolderPath('Desktop'); $shortcut=Join-Path $desktop 'Mi Organizador de Tareas.lnk'; $shell=New-Object -ComObject WScript.Shell; $link=$shell.CreateShortcut($shortcut); $link.TargetPath=$javaw; $link.Arguments='-jar ""%INSTALL_DIR%\%JAR_NAME%""'; $link.WorkingDirectory='%INSTALL_DIR%'; $link.IconLocation='%INSTALL_DIR%\%ICON_NAME%'; $link.Save()"

echo Instalacion completada.
echo Se creo un acceso directo en el Escritorio.
pause
exit /b 0
