package com.organizador.ui;

import javax.swing.JButton;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

public class ModernButton extends JButton {
    private final Style style;
    private boolean active;
    private ThemeManager themeManager;

    public ModernButton(String text, Style style, ThemeManager themeManager) {
        super(text);
        this.style = style;
        this.themeManager = themeManager;
        setContentAreaFilled(false);
        setOpaque(false);
        setFocusPainted(false);
        setBorderPainted(false);
        applyTheme(themeManager);
    }

    public void setActive(boolean active) {
        this.active = active;
        setForeground(foregroundColor());
        repaint();
    }

    public void applyTheme(ThemeManager themeManager) {
        this.themeManager = themeManager;
        setFont(ThemeManager.FONT_BASE.deriveFont(java.awt.Font.BOLD, style == Style.THEME ? 20f : style == Style.SIDEBAR ? 14f : 13f));
        setForeground(foregroundColor());
        setBorder(javax.swing.BorderFactory.createEmptyBorder(9, style == Style.THEME ? 10 : 14, 9, style == Style.THEME ? 10 : 14));
        if (style == Style.THEME) {
            setPreferredSize(new Dimension(42, 38));
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor());
        if (style == Style.THEME) {
            g2.fillOval(2, 2, getWidth() - 4, getHeight() - 4);
        } else {
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
        }
        g2.dispose();
        super.paintComponent(graphics);
    }

    private Color backgroundColor() {
        if (style == Style.PRIMARY) {
            return themeManager.accent();
        }
        if (style == Style.DANGER) {
            return themeManager.danger();
        }
        if (style == Style.SIDEBAR) {
            return active ? themeManager.accentSoft() : new Color(0, 0, 0, 0);
        }
        if (style == Style.GHOST) {
            return new Color(0, 0, 0, 0);
        }
        if (style == Style.THEME) {
            return themeManager.isDark() ? new Color(254, 240, 138) : new Color(30, 41, 59);
        }
        if (active) {
            return themeManager.accentSoft();
        }
        return themeManager.surfaceAlt();
    }

    private Color foregroundColor() {
        if (style == Style.PRIMARY || style == Style.DANGER) {
            return Color.WHITE;
        }
        if (style == Style.SIDEBAR) {
            return active ? themeManager.text() : themeManager.sidebarMutedText();
        }
        if (style == Style.THEME) {
            return themeManager.isDark() ? new Color(30, 41, 59) : Color.WHITE;
        }
        if (active) {
            return themeManager.text();
        }
        return themeManager.text();
    }

    public enum Style {
        PRIMARY,
        SECONDARY,
        GHOST,
        DANGER,
        SIDEBAR,
        ICON,
        THEME
    }
}
