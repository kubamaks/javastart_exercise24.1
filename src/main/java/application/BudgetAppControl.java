package application;

import exceptions.NoResultsInResultSetException;
import exceptions.NoSuchIdAvailableException;
import exceptions.SqlRuntimeException;
import io.DataReader;
import model.TransactionType;
import exceptions.NoSuchOptionException;
import model.TransactionService;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;

class BudgetAppControl {
    private final DataReader reader = new DataReader();
    private final TransactionService service = new TransactionService(reader);

    public void applicationMenu() {
        AppOption option;
        do {
            printOptions();
            option = getOption();
            switch (option) {
                case ADD_NEW_TRANSACTION -> addNewTransaction();
                case MODIFY_TRANSACTION -> modifyTransaction();
                case PRINT_TOTAL_INCOME_AMOUNT -> printTotalTransactionAmountByType(TransactionType.INCOME);
                case PRINT_TOTAL_OUTCOME_AMOUNT -> printTotalTransactionAmountByType(TransactionType.OUTCOME);
                case PRINT_LIST_OF_TRANSACTIONS -> printListOfTransactions();
                case PRINT_BALANCE -> printBalance();
                case DELETE_TRANSACTION -> deleteTransaction();
                case EXIT -> exit();
                default -> System.out.println("No such option available");
            }
        } while (!option.equals(AppOption.EXIT));
    }

    private void printOptions() {
        System.out.println("\nChose an option:");
        for (AppOption value : AppOption.values()) {
            System.out.println(value.toString());
        }
    }

    private AppOption getOption() {
        AppOption option = null;
        boolean optionOk = false;
        while (!optionOk) {
            try {
                option = AppOption.createFromInt(reader.getInt());
                optionOk = true;
            } catch (NoSuchOptionException e) {
                System.err.println(e.getMessage());
            } catch (InputMismatchException e) {
                System.err.println("Input is not an integer, please try again");
            } finally {
                sleep(250);
            }
        }
        return option;
    }

    private void addNewTransaction() {
        try {
            service.addNewTransaction();
        } catch (InputMismatchException | IndexOutOfBoundsException e) {
            System.err.println("Adding transaction failed. Incorrect input");
        } catch (SqlRuntimeException e) {
            System.err.println(e.getCause());
        } finally {
            sleep(250);
        }
    }

    private void modifyTransaction() {
        try {
            int id = service.getTransactionId();
            service.printChosenTransaction(id);
            service.updateTransactionById(id);
        } catch (InputMismatchException | IndexOutOfBoundsException e) {
            System.err.println("Modifying transaction failed. Incorrect input");
        } catch (SqlRuntimeException e) {
            System.err.println(e.getCause());
        } catch (NoSuchIdAvailableException | NoResultsInResultSetException e) {
            System.err.println(e.getMessage());
        } finally {
            sleep(250);
        }
    }

    private void printTotalTransactionAmountByType(TransactionType type) {
        double amount = service.sumTransactionsByType(type);
        System.out.println("Total " + type.name() + " = " + amount);
    }

    private void printListOfTransactions() {
        try {
            service.printListOfTransactionsFor30Days();
        } catch (NoResultsInResultSetException e) {
            System.err.println(e.getMessage());
        } finally {
            sleep(250);
        }
    }

    private void printBalance() {
        double balance = service.getBalance();
        System.out.println(balance);
    }

    private void deleteTransaction() {
        try {
            int id = service.getTransactionId();
            service.printChosenTransaction(id);
            service.deleteTransactionById(id);
        } catch (InputMismatchException | IndexOutOfBoundsException e) {
            System.err.println("Deleting transaction failed. Incorrect input");
        } catch (SqlRuntimeException e) {
            System.err.println(e.getCause());
        } catch (NoSuchIdAvailableException | NoResultsInResultSetException e) {
            System.err.println(e.getMessage());
        } finally {
            sleep(250);
        }
    }

    static void sleep(long milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private void exit() {
        System.out.println("Bye bye");
        reader.close();
        service.closeConnection();
    }

    private enum AppOption {
        EXIT(0, "EXIT"),
        ADD_NEW_TRANSACTION(1, "ADD NEW TRANSACTION"),
        MODIFY_TRANSACTION(2, "MODIFY TRANSACTION"),
        PRINT_TOTAL_INCOME_AMOUNT(3, "PRINT TOTAL INCOME"),
        PRINT_TOTAL_OUTCOME_AMOUNT(4, "PRINT TOTAL OUTCOME"),
        PRINT_LIST_OF_TRANSACTIONS(5, "PRINT LIST OF TRANSACTIONS"),
        PRINT_BALANCE(6, "PRINT BALANCE"),
        DELETE_TRANSACTION(7, "DELETE TRANSACTION");

        private final int value;
        private final String description;

        AppOption(int value, String description) {
            this.value = value;
            this.description = description;
        }

        public int getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return value + " - " + description;
        }

        static AppOption createFromInt(int optionId) throws NoSuchOptionException {
            try {
                return AppOption.values()[optionId];
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new NoSuchOptionException("No such option: " + optionId + ", please try again.");
            } finally {
                sleep(250);
            }
        }
    }
}
