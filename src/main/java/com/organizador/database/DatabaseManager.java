package com.organizador.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private final Path databasePath;

    public DatabaseManager() {
        this.databasePath = Path.of(System.getProperty("user.home"), ".mi-organizador-tareas", "tareas.db");
    }

    public void initializeDatabase() throws IOException, SQLException {
        Files.createDirectories(databasePath.getParent());

        try (Connection connection = getConnection(); Statement statement = connection.createStatement()) {
            // Crea la estructura local si es la primera ejecucion de la aplicacion.
            statement.executeUpdate("""
                    CREATE TABLE IF NOT EXISTS tareas (
                        id INTEGER PRIMARY KEY AUTOINCREMENT,
                        fecha TEXT NOT NULL,
                        hora TEXT NOT NULL,
                        descripcion TEXT NOT NULL
                    )
                    """);
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_tareas_fecha ON tareas(fecha)");
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databasePath.toAbsolutePath());
    }
}
