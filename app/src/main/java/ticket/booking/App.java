package ticket.booking;

import ticket.booking.entities.Train;
import ticket.booking.entities.User;
import ticket.booking.services.TrainService;
import ticket.booking.services.UserBookingService;
import ticket.booking.util.UserServiceUtilities;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        System.out.println("Welcome to Train Booking System");
        Scanner scanner = new Scanner(System.in);
        UserBookingService userBookingService = null;

        try {
            userBookingService = new UserBookingService();
        } catch (IOException e) {
            System.out.println("Error initializing application.");
            return;
        }

        User currentUser = null;
        int option;

        do {
            System.out.println("\nChoose an option:");
            System.out.println("1. Sign Up");
            System.out.println("2. Log In");
            System.out.println("3. Fetch Bookings");
            System.out.println("4. Search Trains");
            System.out.println("5. Book a Seat");
            System.out.println("6. Cancel a Ticket");
            System.out.println("7. Exit");
            option = scanner.nextInt();

            switch (option) {
                case 1:
                    System.out.print("Enter username: ");
                    String username = scanner.next();
                    System.out.print("Enter password: ");
                    String password = scanner.next();
                    User newUser = new User(
                            username,
                            password,
                            UserServiceUtilities.hashPassword(password),
                            null,
                            java.util.UUID.randomUUID().toString()
                    );
                    if (userBookingService.signUp(newUser)) {
                        System.out.println("Sign up successful!");
                    }
                    break;

                case 2:
                    System.out.print("Enter username: ");
                    username = scanner.next();
                    System.out.print("Enter password: ");
                    password = scanner.next();
                    currentUser = new User(username, password, null, null, null);
                    try {
                        userBookingService = new UserBookingService(currentUser);
                    } catch (IOException e) {
                        System.out.println("Error during login.");
                        break;
                    }
                    if (userBookingService.loginUser()) {
                        System.out.println("Login successful!");
                    } else {
                        System.out.println("Invalid credentials.");
                    }
                    break;

                case 3:
                    if (currentUser == null) {
                        System.out.println("Please log in first.");
                        break;
                    }
                    System.out.println("Fetching your bookings...");
                    userBookingService.fetchBooking();
                    break;

                case 4:
                    System.out.print("Enter source station: ");
                    String source = scanner.next();
                    System.out.print("Enter destination station: ");
                    String destination = scanner.next();
                    try {
                        TrainService trainService = new TrainService();
                        List<Train> trains = trainService.fetchTrain(source, destination);
                        if (trains.isEmpty()) {
                            System.out.println("No trains available for this route.");
                        } else {
                            System.out.println("Available Trains:");
                            for (int i = 0; i < trains.size(); i++) {
                                Train train = trains.get(i);
                                System.out.println((i + 1) + ". " + train.getTrainInfo());
                            }
                        }
                    } catch (IOException e) {
                        System.out.println("Error fetching trains.");
                    }
                    break;

                case 5:
                    if (currentUser == null) {
                        System.out.println("Please log in first.");
                        break;
                    }

                    // Fetch train for booking
                    System.out.print("Enter the train ID to book a seat: ");
                    String trainId = scanner.next();

                    TrainService trainService;
                    Train selectedTrain = null;
                    try {
                        trainService = new TrainService();
                        List<Train> trains = trainService.fetchTrain("", ""); // Pass empty source/destination for full train list

                       /*System.out.println("Loaded Trains:");
                        for (Train train : trains) {
                            System.out.println(train.getTrainInfo());
                        }*/

                        // Find train by ID
                        for (Train train : trains) {
                            if (train.getTrainId().equalsIgnoreCase(trainId)) {
                                selectedTrain = train;
                                break;
                            }
                        }

                        if (selectedTrain == null) {
                            System.out.println("Invalid train ID. Please try again.");
                            break;
                        }
                    } catch (IOException e) {
                        System.out.println("Error fetching trains.");
                        break;
                    }

                    // Display available seats
                    List<List<Integer>> seats = trainService.viewSeats(selectedTrain);
                    System.out.println("Available seats (0 = available, 1 = booked):");
                    for (int i = 0; i < seats.size(); i++) {
                        System.out.print("Row " + (i + 1) + ": ");
                        for (int j = 0; j < seats.get(i).size(); j++) {
                            System.out.print(seats.get(i).get(j) + " ");
                        }
                        System.out.println();
                    }

                    // Seat selection
                    System.out.print("Enter row number: ");
                    int row = scanner.nextInt() - 1; // Subtract 1 to match 0-based index
                    System.out.print("Enter column number: ");
                    int col = scanner.nextInt() - 1;

                    if (row < 0 || row >= seats.size() || col < 0 || col >= seats.get(row).size()) {
                        System.out.println("Invalid seat selection. Please try again.");
                        break;
                    }

                    // Book seat
                    System.out.print("Enter source station: ");
                    String sourceStation = scanner.next();
                    System.out.print("Enter destination station: ");
                    String destinationStation = scanner.next();

                    boolean bookingSuccess = userBookingService.bookTrainSeat(selectedTrain, row, col, sourceStation, destinationStation);

                    if (bookingSuccess) {
                        System.out.println("Seat booked successfully! Enjoy your journey.");
                    } else {
                        System.out.println("Failed to book seat. It might already be booked or invalid.");
                    }
                    break;


                case 6:
                    System.out.print("Enter ticket ID to cancel: ");
                    String ticketId = scanner.next();
                    userBookingService.cancelTicket(ticketId);
                    break;

                case 7:
                    System.out.println("Exiting application. Goodbye!");
                    break;

                default:
                    System.out.println("Invalid option. Try again.");
            }
        } while (option != 7);
    }
}
