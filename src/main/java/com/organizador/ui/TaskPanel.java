package com.organizador.ui;

import com.organizador.model.Tarea;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TaskPanel extends RoundedPanel {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("es-ES"));
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private static final int EXPANDED_WIDTH = 370;
    private static final int COLLAPSED_WIDTH = 56;

    private final ThemeManager themeManager;
    private final DefaultListModel<Tarea> model = new DefaultListModel<>();
    private final JList<Tarea> list = new JList<>(model);
    private final JLabel titleLabel = new JLabel("Tareas del día");
    private final JLabel dateLabel = new JLabel();
    private final ModernButton toggleButton;
    private final ModernButton editButton;
    private final ModernButton deleteButton;
    private final JPanel content = new JPanel(new BorderLayout(0, 14));

    private boolean collapsed;

    public TaskPanel(ThemeManager themeManager, Runnable onEdit, Runnable onDelete) {
        super(24);
        this.themeManager = themeManager;
        setLayout(new BorderLayout(0, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        toggleButton = new ModernButton("›", ModernButton.Style.ICON, themeManager);
        toggleButton.addActionListener(event -> setCollapsed(!collapsed));
        editButton = new ModernButton("Editar", ModernButton.Style.SECONDARY, themeManager);
        deleteButton = new ModernButton("Eliminar", ModernButton.Style.DANGER, themeManager);
        editButton.addActionListener(event -> onEdit.run());
        deleteButton.addActionListener(event -> onDelete.run());

        buildContent();
        setCollapsed(false);
        applyTheme();
    }

    public Tarea getSelectedTask() {
        return list.getSelectedValue();
    }

    public void setTasks(LocalDate selectedDate, List<Tarea> tasks) {
        model.clear();
        for (Tarea task : tasks) {
            model.addElement(task);
        }
        dateLabel.setText(capitalize(selectedDate.format(DATE_FORMATTER)));
    }

    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
        content.setVisible(!collapsed);
        setPreferredSize(new Dimension(collapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH, 0));
        toggleButton.setText(collapsed ? "‹" : "›");
        revalidate();
        repaint();
    }

    public void applyTheme() {
        setBackground(themeManager.surface());
        content.setOpaque(false);
        titleLabel.setForeground(themeManager.text());
        dateLabel.setForeground(themeManager.mutedText());
        list.setBackground(themeManager.surface());
        list.setForeground(themeManager.text());
        toggleButton.applyTheme(themeManager);
        editButton.applyTheme(themeManager);
        deleteButton.applyTheme(themeManager);
        repaint();
    }

    private void buildContent() {
        JPanel top = new JPanel(new BorderLayout(8, 0));
        top.setOpaque(false);
        JPanel labels = new JPanel(new GridLayout(2, 1, 0, 4));
        labels.setOpaque(false);
        titleLabel.setFont(ThemeManager.FONT_SUBTITLE);
        dateLabel.setFont(ThemeManager.FONT_SMALL);
        labels.add(titleLabel);
        labels.add(dateLabel);
        top.add(labels, BorderLayout.CENTER);
        top.add(toggleButton, BorderLayout.EAST);

        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setFixedCellHeight(72);
        list.setOpaque(false);
        list.setCellRenderer(this::renderTask);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(editButton);
        actions.add(deleteButton);

        content.add(top, BorderLayout.NORTH);
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(actions, BorderLayout.SOUTH);
        add(toggleButton, BorderLayout.NORTH);
        add(content, BorderLayout.CENTER);
    }

    private Component renderTask(JList<? extends Tarea> source, Tarea task, int index, boolean selected, boolean focused) {
        RoundedPanel card = new RoundedPanel(18);
        card.setLayout(new BorderLayout(12, 0));
        card.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
        card.setBackground(selected ? themeManager.accentSoft() : themeManager.surfaceAlt());

        JLabel hour = new JLabel(HOUR_FORMATTER.format(task.getHora()));
        hour.setFont(ThemeManager.FONT_BASE.deriveFont(java.awt.Font.BOLD));
        hour.setForeground(themeManager.accent());

        JLabel description = new JLabel("<html>" + escape(task.getDescripcion()) + "</html>");
        description.setFont(ThemeManager.FONT_BASE);
        description.setForeground(themeManager.text());

        card.add(hour, BorderLayout.WEST);
        card.add(description, BorderLayout.CENTER);
        return card;
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase(Locale.forLanguageTag("es-ES")) + text.substring(1);
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
