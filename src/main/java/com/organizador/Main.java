package com.organizador;

import com.organizador.dao.TareaDAO;
import com.organizador.database.DatabaseManager;
import com.organizador.service.TareaService;
import com.organizador.ui.MainFrame;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                DatabaseManager databaseManager = new DatabaseManager();
                databaseManager.initializeDatabase();

                TareaDAO tareaDAO = new TareaDAO(databaseManager);
                TareaService tareaService = new TareaService(tareaDAO);
                MainFrame mainFrame = new MainFrame(tareaService);
                mainFrame.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(
                        null,
                        "No se pudo iniciar la aplicación: " + ex.getMessage(),
                        "Mi Organizador de Tareas",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        });
    }
}
