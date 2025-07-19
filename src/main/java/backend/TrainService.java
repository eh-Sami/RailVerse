package backend;
import java.security.spec.ECField;
import java.util.List;
import java.util.ArrayList;

public class TrainService {
    private List<Train> trainList;
    private String filename;

    public TrainService(String filename, String ticketFilePath) throws Exception { // changed on 2025-7-18 to include ticketFilePath
        this.filename = filename;
        this.trainList = TrainFileHandler.readTrains(filename, ticketFilePath);
    }

    public List<Train> getAllTrains(){
        return trainList;
    }

    public Train getTrainById(int id){
        for(Train train : trainList){
            if(train.getId() == id){
                return train;
            }
        }
        return null;
    }

    public Train getTrainByName(String name){
        for(Train train : trainList){
            if(train.getName().equalsIgnoreCase(name)){
                return train;
            }
        }
        return null;
    }

    public List<Train> getTrainsBySource(String source){
        List<Train> trains = new ArrayList<>();
        for(Train train : trainList){
            if(train.getSource().equalsIgnoreCase(source)){
                trains.add(train);
            }
        }
        return trains;
    }

    public void addTrain(Train train){
        trainList.add(train);
        try {
            TrainFileHandler.writeTrains(filename, trainList);
            System.out.printf("Trains added.");
        } catch (Exception e) {
            System.out.println("Failed adding trains.");
        }
    }

    public void saveTrains(){
        try {
            TrainFileHandler.writeTrains(filename, trainList);
        } catch(Exception e){
            System.out.println("Failed saving trains.");
        }
    }

    public boolean removeTrain(int id){
        boolean removed = trainList.removeIf(train -> train.getId() == id);
        if(removed){
            saveTrains();
        }
        return removed;
    }

    public void printAllTrains(){
        if(trainList.isEmpty()){
            System.out.println("No trains found!");
            return;
        }
        System.out.println("All trains: ");
        for(Train train : trainList){
            System.out.println(train);
            System.out.println("-----------------");
        }
    }
}

