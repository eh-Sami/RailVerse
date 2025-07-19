package com.example.relwey;

import backend.*;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TicketMasterLoginController {

    public PassengerService passengerService;
    public TicketService ticketService;
    public TrainService trainService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;

    public TicketService getTicketService() {
        return ticketService;
    }
    public TrainService getTrainService() {
        return trainService;
    }
    public TicketMasterService getTicketMasterService() {
        return ticketMasterService;
    }
    public TrainOperatorService getTrainOperatorService() {
        return trainOperatorService;
    }
    public PassengerService getPassengerService() {
        return passengerService;
    }
    public void setTicketMasterService(TicketMasterService ticketMasterService) {
        this.ticketMasterService = ticketMasterService;
    }
    public void setTrainOperatorService(TrainOperatorService trainOperatorService) {
        this.trainOperatorService = trainOperatorService;
    }
    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }
    public void setPassengerService(PassengerService passengerService) {
        this.passengerService = passengerService;
    }
    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleLogin() {
        String email = usernameField.getText();
        String password = passwordField.getText();
        TicketMaster tm = ticketMasterService.login(email, password);

        if (tm == null) {
            showError("Invalid username or password");
        }
        else{
            System.out.println("successful login: " + email + " " + password);
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("role_selection.fxml"));
            Parent root = loader.load();
            RoleSelectionController roleController = loader.getController();
            roleController.setPassengerService(passengerService);
            roleController.setTicketMasterService(ticketMasterService);
            roleController.setTrainOperatorService(trainOperatorService);
            roleController.setTicketService(ticketService);
            roleController.setTrainService(trainService);
            Stage stage = (Stage) usernameField.getScene().getWindow();
            stage.setScene(new Scene(root, 600, 500));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
