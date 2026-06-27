package com.organizador.ui;

import com.organizador.model.Tarea;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TareaDialog extends JDialog {
    private static final DateTimeFormatter FECHA_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final JTextField fechaField = new JTextField(12);
    private final JTextField horaField = new JTextField(8);
    private final JTextArea descripcionArea = new JTextArea(5, 30);
    private final Integer tareaId;

    private Tarea tarea;
    private boolean guardado;

    public TareaDialog(MainFrame owner, LocalDate fechaInicial, Tarea tareaExistente) {
        super(owner, tareaExistente == null ? "Añadir tarea" : "Editar tarea", true);
        this.tareaId = tareaExistente == null ? null : tareaExistente.getId();

        if (tareaExistente == null) {
            fechaField.setText(fechaInicial.format(FECHA_FORMATTER));
            horaField.setText(LocalTime.now().withSecond(0).withNano(0).format(HORA_FORMATTER));
        } else {
            fechaField.setText(tareaExistente.getFecha().format(FECHA_FORMATTER));
            horaField.setText(tareaExistente.getHora().format(HORA_FORMATTER));
            descripcionArea.setText(tareaExistente.getDescripcion());
        }

        construirInterfaz();
        pack();
        setResizable(false);
        setLocationRelativeTo(owner);
    }

    public boolean isGuardado() {
        return guardado;
    }

    public Tarea getTarea() {
        return tarea;
    }

    private void construirInterfaz() {
        JPanel contenido = new JPanel(new BorderLayout(12, 12));
        contenido.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        JPanel formulario = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.HORIZONTAL;

        descripcionArea.setLineWrap(true);
        descripcionArea.setWrapStyleWord(true);

        agregarFila(formulario, c, 0, "Fecha (yyyy-MM-dd):", fechaField);
        agregarFila(formulario, c, 1, "Hora (HH:mm):", horaField);
        agregarFila(formulario, c, 2, "Descripción:", new JScrollPane(descripcionArea));

        contenido.add(formulario, BorderLayout.CENTER);
        contenido.add(crearBotones(), BorderLayout.SOUTH);
        setContentPane(contenido);
    }

    private void agregarFila(JPanel panel, GridBagConstraints c, int fila, String etiqueta, java.awt.Component campo) {
        c.gridx = 0;
        c.gridy = fila;
        c.weightx = 0;
        panel.add(new JLabel(etiqueta), c);

        c.gridx = 1;
        c.weightx = 1;
        panel.add(campo, c);
    }

    private JPanel crearBotones() {
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton cancelarButton = new JButton("Cancelar");
        JButton guardarButton = new JButton("Guardar");

        cancelarButton.addActionListener(event -> dispose());
        guardarButton.addActionListener(event -> guardar());

        botones.add(cancelarButton);
        botones.add(guardarButton);
        return botones;
    }

    private void guardar() {
        try {
            LocalDate fecha = LocalDate.parse(fechaField.getText().trim(), FECHA_FORMATTER);
            LocalTime hora = LocalTime.parse(horaField.getText().trim(), HORA_FORMATTER);
            String descripcion = descripcionArea.getText().trim();

            if (descripcion.isEmpty()) {
                throw new IllegalArgumentException("La descripción es obligatoria.");
            }

            tarea = new Tarea(tareaId, fecha, hora, descripcion);
            guardado = true;
            dispose();
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Revise la fecha y la hora. Use fecha yyyy-MM-dd y hora HH:mm.",
                    "Datos no válidos",
                    JOptionPane.WARNING_MESSAGE
            );
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Datos no válidos", JOptionPane.WARNING_MESSAGE);
        }
    }
}
