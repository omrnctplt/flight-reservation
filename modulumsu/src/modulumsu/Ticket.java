package modulumsu;

class Ticket extends BaseEntity 
{
    private final String passengerName;
    private final String passengerID;
    private final String flightNumber;
    private boolean isCheckedIn;
    private String seatNumber;

    public Ticket(String ticketCode, String passengerName, String passengerID, String flightNumber) 
    {
        super(ticketCode);
        this.passengerName = passengerName;
        this.passengerID = passengerID;
        this.flightNumber = flightNumber;
        this.isCheckedIn = false;
        this.seatNumber = null;
    }

    public String getPassengerName() 
    {
        return passengerName;
    }

    public String getPassengerID() 
    {
        return passengerID;
    }

    public String getFlightNumber() 
    {
        return flightNumber;
    }

    public boolean isCheckedIn() 
    {
        return isCheckedIn;
    }

    public void checkIn(String seatNumber) 
    {
        this.isCheckedIn = true;
        this.seatNumber = seatNumber;
    }

    public String getSeatNumber() 
    {
        return seatNumber;
    }
}