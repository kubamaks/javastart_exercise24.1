package model;

import application.Patterns;
import exceptions.NoSuchIdAvailableException;
import io.DataReader;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Collection;

public class TransactionService {
    private final TransactionDao dao = new TransactionDao();
    private final DataReader reader;

    public TransactionService(DataReader reader) {
        this.reader = reader;
    }

    private Transaction createTransactionFromInput() {
        System.out.println("Enter transaction Type: 0 - INCOME, 1 - OUTCOME");
        TransactionType type = TransactionType.values()[reader.getInt()];
        System.out.println("Enter transaction description:");
        String description = reader.getString();
        System.out.println("Enter transaction amount (sign will be adjusted automatically according to transaction type).");
        BigDecimal amount = Transaction.setAmountWithProperSign(type, BigDecimal.valueOf(reader.getDouble()));
        System.out.println("Enter transaction date - use pattern: " + Patterns.DATE_FORMAT +
                " if you type anything else the date will be set up for Today: ");
        LocalDate date = reader.getDate();
        return new Transaction(type, description, amount, date);
    }

    public void addNewTransaction() {
        Transaction transaction = createTransactionFromInput();
        int key = dao.saveNewTransaction(transaction);
        System.out.println("Transaction has bee saved. id = " + key);
    }

    public void updateTransactionById(int id) {
        Transaction update = createTransactionFromInput();
        dao.modifyTransactionById(id, update);
        Transaction updatedTransaction = dao.getTransactionById(id);
        System.out.println("Following transaction has been updated: \n" + updatedTransaction);
    }

    public void deleteTransactionById(int id) {
        boolean deleted = dao.deleteTransactionById(id);
        if (deleted) {
            System.out.println("Transaction with id = " + id + " has been deleted.");
        } else {
            throw new NoSuchIdAvailableException("No data deleted from DB (most probably there are " +
                    "no records with selected id). Please try again.");
        }
    }

    public double sumTransactionsByType(TransactionType type) {
        return dao.sumTransactionsByType(type);
    }

    public int getTransactionId() {
        printListOfTransactionsFor30Days();
        System.out.println("Please input id of transaction");
        return reader.getInt();
    }

    public double getBalance() {
        return dao.getBalance();
    }

    public void printListOfTransactionsFor30Days() {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusMonths(1L);
        String input;
        do {
            System.out.println("\n<<<< List of transactions from: " + from + " to: " + to + " >>>>\n");
            Collection<Transaction> transactions = dao.getAllTransactionsInTimeframe(Date.valueOf(from), Date.valueOf(to));
            for (Transaction t : transactions) {
                System.out.println(t);
            }
            System.out.println("<<<< END OF LIST >>>>\n");
            System.out.println("To see list of transactions from previous timeframe frame type 0." +
                    " Type anything else to move forward.");
            input = reader.getString();
            to = from;
            from = to.minusMonths(1L);
        } while (input.equals("0"));

    }

    public void printChosenTransaction(int id) {
        Transaction transactionToModify = dao.getTransactionById(id);
        System.out.println("You have chosen: " + transactionToModify);
    }

    public void closeConnection() {
        dao.close();
    }

}
