# Mi Organizador de Tareas

Aplicacion profesional de escritorio para Windows hecha con Java Swing, SQLite y Maven.

## Arquitectura

- `Main.java`: punto de entrada de la aplicacion.
- `model/Tarea.java`: entidad principal de la aplicacion.
- `database/DatabaseManager.java`: crea la carpeta local, abre la conexion SQLite y prepara la tabla.
- `dao/TareaDAO.java`: consultas SQLite con `PreparedStatement` para crear, listar, actualizar y eliminar tareas.
- `service/TareaService.java`: validacion y reglas de negocio.
- `ui/MainFrame.java`: ventana principal.
- `ui/CalendarPanel.java`: calendario mensual.
- `ui/TareaDialog.java`: formulario para anadir o editar tareas.
- `src/main/resources/icon.png`: icono usado por la ventana y para el acceso directo.

## Seguridad

- La app no se conecta a Internet durante su ejecucion.
- No envia datos fuera del equipo.
- No ejecuta comandos externos.
- No guarda contrasenas ni informacion sensible.
- Guarda las tareas localmente en SQLite.
- Usa consultas preparadas para SQLite.
- Valida fecha, hora y descripcion antes de guardar.

La base de datos se crea en:

```text
%USERPROFILE%\.mi-organizador-tareas\tareas.db
```

## Requisitos

- Java 17 o superior.
- Maven 3.8 o superior.

## Compilar

Desde la carpeta del proyecto:

```powershell
cd C:\Users\VORPC\mi-organizador-tareas
mvn clean package
```

El JAR ejecutable se genera en:

```text
target\mi-organizador-tareas.jar
```

## Ejecutar desde PowerShell

```powershell
java -jar target\mi-organizador-tareas.jar
```

## Ejecutar con doble clic

Tambien puede abrir la aplicacion haciendo doble clic en:

```text
ejecutar.bat
```

Si el JAR todavia no existe, el lanzador intentara compilar la aplicacion primero.

## Crear acceso directo en Windows

1. Compile la aplicacion con `mvn clean package`.
2. Haga clic derecho en el Escritorio.
3. Seleccione `Nuevo > Acceso directo`.
4. En ubicacion escriba, ajustando la ruta de Java si cambia:

```text
"C:\Program Files\Java\jdk-25\bin\javaw.exe" -jar "C:\Users\VORPC\mi-organizador-tareas\target\mi-organizador-tareas.jar"
```

5. Nombre del acceso directo: `Mi Organizador de Tareas`.
6. Clic derecho en el acceso directo, `Propiedades > Cambiar icono...`.
7. Seleccione un archivo `.ico` si quiere un icono personalizado. Windows no siempre permite usar `.png` directamente como icono del acceso directo.

Use `javaw.exe` para abrir la app sin consola.

## Pruebas manuales recomendadas

1. Abrir la app y comprobar que aparece el calendario del mes actual.
2. Pulsar `Añadir tarea`, introducir fecha, hora y descripcion, y guardar.
3. Verificar que la tarea aparece en el dia correcto del calendario.
4. Cerrar y volver a abrir la app; la tarea debe seguir visible.
5. Seleccionar una tarea, pulsar `Editar`, cambiar la hora o descripcion y guardar.
6. Seleccionar una tarea, pulsar `Eliminar` y confirmar.
7. Intentar guardar una tarea sin descripcion; debe mostrarse un mensaje de validacion.
8. Intentar guardar una hora invalida, por ejemplo `25:99`; debe mostrarse un mensaje de error.
