package Banks;

//Contract for account types that can accept loan requests. 
public interface Loanable 
{
    // Requests a loan, rejecting amounts disallowed by the implementing account. 
    void requestLoan(double amount) throws BankingException;
}
