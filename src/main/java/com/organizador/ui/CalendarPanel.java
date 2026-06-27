package com.organizador.ui;

import com.organizador.model.Tarea;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class CalendarPanel extends JPanel {
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private YearMonth mesVisible;
    private LocalDate fechaSeleccionada;
    private Consumer<LocalDate> onFechaSeleccionada;

    public CalendarPanel() {
        setLayout(new GridLayout(0, 7, 6, 6));
        setOpaque(false);
    }

    public void setOnFechaSeleccionada(Consumer<LocalDate> onFechaSeleccionada) {
        this.onFechaSeleccionada = onFechaSeleccionada;
    }

    public void mostrarMes(YearMonth mesVisible, LocalDate fechaSeleccionada, List<Tarea> tareas) {
        this.mesVisible = mesVisible;
        this.fechaSeleccionada = fechaSeleccionada;
        removeAll();

        Map<LocalDate, List<Tarea>> tareasPorFecha = agruparPorFecha(tareas);
        int huecosIniciales = mesVisible.atDay(1).getDayOfWeek().getValue() - 1;

        for (int i = 0; i < huecosIniciales; i++) {
            JPanel emptyPanel = new JPanel();
            emptyPanel.setOpaque(false);
            add(emptyPanel);
        }

        for (int dia = 1; dia <= mesVisible.lengthOfMonth(); dia++) {
            LocalDate fecha = mesVisible.atDay(dia);
            add(crearBotonDia(fecha, tareasPorFecha.getOrDefault(fecha, List.of())));
        }

        revalidate();
        repaint();
    }

    private JButton crearBotonDia(LocalDate fecha, List<Tarea> tareas) {
        StringBuilder html = new StringBuilder("<html><b>").append(fecha.getDayOfMonth()).append("</b>");
        for (Tarea tarea : tareas.stream().limit(3).toList()) {
            html.append("<br>")
                    .append(HORA_FORMATTER.format(tarea.getHora()))
                    .append(" ")
                    .append(escaparHtml(acortar(tarea.getDescripcion())));
        }
        if (tareas.size() > 3) {
            html.append("<br>+").append(tareas.size() - 3).append(" más");
        }
        html.append("</html>");

        JButton button = new JButton(html.toString());
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setVerticalAlignment(SwingConstants.TOP);
        button.setFocusPainted(false);
        button.setBackground(fecha.equals(fechaSeleccionada) ? new Color(216, 232, 255) : Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(tareas.isEmpty() ? new Color(224, 228, 235) : new Color(45, 112, 218)),
                BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        button.addActionListener(event -> {
            if (onFechaSeleccionada != null) {
                onFechaSeleccionada.accept(fecha);
            }
        });
        return button;
    }

    private Map<LocalDate, List<Tarea>> agruparPorFecha(List<Tarea> tareas) {
        Map<LocalDate, List<Tarea>> resultado = new HashMap<>();
        for (Tarea tarea : tareas) {
            resultado.computeIfAbsent(tarea.getFecha(), ignored -> new ArrayList<>()).add(tarea);
        }
        return resultado;
    }

    private String acortar(String texto) {
        return texto.length() <= 18 ? texto : texto.substring(0, 15) + "...";
    }

    private String escaparHtml(String texto) {
        return texto.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
