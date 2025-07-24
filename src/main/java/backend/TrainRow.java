package backend;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

public class TrainRow {

    private final Train train;
    private final SimpleStringProperty name;
    private final SimpleIntegerProperty trainId;
    private final SimpleStringProperty from;
    private final SimpleStringProperty to;
    private final SimpleStringProperty departureTimeString;
//    private final SimpleIntegerProperty availableSeats;
    private final SimpleDoubleProperty price;



    public TrainRow(Train train) {
        this.train = train;
        this.name = new SimpleStringProperty(train.getName());
        this.trainId = new SimpleIntegerProperty(Integer.parseInt(train.getTrainId()));
        this.from = new SimpleStringProperty(train.getFrom());
        this.to = new SimpleStringProperty(train.getTo());
        this.departureTimeString = new SimpleStringProperty(train.getDepartureTimeString());
//    this.availableSeats = new SimpleIntegerProperty(train.getAvailableSeats());
        this.price = new SimpleDoubleProperty(train.getPrice());
    }

    public Train getTrain() {
        return train;
    }



    public String getName() { return name.get(); }
    public int getTrainId() { return trainId.get(); }
    public String getFrom() { return from.get(); }
    public String getTo() { return to.get(); }
    public String getDepartureTimeString() { return departureTimeString.get(); }
//    public int getAvailableSeats() { return availableSeats.get(); }
    public double getPrice() { return price.get(); }
}
