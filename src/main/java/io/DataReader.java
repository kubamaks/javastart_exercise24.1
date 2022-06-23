package io;

import application.Patterns;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class DataReader {
    private final Scanner sc = new Scanner(System.in);
    private final DateTimeFormatter datePattern = DateTimeFormatter.ofPattern(Patterns.DATE_FORMAT);

    public void close() {
        sc.close();
    }

    public int getInt() {
        try {
            return sc.nextInt();
        } finally {
            sc.nextLine();
        }
    }

    public String getString() {
        return sc.nextLine();
    }

    public LocalDate getDate() {
        String date = sc.nextLine();
        try {
            return LocalDate.parse(date, datePattern);
        } catch (DateTimeParseException e) {
            return LocalDate.now();
        }
    }

    public LocalDate getStrictDate() {
        String date = sc.nextLine();
        return LocalDate.parse(date, datePattern);
    }

    public double getDouble() {
        try {
            return (sc.nextDouble());
        } finally {
            sc.nextLine();
        }
    }
}


