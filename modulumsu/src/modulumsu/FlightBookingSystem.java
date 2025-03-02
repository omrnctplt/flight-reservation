package modulumsu;

import java.awt.*;
import javax.swing.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.PriorityQueue;

public class FlightBookingSystem 
{
    private static final ConcurrentHashMap<String, Flight> flights = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<String, Ticket> tickets = new ConcurrentHashMap<>();
    private static final Set<String> reservedSeats = new HashSet<>();

    public static void main(String[] args) 
    {
        initializeFlights();
        SwingUtilities.invokeLater(FlightBookingSystem::createAndShowGUI);
    }

    private static void initializeFlights() 
    {
        flights.put("TK101", new Flight("TK101", "Istanbul", 10));
        flights.put("TK102", new Flight("TK102", "Ankara", 8));
        flights.put("TK103", new Flight("TK103", "Izmir", 5));
        flights.put("TK104", new Flight("TK104", "Bodrum", 12));
        flights.put("TK105", new Flight("TK105", "Antalya", 15));
        flights.put("TK106", new Flight("TK106", "Dalaman", 7));
        flights.put("TK107", new Flight("TK107", "Kayseri", 9));
        flights.put("TK108", new Flight("TK108", "Trabzon", 11));
        flights.put("TK109", new Flight("TK109", "Gaziantep", 1));
        flights.put("TK110", new Flight("TK110", "Sivas", 1));
    }

public static void createAndShowGUI() 
{
    JFrame frame = new JFrame("Airport Flight Reservation System");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(700, 500);
    frame.setResizable(false);
    frame.setLocationRelativeTo(null);

    BackgroundPanel panel = new BackgroundPanel("uçakFoto.jpg");
    panel.setLayout(null);

    Dimension buttonSize = new Dimension(150, 50);

    JButton viewFlightsButton = new JButton("View Flights");
    JButton purchaseTicketButton = new JButton("Purchase Ticket");
    JButton cancelTicketButton = new JButton("Cancel Ticket");
    JButton checkInButton = new JButton("Check-in");
    JButton viewTicketButton = new JButton("View Ticket Details");
    JButton waitingListButton = new JButton("Waiting List");

    viewFlightsButton.setBounds(100, 50, buttonSize.width, buttonSize.height);
    purchaseTicketButton.setBounds(400, 50, buttonSize.width, buttonSize.height);
    cancelTicketButton.setBounds(400, 200, buttonSize.width, buttonSize.height);
    checkInButton.setBounds(100, 200, buttonSize.width, buttonSize.height);
    viewTicketButton.setBounds(400, 350, buttonSize.width, buttonSize.height);
    waitingListButton.setBounds(100, 350, buttonSize.width, buttonSize.height);

    viewFlightsButton.addActionListener(e -> displayFlights());
    purchaseTicketButton.addActionListener(e -> purchaseTicket());
    cancelTicketButton.addActionListener(e -> cancelTicket());
    checkInButton.addActionListener(e -> checkIn());
    viewTicketButton.addActionListener(e -> viewTicket());
    waitingListButton.addActionListener(e -> displayWaitingList());

    panel.add(viewFlightsButton);
    panel.add(purchaseTicketButton);
    panel.add(cancelTicketButton);
    panel.add(checkInButton);
    panel.add(viewTicketButton);
    panel.add(waitingListButton);

    frame.add(panel);
    frame.setVisible(true);
}


    private static void displayFlights() 
    {
        StringBuilder message = new StringBuilder("Available Flights:\n");
        
        for (Flight flight : flights.values()) 
        {
            message.append("Flight No: ").append(flight.getId())
                    .append(", Destination: ").append(flight.getDestination())
                    .append(", Available Seats: ").append(flight.getAvailableSeats()).append("\n");
        }
        JOptionPane.showMessageDialog(null, message.toString(), "Flights", JOptionPane.INFORMATION_MESSAGE);
    }

    private static String assignSeat(String flightNumber, int seatIndex) {
        String seatNumber = flightNumber + "-Seat" + seatIndex;
        if (!reservedSeats.contains(seatNumber)) {
            reservedSeats.add(seatNumber);
            return seatNumber;
        }
        return null;
    }

    private static void purchaseTicket() 
    {
        String passengerName = JOptionPane.showInputDialog(null, "Enter your name:", "Purchase Ticket", JOptionPane.QUESTION_MESSAGE);
        if (passengerName == null || passengerName.isEmpty()) return;

        String passengerID = JOptionPane.showInputDialog(null, "Enter your ID number:", "Purchase Ticket", JOptionPane.QUESTION_MESSAGE);
        if (passengerID == null || passengerID.isEmpty()) return;

        JComboBox<String> flightComboBox = new JComboBox<>();
        
        for (String flightNumber : flights.keySet()) 
        {
            Flight flight = flights.get(flightNumber);
            flightComboBox.addItem(flightNumber + " - " + flight.getDestination() + " (Seats: " + flight.getAvailableSeats() + ")");
        }

        int result = JOptionPane.showConfirmDialog(null, flightComboBox, "Select a Flight", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        String selectedFlight = (String) flightComboBox.getSelectedItem();
        if (selectedFlight == null) return;

        String flightNumber = selectedFlight.split(" ")[0];
        Flight flight = flights.get(flightNumber);

        if (flight != null) 
        {
            if (flight.reserveSeat()) 
            {
                String ticketCode = flight.generateTicketCode();
                Ticket ticket = new Ticket(ticketCode, passengerName, passengerID, flightNumber);
                tickets.put(ticketCode, ticket);

                JOptionPane.showMessageDialog(null, "Ticket purchased successfully. Ticket Code: " + ticketCode, "Success", JOptionPane.INFORMATION_MESSAGE);
            } 
            else 
            {
                Passenger passenger = new Passenger(passengerName, passengerID);
                flight.addToWaitingList(passenger);
                JOptionPane.showMessageDialog(null, "No seats available. You have been added to the waiting list.", "Info", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        else 
        {
            JOptionPane.showMessageDialog(null, "Invalid flight number.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void cancelTicket() 
    {
        String ticketCode = JOptionPane.showInputDialog(null, "Enter your Ticket Code:", "Cancel Ticket", JOptionPane.QUESTION_MESSAGE);
        
        if (ticketCode == null || ticketCode.isEmpty()) return;

        Ticket ticket = tickets.get(ticketCode);
        
        if (ticket != null) 
        {
            tickets.remove(ticketCode);
            Flight flight = flights.get(ticket.getFlightNumber());
            
            if (flight != null) 
            {
                flight.releaseSeat();
                Passenger nextPassenger = flight.pollWaitingList();
                
                if (nextPassenger != null) 
                {
                    String newTicketCode = flight.generateTicketCode();
                    Ticket newTicket = new Ticket(newTicketCode, nextPassenger.getName(), nextPassenger.getId(), flight.getId());
                    tickets.put(newTicketCode, newTicket);
                    flight.reserveSeat();
                    JOptionPane.showMessageDialog(null, nextPassenger.getName() + " has been moved from the waiting list to the flight. New Ticket Code: " + newTicketCode, "Update", JOptionPane.INFORMATION_MESSAGE);
                }
            }
            JOptionPane.showMessageDialog(null, "Ticket cancelled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Invalid Ticket Code.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void checkIn() 
    {
        String ticketCode = JOptionPane.showInputDialog(null, "Enter your Ticket Code:", "Check-in", JOptionPane.QUESTION_MESSAGE);
        if (ticketCode == null || ticketCode.isEmpty()) return;

        Ticket ticket = tickets.get(ticketCode);
        if (ticket != null) 
        {
            if (!ticket.isCheckedIn()) 
            {
                Flight flight = flights.get(ticket.getFlightNumber());
                
                if (flight != null) 
                {
                    ticket.checkIn(assignSeat(ticket.getFlightNumber(), Integer.parseInt(ticketCode.split("-")[1])));
                }

                JOptionPane.showMessageDialog(null, "Check-in successful for Ticket Code: " + ticketCode + (ticket.getSeatNumber() != null ? "\nSeat Number: " + ticket.getSeatNumber() : ""), "Success", JOptionPane.INFORMATION_MESSAGE);
            } 
            else 
            {
                JOptionPane.showMessageDialog(null, "You have already checked in.", "Warning", JOptionPane.WARNING_MESSAGE);
            }
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Invalid Ticket Code.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void viewTicket() 
    {
        String ticketCode = JOptionPane.showInputDialog(null, "Enter your Ticket Code:", "View Ticket Details", JOptionPane.QUESTION_MESSAGE);
        if (ticketCode == null || ticketCode.isEmpty()) return;

        Ticket ticket = tickets.get(ticketCode);
        if (ticket != null) 
        {
            String details = "Ticket Code: " + ticket.getId() + "\n" +
                    "Passenger Name: " + ticket.getPassengerName() + "\n" +
                    "Passenger ID: " + ticket.getPassengerID() + "\n" +
                    "Flight Number: " + ticket.getFlightNumber() + "\n" +
                    "Check-in Status: " + (ticket.isCheckedIn() ? "Checked-in" : "Not Checked-in") + "\n" +
                    (ticket.isCheckedIn() && ticket.getSeatNumber() != null ? "Seat Number: " + ticket.getSeatNumber() : "");

            JOptionPane.showMessageDialog(null, details, "Ticket Details", JOptionPane.INFORMATION_MESSAGE);
        } 
        else 
        {
            JOptionPane.showMessageDialog(null, "Invalid Ticket Code.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private static void displayWaitingList() 
    {
        StringBuilder message = new StringBuilder("Waiting List:\n");
        
        for (Flight flight : flights.values()) 
        {
            PriorityQueue<Passenger> waitingList = flight.getWaitingList();
            if (!waitingList.isEmpty()) 
            {
                message.append("Flight No: ").append(flight.getId()).append("\n");
                
                int position = 1;
                
                for (Passenger passenger : waitingList) 
                {
                    message.append(position++).append(") ").append(passenger.getName()).append(" (ID: ").append(passenger.getId()).append(")\n");
                }
                message.append("\n");
            }
        }
        JOptionPane.showMessageDialog(null, message.toString(), "Waiting List", JOptionPane.INFORMATION_MESSAGE);
    }
}