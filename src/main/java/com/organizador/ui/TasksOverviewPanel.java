package com.organizador.ui;

import com.organizador.model.Tarea;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class TasksOverviewPanel extends JPanel {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("es-ES"));
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ThemeManager themeManager;
    private final JPanel content = new JPanel(new GridLayout(0, 1, 0, 14));
    private final JScrollPane scrollPane = new JScrollPane(content);

    public TasksOverviewPanel(ThemeManager themeManager) {
        this.themeManager = themeManager;
        setLayout(new BorderLayout(0, 16));
        setOpaque(false);
        content.setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        add(scrollPane, BorderLayout.CENTER);
        applyTheme();
    }

    public void showPendingTasks(List<Tarea> tasks) {
        content.removeAll();

        JLabel title = new JLabel("Tareas pendientes", SwingConstants.LEFT);
        title.setFont(ThemeManager.FONT_TITLE);
        title.setForeground(themeManager.text());
        content.add(title);

        Map<LocalDate, java.util.List<Tarea>> grouped = groupByDate(tasks);
        if (grouped.isEmpty()) {
            JLabel empty = new JLabel("No hay tareas pendientes desde hoy.");
            empty.setFont(ThemeManager.FONT_BASE);
            empty.setForeground(themeManager.mutedText());
            content.add(empty);
        }

        for (Map.Entry<LocalDate, java.util.List<Tarea>> entry : grouped.entrySet()) {
            content.add(createDayGroup(entry.getKey(), entry.getValue()));
        }

        revalidate();
        repaint();
    }

    public void applyTheme() {
        setBackground(themeManager.surface());
        content.setBackground(themeManager.surface());
        scrollPane.getViewport().setBackground(themeManager.surface());
        styleScrollBar(scrollPane.getVerticalScrollBar());
        styleScrollBar(scrollPane.getHorizontalScrollBar());
        repaint();
    }

    private JPanel createDayGroup(LocalDate date, List<Tarea> tasks) {
        RoundedPanel group = new RoundedPanel(24);
        group.setLayout(new BorderLayout(0, 12));
        group.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        group.setBackground(themeManager.surfaceAlt());

        JLabel dateLabel = new JLabel(capitalize(date.format(DATE_FORMATTER)));
        dateLabel.setFont(ThemeManager.FONT_SUBTITLE);
        dateLabel.setForeground(themeManager.text());
        group.add(dateLabel, BorderLayout.NORTH);

        JPanel taskList = new JPanel(new GridLayout(0, 1, 0, 8));
        taskList.setOpaque(false);
        for (Tarea task : tasks) {
            RoundedPanel taskCard = new RoundedPanel(18);
            taskCard.setLayout(new BorderLayout(12, 0));
            taskCard.setBorder(BorderFactory.createEmptyBorder(10, 12, 10, 12));
            taskCard.setBackground(themeManager.surface());

            JLabel hour = new JLabel(HOUR_FORMATTER.format(task.getHora()));
            hour.setFont(ThemeManager.FONT_BASE.deriveFont(java.awt.Font.BOLD));
            hour.setForeground(themeManager.accent());
            JLabel description = new JLabel("<html>" + escape(task.getDescripcion()) + "</html>");
            description.setForeground(themeManager.text());
            taskCard.add(hour, BorderLayout.WEST);
            taskCard.add(description, BorderLayout.CENTER);
            taskList.add(taskCard);
        }
        group.add(taskList, BorderLayout.CENTER);
        return group;
    }

    private Map<LocalDate, java.util.List<Tarea>> groupByDate(List<Tarea> tasks) {
        Map<LocalDate, java.util.List<Tarea>> grouped = new LinkedHashMap<>();
        for (Tarea task : tasks) {
            grouped.computeIfAbsent(task.getFecha(), ignored -> new java.util.ArrayList<>()).add(task);
        }
        return grouped;
    }

    private void styleScrollBar(JScrollBar scrollBar) {
        scrollBar.setBackground(themeManager.surface());
        scrollBar.setForeground(themeManager.accent());
        scrollBar.setPreferredSize(new Dimension(10, 10));
        scrollBar.setUI(new ModernScrollBarUI(themeManager));
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase(Locale.forLanguageTag("es-ES")) + text.substring(1);
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
