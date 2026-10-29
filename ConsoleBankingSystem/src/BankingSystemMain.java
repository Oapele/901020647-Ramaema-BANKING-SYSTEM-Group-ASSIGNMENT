import Banks.*;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.JPasswordField;
import javax.swing.JOptionPane;

// Runs the interactive console menu for the banking system. 
public class BankingSystemMain 
{
    // Shows important operation feedback in a dialog when a graphical desktop is available.
    private static void showNotification(String title, String message, int messageType)
    {
        if (!GraphicsEnvironment.isHeadless())
        {
            JOptionPane.showMessageDialog(null, message, title, messageType);
        }
    }

    // Reads a PIN through a masked dialog when possible, with a console fallback.
    private static String readPin(Scanner scanner)
    {
        if (!GraphicsEnvironment.isHeadless())
        {
            JPasswordField pinField = new JPasswordField();
            int result = JOptionPane.showConfirmDialog(null, pinField, "Enter account PIN",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            return result == JOptionPane.OK_OPTION ? new String(pinField.getPassword()) : null;
            
        }

        System.out.print("Enter account PIN: ");
        return scanner.nextLine();
    }

    // Allows three attempts, then denies access to the selected account.
    private static boolean authenticateAccount(Scanner scanner, BankAccount account)
    {
        for (int attempt = 1; attempt <= 3; attempt++)
        {
            String enteredPin = readPin(scanner);
            if (enteredPin == null)
            {
                System.out.println("PIN entry cancelled.");
                return false;
            }
            if (account.verifyPin(enteredPin))
            {
                return true;
            }

            System.out.println("Incorrect PIN. Attempts remaining: " + (3 - attempt));
        }

        showNotification("Access Denied", "Too many incorrect PIN attempts.", JOptionPane.ERROR_MESSAGE);
        return false;
    }

    // Returns the account with the requested number, or {@code null} if none exists. 
    public static BankAccount findAccount(ArrayList<BankAccount> accounts,String accountNumber) 
    {

        for (BankAccount account : accounts) 
        {
            if (account.getAccountNumber().equals(accountNumber)) 
            {
                return account;
            }
        }

        return null;
    }

    // Reads an integer menu selection, repeating until the input is numeric. 
    public static int readInt(Scanner scanner) 
    {
        while (true) 
        {
            try 
            {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid choice. Enter a number: ");
            }
        }
    }

    // Reads a decimal amount, repeating when the input cannot be parsed. 
    public static double readDouble(Scanner scanner) 
    {
        int i=1;
        while (true) 
        {
            try 
            {
                return Double.parseDouble(scanner.nextLine());
            } 

            catch (NumberFormatException e) 
            {
                if(i!=1)
                {
                    System.out.print("Invalid amount. Enter a number: ");
                }
            }
            i++;
        }
    }


    public static void main(String[] args) 
    {

        Scanner scanner = new Scanner(System.in);

        try 
        {

            // Seed the in-memory account list used by this console session.
            ArrayList<BankAccount> accounts = new ArrayList<>();

            // These demo PINs are hashed by setPin before they are stored on each account.
            BankAccount abramAccount = new SavingsAccount("901020649", "Abram", 500);
            abramAccount.setPin("4821");
            accounts.add(abramAccount);

            BankAccount itumemlengAccount = new CurrentAccount("901020647", "Itumemleng", 500);
            itumemlengAccount.setPin("7392");
            accounts.add(itumemlengAccount);

            BankAccount motubaAccount = new CurrentAccount("901020639", "Motuba", 2333);
            motubaAccount.setPin("1658");
            accounts.add(motubaAccount);

            BankAccount katisoAccount = new CurrentAccount("901020635", "Katiso", 2333);
            katisoAccount.setPin("9034");
            accounts.add(katisoAccount);

            boolean running = true;

            while (running) 
            {

                // Each menu choice performs one operation and returns to this menu.
                System.out.println("\n      BANKING SYSTEM");
                System.out.println("1. Display all accounts");
                System.out.println("2. Deposit");
                System.out.println("3. Withdraw");
                System.out.println("4. Transfer");
                System.out.println("5. Calculate interest");
                System.out.println("6. Request loan");
                System.out.println("7. Display account details");
                System.out.println("8. Mini statement");
                System.out.println("9. Exit");

                System.out.print("Choose an option: ");
                
                int choice = readInt(scanner);

                switch (choice) 
                {

                    case 1:

                        // Keep the account list useful for lookup without exposing balances.
                        System.out.println("\n      ALL ACCOUNTS");


                        for (BankAccount account : accounts) 
                        {

                            System.out.println("Account: " + account.getAccountNumber()+ " | Holder: " + account.getAccountHolder());
                        }

                        break;

                    case 2:
                        // Deposit flow validates the account, applies the operation, and logs its result.
                        System.out.println("         DEPOSIT MENU ");
                        
                        System.out.println("1. Continue with Deposit" );
                        System.out.println("0. Back");
                        int ch = readInt(scanner);
                        if(ch==0)
                        {
                            break;
                        }

                        System.out.print("Enter Account Number: ");
                        String accountNumber = scanner.nextLine();

                        System.out.print("Enter deposit amount: ");
                        double amount = readDouble(scanner);

                        BankAccount account = findAccount(accounts, accountNumber);

                        if (account == null) 
                        {
                            System.out.println("Account not found.");
                            // Explain why the deposit cannot continue.
                            showNotification("Account Not Found", "No account matches that number.", JOptionPane.WARNING_MESSAGE);
                            break;
                        }
                        if (!authenticateAccount(scanner, account))
                        {
                            break;
                        }

                        try 
                        {
                            account.deposit(amount);

                            System.out.println("Deposit successful.");
                            System.out.println("New balance: " + account.getBalance());
                            // Confirm the completed deposit in a desktop dialog.
                            showNotification("Deposit Successful", "The deposit was completed successfully.", JOptionPane.INFORMATION_MESSAGE);

                            Transactions.log(account,"DEPOSIT",amount,"SUCCESS");
                            // Surface the account rule that rejected the deposit.
                        } catch (BankingException e) 
                        {

                            System.out.println("Deposit failed: " + e.getMessage());
                            showNotification("Deposit Failed", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                            Transactions.log(account, "DEPOSIT", amount,"FAILED "+"- "+e.getMessage());
                        }

                        break;

                    case 3:
                        // Withdrawal flow records both successful and rejected attempts.
                        System.out.println("         WITHDRAWAL MENU ");
                        
                        System.out.println("1. Continue with Withdrawal" );
                        System.out.println("0. Back");
                        ch = readInt(scanner);

                        if(ch == 0)
                        {
                            break;
                        }
                        System.out.print("Enter account number: ");

                    
                        String withdrawalAccountNumber = scanner.nextLine();

                        System.out.print("Enter withdrawal amount: ");
                        double withdrawalAmount = readDouble(scanner);

                        BankAccount withdrawalAccount = findAccount(accounts, withdrawalAccountNumber);

                        if (withdrawalAccount == null) 
                        {
                            System.out.println("Account not found.");
                            // Explain why the withdrawal cannot continue.
                            showNotification("Account Not Found", "No account matches that number.", JOptionPane.WARNING_MESSAGE);
                            break;
                        }
                        if (!authenticateAccount(scanner, withdrawalAccount))
                        {
                            break;
                        }

                        try 
                        {
                            withdrawalAccount.withdraw(withdrawalAmount);

                            System.out.println("Withdrawal successful.");
                            System.out.println("New balance: " + withdrawalAccount.getBalance());
                            // Confirm the completed withdrawal in a desktop dialog.
                            showNotification("Withdrawal Successful", "The withdrawal was completed successfully.", JOptionPane.INFORMATION_MESSAGE);
                            Transactions.log(withdrawalAccount,"WITHDRAW",withdrawalAmount,"SUCCESS");

                        } 
                        catch (BankingException e) 
                        {
                            System.out.println("Withdrawal failed: " + e.getMessage());
                            // Surface the account rule that rejected the withdrawal.
                            showNotification("Withdrawal Failed", e.getMessage(), JOptionPane.ERROR_MESSAGE);

                            Transactions.log(withdrawalAccount,"WITHDRAW",withdrawalAmount,"FAILED "+"- "+e.getMessage());
                        }

                        break;

                    case 4:
                        // A transfer updates two accounts and logs the outgoing and incoming sides.
                        System.out.println("         TRANSFER  MENU ");
                        
                        System.out.println("1. Continue making transfer" );
                        System.out.println("0. Back");
                        ch = readInt(scanner);

                        if(ch == 0)
                        {
                            break;
                        }
                        System.out.print("Enter source account number: ");
                        String sourceNumber = scanner.nextLine();

                        System.out.print("Enter destination account number: ");
                        String destinationNumber = scanner.nextLine();

                        System.out.print("Enter transfer amount: ");
                        double transferAmount = readDouble(scanner);

                        BankAccount sourceAccount = findAccount(accounts, sourceNumber);

                        BankAccount destinationAccount = findAccount(accounts, destinationNumber);

                        if (sourceAccount == null || destinationAccount == null)
                        {
                            System.out.println("One or both accounts were not found.");
                            // Identify invalid accounts before attempting to transfer funds.
                            showNotification("Account Not Found", "Check that both source and destination account numbers are valid.", JOptionPane.WARNING_MESSAGE);
                            break;
                        }
                        if (!authenticateAccount(scanner, sourceAccount))
                        {
                            break;
                        }

                        try
                        {
                            sourceAccount.transferTo(destinationAccount,transferAmount);

                            System.out.println("Transfer successful.");
                            System.out.println("Source balance: " + sourceAccount.getBalance());
                            System.out.println("Destination balance: " + destinationAccount.getBalance());
                            // Confirm the transfer after both account balances have been updated.
                            showNotification("Transfer Successful", "The transfer was completed successfully.", JOptionPane.INFORMATION_MESSAGE);

                            Transactions.log(sourceAccount,"TRANSFER BY ",transferAmount,"SUCCESS");

                            Transactions.log(destinationAccount,"TRANSFER ",transferAmount,"SUCCESS");
                        }
                        catch (BankingException e)
                        {
                            System.out.println("Transfer failed: " + e.getMessage());
                            // Explain which account rule prevented the transfer.
                            showNotification("Transfer Failed", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                            Transactions.log(sourceAccount,"TRANSFER OUT",transferAmount,"FAILED "+"- "+e.getMessage());
                        }

                        break;

                    case 5:
                        // Interest rules are implemented by each account subtype.
                        System.out.println("         INTEREST MENU ");
                        
                        System.out.println("1. Continue with Interest Calculation" );
                        System.out.println("0. Back");
                        ch = readInt(scanner);

                        if(ch == 0)
                        {
                            break;
                        }
                        

                        System.out.print("Enter account number: ");
                        String interestAccountNumber = scanner.nextLine();

                        BankAccount interestAccount = findAccount(accounts, interestAccountNumber);

                        if (interestAccount == null)
                        {
                            System.out.println("Account not found.");
                            // Explain why interest cannot be calculated for this request.
                            showNotification("Account Not Found", "No account matches that number.", JOptionPane.WARNING_MESSAGE);
                            break;
                        }
                        if (!authenticateAccount(scanner, interestAccount))
                        {
                            break;
                        }

                        try
                        {
                            double interest = interestAccount.calculateInterest();

                            System.out.println("Account: " + interestAccount.getAccountNumber());
                            System.out.println("Current balance: M" + String.format("%.2f", interestAccount.getBalance()));
                            System.out.println("Calculated interest: M" + String.format("%.2f", interest));
                            // Make the calculated amount easy to notice without scanning console output.
                            showNotification("Interest Calculated", "Calculated interest: M" + String.format("%.2f", interest), JOptionPane.INFORMATION_MESSAGE);
                        }

                        catch (BankingException e)
                        {
                            System.out.println("Interest calculation failed: "+ e.getMessage());
                            // Display the reason the account could not calculate interest.
                            showNotification("Interest Calculation Failed", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                        }

                        break;

                    case 6:
                        // Only accounts implementing Loanable can process a loan request.
                        System.out.println("         LOAN MENU ");
                        
                        System.out.println("1. Request Loan" );
                        System.out.println("0. Back");
                        ch = readInt(scanner);

                        if(ch == 0)
                        {
                            break;
                        }
                        System.out.print("Enter account number: ");
                        String loanAccountNumber = scanner.nextLine();

                        BankAccount loanAccount = findAccount(accounts, loanAccountNumber);

                        if (loanAccount == null)
                        {
                            System.out.println("Account not found.");
                            // Explain why the loan request cannot continue.
                            showNotification("Account Not Found", "No account matches that number.", JOptionPane.WARNING_MESSAGE);
                            break;
                        }
                        if (!authenticateAccount(scanner, loanAccount))
                        {
                            break;
                        }

                        System.out.print("Enter loan amount: ");
                        double loanAmount = readDouble(scanner);

                        if (!(loanAccount instanceof Loanable))
                        {
                            System.out.println("This account cannot request a loan.");
                            // Clarify that loan eligibility depends on the account type.
                            showNotification("Loan Unavailable", "This account cannot request a loan.", JOptionPane.WARNING_MESSAGE);
                            break;
                        }

                        Loanable loan = (Loanable) loanAccount;

                        try
                        {
                            loan.requestLoan(loanAmount);

                            System.out.println("Loan approved.");
                            System.out.println("Loan amount: M" + String.format("%.2f", loanAmount));

                            System.out.println("New balance: M" + String.format("%.2f", loanAccount.getBalance()));
                            // Confirm approval and show the amount added to the account.
                            showNotification("Loan Approved", "Loan amount: M" + String.format("%.2f", loanAmount), JOptionPane.INFORMATION_MESSAGE);
                            Transactions.log(loanAccount,"LOAN",loanAmount,"SUCCESS");
                        }
                        catch (BankingException e)
                        {
                            System.out.println("Loan request failed: " + e.getMessage());
                            // Explain which loan rule caused the request to be rejected.
                            showNotification("Loan Request Failed", e.getMessage(), JOptionPane.ERROR_MESSAGE);
                            Transactions.log(loanAccount,"LOAN",loanAmount,"FAILED "+"- "+e.getMessage());
                        }

                        break;

                    case 7:
                        // Display account data and its subtype-specific calculated interest.
                        System.out.println("         ACCOUNT DETAILS  ");
                        
                        System.out.println("1. Show Account Details" );
                        System.out.println("0. Back");
                        ch = readInt(scanner);

                        if(ch == 0)
                        {
                            break;
                        }
                        System.out.print("Enter account number: ");
                        String detailsAccountNumber = scanner.nextLine();

                        BankAccount detailsAccount = findAccount(accounts, detailsAccountNumber);

                        if (detailsAccount == null)
                        {
                            System.out.println("Account not found.");
                            // Explain why account details cannot be displayed.
                            showNotification("Account Not Found", "No account matches that number.", JOptionPane.WARNING_MESSAGE);
                            break;
                        }
                        if (!authenticateAccount(scanner, detailsAccount))
                        {
                            break;
                        }

                        System.out.println("\n        ACCOUNT DETAILS");

                        System.out.println("Account number: " + detailsAccount.getAccountNumber());
                        System.out.println("Account holder: " + detailsAccount.getAccountHolder());
                        System.out.println("Account type: "+ detailsAccount.getClass().getSimpleName());
                        System.out.println("Balance: M"+ String.format("%.2f", detailsAccount.getBalance()));

                        try
                        {
                            double interest = detailsAccount.calculateInterest();

                            System.out.println("Calculated interest: M"+ String.format("%.2f", interest));
                        }
                        catch (BankingException e)
                        {
                            System.out.println("Interest calculation failed: "+ e.getMessage());
                        }

                        break;

                    case 8:
                        // Show the five newest transactions recorded for the selected account.
                        System.out.print("Enter account number: ");
                        String statementAccountNumber = scanner.nextLine();
                        BankAccount statementAccount = findAccount(accounts, statementAccountNumber);

                        if (statementAccount == null)
                        {
                            System.out.println("Account not found.");
                            // Stop before reading history for an account that does not exist.
                            showNotification("Account Not Found", "No account matches that number.", JOptionPane.WARNING_MESSAGE);
                            break;
                        }
                        if (!authenticateAccount(scanner, statementAccount))
                        {
                            break;
                        }

                        try
                        {
                            List<String> recentTransactions = Transactions.getRecent(statementAccount, 5);
                            // An empty result is valid when this account has no logged activity.
                            if (recentTransactions.isEmpty())
                            {
                                System.out.println("No transactions found for this account.");
                                showNotification("Mini Statement", "No transactions have been recorded for this account.", JOptionPane.INFORMATION_MESSAGE);
                                break;
                            }

                            System.out.println("\n      RECENT TRANSACTIONS");
                            for (String transaction : recentTransactions)
                            {
                                System.out.println(transaction);
                            }
                        }
                        catch (IOException e)
                        {
                            // Distinguish an unreadable log from an account with no transactions.
                            System.out.println("Unable to read transaction history.");
                            showNotification("History Unavailable", "The transaction log could not be read.", JOptionPane.ERROR_MESSAGE);
                        }

                        break;

                    case 9:
                        
                        
                        // Confirm the exit choice so an accidental selection can be cancelled.
                        System.out.println("1. Exit?" );
                        System.out.println("0. Back");
                        ch = readInt(scanner);

                        if(ch == 0)
                        {
                            break;
                        }
                        running = false;
                        System.out.println("THANK YOU FOR USING OUR BANKING SYSTEM!!");
                        break;

                    default:
                        System.out.println("Invalid menu choice");
                }
            }

            
        } 
        catch (BankingException e) 
        {

            System.out.println(e.getMessage());
        }

        scanner.close();
    }
}