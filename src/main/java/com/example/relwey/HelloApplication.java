package com.example.relwey;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import backend.*;
import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws Exception {
        TrainService trainService = new TrainService("src/main/resources/trains.txt","src/main/resources/tickets.txt");
        PassengerService passengerService = new PassengerService("src/main/resources/passengers.txt", trainService);
        TicketService ticketService = new TicketService("src/main/resources/tickets.txt", passengerService);
        TicketMasterService ticketMasterService = new TicketMasterService("src/main/resources/ticketmasters.txt", trainService);
        TrainOperatorService trainOperatorService = new TrainOperatorService("src/main/resources/trainoperators.txt", trainService, ticketMasterService);

        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("role_selection.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 960, 540);

        RoleSelectionController rscontroller = fxmlLoader.<RoleSelectionController>getController();
        rscontroller.setPassengerService(passengerService);
        rscontroller.setTicketMasterService(ticketMasterService);
        rscontroller.setTicketService(ticketService);
        rscontroller.setTrainOperatorService(trainOperatorService);
        rscontroller.setTrainService(trainService);

        System.out.println(ticketMasterService.getAllTicketMasters().getFirst() + " passengers in the system.");
        System.out.println(ticketService.getAllTickets().getFirst() + " tickets in the system.");
        System.out.println(trainService.getAllTrains().getFirst() + " tickets in the system.");
        System.out.println(ticketMasterService.getAllTicketMasters().getFirst() + " passengers in the system.");
        System.out.println(passengerService.getAllPassengers().getFirst() + " tickets in the system.");
        System.out.println(trainOperatorService.getAllTrainOperators().getFirst() + " tickets in the system.");

        stage.setTitle("ishmamGayrif");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) throws Exception {
        launch();
    }
}