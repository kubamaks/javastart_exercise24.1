package model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;
import static java.lang.Math.*;

class Transaction {
    private Integer id;
    private TransactionType type;
    private String description;
    private BigDecimal amount;
    private LocalDate date;

    public Transaction(Integer id, TransactionType type, String description, BigDecimal amount, LocalDate date) {
        this(type, description, amount, date);
        this.id = id;
    }

    public Transaction(TransactionType type, String description, BigDecimal amount, LocalDate date) {
        this.type = type;
        this.description = description;
        this.amount = setAmountWithProperSign(type, amount);
        this.date = date;
    }

    public Integer getId() {
        return id;
    }

    public TransactionType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public static BigDecimal setAmountWithProperSign(TransactionType type, BigDecimal amount) {
        BigDecimal amountAbsoluteValue = BigDecimal.valueOf(abs(amount.doubleValue()));
        BigDecimal result;
        if (type.equals(TransactionType.INCOME)) {
            result = amountAbsoluteValue;
        } else {
            result = amountAbsoluteValue.multiply(BigDecimal.valueOf(-1L));
        }
        return result;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id)
                && type == that.type
                && Objects.equals(description, that.description)
                && Objects.equals(amount, that.amount)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, description, amount, date);
    }

    @Override
    public String toString() {
        return "transaction (id): " + id
                + " <" + type.name() + ">"
                + " date: " + date
                + ", amount: " + amount
                + ", description: " + description;
    }
}
