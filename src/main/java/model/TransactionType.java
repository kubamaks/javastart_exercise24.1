package model;

public enum TransactionType {
    INCOME(0), OUTCOME(1);

    private final int value;

    TransactionType(int value) {
        this.value = value;
    }

    public TransactionType createFromInt(int value) {
        return TransactionType.values()[value];
    }

    public TransactionType createFromString(String type) {
        return TransactionType.valueOf(type);
    }

}
