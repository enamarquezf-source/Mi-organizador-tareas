package com.organizador.ui;

import com.organizador.model.Tarea;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class DayViewPanel extends JPanel {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", Locale.forLanguageTag("es-ES"));
    private static final DateTimeFormatter HOUR_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    private final ThemeManager themeManager;

    public DayViewPanel(ThemeManager themeManager) {
        this.themeManager = themeManager;
        setLayout(new BorderLayout(0, 12));
        setOpaque(false);
    }

    public void showDay(LocalDate date, List<Tarea> tasks) {
        removeAll();
        JLabel title = new JLabel(capitalize(date.format(DATE_FORMATTER)), SwingConstants.LEFT);
        title.setFont(ThemeManager.FONT_SUBTITLE);
        title.setForeground(themeManager.text());
        add(title, BorderLayout.NORTH);

        JPanel list = new JPanel(new GridLayout(0, 1, 0, 10));
        list.setOpaque(false);
        if (tasks.isEmpty()) {
            JLabel empty = new JLabel("No hay tareas para este día.");
            empty.setForeground(themeManager.mutedText());
            list.add(empty);
        }
        for (Tarea task : tasks) {
            RoundedPanel card = new RoundedPanel(20);
            card.setLayout(new BorderLayout(12, 0));
            card.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));
            card.setBackground(themeManager.surfaceAlt());

            JLabel hour = new JLabel(HOUR_FORMATTER.format(task.getHora()));
            hour.setFont(ThemeManager.FONT_BASE.deriveFont(java.awt.Font.BOLD));
            hour.setForeground(themeManager.accent());
            JLabel description = new JLabel("<html>" + escape(task.getDescripcion()) + "</html>");
            description.setForeground(themeManager.text());
            card.add(hour, BorderLayout.WEST);
            card.add(description, BorderLayout.CENTER);
            list.add(card);
        }

        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    public void applyTheme() {
        repaint();
    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase(Locale.forLanguageTag("es-ES")) + text.substring(1);
    }

    private String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
