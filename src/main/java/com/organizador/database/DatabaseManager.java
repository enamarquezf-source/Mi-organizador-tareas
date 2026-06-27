package com.organizador.database;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
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
                        fecha_fin TEXT,
                        hora TEXT NOT NULL,
                        descripcion TEXT NOT NULL
                    )
                    """);
            if (!columnExists(connection, "tareas", "fecha_fin")) {
                statement.executeUpdate("ALTER TABLE tareas ADD COLUMN fecha_fin TEXT");
                statement.executeUpdate("UPDATE tareas SET fecha_fin = fecha WHERE fecha_fin IS NULL");
            }
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_tareas_fecha ON tareas(fecha)");
            statement.executeUpdate("CREATE INDEX IF NOT EXISTS idx_tareas_fecha_fin ON tareas(fecha_fin)");
        }
    }

    private boolean columnExists(Connection connection, String tableName, String columnName) throws SQLException {
        try (Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("PRAGMA table_info(" + tableName + ")")) {
            while (resultSet.next()) {
                if (columnName.equals(resultSet.getString("name"))) {
                    return true;
                }
            }
            return false;
        }
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:sqlite:" + databasePath.toAbsolutePath());
    }
}
