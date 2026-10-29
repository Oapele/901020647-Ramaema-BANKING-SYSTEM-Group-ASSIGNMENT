package Banks;

// A savings account with a required minimum balance, interest, and capped loans. */
public class SavingsAccount extends BankAccount implements Loanable
{
    /** Creates a savings account with the supplied initial details. */
    public SavingsAccount(String accountNumber, String accountHolder, double balance) throws BankingException
    {
        super(accountNumber, accountHolder, balance);

    }

    /** Deposits a positive amount into this account. */
    @Override
    public void deposit(double amount) throws BankingException
    {
        if (amount <= 0) {
            throw new BankingException("Deposit amount must be greater than zero.");
        }
        increaseBalance(amount);
    }

    /** Withdraws funds only when at least M100 remains in the account. */
    @Override
    public void withdraw(double amount) throws BankingException {

        if (amount <= 0) {
            throw new BankingException("Withdrawal amount must be greater than zero.");
        }

        if (getBalance() - amount < 100) {
            throw new BankingException("Savings account must maintain a minimum balance of M100.00.");
        }

        decreaseBalance(amount);
    }

    /** Calculates simple interest at five percent of the current balance. */
    @Override
    public double calculateInterest() 
    {
        return getBalance() * 0.05;
    }

    /** Adds a loan up to the savings account's M5,000 limit. */
    @Override
    public void requestLoan(double amount) throws BankingException
    {
        if (amount <= 0) {
            throw new BankingException("Loan amount must be greater than zero.");
        }

        if (amount > 5000) {
            throw new BankingException("Savings account loan limit is M5,000.00.");
        }

        increaseBalance(amount);

    }

    /** Checks whether withdrawal leaves the required M100 minimum balance. */
    @Override
    public boolean canWithdraw(double amount)
    {
        return amount > 0 && getBalance() - amount >= 100;
    }

    /** Checks that a deposit amount is positive. */
    @Override
    public boolean canDeposit(double amount)
    {
        return amount > 0;
    }

}
