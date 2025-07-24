package com.example.relwey;

import backend.PassengerService;
import backend.Passenger;
import com.sun.tools.javac.Main;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import backend.PassengerService;
import backend.Passenger;
import backend.PassengerFileHandler;
import backend.*;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

import java.io.IOException;


public class PassengerLoginController {

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
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Login Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Button login;

    @FXML
    private void handleLogin() throws IOException {
        String email = emailField.getText();
        String password = passwordField.getText();
        Passenger p = passengerService.login(email, password);

        if (p == null) {
            showError("Invalid username or password");
        }
        else{
            System.out.println("successful login: " + email + " " + password);


            FXMLLoader loader = new FXMLLoader(getClass().getResource("PassengerDashboard.fxml"));
            Parent root = loader.load();

            PassengerDashboardController pdController = loader.getController();
            pdController.setPassengerService(passengerService);
            pdController.setTicketService(ticketService);
            pdController.setTrainOperatorService(trainOperatorService);
            pdController.setTrainService(trainService);
            pdController.setTicketMasterService(ticketMasterService);
            pdController.setP(p);


            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));

        }
    }

    @FXML
    private void handleSignUp() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("passenger_signup.fxml"));
            Parent root = loader.load();

            // Pass the passengerService to the signup controller
            PassengerSignupController controller = loader.getController();
            controller.setPassengerService(passengerService);
            controller.setTicketService(ticketService);
            controller.setTrainOperatorService(trainOperatorService);
            controller.setTrainService(trainService);
            controller.setTicketMasterService(ticketMasterService);

            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));

        } catch (IOException e) {
            e.printStackTrace();
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
            Stage stage = (Stage) emailField.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
