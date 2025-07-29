package com.example.relwey;


import backend.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class AvailableTrainsController {
    @FXML
    private Button bookButton;
    @FXML private Button backButton;
    @FXML private TableView<TrainRow> trainTable;
    @FXML private TableColumn<TrainRow, String> trainNameCol;
    @FXML private TableColumn<TrainRow, String> trainIdCol;
    @FXML private TableColumn<TrainRow, String> departureTimeCol;
//    @FXML private TableColumn<TrainRow, Integer> seatsCol;
    @FXML private TableColumn<TrainRow, Double> priceCol;
    @FXML private TableColumn<TrainRow, String> FromCol;
    @FXML private TableColumn<TrainRow, String> ToCol;
    @FXML private ComboBox<Integer> compartmentSelector;
    @FXML private GridPane seatGrid;

    private List<Train> trainList;



    private int compartment;
    private String from, to;
    private LocalDate date;
    private String dateStr;
    private long dateLong;
    private Passenger passenger;

    public TicketService ticketService;
    public TrainService trainService;
    public PassengerService passengerService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;

    public void setTrainList(List<Train> list) {
        this.trainList = list;
        loadData();
    }


    public void setFrom(String from) { this.from = from; }
    public void setTo(String to) { this.to = to; }
    public void setDate(LocalDate date) { this.date = date; }
    public void setPassenger(Passenger p) { this.passenger = p; }

    public void setTicketService(TicketService s) { this.ticketService = s; }
    public void setTrainService(TrainService s) { this.trainService = s; }
    public void setPassengerService(PassengerService s) { this.passengerService = s; }
    public void setTicketMasterService(TicketMasterService s) { this.ticketMasterService = s; }
    public void setTrainOperatorService(TrainOperatorService s) { this.trainOperatorService = s; }

    public void initialize() {
        trainNameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        trainIdCol.setCellValueFactory(new PropertyValueFactory<>("trainId"));
        departureTimeCol.setCellValueFactory(new PropertyValueFactory<>("departureTimeString"));
//        seatsCol.setCellValueFactory(new PropertyValueFactory<>("availableSeats"));
        priceCol.setCellValueFactory(new PropertyValueFactory<>("price"));
        FromCol.setCellValueFactory(new PropertyValueFactory<>("from"));
        ToCol.setCellValueFactory(new PropertyValueFactory<>("to"));
        trainTable.getSelectionModel().selectedItemProperty().addListener((obs, oldRow, newRow) -> {
            if (newRow != null) {
                Train selectedTrain = newRow.getTrain();
                int numCompartments = selectedTrain.getCompartmentCount();
                List<Integer> compartments = new ArrayList<>();
                for (int i = 0; i < numCompartments; i++) {
                    compartments.add(i);
                }
                compartmentSelector.getItems().setAll(compartments);
                compartmentSelector.setDisable(false);
                compartmentSelector.getSelectionModel().select(0);

                seatGrid.setDisable(false);
                seatGrid.setVisible(true);

                showCompartmentSeatMap(selectedTrain, 0);
            }
        });
        compartmentSelector.setOnAction(e -> {
            TrainRow selectedRow = trainTable.getSelectionModel().getSelectedItem();
            Integer selectedCompartment = compartmentSelector.getValue();
            if (selectedRow != null && selectedCompartment != null) {
                showCompartmentSeatMap(selectedRow.getTrain(), selectedCompartment);
            }
        });

    }

    private void loadData() {
        if (trainList == null) return;
        List<TrainRow> trainRows = new ArrayList<>();

        for (Train t : trainList) {
            trainRows.add(new TrainRow(t));
        }
        trainTable.setItems(FXCollections.observableArrayList(trainRows));
    }

    public void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("PassengerDashboard.fxml"));
            Parent root = loader.load();
            PassengerDashboardController dashboardController = loader.getController();
            dashboardController.setPassengerService(passengerService);
            dashboardController.setTicketMasterService(ticketMasterService);
            dashboardController.setTrainOperatorService(trainOperatorService);
            dashboardController.setTicketService(ticketService);
            dashboardController.setTrainService(trainService);
            dashboardController.setP(passengerService.getPassengerById(passenger.getId()));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");

    private long generateTicketId(String trainIdStr, int compartment, int seat) {
        int trainId = Integer.parseInt(trainIdStr);
        LocalDate date = trainService.getTrainById(trainId).getDepartureTime().toLocalDate();
        String dateStr = date.format(formatter);
        long dateLong = Long.parseLong(dateStr);
        return dateLong * 1000000 + trainId * 1000 + (compartment) * 100 + seat;
    }


    @FXML
    public void handleBookTrain(ActionEvent event) {
        ticketService.reloadTicketsFromFile();
        TrainRow selectedRow = trainTable.getSelectionModel().getSelectedItem();
        if (selectedRow == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a train first.", ButtonType.OK);
            alert.showAndWait();
            return;
        }
        Train train = selectedRow.getTrain();

        if (selectedSeat == -1) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Please select a seat first.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        int compartmentNum = compartmentSelector.getValue();
        System.out.println(compartmentNum + " " +selectedSeat);

        long ticketId = generateTicketId(train.getTrainId(), compartmentNum, selectedSeat);
        System.out.println(ticketId);

        Ticket ticket = ticketService.getTicketById(ticketId);
        if (ticket == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Selected ticket not found or already booked.", ButtonType.OK);
            alert.showAndWait();
            return;
        }

        try {
//
//            System.out.println(passenger.getId());
//            passenger.bookTicket(train, ticket);
//            for(Ticket t: ticketService.getAllTickets()){
//                if(t.getTicketId() == ticket.getTicketId()){
//                    t.setStatus("Booked");
//                    t.setPrice(train.getPrice());
//                }
//            }
//            ticketService.reloadTicketsFromFile();


            int id = passenger.getId();
            ticket.setPassengerId(id);
            System.out.println(id);

            ticket.setStatus("Booked");
            ticket.setBookingDate(LocalDateTime.now());
            ticket.setPrice(train.getPrice());

            ticketService.saveAllTickets();
            ticketService.reloadTicketsFromFile();

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Ticket booked successfully! Ticket ID: " + ticketId, ButtonType.OK);
            alert.showAndWait();
            seatGrid.getChildren().clear();
            seatGrid.setDisable(true);
            seatGrid.setVisible(false);
            compartmentSelector.getItems().clear();
            compartmentSelector.setDisable(true);
            compartmentSelector.setPromptText("Select a train");
            selectedSeat = -1;
            loadData();

        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Failed to book ticket: " + e.getMessage(), ButtonType.OK);
            alert.showAndWait();
        }
    }




    private void showCompartmentSeatMap(Train train, int compartmentNumber) {
        ticketService.reloadTicketsFromFile();

        boolean[] seatAvailability = new boolean[24];
        for (int i = 0; i < 24; i++) seatAvailability[i] = true;

        List<Ticket> allTickets = ticketService.getAllTickets();
        List<Ticket> ticketsOfThisTrain = new ArrayList<>();
        for(Ticket t: allTickets) {
            if(t.getTrainId() == Integer.parseInt(train.getTrainId())){
                ticketsOfThisTrain.add(t);
            }
        }
        for (Ticket t : ticketsOfThisTrain) {
            long tWOdate = t.getTicketId() % 1000000;
            int comp = (int)((tWOdate % 1000) / 100);
            int seatNum = (int)(tWOdate % 100);
            tWOdate = t.getTicketId() / 1000000;
            dateStr = date.format(formatter);
            dateLong = Long.parseLong(dateStr);
            System.out.println(comp + "," +seatNum);
            if ((comp == compartmentNumber && t.getStatus().equalsIgnoreCase("Booked") && tWOdate == dateLong)) {
                System.out.println(comp + "," + seatNum + "," +t.getStatus());
                if (seatNum >= 1 && seatNum <= 24) {
                    seatAvailability[seatNum - 1] = false;
                }

            }
        }
        System.out.println(seatAvailability[0] + "," + seatAvailability[1] + "," + seatAvailability[2] + "," + seatAvailability[3]);

        showSeatMap(seatAvailability, train, compartmentNumber);
    }


    private int selectedSeat = -1;

    public void showSeatMap(boolean[] seatAvailability, Train train, int compartment) {
        seatGrid.getChildren().clear();

        int rows = 6;
        int cols = 4;
        int seatNumber = 1;

        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int adjustedCol = col;
                if (col >= 2) {
                    adjustedCol += 5;
                }
                Button seatButton = new Button("S" + seatNumber);
                seatButton.setPrefSize(60, 30);
                boolean available = seatAvailability[seatNumber - 1];


                seatButton.setStyle(available ? "-fx-background-color: lightgreen;" : "-fx-background-color: gray;");
                seatButton.setDisable(!available);

                int finalSeatNumber = seatNumber;
                if (available) {
                    seatButton.setOnAction(e -> {
                        selectedSeat = finalSeatNumber;
                        highlightSelectedSeat(finalSeatNumber);
                    });
                }

                seatGrid.add(seatButton, adjustedCol, row);
                seatNumber++;
            }
        }
    }

    private void highlightSelectedSeat(int seatNumber) {
        for (javafx.scene.Node node : seatGrid.getChildren()) {
            if (node instanceof Button) {
                Button btn = (Button) node;
                if (btn.getText().equals("S" + seatNumber)) {
                    btn.setStyle("-fx-background-color: deepskyblue;");
                } else if (!btn.isDisabled()) {
                    btn.setStyle("-fx-background-color: lightgreen;");
                }
            }
        }
    }

}
