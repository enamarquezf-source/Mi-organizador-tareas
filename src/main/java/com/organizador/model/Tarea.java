package com.organizador.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Tarea {
    private Integer id;
    private LocalDate fecha;
    private LocalDate fechaFin;
    private LocalTime hora;
    private String descripcion;

    public Tarea(Integer id, LocalDate fecha, LocalTime hora, String descripcion) {
        this(id, fecha, fecha, hora, descripcion);
    }

    public Tarea(Integer id, LocalDate fecha, LocalDate fechaFin, LocalTime hora, String descripcion) {
        this.id = id;
        this.fecha = fecha;
        this.fechaFin = fechaFin == null ? fecha : fechaFin;
        this.hora = hora;
        this.descripcion = descripcion;
    }

    public Integer getId() {
        return id;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public LocalTime getHora() {
        return hora;
    }

    public LocalDate getFechaFin() {
        return fechaFin;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setFechaFin(LocalDate fechaFin) {
        this.fechaFin = fechaFin;
    }

    public void setHora(LocalTime hora) {
        this.hora = hora;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
