package com.dana.models;

import com.dana.utils.IO;

import java.util.*;

/**
 * Created by dana on 10/10/16.
 */
public class AccountManager {

    private final List<Account> accounts;

    public List<Account> getAccounts() {
        return accounts;
    }

    // ensure thread-safe when iterating voer accounts during consistency check
    public AccountManager() {
        this.accounts = Collections.synchronizedList(new ArrayList<Account>());
    }

    public void addAccount(String name, long balance) {
        Account acc = new Account(name, balance);
        this.accounts.add(acc);
    }

    public Account getAccount(int idx) {
        return this.accounts.get(idx);
    }

    public void transferMoney(Account sender, Account recipient, int amount) {
        // method for money transfer
        // both add and subtract are thread safe because
        // of LongAdder usage
        if (sender.getBalance() - amount > 0) {
            Operation op = new Operation(amount, sender, recipient);

            // subtract transferred amount from sender...
            sender.substractFromBalance(amount);
            // append current operation to the first log (thread safe, using synchronized list)
            sender.addToLog(op);
            //System.out.printf("Sent " + amount + " from " + sender.getOwner() + " to " + recipient.getOwner() + "\n");

            // ...and add the amount to the receiver
            recipient.addToBalance(amount);
            // append current operation to second log (thread safe, using synchronized list)
            recipient.addToLog(op);
            //System.out.printf("Received " + amount + " to " + recipient.getOwner() + " from " + sender.getOwner() + "\n");
        } else {
            IO.print("Not enough money!");
        }
    }

    @SuppressWarnings("ALL")
    public void consistentState() {
        Boolean consistent = true;

        // manual synchronization to ensure thread-safe behaviour
        // oly one thread has acces to the code block at a time
        synchronized (accounts) {
            for (Account account : accounts) {
                // get account and balance of current account
                List<Operation> currentLog = account.getLog();
                long finalBalance = account.getBalance();

                synchronized (currentLog) {
                    for (Operation op : currentLog) {
                        // get unique id of current account
                        int opId = op.getSerialNumber();
                        Account interaction = new Account();

                        // if the owner of the current account is a receiver of money
                        // decrease current amount, else increase by the selected amount
                        if (Objects.equals(op.getRecipient().getOwner(), account.getOwner())) {
                            finalBalance -= op.getAmount();
                            interaction = op.getSender();
                        } else if (Objects.equals(op.getSender().getOwner(), account.getOwner())) {
                            finalBalance += op.getAmount();
                            interaction = op.getRecipient();
                        }

                        // get the log of the other person in the transaction
                        // and check if the current operation is there too
                        // (checking by unique id)
                        List<Operation> interLog = interaction.getLog();
                        synchronized (interLog) {
                            consistent = interLog
                                    .stream()
                                    .filter(o -> o.getSerialNumber() == opId)
                                    .findFirst()
                                    .isPresent();
                        }
                    }
                }

                // if the 2 balances don't match, system is inconsistent
                if (finalBalance != account.getInitBalance()) {
                    consistent = false;
                }
            }
        }

        if (!consistent) {
            // here system inconsistency can be treated
            IO.print("System is inconsistent.");
        }
    }
}
