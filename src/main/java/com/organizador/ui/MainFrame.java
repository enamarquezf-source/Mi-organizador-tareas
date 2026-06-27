package com.organizador.ui;

import com.organizador.model.Tarea;
import com.organizador.service.TareaService;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

public class MainFrame extends JFrame {
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final Locale SPANISH = Locale.forLanguageTag("es-ES");

    private final TareaService tareaService;
    private final CalendarPanel calendarPanel = new CalendarPanel();
    private final JLabel mesLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel fechaSeleccionadaLabel = new JLabel("Tareas del día");
    private final DefaultListModel<Tarea> tareasListModel = new DefaultListModel<>();
    private final JList<Tarea> tareasList = new JList<>(tareasListModel);

    private YearMonth mesVisible = YearMonth.now();
    private LocalDate fechaSeleccionada = LocalDate.now();

    public MainFrame(TareaService tareaService) {
        this.tareaService = tareaService;
        configurarVentana();
        construirInterfaz();
        recargarTodo();
    }

    private void configurarVentana() {
        setTitle("Mi Organizador de Tareas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1050, 680));
        setLocationRelativeTo(null);

        URL iconUrl = getClass().getResource("/icon.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }

    private void construirInterfaz() {
        JPanel root = new JPanel(new BorderLayout(14, 14));
        root.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        root.setBackground(new Color(245, 247, 250));

        calendarPanel.setOnFechaSeleccionada(fecha -> {
            fechaSeleccionada = fecha;
            recargarTodo();
        });

        root.add(crearCabecera(), BorderLayout.NORTH);
        root.add(crearCentro(), BorderLayout.CENTER);
        root.add(crearPanelTareas(), BorderLayout.EAST);
        setContentPane(root);
    }

    private JPanel crearCabecera() {
        JPanel header = new JPanel(new BorderLayout(10, 10));
        header.setOpaque(false);

        JLabel titulo = new JLabel("Mi Organizador de Tareas");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 24f));

        JButton anteriorButton = new JButton("< Mes anterior");
        anteriorButton.addActionListener(event -> cambiarMes(-1));

        JButton siguienteButton = new JButton("Mes siguiente >");
        siguienteButton.addActionListener(event -> cambiarMes(1));

        JButton agregarButton = new JButton("+ Añadir tarea");
        agregarButton.addActionListener(event -> abrirDialogoNuevaTarea());

        mesLabel.setFont(mesLabel.getFont().deriveFont(Font.BOLD, 18f));

        JPanel navegacion = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        navegacion.setOpaque(false);
        navegacion.add(anteriorButton);
        navegacion.add(mesLabel);
        navegacion.add(siguienteButton);

        JPanel acciones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        acciones.setOpaque(false);
        acciones.add(agregarButton);

        header.add(titulo, BorderLayout.WEST);
        header.add(navegacion, BorderLayout.CENTER);
        header.add(acciones, BorderLayout.EAST);
        return header;
    }

    private JPanel crearCentro() {
        JPanel centro = new JPanel(new BorderLayout(0, 8));
        centro.setOpaque(false);

        JPanel diasSemana = new JPanel(new GridLayout(1, 7, 6, 6));
        diasSemana.setOpaque(false);
        for (String dia : List.of("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")) {
            JLabel label = new JLabel(dia, SwingConstants.CENTER);
            label.setFont(label.getFont().deriveFont(Font.BOLD));
            diasSemana.add(label);
        }

        centro.add(diasSemana, BorderLayout.NORTH);
        centro.add(calendarPanel, BorderLayout.CENTER);
        return centro;
    }

    private JPanel crearPanelTareas() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setPreferredSize(new Dimension(330, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220, 225, 232)),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));

        fechaSeleccionadaLabel.setFont(fechaSeleccionadaLabel.getFont().deriveFont(Font.BOLD, 18f));

        tareasList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tareasList.setCellRenderer((list, tarea, index, isSelected, cellHasFocus) -> {
            JLabel label = new JLabel(HORA_FORMATTER.format(tarea.getHora()) + " - " + tarea.getDescripcion());
            label.setOpaque(true);
            label.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
            label.setBackground(isSelected ? new Color(216, 232, 255) : Color.WHITE);
            return label;
        });

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton editarButton = new JButton("Editar");
        JButton eliminarButton = new JButton("Eliminar");

        editarButton.addActionListener(event -> editarTareaSeleccionada());
        eliminarButton.addActionListener(event -> eliminarTareaSeleccionada());

        botones.add(editarButton);
        botones.add(eliminarButton);

        panel.add(fechaSeleccionadaLabel, BorderLayout.NORTH);
        panel.add(new JScrollPane(tareasList), BorderLayout.CENTER);
        panel.add(botones, BorderLayout.SOUTH);
        return panel;
    }

    private void cambiarMes(int diferencia) {
        mesVisible = mesVisible.plusMonths(diferencia);
        fechaSeleccionada = mesVisible.atDay(1);
        recargarTodo();
    }

    private void recargarTodo() {
        try {
            mesLabel.setText(formatearMes(mesVisible));
            List<Tarea> tareasMes = tareaService.listarPorMes(mesVisible.getYear(), mesVisible.getMonthValue());
            calendarPanel.mostrarMes(mesVisible, fechaSeleccionada, tareasMes);
            recargarTareasDelDia();
        } catch (SQLException ex) {
            mostrarError("No se pudieron cargar las tareas: " + ex.getMessage());
        }
    }

    private void recargarTareasDelDia() throws SQLException {
        tareasListModel.clear();
        fechaSeleccionadaLabel.setText("Tareas del " + fechaSeleccionada);
        for (Tarea tarea : tareaService.listarPorFecha(fechaSeleccionada)) {
            tareasListModel.addElement(tarea);
        }
    }

    private void abrirDialogoNuevaTarea() {
        TareaDialog dialog = new TareaDialog(this, fechaSeleccionada, null);
        dialog.setVisible(true);

        if (!dialog.isGuardado()) {
            return;
        }

        try {
            Tarea tarea = dialog.getTarea();
            tareaService.crearTarea(tarea.getFecha(), tarea.getHora(), tarea.getDescripcion());
            mesVisible = YearMonth.from(tarea.getFecha());
            fechaSeleccionada = tarea.getFecha();
            recargarTodo();
            mostrarInfo("Tarea guardada correctamente.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (SQLException ex) {
            mostrarError("No se pudo guardar la tarea: " + ex.getMessage());
        }
    }

    private void editarTareaSeleccionada() {
        Tarea seleccionada = tareasList.getSelectedValue();
        if (seleccionada == null) {
            mostrarInfo("Seleccione una tarea para editar.");
            return;
        }

        TareaDialog dialog = new TareaDialog(this, seleccionada.getFecha(), seleccionada);
        dialog.setVisible(true);

        if (!dialog.isGuardado()) {
            return;
        }

        try {
            Tarea actualizada = dialog.getTarea();
            tareaService.actualizarTarea(actualizada);
            mesVisible = YearMonth.from(actualizada.getFecha());
            fechaSeleccionada = actualizada.getFecha();
            recargarTodo();
            mostrarInfo("Tarea actualizada correctamente.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (SQLException ex) {
            mostrarError("No se pudo actualizar la tarea: " + ex.getMessage());
        }
    }

    private void eliminarTareaSeleccionada() {
        Tarea seleccionada = tareasList.getSelectedValue();
        if (seleccionada == null) {
            mostrarInfo("Seleccione una tarea para eliminar.");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar la tarea seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (respuesta != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            tareaService.eliminarTarea(seleccionada);
            recargarTodo();
            mostrarInfo("Tarea eliminada correctamente.");
        } catch (IllegalArgumentException ex) {
            mostrarError(ex.getMessage());
        } catch (SQLException ex) {
            mostrarError("No se pudo eliminar la tarea: " + ex.getMessage());
        }
    }

    private String formatearMes(YearMonth mes) {
        String nombreMes = mes.getMonth().getDisplayName(TextStyle.FULL, SPANISH);
        return nombreMes.substring(0, 1).toUpperCase(SPANISH) + nombreMes.substring(1) + " " + mes.getYear();
    }

    private void mostrarInfo(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Mi Organizador de Tareas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Mi Organizador de Tareas", JOptionPane.ERROR_MESSAGE);
    }
}
