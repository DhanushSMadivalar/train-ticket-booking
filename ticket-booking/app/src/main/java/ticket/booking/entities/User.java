package ticket.booking.entities;

import java.util.List;

public class User {
    private String name;

    private String password;

    private String hashedPassword;

    private List<Ticket> TicketBooked;

    private String userId;

    public User(){}

    public User(String name, String password, String hashedPassword, List<Ticket> ticketBooked, String userId) {
        this.name = name;
        this.password = password;
        this.hashedPassword = hashedPassword;
        TicketBooked = ticketBooked;
        this.userId = userId;
    }

    public void printTickets(){
        for (int i = 0; i<TicketBooked.size(); i++){
            System.out.println(TicketBooked.get(i).getTicketInfo());
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
        return TicketBooked;
    }

    public void setTicketBooked(List<Ticket> ticketBooked) {
        TicketBooked = ticketBooked;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
