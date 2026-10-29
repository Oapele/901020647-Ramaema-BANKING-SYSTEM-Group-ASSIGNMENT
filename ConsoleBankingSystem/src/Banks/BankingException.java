package Banks;

//Represents a rejected banking operation or invalid account state. 
public class BankingException extends Exception
{
    // Creates a banking exception with a message suitable for the console user. 
    public BankingException(String message) 
    {
        super(message);
    }


}
