package com.organizador.ui;

import com.organizador.model.Tarea;
import com.organizador.service.TareaService;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainFrame extends JFrame {
    private static final Locale SPANISH = Locale.forLanguageTag("es-ES");

    private final TareaService tareaService;
    private final ThemeManager themeManager = new ThemeManager();
    private final CalendarPanel calendarPanel = new CalendarPanel(themeManager);
    private final HeaderPanel headerPanel;
    private final SidebarPanel sidebarPanel;
    private final TaskPanel taskPanel;
    private final JPanel root = new JPanel(new BorderLayout(16, 16));
    private final RoundedPanel centerPanel = new RoundedPanel(26);
    private final JPanel centerCards = new JPanel(new CardLayout());
    private final TasksOverviewPanel tasksOverviewPanel = new TasksOverviewPanel(themeManager);

    private YearMonth visibleMonth = YearMonth.now();
    private LocalDate selectedDate = LocalDate.now();
    private CalendarPanel.ViewMode viewMode = CalendarPanel.ViewMode.MONTH;
    private Section activeSection = Section.CALENDAR;
    private LocalDate lastReminderDate;

    public MainFrame(TareaService tareaService) {
        this.tareaService = tareaService;
        headerPanel = new HeaderPanel(
                themeManager,
                () -> changePeriod(-1),
                () -> changePeriod(1),
                this::changeView,
                this::setTheme,
                this::openNewTaskDialog
        );
        sidebarPanel = new SidebarPanel(themeManager, this::handleSidebarOption);
        taskPanel = new TaskPanel(themeManager, this::editSelectedTask, this::deleteSelectedTask);

        configureWindow();
        buildInterface();
        reloadAll();
        showTodayReminderIfNeeded();
    }

    private void configureWindow() {
        setTitle("Mi Organizador de Tareas");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1280, 780));
        setLocationRelativeTo(null);

        URL iconUrl = getClass().getResource("/icon.png");
        if (iconUrl != null) {
            setIconImage(new ImageIcon(iconUrl).getImage());
        }
    }

    private void buildInterface() {
        root.setBorder(BorderFactory.createEmptyBorder(18, 18, 18, 18));
        centerPanel.setLayout(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        centerCards.setOpaque(false);
        centerCards.add(calendarPanel, Section.CALENDAR.name());
        centerCards.add(tasksOverviewPanel, Section.TASKS.name());
        centerPanel.add(centerCards, BorderLayout.CENTER);

        calendarPanel.setOnFechaSeleccionada(date -> {
            selectedDate = date;
            visibleMonth = YearMonth.from(date);
            activeSection = Section.CALENDAR;
            sidebarPanel.setActiveOption("Calendario");
            reloadAll();
        });
        calendarPanel.setOnFechaDobleClick(date -> {
            selectedDate = date;
            visibleMonth = YearMonth.from(date);
            activeSection = Section.CALENDAR;
            sidebarPanel.setActiveOption("Calendario");
            taskPanel.setCollapsed(false);
            reloadAll();
        });

        root.add(headerPanel, BorderLayout.NORTH);
        root.add(sidebarPanel, BorderLayout.WEST);
        root.add(centerPanel, BorderLayout.CENTER);
        root.add(taskPanel, BorderLayout.EAST);
        setContentPane(root);
        applyTheme();
    }

    private void handleSidebarOption(String option) {
        sidebarPanel.setActiveOption(option);
        if ("Hoy".equals(option)) {
            activeSection = Section.CALENDAR;
            selectedDate = LocalDate.now();
            visibleMonth = YearMonth.from(selectedDate);
            viewMode = CalendarPanel.ViewMode.DAY;
        }
        if ("Tareas".equals(option)) {
            activeSection = Section.TASKS;
            taskPanel.setCollapsed(false);
        }
        if ("Calendario".equals(option)) {
            activeSection = Section.CALENDAR;
            viewMode = CalendarPanel.ViewMode.MONTH;
            visibleMonth = YearMonth.from(selectedDate);
        }
        reloadAll();
    }

    private void changeView(CalendarPanel.ViewMode mode) {
        activeSection = Section.CALENDAR;
        sidebarPanel.setActiveOption("Calendario");
        viewMode = mode;
        calendarPanel.setViewMode(mode);
        headerPanel.setViewMode(mode);
        reloadAll();
    }

    private void changePeriod(int amount) {
        if (viewMode == CalendarPanel.ViewMode.MONTH) {
            visibleMonth = visibleMonth.plusMonths(amount);
            selectedDate = visibleMonth.atDay(Math.min(selectedDate.getDayOfMonth(), visibleMonth.lengthOfMonth()));
        } else if (viewMode == CalendarPanel.ViewMode.WEEK) {
            selectedDate = selectedDate.plusWeeks(amount);
            visibleMonth = YearMonth.from(selectedDate);
        } else {
            selectedDate = selectedDate.plusDays(amount);
            visibleMonth = YearMonth.from(selectedDate);
        }
        reloadAll();
    }

    private void setTheme(ThemeManager.Theme theme) {
        themeManager.setTheme(theme);
        applyTheme();
        reloadAll();
    }

    private void applyTheme() {
        root.setBackground(themeManager.background());
        centerPanel.setBackground(themeManager.surface());
        headerPanel.applyTheme();
        sidebarPanel.applyTheme();
        taskPanel.applyTheme();
        calendarPanel.applyTheme();
        tasksOverviewPanel.applyTheme();
        applyTextTheme(root);
        repaint();
    }

    private void reloadAll() {
        try {
            headerPanel.setMonthText(formatMonth(visibleMonth));
            headerPanel.setViewMode(viewMode);
            List<Tarea> monthTasks = tareaService.listarPorMes(visibleMonth.getYear(), visibleMonth.getMonthValue());
            List<Tarea> visibleRangeTasks = loadVisibleRangeTasks();
            List<Tarea> dayTasks = tareaService.listarPorFecha(selectedDate);
            calendarPanel.setViewMode(viewMode);
            calendarPanel.mostrar(visibleMonth, selectedDate, monthTasks, visibleRangeTasks, dayTasks);
            tasksOverviewPanel.showPendingTasks(loadPendingTasks());
            taskPanel.setTasks(selectedDate, dayTasks);
            showActiveSection();
        } catch (SQLException ex) {
            showError("No se pudieron cargar las tareas. Inténtelo de nuevo.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        }
    }

    private List<Tarea> loadVisibleRangeTasks() throws SQLException {
        if (viewMode == CalendarPanel.ViewMode.DAY) {
            return tareaService.listarPorFecha(selectedDate);
        }
        LocalDate start = selectedDate.minusDays(selectedDate.getDayOfWeek().getValue() - 1L);
        LocalDate end = start.plusDays(6);
        List<Tarea> result = new ArrayList<>();
        YearMonth current = YearMonth.from(start);
        YearMonth endMonth = YearMonth.from(end);
        while (!current.isAfter(endMonth)) {
            for (Tarea task : tareaService.listarPorMes(current.getYear(), current.getMonthValue())) {
                if (!task.getFecha().isBefore(start) && !task.getFecha().isAfter(end)) {
                    result.add(task);
                }
            }
            current = current.plusMonths(1);
        }
        return result;
    }

    private List<Tarea> loadPendingTasks() throws SQLException {
        LocalDate today = LocalDate.now();
        YearMonth current = YearMonth.from(today);
        YearMonth limit = current.plusMonths(12);
        List<Tarea> result = new ArrayList<>();
        while (!current.isAfter(limit)) {
            for (Tarea task : tareaService.listarPorMes(current.getYear(), current.getMonthValue())) {
                if (!task.getFecha().isBefore(today)) {
                    result.add(task);
                }
            }
            current = current.plusMonths(1);
        }
        return result;
    }

    private void showActiveSection() {
        CardLayout layout = (CardLayout) centerCards.getLayout();
        layout.show(centerCards, activeSection.name());
    }

    private void openNewTaskDialog() {
        TareaDialog dialog = new TareaDialog(this, selectedDate, null, themeManager);
        dialog.setVisible(true);
        if (!dialog.isGuardado()) {
            return;
        }

        try {
            Tarea task = dialog.getTarea();
            tareaService.crearTarea(task.getFecha(), task.getHora(), task.getDescripcion());
            selectedDate = task.getFecha();
            visibleMonth = YearMonth.from(selectedDate);
            reloadAll();
            if (task.getFecha().equals(LocalDate.now())) {
                showTaskReminder(List.of(task));
            }
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            showError("No se pudo guardar la tarea. Inténtelo de nuevo.");
        }
    }

    private void showTodayReminderIfNeeded() {
        LocalDate today = LocalDate.now();
        if (today.equals(lastReminderDate)) {
            return;
        }
        try {
            List<Tarea> todayTasks = tareaService.listarPorFecha(today);
            if (!todayTasks.isEmpty()) {
                lastReminderDate = today;
                showTaskReminder(todayTasks);
            }
        } catch (SQLException ex) {
            showError("No se pudieron comprobar los avisos de hoy.");
        }
    }

    private void showTaskReminder(List<Tarea> tasks) {
        StringBuilder message = new StringBuilder("Tienes tareas para hoy:\n\n");
        for (Tarea task : tasks) {
            message.append(task.getHora()).append(" - ").append(task.getDescripcion()).append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Aviso de tareas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void editSelectedTask() {
        Tarea selected = taskPanel.getSelectedTask();
        if (selected == null) {
            showInfo("Seleccione una tarea para editar.");
            return;
        }

        TareaDialog dialog = new TareaDialog(this, selected.getFecha(), selected, themeManager);
        dialog.setVisible(true);
        if (!dialog.isGuardado()) {
            return;
        }

        try {
            Tarea updated = dialog.getTarea();
            tareaService.actualizarTarea(updated);
            selectedDate = updated.getFecha();
            visibleMonth = YearMonth.from(selectedDate);
            reloadAll();
            showInfo("Tarea actualizada correctamente.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            showError("No se pudo actualizar la tarea. Inténtelo de nuevo.");
        }
    }

    private void deleteSelectedTask() {
        Tarea selected = taskPanel.getSelectedTask();
        if (selected == null) {
            showInfo("Seleccione una tarea para eliminar.");
            return;
        }

        int response = JOptionPane.showConfirmDialog(
                this,
                "¿Desea eliminar la tarea seleccionada?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );
        if (response != JOptionPane.YES_OPTION) {
            return;
        }

        try {
            tareaService.eliminarTarea(selected);
            reloadAll();
            showInfo("Tarea eliminada correctamente.");
        } catch (IllegalArgumentException ex) {
            showError(ex.getMessage());
        } catch (SQLException ex) {
            showError("No se pudo eliminar la tarea. Inténtelo de nuevo.");
        }
    }

    private void applyTextTheme(Component component) {
        if (component instanceof ModernButton) {
            return;
        }
        component.setForeground(themeManager.text());
        if (component instanceof java.awt.Container container) {
            for (Component child : container.getComponents()) {
                applyTextTheme(child);
            }
        }
    }

    private String formatMonth(YearMonth month) {
        String monthName = month.getMonth().getDisplayName(TextStyle.FULL, SPANISH);
        return monthName.substring(0, 1).toUpperCase(SPANISH) + monthName.substring(1) + " " + month.getYear();
    }

    private void showInfo(String message) {
        JOptionPane.showMessageDialog(this, message, "Mi Organizador de Tareas", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Mi Organizador de Tareas", JOptionPane.ERROR_MESSAGE);
    }

    private enum Section {
        CALENDAR,
        TASKS
    }
}
