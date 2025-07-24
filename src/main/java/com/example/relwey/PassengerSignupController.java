package com.example.relwey;

import backend.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;

public class PassengerSignupController {

    public PassengerService passengerService;
    public TicketService ticketService;
    public TrainService trainService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;

    public void setPassengerService(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void setTrainOperatorService(TrainOperatorService trainOperatorService) {
        this.trainOperatorService = trainOperatorService;
    }

    public void setTicketMasterService(TicketMasterService ticketMasterService) {
        this.ticketMasterService = ticketMasterService;
    }

    @FXML private TextField nameField;
    @FXML private TextField nidField;
    @FXML private TextField emailField;
    @FXML private TextField addressField;
    @FXML private PasswordField passwordField;

    @FXML
    private void handleSignUp() {
        String name = nameField.getText().trim();
        String nid = nidField.getText().trim();
        String email = emailField.getText().trim();
        String address = addressField.getText().trim();
        String password = passwordField.getText().trim();
        Passenger p;
        p = passengerService.signUp(name, nid, email, address, password);
        if(p==null){
            showError("Unable to sign up");
        }
        else{
            showInfo("Successfully signed up");
            handleBack();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("passenger_login.fxml"));
            Parent root = loader.load();
            PassengerLoginController controller = loader.getController();
            controller.setPassengerService(passengerService);
            controller.setTicketService(ticketService);
            controller.setTrainOperatorService(trainOperatorService);
            controller.setTrainService(trainService);
            controller.setTicketMasterService(ticketMasterService);
            Stage stage = (Stage) nameField.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        showAlert(Alert.AlertType.ERROR, "Error", message);
    }

    private void showInfo(String message) {
        showAlert(Alert.AlertType.INFORMATION, "Success", message);
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
