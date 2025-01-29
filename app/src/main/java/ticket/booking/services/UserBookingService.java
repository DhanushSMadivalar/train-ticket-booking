package ticket.booking.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import ticket.booking.entities.Ticket;
import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.util.UserServiceUtilities;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UserBookingService {

    private User user;
    private List<User> userList;
    private ObjectMapper objectMapper = new ObjectMapper();
    private static final String USERS_PATH = "app/src/main/java/ticket/booking/localDb/users.json";

    public UserBookingService() throws IOException {
        loadUserListFromFile();
    }

    public UserBookingService(User user) throws IOException {
        this.user = user;
        loadUserListFromFile();
    }

    private void loadUserListFromFile() throws IOException {
        File file = new File(USERS_PATH);
        if (!file.exists() || file.length() == 0) {
            userList = new ArrayList<>();
        } else {
            userList = objectMapper.readValue(file, new TypeReference<List<User>>() {});
        }
    }

    public void saveUserListToFile() {
        try {
            objectMapper.writeValue(new File(USERS_PATH), userList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean signUp(User newUser) {
        Optional<User> existingUser = userList.stream()
                .filter(user -> user.getName().equalsIgnoreCase(newUser.getName()))
                .findFirst();
        if (existingUser.isPresent()) {
            System.out.println("User already exists.");
            return false;
        }
        userList.add(newUser);
        saveUserListToFile();
        return true;
    }

    public boolean loginUser() {
        Optional<User> foundUser = userList.stream()
                .filter(user -> user.getName().equalsIgnoreCase(this.user.getName())
                        && UserServiceUtilities.checkPassword(this.user.getPassword(), user.getHashedPassword()))
                .findFirst();
        if (foundUser.isPresent()) {
            this.user = foundUser.get();
            return true;
        }
        return false;
    }

    public void fetchBooking() {
        if (user.getTicketBooked().isEmpty()) {
            System.out.println("No tickets booked yet.");
        } else {
            user.printTickets();
        }
    }

    public boolean bookTrainSeat(Train train, int row, int col, String source, String destination) {
        TrainService trainService;
        try {
            trainService = new TrainService();
        } catch (IOException e) {
            System.out.println("Error accessing train database.");
            return false;
        }

        List<List<Integer>> seats = trainService.viewSeats(train);

        if (row >= 0 && row < seats.size() && col >= 0 && col < seats.get(row).size() && seats.get(row).get(col) == 0) {
            seats.get(row).set(col, 1);
            trainService.updateTrainSeats(train);

            Ticket ticket = new Ticket(
                    java.util.UUID.randomUUID().toString(),
                    user.getUserId(),
                    source,
                    destination,
                    new java.util.Date(),
                    train
            );
            user.getTicketBooked().add(ticket);
            saveUserListToFile();
            System.out.println("Seat booked successfully.");
            return true;
        } else {
            System.out.println("Seat is already booked or invalid.");
            return false;
        }
    }

    public boolean cancelTicket(String ticketId) {
        Optional<Ticket> ticketToCancel = user.getTicketBooked()
                .stream()
                .filter(ticket -> ticket.getTicketId().equals(ticketId))
                .findFirst();

        if (ticketToCancel.isPresent()) {
            user.getTicketBooked().remove(ticketToCancel.get());
            saveUserListToFile();
            System.out.println("Ticket cancelled successfully.");
            return true;
        } else {
            System.out.println("Ticket not found.");
            return false;
        }
    }
}
