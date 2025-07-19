package com.example.relwey;

import backend.*;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

import java.io.IOException;

public class RoleSelectionController {

    public PassengerService passengerService;
    public TrainService trainService;
    public TicketMasterService ticketMasterService;
    public TrainOperatorService trainOperatorService;
    public TicketService ticketService;

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

    @FXML private Button passengerButton;
    @FXML private Button ticketMasterButton;
    @FXML private Button trainOperatorButton;

    @FXML
    protected void handlePassenger() throws IOException {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("passenger_login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) passengerButton.getScene().getWindow();
        PassengerLoginController passengerController = loader.getController();
        passengerController.setPassengerService(passengerService);
        passengerController.setTicketService(ticketService);
        passengerController.setTrainOperatorService(trainOperatorService);
        passengerController.setTrainService(trainService);
        passengerController.setTicketMasterService(ticketMasterService);
        stage.setScene(new Scene(root));
//        loadScene("passenger_login.fxml");
    }

    @FXML
    protected void handleTicketMaster() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("ticketmaster_login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) ticketMasterButton.getScene().getWindow();
        TicketMasterLoginController masterController = loader.getController();
        masterController.setTicketMasterService(ticketMasterService);
        masterController.setPassengerService(passengerService);
        masterController.setTicketService(ticketService);
        masterController.setTrainOperatorService(trainOperatorService);
        masterController.setTrainService(trainService);
        stage.setScene(new Scene(root));
//        loadScene("ticketmaster_login.fxml");
    }

    @FXML
    protected void handleTrainOperator() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("trainoperator_login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) trainOperatorButton.getScene().getWindow();
        TrainOperatorLoginController operatorController = loader.getController();
        operatorController.setTrainOperatorService(trainOperatorService);
        operatorController.setPassengerService(passengerService);
        operatorController.setTicketService(ticketService);
        operatorController.setTrainService(trainService);
        operatorController.setTicketMasterService(ticketMasterService);
        stage.setScene(new Scene(root));
//        loadScene("trainoperator_login.fxml");
    }

//    private void loadScene(String fxmlFile) {
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlFile));
//            Parent root = loader.load();
//            Stage stage = (Stage) passengerButton.getScene().getWindow();
//            stage.setScene(new Scene(root));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
}
