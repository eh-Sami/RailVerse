package com.example.relwey;


import backend.*;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class EditProfileController {

    public PassengerService passengerService;
    public TrainService trainService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;
    public TicketService ticketService;
    public Passenger passenger;


    public void setPassengerService(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    public void setTicketMasterService(TicketMasterService ticketMasterService) {
        this.ticketMasterService = ticketMasterService;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    public void setTrainOperatorService(TrainOperatorService trainOperatorService) {
        this.trainOperatorService = trainOperatorService;
    }

    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }

    public void setPassenger(Passenger passenger) {
        this.passenger = passenger;
    }

    @FXML private VBox emailForm;
    @FXML private TextField emailField;
    @FXML private PasswordField emailPasswordField;

    @FXML private VBox passwordForm;
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    @FXML private VBox addressForm;
    @FXML private TextField addressField;


    @FXML private Button changeEmailButton;
    @FXML private Button changePasswordButton;
    @FXML private Button backButton;
    @FXML private Button changeAddressButton;
    @FXML private Button showAdressButton;
    @FXML private Label addressLabel;
    @FXML private Label statusLabel;

    @FXML
    private void showEmailSection() {
        emailForm.setVisible(true);
        emailForm.setManaged(true);
        passwordForm.setVisible(false);
        passwordForm.setManaged(false);
        addressForm.setVisible(false);
        addressForm.setManaged(false);

        statusLabel.setText("");
    }

    @FXML
    private void showPasswordSection() {
        passwordForm.setVisible(true);
        passwordForm.setManaged(true);
        emailForm.setVisible(false);
        emailForm.setManaged(false);
        addressForm.setVisible(false);
        addressForm.setManaged(false);

        statusLabel.setText("");
    }

    @FXML
    private void showAddressSection() {
        addressForm.setVisible(true);
        addressForm.setManaged(true);

        emailForm.setVisible(false);
        emailForm.setManaged(false);

        passwordForm.setVisible(false);
        passwordForm.setManaged(false);

        statusLabel.setText("");
    }

    @FXML
    private void hideForms() {
        emailForm.setVisible(false);
        emailForm.setManaged(false);
        passwordForm.setVisible(false);
        passwordForm.setManaged(false);
        statusLabel.setText("");
        addressForm.setVisible(false);
        clearFields();
    }

    @FXML
    private void handleChangeEmail() {
        String email = emailField.getText().trim();
        String password = emailPasswordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Please fill all email fields.");
            return;
        }

        if (!email.contains("@")) {
            statusLabel.setText("Invalid email format.");
            return;
        }

        if (!password.equals(passenger.getPassword())) {
            statusLabel.setText("Wrong Password. Please try again.");
            return;
        }

        try {
            if(passenger.updateEmail(email)){
                statusLabel.setText("Email updated successfully.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearFields();
        hideForms();
    }

    @FXML
    private void handleChangePassword() {
        String current = currentPasswordField.getText();
        String newPass = newPasswordField.getText();
        String confirm = confirmPasswordField.getText();

        if (current.isEmpty() || newPass.isEmpty() || confirm.isEmpty()) {
            statusLabel.setText("Please fill all password fields.");
            return;
        }

        if (!newPass.equals(confirm)) {
            statusLabel.setText("New passwords do not match.");
            return;
        }

        if (newPass.length() < 6) {
            statusLabel.setText("Password too short (min 6 chars).");
            return;
        }
        if (!current.equals(passenger.getPassword())) {
            statusLabel.setText("Wrong Password. Please try again.");
            return;
        }

        try {
            if(passenger.updatePassword(newPass)) {
                statusLabel.setText("Password updated successfully.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearFields();
        hideForms();
    }

    @FXML
    private void handleChangeAddress(ActionEvent event) {
        String newAddress = addressField.getText().trim();

        if (newAddress.isEmpty()) {
            statusLabel.setText("Please enter a new address.");
            return;
        }
        try {
            if(passenger.updateAddress(newAddress)){
                statusLabel.setText("Address updated successfully.");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        clearFields();
        hideForms();
    }

    @FXML
    private void goBack(){
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("PassengerDashboard.fxml"));
                Parent root = loader.load();
                PassengerDashboardController dashboardController = loader.getController();
                dashboardController.setPassengerService(passengerService);
                dashboardController.setTicketMasterService(ticketMasterService);
                dashboardController.setTrainOperatorService(trainOperatorService);
                dashboardController.setTicketService(ticketService);
                dashboardController.setTrainService(trainService);
                dashboardController.setP(passenger);
                Stage stage = (Stage) backButton.getScene().getWindow();
                stage.setScene(new Scene(root, 960, 540));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
    }

    private void clearFields() {
        emailField.clear();
        emailPasswordField.clear();
        currentPasswordField.clear();
        newPasswordField.clear();
        confirmPasswordField.clear();
        addressField.clear();
    }

}
