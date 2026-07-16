package it.unifi.ast.studytaskmanager.transaction;

import java.util.function.Supplier;

public interface TransactionManager {

    <T> T doInTransaction(Supplier<T> operation);

    default void doInTransaction(Runnable operation) {
        doInTransaction(() -> {
            operation.run();
            return null;
        });
    }
}
