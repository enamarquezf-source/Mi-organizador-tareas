package com.organizador.ui;

import com.organizador.model.Tarea;

import javax.swing.JPanel;
import java.awt.CardLayout;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.function.Consumer;

public class CalendarPanel extends JPanel {
    private final CardLayout cardLayout = new CardLayout();
    private final MonthViewPanel monthViewPanel;
    private final WeekViewPanel weekViewPanel;
    private final DayViewPanel dayViewPanel;

    private ViewMode viewMode = ViewMode.MONTH;

    public CalendarPanel(ThemeManager themeManager) {
        setLayout(cardLayout);
        setOpaque(false);
        monthViewPanel = new MonthViewPanel(themeManager);
        weekViewPanel = new WeekViewPanel(themeManager);
        dayViewPanel = new DayViewPanel(themeManager);
        add(monthViewPanel, ViewMode.MONTH.name());
        add(weekViewPanel, ViewMode.WEEK.name());
        add(dayViewPanel, ViewMode.DAY.name());
    }

    public void setOnFechaSeleccionada(Consumer<LocalDate> onDateSelected) {
        monthViewPanel.setOnDateSelected(onDateSelected);
        weekViewPanel.setOnDateSelected(onDateSelected);
    }

    public void setOnFechaDobleClick(Consumer<LocalDate> onDateDoubleClicked) {
        monthViewPanel.setOnDateDoubleClicked(onDateDoubleClicked);
    }

    public void setViewMode(ViewMode viewMode) {
        this.viewMode = viewMode;
        cardLayout.show(this, viewMode.name());
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void mostrar(YearMonth visibleMonth, LocalDate selectedDate, List<Tarea> monthTasks, List<Tarea> visibleRangeTasks, List<Tarea> dayTasks) {
        monthViewPanel.showMonth(visibleMonth, selectedDate, monthTasks);
        weekViewPanel.showWeek(selectedDate, visibleRangeTasks);
        dayViewPanel.showDay(selectedDate, dayTasks);
        cardLayout.show(this, viewMode.name());
    }

    public void applyTheme() {
        monthViewPanel.applyTheme();
        weekViewPanel.applyTheme();
        dayViewPanel.applyTheme();
        repaint();
    }

    public enum ViewMode {
        MONTH,
        WEEK,
        DAY
    }
}
