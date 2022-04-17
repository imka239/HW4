package ru.gnatyuk.sd.client.client;

import ru.gnatyuk.sd.client.client.Client;

public interface ClientModel {
    void addClient(final Client client);

    Client getClient(final String name);

    void addFunds(final String name, final double delta);

    boolean hasStock(final String name, final String stockName, final String corpName, final long quantity);

    void doWork(final String name, final String stockName, final String corpName, final long quantity);

    double totalValue(final String name);

    double queryPrice(final String stockName);
}
