package com.dana;

//2. Bank accounts
//
//At a bank, we have to keep track of the balance of some accounts.
//Also, each account has an associated log (the list of records of operations performed on that account).
//Each operation record shall have a unique serial number, that is incremented for each operation performed in the bank.
//We have concurrently run transfer operations, to be executed on multiple threads.
//Each operation transfers a given amount of money from one account to some other account,
//and also appends the information about the transfer to the logs of both accounts.
//From time to time, as well as at the end of the program, a consistency check shall be executed.
//It shall verify that the amount of money in each account corresponds with the
//operations records associated to that account, and also that all operations on
//each account appear also in the logs of the source or destination of the transfer.

import com.dana.models.AccountManager;

import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    public static void main(String[] args) {
        AccountManager manager = new AccountManager();

        manager.addAccount("Maria", 9000);
        manager.addAccount("Ana", 3000);
        manager.addAccount("George", 1800);

        // Seed for random amounts
        Random values = new Random();

        // Simulating transfers from 0->1, 1->2, 2->0 and so on...
        IntStream.range(0, 9000)
                .parallel()
                .forEach(i -> {
                            manager.transferMoney(
                                    manager.getAccount(i % 3),
                                    manager.getAccount((i + 1) % 3),
                                    values.nextInt(9) * 10
                            );
                            // A consistency check is done every 10 transactions
                            if (i % 10 == 0) manager.consistentState();
                        }
                        );
        // A check is done at the end
        manager.consistentState();
    }
}
