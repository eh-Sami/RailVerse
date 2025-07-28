package backend;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class TicketMasterService{
    private List<TicketMaster>  ticketMasters;
    private String filename;

    public TicketMasterService(String filename, TrainService trainService) throws IOException {
        this.filename = filename;
        this.ticketMasters = TicketMasterHandler.readTicketMasters(filename, trainService);
    }



    public TicketMaster login(String email, String password){
        for(TicketMaster tm : ticketMasters){
            if(tm.getEmail().equalsIgnoreCase(email) && tm.getPassword().equals(password)){
                return tm;
            }
        }
        System.out.println("Invalid email or password!");
        return null;
    }

    public TicketMaster forgetPassword(String email, String newPassword) {
        if(!User.emailValidityCheck(email) || !User.passwordValidityCheck(newPassword)) {
            System.out.println("Invalid email or password format!");
            return null;
        }
        for(TicketMaster tm : ticketMasters){
            if(tm.getEmail().equals(email)){
                if(tm.updatePassword(newPassword)){
                    try {
                        TicketMasterHandler.writeTicketMasters(filename, ticketMasters);
                        return tm;
                    } catch (IOException e) {
                        System.out.println("Failed to update password.");
                        return null;
                    }
                }
            }
        }
        System.out.println("Email not found!");
        return null;
    }

    public boolean addTicketMaster(TicketMaster tm) {
        for(TicketMaster t : ticketMasters){
            if(t.getEmail().equalsIgnoreCase(tm.getEmail())){
                System.out.println("Email already exists!");
                return false;
            }
        }
        ticketMasters.add(tm);
        try {
            TicketMasterHandler.writeTicketMasters(filename, ticketMasters);
            System.out.println("TicketMaster added successfully!");
            return true;
        } catch (IOException e) {
            System.out.println("Failed adding TicketMaster.");
            return false;
        }
    }

    public List<TicketMaster> getAllTicketMasters(){
        return ticketMasters;
    }

    public String getTicketMasterBytrainId(String trainId) {
        int trainsId = Integer.parseInt(trainId);
        for(TicketMaster tm : ticketMasters){
            if(tm.getAssignedTrain().getId() == trainsId){
                return String.valueOf(tm.getId());
            }
        }
        return "none";
    }
}
