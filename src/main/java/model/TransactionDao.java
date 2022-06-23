package model;

import exceptions.NoResultsInResultSetException;
import exceptions.SqlRuntimeException;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;

class TransactionDao {
    private final Connection connection;

    public TransactionDao() {
        try {
            this.connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/budget_app",
                    "root", "Mynewpassword1!");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection<Transaction> getAllTransactions() {
        final String sql = "SELECT * FROM transaction";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            return getTransactionsFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public int saveNewTransaction(Transaction transaction) {
        final String sql = "INSERT INTO transaction (type, description, amount, date) VALUES (?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, transaction.getType().name());
            preparedStatement.setString(2, transaction.getDescription());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setDate(4, Date.valueOf(transaction.getDate()));
            preparedStatement.executeUpdate();
            ResultSet generatedKey = preparedStatement.getGeneratedKeys();
            generatedKey.next();
            return generatedKey.getInt(1);
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public boolean modifyTransactionById(int id, Transaction transaction) {
        final String sql = "UPDATE transaction SET type = ?, description = ?, amount =?, date = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, transaction.getType().name());
            preparedStatement.setString(2, transaction.getDescription());
            preparedStatement.setBigDecimal(3, transaction.getAmount());
            preparedStatement.setDate(4, Date.valueOf(transaction.getDate()));
            preparedStatement.setInt(5, id);
            int updatedRows = preparedStatement.executeUpdate();
            return updatedRows == 1;
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public boolean deleteTransactionById(int id) {
        final String sql = "DELETE FROM transaction WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            int updatedRows = preparedStatement.executeUpdate();
            return updatedRows == 1;
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public double sumTransactionsByType(TransactionType type) {
        final String sql = "SELECT SUM(amount) AS sum FROM transaction WHERE type =?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, type.name());
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getDouble("sum");
            } else {
                throw new RuntimeException("No data has been read");
            }
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public double getBalance() {
        final String sql = "SELECT SUM(amount) AS balance FROM transaction";
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            if (resultSet.next()) {
                return resultSet.getDouble("balance");
            } else {
                throw new RuntimeException("No data has been read");
            }
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public Collection<Transaction> getAllTransactionsInTimeframe(Date from, Date to) {
        final String sql = "SELECT * FROM transaction WHERE date BETWEEN ? AND ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setDate(1, from);
            preparedStatement.setDate(2, to);
            ResultSet resultSet = preparedStatement.executeQuery();
            return getTransactionsFromResultSet(resultSet);
        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }

    public Transaction getTransactionById(int id) {
        final String sql = "SELECT * FROM transaction WHERE id = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return getTransactionFromResultSet(resultSet);
            } else {
                throw new NoResultsInResultSetException("No transaction with id = " + id + " found in DB");
            }

        } catch (SQLException e) {
            throw new SqlRuntimeException(e);
        }
    }

    private Collection<Transaction> getTransactionsFromResultSet(ResultSet resultSet) throws SQLException {
        Collection<Transaction> transactions = new ArrayList<>();
        while (resultSet.next()) {
            transactions.add(getTransactionFromResultSet(resultSet));
        }
        if (transactions.isEmpty()) {
            throw new NoResultsInResultSetException("No transactions available with selected query parameters.");
        }
        return transactions;
    }

    private Transaction getTransactionFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        TransactionType type = TransactionType.valueOf(resultSet.getString("type"));
        String description = resultSet.getString("description");
        BigDecimal amount = resultSet.getBigDecimal("amount");
        LocalDate date = resultSet.getDate("date").toLocalDate();
        return new Transaction(id, type, description, amount, date);
    }

}
