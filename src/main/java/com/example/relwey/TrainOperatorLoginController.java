package com.example.relwey;

import backend.*;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class TrainOperatorLoginController {

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
        TrainOperator to = trainOperatorService.login(email, password);

        if (to == null) {
            showError("Invalid username or password");
        }
        else{
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("trainOperatorDashboard.fxml"));
                Parent root = loader.load();
                TrainOperatorDashboardController todashboard = loader.getController();
                todashboard.setPassengerService(passengerService);
                todashboard.setTicketMasterService(ticketMasterService);
                todashboard.setTrainOperatorService(trainOperatorService);
                todashboard.setTicketService(ticketService);
                todashboard.setTrainService(trainService);
                todashboard.setTrainOperator(to);
                Stage stage = (Stage) usernameField.getScene().getWindow();
                stage.setScene(new Scene(root, 960, 540));
                System.out.println("successful login: " + email + " " + password);
            } catch (Exception e) {
                e.printStackTrace();
            }
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
            stage.setScene(new Scene(root, 960, 540));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
