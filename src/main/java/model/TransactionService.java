package model;

import application.Patterns;
import io.DataReader;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TransactionService {
    private TransactionRepository transactionRepository;
    private TransactionDao transactionDao;
    private DataReader reader;

    public TransactionService(DataReader reader) {
        transactionDao = new TransactionDao();
        transactionRepository = new TransactionRepository();
        this.reader = reader;
    }

    public void readData() {
        transactionRepository.setTransactions(transactionDao.readTransactionsFromDataBase());
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
        int key = transactionDao.saveNewTransaction(transaction);
        System.out.println("Transaction has bee saved. id = " + key);
        readData();
    }

    public void updateTransactionById() {
        int id = getTransactionId();
        printChosenTransaction(id);
        System.out.println("\nEnter new data:");
        Transaction update = createTransactionFromInput();
        transactionDao.modifyTransactionById(id, update);
        readData();
        Transaction updatedTransaction = transactionRepository.findTransactionById(id);
        System.out.println("Following transaction has been updated: \n" + updatedTransaction);
    }

    public void deleteTransactionById() {
        int id = getTransactionId();
        printChosenTransaction(id);
        transactionDao.deleteTransactionById(id);
        System.out.println("Transaction with id = " + id + " has been deleted.");
        readData();
    }

    public BigDecimal getTotalTransactionAmountByType(TransactionType type) {
        return transactionRepository.getTransactions().stream()
                .filter(t -> t.getType().equals(type))
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public BigDecimal getBalance() {
        return transactionRepository.getTransactions().stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private int getTransactionId() {
        printAllTransactions();
        System.out.println("Choose id of transaction from list above:");
        return reader.getInt();
    }

    private void printChosenTransaction(int id) {
        Transaction transactionToModify = transactionRepository.findTransactionById(id);
        System.out.println("You have chosen: " + transactionToModify);
    }

    public void printAllTransactions() {
        System.out.println("\nList of all transactions:");
        for (Transaction transaction : transactionRepository.getTransactions()) {
            System.out.println(transaction);
        }
        System.out.println("<<<< END OF LIST >>>>\n");
    }

    public void closeConnection() {
        transactionDao.close();
    }

}
