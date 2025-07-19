//import java.util.Scanner;
//
//public class Main {
//    public static void main(String[] args) {
//        Scanner sc = new Scanner(System.in);
//        try {
//            // Initialize services
//            TrainService trainService = new TrainService("src/trains.txt");
//            PassengerService passengerService = new PassengerService("src/passengers.txt", trainService);
//            TicketService ticketService = new TicketService("src/tickets.txt", passengerService);
//            TicketMasterService ticketMasterService = new TicketMasterService("src/ticketmasters.txt", trainService);
//            TrainOperatorService trainOperatorService = new TrainOperatorService("src/trainoperators.txt", trainService, ticketMasterService);
//
//            System.out.println("Welcome to Railway Management System");
//            System.out.println("Login as:");
//            System.out.println("1. Passenger");
//            System.out.println("2. Ticket Master");
//            System.out.println("3. Train Operator");
//            System.out.print("Choice: ");
//            int choice = Integer.parseInt(sc.nextLine());
//
//            System.out.print("Email: ");
//            String email = sc.nextLine();
//            System.out.print("Password: ");
//            String password = sc.nextLine();
//
//            switch (choice) {
//                case 1:
//                    Passenger p = passengerService.login(email, password);
//                    if (p != null) p.dashboard(ticketService, trainService);
//                    break;
//                case 2:
//                    TicketMaster tm = ticketMasterService.login(email, password);
//                    if (tm != null) tm.dashboard(ticketService, passengerService);
//                    break;
//                case 3:
//                    TrainOperator op = trainOperatorService.login(email, password);
//                    if (op != null) op.dashboard(trainService, ticketService);
//                    break;
//                default:
//                    System.out.println("Invalid choice.");
//            }
//
//        } catch (Exception e) {
//            System.out.println("Error: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        sc.close();
//    }
//}
