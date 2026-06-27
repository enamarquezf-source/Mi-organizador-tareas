package com.organizador.service;

import com.organizador.dao.TareaDAO;
import com.organizador.model.Tarea;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class TareaService {
    private static final int MAX_DESCRIPCION = 300;

    private final TareaDAO tareaDAO;

    public TareaService(TareaDAO tareaDAO) {
        this.tareaDAO = tareaDAO;
    }

    public void crearTarea(LocalDate fecha, LocalTime hora, String descripcion) throws SQLException {
        validar(fecha, hora, descripcion);
        tareaDAO.crear(new Tarea(null, fecha, hora, descripcion.trim()));
    }

    public void actualizarTarea(Tarea tarea) throws SQLException {
        if (tarea == null || tarea.getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una tarea válida para editar.");
        }
        validar(tarea.getFecha(), tarea.getHora(), tarea.getDescripcion());
        tarea.setDescripcion(tarea.getDescripcion().trim());
        tareaDAO.actualizar(tarea);
    }

    public void eliminarTarea(Tarea tarea) throws SQLException {
        if (tarea == null || tarea.getId() == null) {
            throw new IllegalArgumentException("Debe seleccionar una tarea válida para eliminar.");
        }
        tareaDAO.eliminar(tarea.getId());
    }

    public List<Tarea> listarPorMes(int year, int month) throws SQLException {
        return tareaDAO.listarPorMes(year, month);
    }

    public List<Tarea> listarPorFecha(LocalDate fecha) throws SQLException {
        if (fecha == null) {
            throw new IllegalArgumentException("Debe seleccionar una fecha.");
        }
        return tareaDAO.listarPorFecha(fecha);
    }

    private void validar(LocalDate fecha, LocalTime hora, String descripcion) {
        // Las reglas se centralizan para que crear y editar usen la misma proteccion.
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria.");
        }
        if (hora == null) {
            throw new IllegalArgumentException("La hora es obligatoria y debe tener formato HH:mm.");
        }
        if (descripcion == null || descripcion.trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
        if (descripcion.trim().length() > MAX_DESCRIPCION) {
            throw new IllegalArgumentException("La descripción no puede superar " + MAX_DESCRIPCION + " caracteres.");
        }
    }
}
