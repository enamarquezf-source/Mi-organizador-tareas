package com.organizador.ui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.UIManager;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.prefs.Preferences;

public class ThemeManager {
    private static final String PREF_THEME = "theme";
    private static final Preferences PREFERENCES = Preferences.userNodeForPackage(ThemeManager.class);

    public static final Font FONT_BASE = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 24);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 18);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 12);

    private Theme theme;

    public ThemeManager() {
        String savedTheme = PREFERENCES.get(PREF_THEME, Theme.LIGHT.name());
        try {
            this.theme = Theme.valueOf(savedTheme);
        } catch (IllegalArgumentException ex) {
            this.theme = Theme.LIGHT;
        }
        applySwingDefaults();
    }

    public Theme getTheme() {
        return theme;
    }

    public boolean isDark() {
        return theme == Theme.DARK;
    }

    public void toggleTheme() {
        theme = isDark() ? Theme.LIGHT : Theme.DARK;
        PREFERENCES.put(PREF_THEME, theme.name());
        applySwingDefaults();
    }

    public Color background() {
        return isDark() ? new Color(18, 24, 38) : new Color(241, 245, 249);
    }

    public Color surface() {
        return isDark() ? new Color(30, 41, 59) : Color.WHITE;
    }

    public Color surfaceAlt() {
        return isDark() ? new Color(39, 51, 73) : new Color(248, 250, 252);
    }

    public Color sidebar() {
        return isDark() ? new Color(15, 23, 42) : new Color(30, 41, 59);
    }

    public Color sidebarText() {
        return new Color(226, 232, 240);
    }

    public Color sidebarMutedText() {
        return new Color(148, 163, 184);
    }

    public Color text() {
        return isDark() ? new Color(226, 232, 240) : new Color(15, 23, 42);
    }

    public Color mutedText() {
        return isDark() ? new Color(148, 163, 184) : new Color(100, 116, 139);
    }

    public Color border() {
        return isDark() ? new Color(51, 65, 85) : new Color(226, 232, 240);
    }

    public Color accent() {
        return isDark() ? new Color(129, 140, 248) : new Color(37, 99, 235);
    }

    public Color accentSoft() {
        return isDark() ? new Color(49, 46, 129) : new Color(219, 234, 254);
    }

    public Color danger() {
        return isDark() ? new Color(248, 113, 113) : new Color(220, 38, 38);
    }

    public Color weekend() {
        return isDark() ? new Color(42, 45, 68) : new Color(255, 247, 237);
    }

    public Color successSoft() {
        return isDark() ? new Color(20, 83, 45) : new Color(220, 252, 231);
    }

    public Color shadow() {
        return isDark() ? new Color(8, 13, 24, 120) : new Color(148, 163, 184, 80);
    }

    public Border roundedBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border()),
                BorderFactory.createEmptyBorder(12, 12, 12, 12)
        );
    }

    public void stylePrimaryButton(JButton button) {
        styleButton(button, accent(), Color.WHITE);
    }

    public void styleSecondaryButton(JButton button) {
        styleButton(button, surfaceAlt(), text());
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border()),
                BorderFactory.createEmptyBorder(9, 14, 9, 14)
        ));
    }

    public void styleDangerButton(JButton button) {
        styleButton(button, danger(), Color.WHITE);
    }

    public void styleGhostButton(JButton button) {
        styleButton(button, new Color(0, 0, 0, 0), text());
        button.setContentAreaFilled(false);
        button.setBorder(BorderFactory.createEmptyBorder(9, 12, 9, 12));
    }

    public void styleIconButton(JButton button) {
        styleButton(button, surfaceAlt(), text());
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border()),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    public void styleInput(JComponent component) {
        component.setFont(FONT_BASE);
        component.setForeground(text());
        component.setBackground(surfaceAlt());
        component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(border()),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
    }

    private void styleButton(JButton button, Color background, Color foreground) {
        button.setFont(FONT_BASE.deriveFont(Font.BOLD));
        button.setForeground(foreground);
        button.setBackground(background);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
    }

    private void applySwingDefaults() {
        UIManager.put("Panel.background", background());
        UIManager.put("Label.foreground", text());
        UIManager.put("List.background", surface());
        UIManager.put("List.foreground", text());
        UIManager.put("TextField.background", surfaceAlt());
        UIManager.put("TextField.foreground", text());
        UIManager.put("TextArea.background", surfaceAlt());
        UIManager.put("TextArea.foreground", text());
        UIManager.put("OptionPane.background", surface());
        UIManager.put("OptionPane.messageForeground", text());
        UIManager.put("Button.font", FONT_BASE);
        UIManager.put("Label.font", FONT_BASE);
        UIManager.put("List.font", FONT_BASE);
    }

    public enum Theme {
        LIGHT,
        DARK
    }
}
