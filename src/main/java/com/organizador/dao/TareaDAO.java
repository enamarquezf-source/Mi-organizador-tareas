package com.organizador.dao;

import com.organizador.database.DatabaseManager;
import com.organizador.model.Tarea;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class TareaDAO {
    private final DatabaseManager databaseManager;

    public TareaDAO(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    public void crear(Tarea tarea) throws SQLException {
        String sql = "INSERT INTO tareas(fecha, hora, descripcion) VALUES (?, ?, ?)";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tarea.getFecha().toString());
            statement.setString(2, tarea.getHora().toString());
            statement.setString(3, tarea.getDescripcion());
            statement.executeUpdate();
        }
    }

    public void actualizar(Tarea tarea) throws SQLException {
        String sql = "UPDATE tareas SET fecha = ?, hora = ?, descripcion = ? WHERE id = ?";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, tarea.getFecha().toString());
            statement.setString(2, tarea.getHora().toString());
            statement.setString(3, tarea.getDescripcion());
            statement.setInt(4, tarea.getId());
            statement.executeUpdate();
        }
    }

    public void eliminar(int id) throws SQLException {
        String sql = "DELETE FROM tareas WHERE id = ?";

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    public List<Tarea> listarPorMes(int year, int month) throws SQLException {
        LocalDate inicio = LocalDate.of(year, month, 1);
        LocalDate fin = inicio.plusMonths(1);
        String sql = """
                SELECT id, fecha, hora, descripcion
                FROM tareas
                WHERE fecha >= ? AND fecha < ?
                ORDER BY fecha ASC, hora ASC
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, inicio.toString());
            statement.setString(2, fin.toString());
            return ejecutarListado(statement);
        }
    }

    public List<Tarea> listarPorFecha(LocalDate fecha) throws SQLException {
        String sql = """
                SELECT id, fecha, hora, descripcion
                FROM tareas
                WHERE fecha = ?
                ORDER BY hora ASC
                """;

        try (Connection connection = databaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, fecha.toString());
            return ejecutarListado(statement);
        }
    }

    private List<Tarea> ejecutarListado(PreparedStatement statement) throws SQLException {
        try (ResultSet resultSet = statement.executeQuery()) {
            List<Tarea> tareas = new ArrayList<>();
            while (resultSet.next()) {
                tareas.add(new Tarea(
                        resultSet.getInt("id"),
                        LocalDate.parse(resultSet.getString("fecha")),
                        LocalTime.parse(resultSet.getString("hora")),
                        resultSet.getString("descripcion")
                ));
            }
            return tareas;
        }
    }
}
