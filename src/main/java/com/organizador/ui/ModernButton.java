package com.organizador.ui;

import javax.swing.JButton;
import java.awt.Color;
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
        setFont(ThemeManager.FONT_BASE.deriveFont(java.awt.Font.BOLD, style == Style.SIDEBAR ? 14f : 13f));
        setForeground(foregroundColor());
        setBorder(javax.swing.BorderFactory.createEmptyBorder(9, 14, 9, 14));
        repaint();
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        Graphics2D g2 = (Graphics2D) graphics.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(backgroundColor());
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 18, 18);
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
            return active ? new Color(255, 255, 255, 34) : new Color(0, 0, 0, 0);
        }
        if (style == Style.GHOST) {
            return new Color(0, 0, 0, 0);
        }
        return themeManager.surfaceAlt();
    }

    private Color foregroundColor() {
        if (style == Style.PRIMARY || style == Style.DANGER) {
            return Color.WHITE;
        }
        if (style == Style.SIDEBAR) {
            return active ? Color.WHITE : themeManager.sidebarMutedText();
        }
        return themeManager.text();
    }

    public enum Style {
        PRIMARY,
        SECONDARY,
        GHOST,
        DANGER,
        SIDEBAR,
        ICON
    }
}
