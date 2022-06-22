package application;

import exceptions.NoSuchIdAvailableException;
import exceptions.SqlRuntimeException;
import io.DataReader;
import model.TransactionType;
import exceptions.NoSuchOptionException;
import model.TransactionService;
import java.math.BigDecimal;
import java.util.InputMismatchException;
import java.util.concurrent.TimeUnit;

class BudgetAppControl {
    private final DataReader reader;
    private final TransactionService service;

    BudgetAppControl() {
        reader = new DataReader();
        service = new TransactionService(reader);
    }

    public void readData() {
        try {
            service.readData();
        } catch (SqlRuntimeException e) {
            System.out.println(e.getCause());
        }
        System.out.println("Data loaded successfully");
    }

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
                case PRINT_LIST_OF_ALL_TRANSACTIONS -> printListOfAllTransactions();
                case PRINT_BALANCE -> printBalance();
                case DELETE_TRANSACTION -> deleteTransaction();
                case EXIT -> exit();
                default -> System.out.println("No such option available");
            }
        } while (!option.equals(AppOption.EXIT));
    }

    private void printOptions() {
        System.out.println("Chose an option:");
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
            service.updateTransactionById();
        } catch (InputMismatchException | IndexOutOfBoundsException e) {
            System.err.println("Modifying transaction failed. Incorrect input");
        } catch (SqlRuntimeException e) {
            System.err.println(e.getCause());
        } catch (NoSuchIdAvailableException e) {
            System.err.println(e.getMessage());
        } finally {
            sleep(250);
        }
    }

    private void printTotalTransactionAmountByType(TransactionType type) {
        BigDecimal totalAmount = service.getTotalTransactionAmountByType(type);
        System.out.println("Total " + type.name() + " = " + totalAmount);
    }

    private void printListOfAllTransactions() {
        service.printAllTransactions();
    }

    private void printBalance() {
        BigDecimal balance = service.getBalance();
        System.out.println("Balance of all transactions = " + balance);
    }

    private void deleteTransaction() {
        try {
            service.deleteTransactionById();
        } catch (InputMismatchException | IndexOutOfBoundsException e) {
            System.err.println("Deleting transaction failed. Incorrect input");
        } catch (SqlRuntimeException e) {
            System.err.println(e.getCause());
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
        PRINT_LIST_OF_ALL_TRANSACTIONS(5, "PRINT LIST OF ALL TRANSACTIONS"),
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
