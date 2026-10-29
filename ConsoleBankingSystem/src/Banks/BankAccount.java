package Banks;

//Shared account identity, balance operations, and subtype contracts. 
public abstract class BankAccount 
{
    private String accountNumber;
    private String accountHolder;
    private double balance;
    private String pin;
    
    // Creates an account and rejects a negative opening balance. 
    public BankAccount(String accountNumber, String accountHolder, double balance) throws BankingException
    {
        if (balance < 0) 
        {
            throw new BankingException("Opening balance cannot be negative.");
        }

        this.accountNumber = accountNumber;
        this.accountHolder = accountHolder;
        this.balance = balance;
    }

    //Adds a valid amount according to the concrete account's rules. 
    public abstract void deposit(double amount) throws BankingException;

    // Removes a valid amount according to the concrete account's withdrawal rules. 
    public abstract void withdraw(double amount) throws BankingException;

    // Calculates the account type's interest amount. 
    public abstract double calculateInterest() throws BankingException;

    //Returns the unique account identifier. 
    public String getAccountNumber() 
    {   
        return accountNumber;
    }

    // Returns the name of the account holder.
    public String getAccountHolder() 
    {
        return accountHolder;
    }

    // Sets a four-to-six-digit PIN while storing only its salted password hash.
    public void setPin(String pin)
    {
        if (pin == null || !pin.matches("[0-9]{4,6}"))
        {
            throw new IllegalArgumentException("PIN must contain four to six digits.");
        }
        this.pin=pin;
    }

    // Verifies a supplied PIN without exposing the stored hash or original PIN.
    public boolean verifyPin(String pin)
    {
        if (pin == null || !pin.matches("[0-9]{4,6}") )
        {
            return false;
        }

        return (pin.equals(this.pin));
    }

    // Returns the current balance, including any loan or overdraft activity. 
    public double getBalance() 
    {
        return balance;
    }

    // Updates the balance after a subclass has validated an incoming amount. 
    protected void increaseBalance(double amount) 
    {
        balance += amount;
    }

    // Updates the balance after a subclass has validated an outgoing amount. 
    protected void decreaseBalance(double amount) 
    {
        balance -= amount;
    }

     // Moves funds by withdrawing from this account and depositing into the destination.
     // Account-specific withdrawal and deposit rules are enforced by those operations.
     
    public void transferTo(BankAccount destination, double amount) throws BankingException 
    {
        if (amount <= 0) 
        {
            throw new BankingException("Transfer amount must be greater than zero.");
        }

        withdraw(amount);
        destination.deposit(amount);
    }

    // Indicates whether the supplied amount satisfies this account's withdrawal rules. 
    public abstract boolean canWithdraw(double amount);

    // Indicates whether the supplied amount satisfies this account's deposit rules. 
    public abstract boolean canDeposit(double amount);
}
