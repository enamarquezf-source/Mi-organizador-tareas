package com.organizador.ui;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SidebarPanel extends RoundedPanel {
    private final ThemeManager themeManager;
    private final Map<String, ModernButton> options = new LinkedHashMap<>();
    private String activeOption = "Calendario";

    public SidebarPanel(ThemeManager themeManager, Consumer<String> onOptionSelected) {
        super(24);
        this.themeManager = themeManager;
        setPreferredSize(new Dimension(198, 0));
        setLayout(new BorderLayout(0, 20));
        setBorder(BorderFactory.createEmptyBorder(20, 14, 20, 14));

        JLabel title = new JLabel("Menú", SwingConstants.LEFT);
        title.setFont(ThemeManager.FONT_SUBTITLE);

        JPanel menu = new JPanel(new GridLayout(4, 1, 0, 10));
        menu.setOpaque(false);
        addOption(menu, "Calendario", onOptionSelected);
        addOption(menu, "Hoy", onOptionSelected);
        addOption(menu, "Tareas", onOptionSelected);
        addOption(menu, "Ajustes", onOptionSelected);

        add(title, BorderLayout.NORTH);
        add(menu, BorderLayout.CENTER);
        applyTheme();
    }

    public void setActiveOption(String activeOption) {
        this.activeOption = activeOption;
        for (Map.Entry<String, ModernButton> entry : options.entrySet()) {
            entry.getValue().setActive(entry.getKey().equals(activeOption));
            entry.getValue().applyTheme(themeManager);
        }
    }

    public void applyTheme() {
        setBackground(themeManager.sidebar());
        for (ModernButton button : options.values()) {
            button.applyTheme(themeManager);
        }
        for (java.awt.Component child : getComponents()) {
            child.setForeground(themeManager.sidebarText());
        }
        setActiveOption(activeOption);
    }

    private void addOption(JPanel menu, String text, Consumer<String> onOptionSelected) {
        ModernButton button = new ModernButton(text, ModernButton.Style.SIDEBAR, themeManager);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.addActionListener(event -> onOptionSelected.accept(text));
        options.put(text, button);
        menu.add(button);
    }
}
