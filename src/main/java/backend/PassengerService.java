package backend;


import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

public class PassengerService {
    private List<Passenger> passengerList;
    private String filename;
    private TicketService ticketService;
    private TrainService trainService;

    public PassengerService(String filename, TrainService trainService) throws Exception{
        this.filename = filename;
//        this.ticketService = ticketService;
        this.trainService = trainService;
        this.passengerList = PassengerFileHandler.readPassengers(filename, trainService);
    }

    public Passenger signUp(String name, String nid, String email, String address, String password) {
        if(!User.emailValidityCheck(email) || !User.nidValidityCheck(nid) || !User.passwordValidityCheck(password)) {
            System.out.println("Invalid input! Please ensure all fields are filled correctly.");
            return null;
        }
        int id = passengerList.size() + 1000;
        Passenger newPassenger = new Passenger(id, name, nid, email, address, password, 0, trainService, filename);
        if (addPassenger(newPassenger)) {
            return newPassenger;
        }
        return null;
    }

    public Passenger login(String email, String password){
        for(Passenger p : passengerList){
            if(p.getEmail().equals(email) && p.getPassword().equals(password)){
                return p;
            }
        }
        System.out.println("Invalid email or password!");
        return null;
    }

    public Passenger forgetPassword(String email, String newPassword) {
        if(!User.emailValidityCheck(email) || !User.passwordValidityCheck(newPassword)) {
            System.out.println("Invalid email or password format!");
            return null;
        }
        for(Passenger p : passengerList){
            if(p.getEmail().equals(email)){
                try {
                    if(p.updatePassword(newPassword)){
                        try {
                            PassengerFileHandler.writePassengers(filename, passengerList);
                            return p;
                        } catch (Exception e) {
                            System.out.println("Failed to update password.");
                            return null;
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        System.out.println("Email not found!");
        return null;
    }

    public Passenger findPassenger(Object o) {
        if(o instanceof Integer) {
            int id = (Integer) o;
            for(Passenger p : passengerList){
                if(p.getId() == id){
                    return p;
                }
            }
        } else if(o instanceof String) {
            String email = (String) o;
            for(Passenger p : passengerList){
                if(p.getEmail().equals(email)){
                    return p;
                }
            }
        }
        return null;
    }

    public boolean addPassenger(Passenger p){
        for(Passenger passenger : passengerList){
            if(passenger.getEmail().equals(p.getEmail())){
                System.out.println("This email is already registered!");
                return false;
            }
        }

        passengerList.add(p);
        try {
            PassengerFileHandler.appendPassengers(filename, p);
            System.out.println("Passenger added successfully!");
            return true;
        } catch (Exception e) {
            System.out.println("Failed adding Passenger.");
            return false;
        }
    }

    public boolean updatePassengerFine(Passenger passenger, double fine){
        passenger.setFine(fine);
        try {
            PassengerFileHandler.writePassengers(filename, passengerList);
            return true;
        } catch (Exception e) {
            System.out.println("Error updating passenger fine: " + e.getMessage());
            return false;
        }
    }

    public Passenger getPassengerById(int id) {
        for (Passenger p : passengerList) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    public List<Passenger> getAllPassengers() {
        return new ArrayList<>(passengerList);
    }
}
