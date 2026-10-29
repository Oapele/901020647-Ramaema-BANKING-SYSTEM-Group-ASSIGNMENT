package Banks;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//Appends account activity records to the local transaction log. 
public final class Transactions 
{
    private static final String FILE_NAME="transactions.txt";

    // Prevents instances; transaction logging is exposed through the static method. 
    private Transactions()
    {
    }

    
     //Appends a timestamped transaction record. File errors are reported to the console
     // without interrupting the banking operation that was already attempted.
     
    public static void log(BankAccount account, String transactionType,double amount,String result)
    {
        try(FileWriter writer =new FileWriter(FILE_NAME, true))
        {
            // Use a consistent timestamp format so entries are easy to scan in the log.
            LocalDateTime now= LocalDateTime.now();
            DateTimeFormatter formatter =DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            writer.write("Date/Time: " + now.format(formatter)+ " | Account: " + account.getAccountNumber()+ " | Type: " + transactionType+ " | Amount: " + amount+ " | Result: " + result + "\n");
        }
        catch (IOException e)
        {
            System.out.println("Unable to write transaction to file.");
        }
    }

    // Returns the newest matching records first, retaining only the requested number.
    public static List<String> getRecent(BankAccount account, int limit) throws IOException
    {
        List<String> recentTransactions = new ArrayList<>();
        if (limit <= 0)
        {
            return recentTransactions;
        }

        // Match the complete account field so similar account numbers are not confused.
        String accountMarker = "| Account: " + account.getAccountNumber() + " |";
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME)))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                if (line.contains(accountMarker))
                {
                    // The file is scanned oldest-first, so discard the oldest match at capacity.
                    if (recentTransactions.size() == limit)
                    {
                        recentTransactions.remove(0);
                    }
                    recentTransactions.add(line);
                }
            }
        }

        // Convert file order to newest-first order for display in the mini-statement.
        Collections.reverse(recentTransactions);
        return recentTransactions;
    }
}
