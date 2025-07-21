package com.example.relwey;

import backend.*;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import java.io.IOException;
import java.time.format.DateTimeFormatter;

import static java.lang.Integer.parseInt;


public class ViewTicketsController {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, h:mm a");

    public PassengerService passengerService;
    public TrainService trainService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;
    public TicketService ticketService;
    public int passengerId;
    public String passengerEmail;

    public ViewTicketsController(){

    }

    public void setPassengerEmail(String passengerEmail) {
        this.passengerEmail = passengerEmail;
    }

    public void setPassengerId(int passengerId) {
        this.passengerId = passengerId;
    }

    public void setPassengerService(PassengerService passengerService) {
        this.passengerService = passengerService;
    }

    public void setTicketMasterService(TicketMasterService ticketMasterService) {
        this.ticketMasterService = ticketMasterService;
    }

    public void setTicketService(TicketService ticketService) {
        this.ticketService = ticketService;
        System.out.println(ticketService==null);
    }

    public void setTrainOperatorService(TrainOperatorService trainOperatorService) {
        this.trainOperatorService = trainOperatorService;
    }

    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
        System.out.println(trainService==null);
    }
    @FXML private TableView<TicketRow> ticketTable;
    @FXML private TableColumn<TicketRow, String> trainNameCol;
    @FXML private TableColumn<TicketRow, String> trainIdCol;
    @FXML private TableColumn<TicketRow, String> seatCol;
    @FXML private TableColumn<TicketRow, String> departureTimeCol;
    @FXML private TableColumn<TicketRow, String> priceCol;
    @FXML private TableColumn<TicketRow, String> departurePlaceCol;
    @FXML private TableColumn<TicketRow, String> arrivalPlaceCol;
    @FXML private TableColumn<TicketRow, Void> cancelCol;


    @FXML
    private Button backButton;

    public void initialize() {
        trainNameCol.setCellValueFactory(cellData -> cellData.getValue().trainNameProperty());
        trainIdCol.setCellValueFactory(cellData -> cellData.getValue().trainIdProperty());
        seatCol.setCellValueFactory(cellData -> cellData.getValue().seatProperty());
        departureTimeCol.setCellValueFactory(cellData -> cellData.getValue().departureProperty());
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        departurePlaceCol.setCellValueFactory(cellData -> cellData.getValue().departurePlaceProperty());
        arrivalPlaceCol.setCellValueFactory(cellData -> cellData.getValue().arrivalPlaceProperty());
        addCancelButtonToTable();
    }

    public void loadTickets() {
        if (passengerEmail == null || ticketService == null || trainService == null){
            System.out.println("returning");
            if(passengerEmail == null){
                System.out.println("passenger email is null");
            }
            if(ticketService == null){
                System.out.println("ticket service is null");
            }
            if(trainService == null){
                System.out.println("train service is null");
            }
            return;
        };


        System.out.println(passengerEmail + ticketService + trainService);
        List<Ticket> tickets = ticketService.getTicketsForUser(passengerId);
        ObservableList<TicketRow> rows = FXCollections.observableArrayList();

        for (Ticket ticket : tickets) {
            if(ticket.getStatus().equalsIgnoreCase("cancelled") || ticket.getStatus().equalsIgnoreCase("vacant")){
                continue;
            }
            Train train = trainService.getTrainById(ticket.getTrainId());
            if (train != null) {

                rows.add(new TicketRow(
                        train.getName(),
                        train.getId() + "",
                        ticket.getSeatNumber(),
                        train.getDepartureTime().format(formatter),
                        String.format("%.2f", ticket.getPrice()),
                        train.getSource(), train.getDestination(),
                        ticket.getTicketId() + "", ticket.getStatus(), ticket.getPassengerId() + ""
                ));
            }
        }
        ticketTable.setItems(rows);
    }

    private void addCancelButtonToTable() {
        cancelCol.setCellFactory(col -> new TableCell<>() {
            private final Button cancelButton = new Button("Cancel");

            {
                cancelButton.setOnAction(event -> {
                    TicketRow ticketRow = getTableView().getItems().get(getIndex());
                    handleCancelTicket(ticketRow);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(cancelButton);
                }
            }
        });
    }


    private void handleCancelTicket(TicketRow row) {
        // TODO: You implement the cancellation logic here
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirm Cancellation");
        confirm.setHeaderText("Cancel Ticket");
        confirm.setContentText("Are you sure you want to cancel ticket for Train ID: " + row.getTrainId() + ", Seat: " + row.getSeat() + "?");

        confirm.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.out.println("Cancelling ticket...");
                try {
                    ticketService.cancelTicket(parseInt(row.getTicketId()), parseInt(row.getPassengerId()));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ticketTable.getItems().remove(row);
            }
        });
    }


    @FXML
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
            dashboardController.setP(passengerService.getPassengerById(passengerId));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
