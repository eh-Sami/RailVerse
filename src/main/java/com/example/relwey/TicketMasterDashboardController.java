package com.example.relwey;

import backend.*;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.text.BreakIterator;
import java.util.ArrayList;
import java.util.List;
public class TicketMasterDashboardController {

    @FXML
    private VBox centerContent;

    private TicketMaster loggedInTicketMaster;
    private TicketService ticketService;
    private PassengerService passengerService;
    private TrainService trainService;
    private TrainOperatorService trainOperatorService;
    private TicketMasterService ticketMasterService;


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

    public void setTicketMaster(TicketMaster ticketMaster) {
        this.loggedInTicketMaster = ticketMaster;
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML private Button logoutButton;

    @FXML
    private void handleValidateTicket() {
        centerContent.getChildren().clear();

        Label label = new Label("Enter Ticket ID to validate:");
        TextField ticketField = new TextField();
        Label label2 = new Label("Enter Passenger ID to validate:");
        TextField passengerField = new TextField();
        Button validateBtn = new Button("Validate");

        label.setStyle("-fx-font-size: 15px;");
        label2.setStyle("-fx-font-size: 15px;");

        Label result = new Label();

        validateBtn.setOnAction(e -> {
            try {
                long ticketId = Long.parseLong(ticketField.getText());
                int passengerId = Integer.parseInt(passengerField.getText());
                Ticket t = ticketService.getTicketById(ticketId);
                boolean valid = loggedInTicketMaster.validateTicket(t);
                if(valid&&(t.getPassengerId()==passengerId)){
                    result.setText("Ticket is valid");
                }
                else{
                    result.setText("Ticket is invalid");

                }
            } catch (NumberFormatException ex) {
                result.setText("Invalid Ticket ID.");
            }
        });

        centerContent.getChildren().addAll(label, ticketField, label2, passengerField, validateBtn, result);

    }

    @FXML
    private void handleAddFine() {
        centerContent.getChildren().clear();

        Label label1 = new Label("Passenger ID:");
        TextField passengerIdField = new TextField();
        Label label2 = new Label("Fine Amount:");
        TextField fineAmountField = new TextField();
        Button fineBtn = new Button("Add Fine");

        label1.setStyle("-fx-font-size: 15px;");
        label2.setStyle("-fx-font-size: 15px;");

        Label result = new Label();

        fineBtn.setOnAction(e -> {
            try {
                int id = Integer.parseInt(passengerIdField.getText());
                double fine = Double.parseDouble(fineAmountField.getText());

                Passenger p = passengerService.getPassengerById(id);
                if (p == null) {
                    result.setText("Passenger not found.");
                } else {
                    boolean success = loggedInTicketMaster.addFineToPassenger(p, fine);
                    if (success) {
                        passengerService.updatePassengerFine(p, p.getFine());
                        result.setText("Fine added. Total: " + p.getFine());
                    } else {
                        result.setText("Failed to add fine.");
                    }
                }
            } catch (NumberFormatException ex) {
                result.setText("Invalid input.");
            }
        });

        centerContent.getChildren().addAll(label1, passengerIdField, label2, fineAmountField, fineBtn, result);
    }

    @FXML
    private void handleViewTickets() {
        centerContent.getChildren().clear();

        if (loggedInTicketMaster == null) {
            System.out.println("No TicketMaster logged in.");
            return;
        }

        Train assignedTrain = loggedInTicketMaster.getAssignedTrain();
        if (assignedTrain == null) {
            System.out.println("Train is null");
            return;
        }

        System.out.println("Assigned Train: " + assignedTrain);

        List<Ticket> ticketList = ticketService.getAllTickets();
        System.out.println("All tickets fetched: " + ticketList);

        List<Ticket> filteredTicketList = new ArrayList<>();
        for (Ticket ticket : ticketList) {
            if (ticket.getTrainId() == Integer.parseInt(assignedTrain.getTrainId()) && !ticket.getStatus().equalsIgnoreCase("Vacant")) {
                filteredTicketList.add(ticket);
            }
        }

        System.out.println("Filtered tickets: " + filteredTicketList);

        for (Ticket ticket : filteredTicketList) {
            Passenger passenger = passengerService.getPassengerById(ticket.getPassengerId());
            if (passenger != null) {
                ticket.setPassenger(passenger);
            }
        }

        Label title = new Label("Tickets for Train ID " + assignedTrain.getId());
        TableView<Ticket> table = new TableView<>();
        title.setStyle("-fx-font-size: 15px;");

        TableColumn<Ticket, Long> idCol = new TableColumn<>("Ticket ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleLongProperty(data.getValue().getTicketId()).asObject());

        TableColumn<Ticket, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        TableColumn<Ticket, String> seatCol = new TableColumn<>("Seat");
        seatCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getSeatNumber()));

        TableColumn<Ticket, String> ownerCol = new TableColumn<>("Owner Id");
        ownerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getPassengerId())));

        TableColumn<Ticket, String> ownernameCol = new TableColumn<>("Owner Name");
        ownernameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getPassenger().getName()));

        TableColumn<Ticket, String> bookingCol = new TableColumn<>("Booking Date");
        bookingCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getBookingDate())));

        table.getColumns().addAll(idCol, statusCol, seatCol, ownerCol, ownernameCol, bookingCol);

        table.getItems().addAll(filteredTicketList);

        this.centerContent.getChildren().addAll(title, table);
    }



    @FXML
    private void handleViewProfile() {
        centerContent.getChildren().clear();

        Label nameLabel = new Label("Name: ");
        Label emailLabel = new Label("Email: ");
        Label addressLabel = new Label("Address: ");
        Label idLabel = new Label("Ticket Master Id: ");
        Label nidLabel = new Label("Nid: ");
        Label TrainLabel = new Label("Assigned Train: ");

        Label nameValueLabel = new Label(loggedInTicketMaster.getName());
        Label emailValueLabel = new Label(loggedInTicketMaster.getEmail());
        Label addressValueLabel = new Label(loggedInTicketMaster.getAddress());
        Label idValueLabel = new Label(String.valueOf(loggedInTicketMaster.getId()));
        Label nidValueLabel = new Label(loggedInTicketMaster.getNid());
        Label assignedTrainValueLabel = new Label(loggedInTicketMaster.getAssignedTrain().getTrainId());
        Label assignedTrainNameLabel = new Label(loggedInTicketMaster.getAssignedTrain().getName());
        Label assignedTrainFrom = new Label(loggedInTicketMaster.getAssignedTrain().getFrom());
        Label assignedTrainTo = new Label(loggedInTicketMaster.getAssignedTrain().getTo());
        Label assingedTrainDepartureTime = new Label(loggedInTicketMaster.getAssignedTrain().getDepartureTimeString());
        Label assignedTrainStatus = new Label(loggedInTicketMaster.getAssignedTrain().getStatus());

        nameLabel.setStyle("-fx-font-size: 15px;");
        nameValueLabel.setStyle("-fx-font-size: 15px;");
        emailLabel.setStyle("-fx-font-size: 15px;");
        emailValueLabel.setStyle("-fx-font-size: 15px;");
        addressLabel.setStyle("-fx-font-size: 15px;");
        addressValueLabel.setStyle("-fx-font-size: 15px;");
        idLabel.setStyle("-fx-font-size: 15px;");
        idValueLabel.setStyle("-fx-font-size: 15px;");
        nidLabel.setStyle("-fx-font-size: 15px;");
        nidValueLabel.setStyle("-fx-font-size: 15px;");

        HBox nameBox = new HBox(10, nameLabel, nameValueLabel);
        HBox emailBox = new HBox(10, emailLabel, emailValueLabel);
        HBox addressBox = new HBox(10, addressLabel, addressValueLabel);
        HBox idBox = new HBox(10, idLabel, idValueLabel);
        HBox nidBox = new HBox(10, nidLabel, nidValueLabel);
        if(loggedInTicketMaster.getAssignedTrain().getId() != 0) {
            Label StatusLabel = new Label("Status: ");
            Label NameLabel = new Label("Train Name: ");
            Label FromLabel = new Label("From: ");
            Label ToLabel = new Label("To: ");
            Label DepartureTimeLabel = new Label("Departure Time: ");

            HBox assignedTrainBox = new HBox(10, TrainLabel, assignedTrainValueLabel);
            HBox assignedTrainNameBox = new HBox(10, NameLabel, assignedTrainNameLabel);
            HBox assignedTrainFromBox = new HBox(10, FromLabel, assignedTrainFrom);
            HBox assignedTrainToBox = new HBox(10, ToLabel, assignedTrainTo);
            HBox assignedTrainDepartureTimeBox = new HBox(10, DepartureTimeLabel, assingedTrainDepartureTime);
            HBox assignedTrainStatusBox = new HBox(10, StatusLabel, assignedTrainStatus);

            nameBox.setSpacing(100);
            emailBox.setSpacing(105);
            addressBox.setSpacing(87);
            idBox.setSpacing(35);
            nidBox.setSpacing(116);
            assignedTrainBox.setSpacing(44);
            assignedTrainNameBox.setSpacing(44);
            assignedTrainFromBox.setSpacing(44);
            assignedTrainToBox.setSpacing(44);
            assignedTrainDepartureTimeBox.setSpacing(44);
            assignedTrainStatusBox.setSpacing(44);



            centerContent.getChildren().addAll(
                    nameBox, emailBox, addressBox, idBox, nidBox, assignedTrainBox, assignedTrainNameBox, assignedTrainFromBox, assignedTrainToBox, assignedTrainDepartureTimeBox, assignedTrainStatusBox
            );
        }
        else{
            assignedTrainValueLabel = new Label("None");
            HBox assignedTrainBox = new HBox(10, TrainLabel, assignedTrainValueLabel);
            assignedTrainBox.setSpacing(44);

            centerContent.getChildren().addAll(
                    nameBox, emailBox, addressBox, idBox, nidBox, assignedTrainBox
            );
        }


        System.out.println("View Profile clicked.");
    }


    @FXML
    private void handleEditProfile() {
        centerContent.getChildren().clear();

        Label nameLabel = new Label("Name:");
        TextField nameField = new TextField();

        Label addressLabel = new Label("Address:");
        TextField addressField = new TextField();

        Label newpasswordLabel = new Label("New Password:");
        PasswordField newpasswordField = new PasswordField();

        Label oldpasswordLabel = new Label("Password:");
        PasswordField oldpasswordField = new PasswordField();

        Button saveBtn = new Button("Save Changes");
        Label result = new Label();


        nameLabel.setStyle("-fx-font-size: 15px;");
        addressLabel.setStyle("-fx-font-size: 15px;");
        newpasswordLabel.setStyle("-fx-font-size: 15px;");
        oldpasswordLabel.setStyle("-fx-font-size: 15px;");



        saveBtn.setOnAction(e -> {
            if (oldpasswordField.getText().isEmpty()) {
                showError("Put your password");
            } else {
                String password = oldpasswordField.getText();
                if (password.equals(loggedInTicketMaster.getPassword())) {
                    boolean changepass = false;
                    boolean changename = false;
                    boolean changeadress = false;

                    if (!newpasswordField.getText().isEmpty()) {
                        changepass = true;
                    }
                    if (!nameField.getText().isEmpty()) {
                        changename = true;
                    }
                    if (!addressField.getText().isEmpty()) {
                        changeadress = true;
                    }

                    if (changename) {
                        String name = nameField.getText();
                        loggedInTicketMaster.changeName(name);
                    }
                    if (changeadress) {
                        String adress = addressField.getText();
                        loggedInTicketMaster.changeAddress(adress);
                    }

                    boolean ok = false;
                    if (changepass) {
                        String newPass = newpasswordField.getText();
                        ok = loggedInTicketMaster.changePassword(password, newPass);
                    }

                    if (changename && changeadress && changepass && ok) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed name, address and password");
                        alert.showAndWait();
                    } else if (changename && changeadress) {
                        result.setText("Successfully changed name and address");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed name, address");
                        alert.showAndWait();
                    } else if (changename && changepass && ok) {
                        result.setText("Successfully changed name and password");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed name and password");
                        alert.showAndWait();
                    } else if (changeadress && changepass && ok) {
                        result.setText("Successfully changed address and password");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed address and password");
                        alert.showAndWait();
                    } else if (changename) {
                        result.setText("Successfully changed name");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed name");
                        alert.showAndWait();
                    } else if (changeadress) {
                        result.setText("Successfully changed address");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed address");
                        alert.showAndWait();
                    } else if (changepass && ok) {
                        result.setText("Successfully changed password");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed password");
                        alert.showAndWait();
                    } else if (changepass && !ok) {
                        result.setText("Password change failed.");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Password change failed.");
                        alert.showAndWait();
                    }
                } else {
                    showError("Wrong Password");
                }
            }
        });

        centerContent.getChildren().addAll(nameLabel, nameField, addressLabel, addressField, oldpasswordLabel, oldpasswordField, newpasswordLabel, newpasswordField, saveBtn, result);
    }

    @FXML
    private void handleLogout() {
        try {
            System.out.println(ticketService.getAllTickets());
            FXMLLoader loader = new FXMLLoader(getClass().getResource("role_selection.fxml"));
            Parent root = loader.load();
            RoleSelectionController roleController = loader.getController();
            roleController.setPassengerService(passengerService);
            roleController.setTicketMasterService(ticketMasterService);
            roleController.setTrainOperatorService(trainOperatorService);
            roleController.setTicketService(ticketService);
            roleController.setTrainService(trainService);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
