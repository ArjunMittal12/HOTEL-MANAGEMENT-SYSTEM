package com.shreyas.miniproject.util;

import java.net.URL;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Dialog;

/**
 * Utility class for reusable Alert dialogs.
 */
public class AlertHelper {

    private static final String DARK_THEME_STYLESHEET = "/styles/dark-theme.css";

    public static void showError(String title, String message) {
        showAlert(AlertType.ERROR, title, message);
    }

    public static void showInfo(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    public static void showWarning(String title, String message) {
        showAlert(AlertType.WARNING, title, message);
    }

    public static void showSuccess(String title, String message) {
        showAlert(AlertType.INFORMATION, title, message);
    }

    private static void showAlert(AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        applyTheme(alert);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void applyTheme(Dialog<?> dialog) {
        if (dialog == null) {
            return;
        }
        URL darkTheme = AlertHelper.class.getResource(DARK_THEME_STYLESHEET);
        if (darkTheme != null) {
            dialog.getDialogPane().getStylesheets().add(darkTheme.toExternalForm());
        }
        dialog.getDialogPane().getStyleClass().add("dark-dialog");
    }
}

