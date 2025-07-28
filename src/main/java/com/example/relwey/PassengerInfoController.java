package com.example.relwey;

import backend.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class PassengerInfoController {
    public Button backButton;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy, h:mm a");

    public PassengerService passengerService;
    public TrainService trainService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;
    public TicketService ticketService;
    public int passengerId;
    public String passengerEmail;
    public Passenger passenger;


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
    }

    public void setTrainOperatorService(TrainOperatorService trainOperatorService) {
        this.trainOperatorService = trainOperatorService;
    }

    public void setTrainService(TrainService trainService) {
        this.trainService = trainService;
    }


    @FXML
    private Label nidLabel;
    @FXML
    public Label idLabel;
    @FXML
    private Label nameLabel;

    @FXML
    private Label emailLabel;

    @FXML
    private Label addressLabel;

    @FXML
    private Label fineLabel;

    @FXML
    private TableView<TicketRow> ticketTable;

    @FXML
    private TableColumn<TicketRow, String> trainNameCol;

    @FXML
    private TableColumn<TicketRow, String> trainIdCol;

    @FXML
    private TableColumn<TicketRow, String> seatCol;

    @FXML
    private TableColumn<TicketRow, String> departureTimeCol;

    @FXML
    private TableColumn<TicketRow, String> departurePlaceCol;

    @FXML
    private TableColumn<TicketRow, String> arrivalPlaceCol;

    @FXML
    private TableColumn<TicketRow, String> priceCol;

    @FXML
    public void initialize() {
        trainNameCol.setCellValueFactory(cellData -> cellData.getValue().trainNameProperty());
        trainIdCol.setCellValueFactory(cellData -> cellData.getValue().trainIdProperty());
        seatCol.setCellValueFactory(cellData -> cellData.getValue().seatProperty());
        departureTimeCol.setCellValueFactory(cellData -> cellData.getValue().departureProperty());
        priceCol.setCellValueFactory(cellData -> cellData.getValue().priceProperty());
        departurePlaceCol.setCellValueFactory(cellData -> cellData.getValue().departurePlaceProperty());
        arrivalPlaceCol.setCellValueFactory(cellData -> cellData.getValue().arrivalPlaceProperty());
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
        passenger = passengerService.getPassengerById(passengerId);
        List<Ticket> tickets = passenger.viewPastTickets(ticketService, passengerId);
        ObservableList<TicketRow> rows = FXCollections.observableArrayList();

        for (Ticket ticket : tickets) {
            Train train = trainService.getTrainById(ticket.getTrainId());
            if (train != null) {

                rows.add(new TicketRow(
                        train.getName(),
                        train.getId() + "",
                        ticket.getSeatNumber(),
                        train.getDepartureTime().format(formatter),
                        String.format("%.2f", ticket.getPrice()),
                        train.getSource(), train.getDestination(),
                        ticket.getTicketId() + "", ticket.getStatus(), ticket.getPassengerId() + "",
                        train.getStatus()
                ));
            }
        }
        ticketTable.setItems(rows);
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

    public void setPassengerData(String name, String email, String address, double fine, int id, String nid) {
        nameLabel.setText(name);
        emailLabel.setText(email);
        addressLabel.setText(address);
        fineLabel.setText(String.format("%.2f", fine));
        idLabel.setText(String.valueOf(id));
        nidLabel.setText(nid);
    }
}
