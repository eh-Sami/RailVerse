package com.example.relwey;

import backend.*;

import javafx.beans.property.SimpleLongProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.BreakIterator;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TrainOperatorDashboardController {
    @FXML
    private VBox centerContent;

    private TrainOperator loggedInTrainOperatorr;
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

    public void setTrainOperator(TrainOperator trainOperator) {
        this.loggedInTrainOperatorr = trainOperator;
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
    private void handleSetTrainStatus() {
        centerContent.getChildren().clear();


        Label label = new Label("Enter Train ID to change status:");
        TextField trainField = new TextField();
        Label label2 = new Label("Select the Status of the Train:");
        ChoiceBox<String> statusChoiceBox = new ChoiceBox<>();
        statusChoiceBox.getItems().addAll("Delayed", "On Time");

        statusChoiceBox.setValue("On Time");

        VBox layout = new VBox(10);

        Button validateBtn = new Button("Confirm");

        label.setStyle("-fx-font-size: 15px;");
        label2.setStyle("-fx-font-size: 15px;");

        Label result = new Label();

        validateBtn.setOnAction(e -> {
            try {
                int trainId = Integer.parseInt(trainField.getText());
                String choice = statusChoiceBox.getValue();
                Train t =   trainService.getTrainById(trainId);

                if(t==null){
                    result.setText("Invalid Train ID.");
                    showError("Invalid Train ID.");
                }
                else {
                    loggedInTrainOperatorr.setTrainStatus(choice, t);
                }
            } catch (NumberFormatException ex) {
                result.setText("Invalid Ticket ID.");
            }
        });

        centerContent.getChildren().addAll(label, trainField, label2, statusChoiceBox, validateBtn, result);

    }

    @FXML
    private void handleChangeTime() {
        centerContent.getChildren().clear();

        Label label = new Label("Enter Train ID to change time:");
        TextField trainField = new TextField();
        Label label2 = new Label("Select the time of the train:");
        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        timeField.setPromptText("HH:mm");
        Button submitButton = new Button("Update");
        Label result = new Label();

        label.setStyle("-fx-font-size: 15px;");
        label2.setStyle("-fx-font-size: 15px;");


        submitButton.setOnAction(e -> {
            try {
                int id = Integer.parseInt(trainField.getText());
                LocalDate date = datePicker.getValue();
                String time = timeField.getText();
                LocalTime times = LocalTime.parse(time);
                LocalDateTime dateTime = LocalDateTime.of(date, times);
                LocalDateTime current = trainService.getTrainById(id).getDepartureTime();
                if(current.isAfter(dateTime)){
                    result.setText("Cannot change time to a time in the past.");
                    showError("Cannot change time to a time in the past.");
                    return;
                }

                Train t = trainService.getTrainById(id);
                if (t == null) {
                    result.setText("Train not found.");
                    showError("Train not found.");
                } else {
                    loggedInTrainOperatorr.changeDepartureTime(dateTime, t);
                    loggedInTrainOperatorr.setTrainStatus("delayed", t);
                        result.setText("Changed Time of Train " + t.getTrainId() + " to " + dateTime.toString() + " successfully.");
                }
            } catch (NumberFormatException ex) {
                result.setText("Invalid input.");
            }
        });

        centerContent.getChildren().addAll(label, trainField, label2, datePicker, timeField, submitButton, result);
    }

    @FXML
    private void handleAssignTicketMaster() {
        centerContent.getChildren().clear();

        List<TicketMaster> ticketMasterList = ticketMasterService.getAllTicketMasters();

        Label title = new Label("Ticket Masters Available: ");
        TableView<TicketMaster> table = new TableView<>();

        title.setStyle("-fx-font-size: 15px;");

        TableColumn<TicketMaster, String> idCol = new TableColumn<>("Ticket Master Id");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getId())));

        TableColumn<TicketMaster, String> ticketmasternameCol = new TableColumn<>("Ticket Master Name");
        ticketmasternameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<TicketMaster, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        TableColumn<TicketMaster, String> trainCol = new TableColumn<>("Assigned Train Id");
        trainCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAssignedTrain().getTrainId()));

        TableColumn<TicketMaster, String> trainnameCol = new TableColumn<>("Assigned Train Name");
        trainnameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getAssignedTrain().getName()));

        TableColumn<TicketMaster, Void> assignCol = new TableColumn<>("Assign Train");
        assignCol.setCellFactory(param -> new TableCell<TicketMaster, Void>() {
            private final Button assignButton = new Button("Assign");
            {
                assignButton.setOnAction(event -> {
                    TicketMaster ticketMaster = getTableRow().getItem();
                    if (ticketMaster != null) {
                        showTrainIdDialog(ticketMaster);
                        table.refresh();
                    }
                });
            }

            @Override
            public void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(assignButton);
                }
            }
        });
        table.getColumns().addAll(idCol, ticketmasternameCol, statusCol, trainCol, trainnameCol, assignCol);

        if (ticketMasterList != null) {
            table.getItems().addAll(ticketMasterList);
        }

        centerContent.getChildren().addAll(title, table);
    }

    private void showTrainIdDialog(TicketMaster ticketMaster) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Assign Train");
        dialog.setHeaderText("Assign a train to " + ticketMaster.getName());
        dialog.setContentText("Please enter the Train ID:");

        Optional<String> result = dialog.showAndWait();
        result.ifPresent(trainId -> {
            try {
                int trainIdInt = Integer.parseInt(trainId);
                Train train = trainService.getTrainById(trainIdInt);
                if (train == null) {
                    showError("Invalid Train ID entered. Please enter a valid Train ID.");
                    return;
                }
                else {
                    if(trainId.equalsIgnoreCase("0")){
                        loggedInTrainOperatorr.assignTicketMaster(ticketMaster, trainIdInt, "inactive");
                    }
                    else {
                        loggedInTrainOperatorr.assignTicketMaster(ticketMaster, trainIdInt, "active");
                    }
                    System.out.println("Train assigned to TicketMaster " + ticketMaster.getName());
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, trainService.getTrainById(trainIdInt).getName() + " Train assigned to TicketMaster " + ticketMaster.getName());
                    alert.showAndWait();
                }
            } catch (NumberFormatException e) {
                showError("Invalid Train ID entered. Please enter a valid integer.");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });
    }



    @FXML
    private void handleViewProfile() {
        centerContent.getChildren().clear();

        Label nameLabel = new Label("Name: ");
        Label emailLabel = new Label("Email: ");
        Label addressLabel = new Label("Address: ");
        Label idLabel = new Label("Ticket Master Id: ");
        Label nidLabel = new Label("Nid: ");
        Label assignedTrainLabel = new Label("Assigned Train: ");

        Label nameValueLabel = new Label(loggedInTrainOperatorr.getName());
        Label emailValueLabel = new Label(loggedInTrainOperatorr.getEmail());
        Label addressValueLabel = new Label(loggedInTrainOperatorr.getAddress());
        Label idValueLabel = new Label(String.valueOf(loggedInTrainOperatorr.getId()));
        Label nidValueLabel = new Label(loggedInTrainOperatorr.getNid());

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
        assignedTrainLabel.setStyle("-fx-font-size: 15px;");

        HBox nameBox = new HBox(10, nameLabel, nameValueLabel);
        HBox emailBox = new HBox(10, emailLabel, emailValueLabel);
        HBox addressBox = new HBox(10, addressLabel, addressValueLabel);
        HBox idBox = new HBox(10, idLabel, idValueLabel);
        HBox nidBox = new HBox(10, nidLabel, nidValueLabel);

        nameBox.setSpacing(100);
        emailBox.setSpacing(105);
        addressBox.setSpacing(87);
        idBox.setSpacing(35);
        nidBox.setSpacing(116);

        centerContent.getChildren().addAll(
                nameBox, emailBox, addressBox, idBox, nidBox
        );

        System.out.println("View Profile clicked.");
    }

    public void handleViewTrains() {
        centerContent.getChildren().clear();

        List<Train> trainList = new ArrayList<>();
                for(int i=0;i<trainService.getAllTrains().size();i++){
                    trainList.add(trainService.getAllTrains().get(i));;
                }

        LocalDateTime currtime = LocalDateTime.now();
        for (int i = trainList.size() - 1; i >= 0; i--) {
            if (trainList.get(i).getDepartureTime().isBefore(currtime)) {
                trainList.remove(i);
            }
        }

        Label title = new Label("Available Trains ");
        TableView<Train> table = new TableView<>();

        title.setStyle("-fx-font-size: 15px;");

        TableColumn<Train, String> idCol = new TableColumn<>("Train ID");
        idCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getTrainId())));

        TableColumn<Train, String> nameCol = new TableColumn<>("Train Name ");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        TableColumn<Train, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getStatus()));

        TableColumn<Train, String> fromCol = new TableColumn<>("From");
        fromCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getFrom()));

        TableColumn<Train, String> toCol = new TableColumn<>("To");
        toCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTo()));

        TableColumn<Train, String> timeCol = new TableColumn<>("Depature Time");
        timeCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDepartureTimeString()));

        TableColumn<Train, String> compartmentCol = new TableColumn<>("Compartment Count");
        compartmentCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.valueOf(data.getValue().getCompartmentCount())));

        TableColumn<Train, String> masterCol = new TableColumn<>("Master Assigned");
        masterCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(ticketMasterService.getTicketMasterBytrainId(data.getValue().getTrainId())));

        table.getColumns().addAll(idCol, nameCol, statusCol, fromCol, toCol, timeCol, compartmentCol, masterCol);

        if (trainList != null) {
            table.getItems().addAll(trainList);
        }

        centerContent.getChildren().addAll(title, table);
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
                if (password.equals(loggedInTrainOperatorr.getPassword())) {
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
                        loggedInTrainOperatorr.changeName(name);
                    }
                    if (changeadress) {
                        String adress = addressField.getText();
                        loggedInTrainOperatorr.changeAddress(adress);
                    }
                    if (changepass) {
                        String newPass = newpasswordField.getText();
                        loggedInTrainOperatorr.changePassword(newPass);
                    }

                    if (changename && changeadress && changepass) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed name, address and password");
                        alert.showAndWait();
                    } else if (changename && changeadress) {
                        result.setText("Successfully changed name and address");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed name, address");
                        alert.showAndWait();
                    } else if (changename && changepass) {
                        result.setText("Successfully changed name and password");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed name and password");
                        alert.showAndWait();
                    } else if (changeadress && changepass) {
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
                    } else if (changepass) {
                        result.setText("Successfully changed password");
                        Alert alert = new Alert(Alert.AlertType.INFORMATION, "Successfully changed password");
                        alert.showAndWait();
                    } else {
                        showError("Wrong Password");
                    }
                }
            }
        });

        centerContent.getChildren().addAll(nameLabel, nameField, addressLabel, addressField, oldpasswordLabel, oldpasswordField, newpasswordLabel, newpasswordField, saveBtn, result);
    }

    @FXML
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
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root, 960, 540));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
