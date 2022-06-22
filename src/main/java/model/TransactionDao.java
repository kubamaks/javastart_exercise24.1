package model;

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
                    "root", "********");
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

    public Collection<Transaction> readTransactionsFromDataBase() {
        final String sql = "SELECT * FROM transaction";
        Collection<Transaction> transactions = new ArrayList<>();
        try (Statement statement = connection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                TransactionType type = TransactionType.valueOf(resultSet.getString("type"));
                String description = resultSet.getString("description");
                BigDecimal amount = resultSet.getBigDecimal("amount");
                LocalDate date = resultSet.getDate("date").toLocalDate();
                transactions.add(new Transaction(id, type, description, amount, date));
            }
            return transactions;
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
}
