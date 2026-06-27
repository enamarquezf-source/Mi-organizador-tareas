package com.organizador.ui;

import com.organizador.model.Tarea;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Consumer;

public class WeekViewPanel extends JPanel {
    private static final DateTimeFormatter DAY_FORMATTER = DateTimeFormatter.ofPattern("EEE d", Locale.forLanguageTag("es-ES"));
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ThemeManager themeManager;
    private Consumer<LocalDate> onDateSelected;
    private LocalDate selectedDate;

    public WeekViewPanel(ThemeManager themeManager) {
        this.themeManager = themeManager;
        setLayout(new GridLayout(1, 7, 10, 10));
        setOpaque(false);
    }

    public void setOnDateSelected(Consumer<LocalDate> onDateSelected) {
        this.onDateSelected = onDateSelected;
    }

    public void showWeek(LocalDate selectedDate, List<Tarea> tasks) {
        this.selectedDate = selectedDate;
        removeAll();
        LocalDate start = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - 1L);
        Map<LocalDate, List<Tarea>> groupedTasks = groupByDate(tasks);
        for (int i = 0; i < 7; i++) {
            LocalDate date = start.plusDays(i);
            add(createDayColumn(date, groupedTasks.getOrDefault(date, List.of())));
        }
        revalidate();
        repaint();
    }

    public void applyTheme() {
        repaint();
    }

    private JPanel createDayColumn(LocalDate date, List<Tarea> tasks) {
        RoundedPanel panel = new RoundedPanel(22);
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(14, 12, 14, 12));
        panel.setBackground(date.equals(selectedDate) ? themeManager.accentSoft() : date.getDayOfWeek().getValue() >= 6 ? themeManager.weekend() : themeManager.surfaceAlt());
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent event) {
                if (onDateSelected != null) {
                    onDateSelected.accept(date);
                }
            }
        });

        JLabel title = new JLabel(capitalize(date.format(DAY_FORMATTER)), SwingConstants.CENTER);
        title.setFont(ThemeManager.FONT_BASE.deriveFont(java.awt.Font.BOLD));
        title.setForeground(themeManager.text());
        panel.add(title, BorderLayout.NORTH);

        JPanel taskList = new JPanel(new GridLayout(0, 1, 0, 7));
        taskList.setOpaque(false);
        for (Tarea task : tasks) {
            JLabel label = new JLabel("<html><b>" + HOUR_FORMATTER.format(task.getHora()) + "</b><br>" + escape(shorten(task.getDescripcion(), 28)) + "</html>");
            label.setFont(ThemeManager.FONT_SMALL);
            label.setForeground(themeManager.text());
            taskList.add(label);
        }
        if (tasks.isEmpty()) {
            JLabel empty = new JLabel("Sin tareas", SwingConstants.CENTER);
            empty.setForeground(themeManager.mutedText());
            taskList.add(empty);
        }
        panel.add(taskList, BorderLayout.CENTER);
        return panel;
    }

    private Map<LocalDate, List<Tarea>> groupByDate(List<Tarea> tasks) {
        Map<LocalDate, List<Tarea>> result = new HashMap<>();
        for (Tarea task : tasks) {
            result.computeIfAbsent(task.getFecha(), ignored -> new ArrayList<>()).add(task);
        }
        return result;
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase(Locale.forLanguageTag("es-ES")) + text.substring(1);
    }

    private String shorten(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
