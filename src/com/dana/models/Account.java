package com.dana.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.LongAdder;

/**
 * Created by dana on 10/10/16.
 */

public class Account {
    private List<Operation> log;
    private String owner;
    private long initBalance = 0;
    // thread safe variable that maintains a sum of variables
    // until evaluation by calling sum() (init with 0)
    private LongAdder balance;

    public Account() {
    }

    public Account(String owner, long balance) {
        this.owner = owner;
        this.initBalance = balance;
        this.balance = new LongAdder();
        this.balance.add(balance);
        // contains a synchronized (thread-safe) list
        this.log = Collections.synchronizedList(new ArrayList<Operation>());
    }

    public void addToLog(Operation op) {
        this.log.add(op);
    }

    public List<Operation> getLog() {
        return log;
    }

    public long getBalance() {
        return balance.sum();
    }

    public void addToBalance(long amount) {
        this.balance.add(amount);
    }

    public void substractFromBalance(long amount) { this.balance.add(amount * -1); }

    public long getInitBalance() {
        return initBalance;
    }

    public String getOwner() { return owner; }

    @Override
    public String toString() {
        String logString = "Account of "+ this.getOwner() + "\n---------------\n";
        for(Operation op:this.log) {
            if (Objects.equals(op.getRecipient().getOwner(), this.owner)) {
                logString += "Received: " + op.getAmount() + " from " + op.getSender().getOwner() + "\n";
            } else if (Objects.equals(op.getSender().getOwner(), this.owner)) {
                logString += "Sent: " + op.getAmount() + " to " + op.getRecipient().getOwner() + "\n";
            }
        }
        logString += "---------------\nCurrent balance: " + this.getBalance();
        return logString;
    }
}