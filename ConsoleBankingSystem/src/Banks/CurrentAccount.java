package Banks;

//A current account with an overdraft facility and access to capped loans. 
public class CurrentAccount extends BankAccount implements Loanable 
{

    // Creates a current account with the supplied initial details. 
    public CurrentAccount(String accountNumber, String accountHolder, double balance) throws BankingException
    {
        super(accountNumber, accountHolder, balance);
    }

    // Deposits a positive amount into this account. 
    @Override
    public void deposit(double amount) throws BankingException 
    {

        if (amount <= 0) 
        {
            throw new BankingException("Deposit amount must be greater than zero.");
        }

        increaseBalance(amount);
    }
    
    // Withdraws a positive amount without exceeding the M2,000 overdraft limit. 
    @Override
    public void withdraw(double amount) throws BankingException 
    {

        if (amount <= 0) 
        {
            throw new BankingException("Withdrawal amount must be greater than zero.");
        }

        if (getBalance() - amount < -2000) 
        {
            throw new BankingException("Withdrawal exceeds the M2,000.00 overdraft limit.");
        }

        decreaseBalance(amount);
    }

    // Current accounts do not earn interest. 
    @Override
    public double calculateInterest() 
    {
        return 0;
    }

    // Adds a loan up to the current account's M10,000 limit. 
    @Override
    public void requestLoan(double amount) throws BankingException 
    {
        if (amount <= 0) 
        {
            throw new BankingException("Loan amount must be greater than zero.");
        }

        if (amount > 10000) 
        {
            throw new BankingException("Current account loan limit is M10,000.00.");
        }

        increaseBalance(amount);
    }

    // Checks whether withdrawal leaves the balance at or above the overdraft floor. 
    @Override
    public boolean canWithdraw(double amount)
    {
        return amount > 0 && getBalance() - amount >= -2000;
    }

    //Checks that a deposit amount is positive. 
    @Override
    public boolean canDeposit(double amount)
    {
        return amount > 0;
    }
}