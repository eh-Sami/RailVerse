package com.example.relwey;

import backend.*;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

public class PassengerDashboardController {
    public PassengerService passengerService;
    public TrainService trainService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;
    public TicketService ticketService;
    public Passenger p;
    public Button viewAccountButton;

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
        fromComboBox.getItems().addAll("Dhaka", "Chattogram", "Rajshahi", "Khulna", "Sylhet", "Mymensingh", "Rangpur", "Barishal");
        toComboBox.getItems().addAll("Dhaka", "Chattogram", "Rajshahi", "Khulna", "Sylhet", "Mymensingh", "Rangpur", "Barishal");

        searchButton.setOnAction(e -> handleSearchTrain());
        viewTicketsButton.setOnAction(e -> handleViewTickets());
        viewHistoryButton.setOnAction(e -> handleViewHistory());
        editAccountButton.setOnAction(e -> handleEditAccount());
        logoutButton.setOnAction(e -> handleLogout());
        viewAccountButton.setOnAction(e -> viewAccountButtonClicked());
    }

    private void handleSearchTrain() {
        String from = fromComboBox.getValue();
        String to = toComboBox.getValue();
        LocalDate date = datePicker.getValue();
        LocalDate currDate = LocalDate.now();

        if (from == null || to == null || date == null) {
            showAlert(Alert.AlertType.ERROR, "Missing Input", "Please select both stations and a date.");
            return;
        }

        if (currDate.isAfter(date)) {
            showAlert(Alert.AlertType.ERROR, "Invalid", "Please select valid a date.");
            return;
        }

        if (from.equals(to)) {
            showAlert(Alert.AlertType.WARNING, "Invalid Input", "Departure and destination stations cannot be the same.");
            return;
        }


        // TODO: Load available trains screen using these values
        List<Train> filteredTrains = trainService.getTrainsBySource(from, to);
        System.out.println(filteredTrains);

        if (filteredTrains.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Trains", "No trains available for the selected route and date.");
            return;
        }

        try{
            FXMLLoader loader = new FXMLLoader(getClass().getResource("availableTrains.fxml"));
            Parent root = loader.load();
            AvailableTrainsController controller = loader.getController();
            controller.setTrainList(filteredTrains);
            controller.setFrom(from);
            controller.setTo(to);
            controller.setDate(date);
            controller.setPassenger(p);
            controller.setTicketService(ticketService);
            controller.setTrainService(trainService);
            controller.setPassengerService(passengerService);
            controller.setTicketMasterService(ticketMasterService);
            controller.setTrainOperatorService(trainOperatorService);

            Stage stage = (Stage) searchButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));
            System.out.println("Searching for trains from " + from + " to " + to + " on " + date);

        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Error", "Something went wrong while loading available trains.");
        }


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

    private void viewAccountButtonClicked() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("viewAccount.fxml"));
            Parent root = loader.load();
            PassengerInfoController pInfo = loader.getController();
            pInfo.setPassengerData(p.getName(), p.getEmail(), p.getAddress(), p.getFine(), p.getId(), p.getNid());
            pInfo.setPassengerService(passengerService);
            pInfo.setTicketMasterService(ticketMasterService);
            pInfo.setTrainOperatorService(trainOperatorService);
            pInfo.setTicketService(ticketService);
            pInfo.setTrainService(trainService);
            pInfo.setPassengerId(p.getId());
            pInfo.setPassengerEmail(p.getEmail());
            pInfo.loadTickets();
            Stage stage = (Stage) viewAccountButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleViewHistory() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("pastPurchasedTickets.fxml"));
            Parent root = loader.load();

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("edit_profile.fxml"));
            Parent root = loader.load();
            EditProfileController editProf = loader.getController();

            editProf.setPassengerService(passengerService);
            editProf.setTicketMasterService(ticketMasterService);
            editProf.setTrainOperatorService(trainOperatorService);
            editProf.setTicketService(ticketService);
            editProf.setTrainService(trainService);
            editProf.setPassenger(p);

            Stage stage = (Stage) editAccountButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));
        }
        catch (Exception e){
            e.printStackTrace();
        }
        System.out.println("View History clicked");
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
