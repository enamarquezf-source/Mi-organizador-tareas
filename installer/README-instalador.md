# Instalador Windows

Este paquete genera un ZIP instalable para Windows. Se usa este formato porque IExpress no genero un `.exe` fiable en este equipo.

## Crear instalador

```powershell
.\crear-instalador-zip.bat
```

El archivo final queda en:

```text
dist\MiOrganizadorTareas-Instalador.zip
```

La persona que lo recibe debe descomprimir el ZIP y ejecutar `Instalar.bat`.

## Que instala

- Copia `mi-organizador-tareas.jar` en `%LOCALAPPDATA%\MiOrganizadorTareas`.
- Copia el icono de la aplicacion.
- Crea un acceso directo en el Escritorio.

## Datos

No incluye `tareas.db`, por lo que en otro ordenador la aplicacion empieza con calendario vacio.

La base de datos se crea automaticamente en:

```text
%USERPROFILE%\.mi-organizador-tareas\tareas.db
```

## Requisito del ordenador destino

Debe tener Java 17 o superior instalado.
