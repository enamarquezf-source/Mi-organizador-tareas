package com.organizador.ui;

import com.organizador.model.Tarea;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class MonthViewPanel extends JPanel {
    private static final DateTimeFormatter HORA_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ThemeManager themeManager;
    private Consumer<LocalDate> onDateSelected;
    private LocalDate selectedDate;

    public MonthViewPanel(ThemeManager themeManager) {
        this.themeManager = themeManager;
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
    }

    public void setOnDateSelected(Consumer<LocalDate> onDateSelected) {
        this.onDateSelected = onDateSelected;
    }

    public void showMonth(YearMonth month, LocalDate selectedDate, List<Tarea> tasks) {
        this.selectedDate = selectedDate;
        removeAll();
        add(createWeekHeader(), BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 7, 8, 8));
        grid.setOpaque(false);
        Map<LocalDate, List<Tarea>> groupedTasks = groupByDate(tasks);
        int leadingSpaces = month.atDay(1).getDayOfWeek().getValue() - 1;

        for (int i = 0; i < leadingSpaces; i++) {
            JPanel empty = new JPanel();
            empty.setOpaque(false);
            grid.add(empty);
        }

        for (int day = 1; day <= month.lengthOfMonth(); day++) {
            LocalDate date = month.atDay(day);
            grid.add(new DayCard(date, groupedTasks.getOrDefault(date, List.of())));
        }

        add(grid, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void applyTheme() {
        repaint();
    }

    private JPanel createWeekHeader() {
        JPanel header = new JPanel(new GridLayout(1, 7, 8, 8));
        header.setOpaque(false);
        for (String day : List.of("Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom")) {
            JLabel label = new JLabel(day, SwingConstants.CENTER);
            label.setFont(ThemeManager.FONT_BASE.deriveFont(java.awt.Font.BOLD));
            label.setForeground(themeManager.mutedText());
            header.add(label);
        }
        return header;
    }

    private Map<LocalDate, List<Tarea>> groupByDate(List<Tarea> tasks) {
        Map<LocalDate, List<Tarea>> result = new HashMap<>();
        for (Tarea task : tasks) {
            result.computeIfAbsent(task.getFecha(), ignored -> new ArrayList<>()).add(task);
        }
        return result;
    }

    private class DayCard extends RoundedPanel {
        private final LocalDate date;
        private final List<Tarea> tasks;

        private DayCard(LocalDate date, List<Tarea> tasks) {
            super(20);
            this.date = date;
            this.tasks = tasks;
            setLayout(new BorderLayout(0, 6));
            setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            setPreferredSize(new Dimension(105, 96));
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent event) {
                    if (onDateSelected != null) {
                        onDateSelected.accept(date);
                    }
                }
            });
            buildContent();
        }

        @Override
        protected void paintComponent(Graphics graphics) {
            setBackground(backgroundColor());
            super.paintComponent(graphics);
            Graphics2D g2 = (Graphics2D) graphics.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(borderColor());
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
            g2.dispose();
        }

        private void buildContent() {
            JLabel dayLabel = new JLabel(String.valueOf(date.getDayOfMonth()));
            dayLabel.setFont(ThemeManager.FONT_BASE.deriveFont(java.awt.Font.BOLD));
            dayLabel.setForeground(themeManager.text());
            add(dayLabel, BorderLayout.NORTH);

            JPanel taskList = new JPanel(new GridLayout(0, 1, 0, 2));
            taskList.setOpaque(false);
            for (Tarea task : tasks.stream().limit(3).toList()) {
                JLabel label = new JLabel(HORA_FORMATTER.format(task.getHora()) + " " + shorten(task.getDescripcion(), 16));
                label.setFont(ThemeManager.FONT_SMALL);
                label.setForeground(themeManager.mutedText());
                taskList.add(label);
            }
            if (tasks.size() > 3) {
                JLabel more = new JLabel("+" + (tasks.size() - 3) + " más");
                more.setFont(ThemeManager.FONT_SMALL);
                more.setForeground(themeManager.accent());
                taskList.add(more);
            }
            add(taskList, BorderLayout.CENTER);
        }

        private Color backgroundColor() {
            if (date.equals(selectedDate)) {
                return themeManager.accentSoft();
            }
            if (date.getDayOfWeek().getValue() >= 6) {
                return themeManager.weekend();
            }
            return themeManager.surfaceAlt();
        }

        private Color borderColor() {
            if (date.equals(selectedDate) || !tasks.isEmpty()) {
                return themeManager.accent();
            }
            return themeManager.border();
        }
    }

    private String shorten(String text, int maxLength) {
        return text.length() <= maxLength ? text : text.substring(0, maxLength - 3) + "...";
    }
}
