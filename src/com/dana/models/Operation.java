package com.dana.models;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by dana on 10/10/16.
 */

public class Operation {
    // this is used as a seed for the generation of unique ids (thread safe)
    private static AtomicInteger seed = new AtomicInteger();

    public int getSerialNumber() {
        return serialNumber;
    }

    private int serialNumber;

    private int amount = 0;
    private Account sender = new Account();
    private Account recipient = new Account();

    private static int createID() { return seed.getAndIncrement(); }

    public Operation(int amount, Account sender, Account recipient) {
        this.serialNumber = createID();
        this.amount = amount;
        this.sender = sender;
        this.recipient = recipient;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public Account getSender() {
        return sender;
    }

    public void setSender(Account sender) {
        this.sender = sender;
    }

    public Account getRecipient() {
        return recipient;
    }

    public void setRecipient(Account recipient) {
        this.recipient = recipient;
    }
}
