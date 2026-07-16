package it.unifi.ast.studytaskmanager.transaction;

import java.util.function.Supplier;

public class ImmediateTransactionManager implements TransactionManager {

    @Override
    public <T> T doInTransaction(Supplier<T> operation) {
        return operation.get();
    }
}
