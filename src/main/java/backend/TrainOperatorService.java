package backend;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class TrainOperatorService {
    private List<TrainOperator> trainOperatorList;
    private String filename;

    public TrainOperatorService(String filename, TrainService trainService, TicketMasterService ticketMasterService) throws Exception{
        this.filename = filename;
        this.trainOperatorList = TrainOperatorFileHandler.readTrainOperators(filename, trainService, ticketMasterService);
//        System.out.println(trainOperatorList.size() + " train operators loaded.");
    }

    public List<TrainOperator> getAllTrainOperators(){
        return new ArrayList<>(trainOperatorList);
    }
    public TrainOperator getTrainOperatorById(int id){
        for(TrainOperator trainOperator : trainOperatorList){
            if(trainOperator.getId() == id){
                return trainOperator;
            }
        }
        return null;
    }
    public TrainOperator login(String email, String password){
        for(TrainOperator trainOperator : trainOperatorList){
            if(trainOperator.getEmail().equalsIgnoreCase(email) && trainOperator.getPassword().equals(password)){
                return trainOperator;
            }
        }
        System.out.println("Invalid email or password!");
        return null;
    }
    public boolean addTrainOperator(TrainOperator trainOperator){
        for(TrainOperator o : trainOperatorList){
            if(o.getEmail().equalsIgnoreCase(trainOperator.getEmail())){
                System.out.println("Email already exists!");
                return false;
            }
        }
        trainOperatorList.add(trainOperator);
        try {
            TrainOperatorFileHandler.appendTrainOperator(filename, trainOperator);
            return true;
        }
        catch (IOException e){
            System.out.println("Failed to add train operator: " + e.getMessage());
            return false;
        }
    }
}
