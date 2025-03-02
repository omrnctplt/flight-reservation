package modulumsu;

import java.util.PriorityQueue;
import java.util.Comparator;

class Flight extends BaseEntity 
{
    private final String destination;
    private int availableSeats;
    private int ticketCounter;
    private final PriorityQueue<Passenger> waitingList;

    public Flight(String flightNumber, String destination, int availableSeats) 
    {
        super(flightNumber);
        this.destination = destination;
        this.availableSeats = availableSeats;
        this.ticketCounter = 0;
        this.waitingList = new PriorityQueue<>(Comparator.comparing(Passenger::getName));
    }

    public synchronized String generateTicketCode() 
    {
        ticketCounter++;
        return getId() + "-" + ticketCounter;
    }

    public String getDestination() 
    {
        return destination;
    }

    public synchronized int getAvailableSeats() 
    {
        return availableSeats;
    }

    public synchronized boolean reserveSeat() 
    {
        if (availableSeats > 0) 
        {
            availableSeats--;
            return true;
        }
        return false;
    }

    public synchronized void releaseSeat() 
    {
        availableSeats++;
    }

    public synchronized void addToWaitingList(Passenger passenger) 
    {
        waitingList.add(passenger);
    }

    public synchronized Passenger pollWaitingList() 
    {
        return waitingList.poll();
    }

    public synchronized PriorityQueue<Passenger> getWaitingList() 
    {
        return new PriorityQueue<>(waitingList);
    }
}