package ticket.booking.entities;

import java.util.ArrayList;
import java.util.List;

public class User {
    private String name;
    private String password;
    private String hashedPassword;
    private List<Ticket> ticketBooked;
    private String userId;

    public User() {
        this.ticketBooked = new ArrayList<>();
    }

    public User(String name, String password, String hashedPassword, List<Ticket> ticketBooked, String userId) {
        this.name = name;
        this.password = password;
        this.hashedPassword = hashedPassword;
        this.ticketBooked = ticketBooked != null ? ticketBooked : new ArrayList<>();
        this.userId = userId;
    }

    public void printTickets() {
        if (ticketBooked.isEmpty()) {
            System.out.println("No tickets booked.");
        } else {
            for (Ticket ticket : ticketBooked) {
                System.out.println(ticket.getTicketInfo());
            }
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getHashedPassword() {
        return hashedPassword;
    }

    public void setHashedPassword(String hashedPassword) {
        this.hashedPassword = hashedPassword;
    }

    public List<Ticket> getTicketBooked() {
        return ticketBooked;
    }

    public void setTicketBooked(List<Ticket> ticketBooked) {
        this.ticketBooked = ticketBooked != null ? ticketBooked : new ArrayList<>();
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
