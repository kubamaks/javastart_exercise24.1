package model;

import exceptions.NoSuchIdAvailableException;

import java.util.Collection;

class TransactionRepository {

    private Collection<Transaction> transactions;

    public Collection<Transaction> getTransactions() {
        return transactions;
    }

    public void setTransactions(Collection<Transaction> transactions) {
        this.transactions = transactions;
    }

    public Transaction findTransactionById(int id) {
        for (Transaction transaction : transactions) {
            if (transaction.getId() == id) {
                return transaction;
            }
        }
        throw new NoSuchIdAvailableException("No such transaction Id available.");
    }
}

