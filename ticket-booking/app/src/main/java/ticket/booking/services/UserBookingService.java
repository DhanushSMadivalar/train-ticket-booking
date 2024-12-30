package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

public class UserBookingService {

    private User user;

    private List <User> userList;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    public UserBookingService(User user1) throws IOException
    {
            this.user=user1;
            LoadUserListFromFile();
    }

    public UserBookingService() throws IOException{
        LoadUserListFromFile();
    }

    public void LoadUserListFromFile() throws IOException {
        File file = new File(USERS_PATH);

        if (!file.exists() || file.length() == 0) {
            // If the file is missing or empty, initialize an empty list
            System.out.println("User list file is missing or empty. Initializing with an empty list.");
            userList = new ArrayList<>();
        } else {
            // Load the list from the file
            userList = objectMapper.readValue(file, new TypeReference<List<User>>() {});
        }
    }


    public boolean loginUser(){
        Optional<User> foundUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) && UserServiceUtilities.checkPassword(user.getPassword(),user1.getHashedPassword());
        }).findFirst();

        return foundUser.isPresent();
    }

    public boolean signUp(User user1){
        try{
            userList.add(user1);
            saveUserListToFile();
            return Boolean.TRUE;
        }catch(IOException ex){
            return Boolean.FALSE;
        }
    }

    public void saveUserListToFile() throws IOException{
        File usersFile = new File(USERS_PATH);
        objectMapper.writeValue(usersFile, userList);
    }

    public void fetchBooking(){
        Optional<User> ticketsOfUser = userList.stream().filter(user1 -> {
            return user1.getName().equals(user.getName()) &&
                    UserServiceUtilities.checkPassword(user.getPassword(),user1.getHashedPassword());
        }).findFirst();

        if(ticketsOfUser.isPresent())
            ticketsOfUser.get().printTickets();
    }

    public List<Train> getTrains(String source, String destination){
        try {
            TrainService trainService = new TrainService();
            return trainService.fetchTrain(source, destination);

        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    public List<List<Integer>> viewSeats(Train train){
        return train.getSeats();
    }

    public boolean bookTrainSeat(Train train, int row, int seat) {
        try {
            TrainService trainService = new TrainService();
            List<List<Integer>> seats = viewSeats(train);
            if(row >= 0 && row <= seats.size() && seat >= 0 && seat <= seats.get(row).size()){
                if(seats.get(row).get(seat) == 0){
                    seats.get(row).set(seat, 1);
                    train.setSeats(seats);
                    trainService.addTrain(train);
                    return true;
                }
                else{
                    return false;
                }
            }
            else {
                return false;
            }
        } catch (Exception e) {
            return Boolean.FALSE;
        }

    }

    public boolean deleteTicket(){
        Scanner s = new Scanner(System.in);
        System.out.println("Enter the ticket id to be cancelled: ");
        String ticketId = s.next();

        if(ticketId==null || ticketId.isEmpty()){
            System.out.println("Ticket ID cannot be empty");
            return Boolean.FALSE;
        }

        String finalTicketId = ticketId;
        boolean removed = user.getTicketBooked().removeIf(Ticket -> Ticket.getTicketId().equals(finalTicketId));

        if(removed){
            System.out.println("Ticket ID with " + ticketId + " has been cancelled ");
            return Boolean.TRUE;
        }
        else{
            System.out.println("No tickets were found with ID " +ticketId);
            return Boolean.FALSE;
        }
    }
}
