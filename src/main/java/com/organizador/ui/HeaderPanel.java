package com.organizador.ui;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Consumer;

public class HeaderPanel extends JPanel {
    private final ThemeManager themeManager;
    private final JLabel monthLabel = new JLabel();
    private final ModernButton previousButton;
    private final ModernButton nextButton;
    private final ModernButton themeButton;
    private final ModernButton addButton;
    private final Map<CalendarPanel.ViewMode, ModernButton> viewButtons = new EnumMap<>(CalendarPanel.ViewMode.class);

    public HeaderPanel(ThemeManager themeManager,
                       Runnable onPrevious,
                       Runnable onNext,
                       Consumer<CalendarPanel.ViewMode> onViewChanged,
                       Runnable onToggleTheme,
                       Runnable onAddTask) {
        this.themeManager = themeManager;
        setLayout(new BorderLayout(18, 0));
        setOpaque(false);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 0));

        JLabel titleLabel = new JLabel("Mi Organizador de Tareas");
        titleLabel.setFont(ThemeManager.FONT_TITLE);

        previousButton = new ModernButton("‹", ModernButton.Style.ICON, themeManager);
        nextButton = new ModernButton("›", ModernButton.Style.ICON, themeManager);
        previousButton.addActionListener(event -> onPrevious.run());
        nextButton.addActionListener(event -> onNext.run());

        monthLabel.setFont(ThemeManager.FONT_SUBTITLE);
        JPanel navigation = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        navigation.setOpaque(false);
        navigation.add(previousButton);
        navigation.add(monthLabel);
        navigation.add(nextButton);

        JPanel views = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        views.setOpaque(false);
        ButtonGroup group = new ButtonGroup();
        addViewButton(views, group, CalendarPanel.ViewMode.MONTH, "Mes", onViewChanged);
        addViewButton(views, group, CalendarPanel.ViewMode.WEEK, "Semana", onViewChanged);
        addViewButton(views, group, CalendarPanel.ViewMode.DAY, "Día", onViewChanged);

        themeButton = new ModernButton("☾", ModernButton.Style.ICON, themeManager);
        themeButton.addActionListener(event -> onToggleTheme.run());
        addButton = new ModernButton("+ Añadir tarea", ModernButton.Style.PRIMARY, themeManager);
        addButton.addActionListener(event -> onAddTask.run());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);
        actions.add(views);
        actions.add(themeButton);
        actions.add(addButton);

        add(titleLabel, BorderLayout.WEST);
        add(navigation, BorderLayout.CENTER);
        add(actions, BorderLayout.EAST);
        applyTheme();
    }

    public void setMonthText(String monthText) {
        monthLabel.setText(monthText);
    }

    public void setViewMode(CalendarPanel.ViewMode mode) {
        for (Map.Entry<CalendarPanel.ViewMode, ModernButton> entry : viewButtons.entrySet()) {
            entry.getValue().setActive(entry.getKey() == mode);
        }
    }

    public void applyTheme() {
        setBackground(themeManager.background());
        applyThemeRecursive(this);
        themeButton.setText(themeManager.isDark() ? "☀" : "☾");
    }

    private void addViewButton(JPanel views, ButtonGroup group, CalendarPanel.ViewMode mode, String text,
                               Consumer<CalendarPanel.ViewMode> onViewChanged) {
        ModernButton button = new ModernButton(text, ModernButton.Style.SECONDARY, themeManager);
        button.addActionListener(event -> onViewChanged.accept(mode));
        group.add(button);
        views.add(button);
        viewButtons.put(mode, button);
    }

    private void applyThemeRecursive(java.awt.Component component) {
        component.setForeground(themeManager.text());
        if (component instanceof ModernButton button) {
            button.applyTheme(themeManager);
        }
        if (component instanceof java.awt.Container container) {
            for (java.awt.Component child : container.getComponents()) {
                applyThemeRecursive(child);
            }
        }
    }
}
