package com.example.relwey;

import backend.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

    public class PassengerDashboardController {
    public PassengerService passengerService;
    public TrainService trainService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;
    public TicketService ticketService;
    public Passenger p;

        public void setP(Passenger p) {
            this.p = p;
        }

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

    @FXML private ComboBox<String> fromComboBox;
    @FXML private ComboBox<String> toComboBox;
    @FXML private DatePicker datePicker;

    @FXML private Button searchButton;
    @FXML private Button viewTicketsButton;
    @FXML private Button viewHistoryButton;
    @FXML private Button editAccountButton;
    @FXML private Button logoutButton;

    @FXML
    public void initialize() {
        fromComboBox.getItems().addAll("Dhaka", "Chattogram", "Rajshahi", "Khulna", "Sylhet");
        toComboBox.getItems().addAll("Dhaka", "Chattogram", "Rajshahi", "Khulna", "Sylhet");

        searchButton.setOnAction(e -> handleSearchTrain());
        viewTicketsButton.setOnAction(e -> handleViewTickets());
        viewHistoryButton.setOnAction(e -> handleViewHistory());
        editAccountButton.setOnAction(e -> handleEditAccount());
        logoutButton.setOnAction(e -> handleLogout());
    }

    private void handleSearchTrain() {
        String from = fromComboBox.getValue();
        String to = toComboBox.getValue();
        LocalDate date = datePicker.getValue();

        if (from == null || to == null || date == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Input", "Please select both stations and a date.");
            return;
        }

        if (from.equals(to)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Departure and destination stations cannot be the same.");
            return;
        }

        // TODO: Load available trains screen using these values

        System.out.println("Searching for trains from " + from + " to " + to + " on " + date);
    }

    private void handleViewTickets() {
        // TODO: Load view purchased tickets screen
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("purchasedTickets.fxml"));
            Parent root = loader.load();

            ViewTicketsController pTickets = loader.getController();

            pTickets.setPassengerService(passengerService);
            pTickets.setTicketMasterService(ticketMasterService);
            pTickets.setTrainOperatorService(trainOperatorService);
            pTickets.setTicketService(ticketService);
            pTickets.setTrainService(trainService);
            pTickets.setPassengerId(p.getId());
            pTickets.setPassengerEmail(p.getEmail());
            pTickets.loadTickets();

            Stage stage = (Stage) viewTicketsButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));

        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("View Purchased Tickets clicked");
    }

    private void handleViewHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pastPurchasedTickets.fxml"));
            Parent root = loader.load();
            // TODO: Load history screen
            ViewPastTicketsController pastTickets = loader.getController();

            pastTickets.setPassengerService(passengerService);
            pastTickets.setTicketMasterService(ticketMasterService);
            pastTickets.setTrainOperatorService(trainOperatorService);
            pastTickets.setTicketService(ticketService);
            pastTickets.setTrainService(trainService);
            pastTickets.setPassengerId(p.getId());
            pastTickets.setPassengerEmail(p.getEmail());
            pastTickets.loadTickets();
            Stage stage = (Stage) viewTicketsButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("View History clicked");
    }

    private void handleEditAccount() {
        // TODO: Load edit account screen
        System.out.println("Edit Account clicked");
    }

    private void handleLogout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("role_selection.fxml"));
            Parent root = loader.load();
            RoleSelectionController roleController = loader.getController();
            roleController.setPassengerService(passengerService);
            roleController.setTicketMasterService(ticketMasterService);
            roleController.setTrainOperatorService(trainOperatorService);
            roleController.setTicketService(ticketService);
            roleController.setTrainService(trainService);
            Stage stage = (Stage) viewTicketsButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
