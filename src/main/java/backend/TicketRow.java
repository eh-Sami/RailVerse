package backend;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TicketRow {
    private final StringProperty trainName;
    private final StringProperty trainId;
    private final StringProperty seat;
    private final StringProperty departure;
    private final StringProperty price;
    private final StringProperty departurePlace;
    private final StringProperty arrivalPlace;
    private final StringProperty ticketId;
    private final StringProperty ticketStatus;
    private final StringProperty passengerId;


    public TicketRow(String trainName, String trainId, String seat, String departure, String price, String departurePlace, String arrivalPlace, String ticketId, String ticketStatus, String passengerId) {
        this.trainName = new SimpleStringProperty(trainName);
        this.trainId = new SimpleStringProperty(trainId);
        this.seat = new SimpleStringProperty(seat);
        this.departure = new SimpleStringProperty(departure);
        this.departurePlace = new SimpleStringProperty(departurePlace);
        this.price = new SimpleStringProperty(price);
        this.arrivalPlace = new SimpleStringProperty(arrivalPlace);
        this.ticketId = new SimpleStringProperty(ticketId);
        this.ticketStatus = new SimpleStringProperty(ticketStatus);
        this.passengerId = new SimpleStringProperty(passengerId);
    }

    public String getTrainName() {
        return trainName.get();
    }

    public StringProperty trainNameProperty() {
        return trainName;
    }
    public String getDeparturePlace() {
        return departurePlace.get();
    }

    public StringProperty departurePlaceProperty() {
        return departurePlace;
    }
    public String getArrivalPlace() {
        return arrivalPlace.get();
    }

    public StringProperty arrivalPlaceProperty() {
        return arrivalPlace;
    }

    public String getTicketId(){
        return ticketId.get();
    }

    public StringProperty ticketIdProperty() {
        return ticketId;
    }


    public String getTrainId() {
        return trainId.get();
    }

    public StringProperty trainIdProperty() {
        return trainId;
    }

    public String getSeat() {
        return seat.get();
    }

    public StringProperty seatProperty() {
        return seat;
    }

    public String getTicketStatus() {
        return ticketStatus.get();
    }

    public StringProperty ticketStatusProperty() {
        return ticketStatus;
    }

    public String getDeparture() {
        return departure.get();
    }

    public StringProperty passengerIdProperty() {
        return passengerId;
    }

    public String getPassengerId() {
        return passengerId.get();
    }


    public StringProperty departureProperty() {
        return departure;
    }

    public String getPrice() {
        return price.get();
    }

    public StringProperty priceProperty() {
        return price;
    }

}
