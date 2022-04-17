package ru.gnatyuk.sd.client.client;

import ru.gnatyuk.sd.client.stock.StockClient;

import java.util.HashMap;
import java.util.Map;

public class ClientModelImpl implements ClientModel {
    private final Map<String, Client> clientByName = new HashMap<>();
    private final StockClient stockClient;

    public ClientModelImpl(final StockClient stockClient) {
        this.stockClient = stockClient;
    }

    private void checkContains(final String name) {
        if (!this.clientByName.containsKey(name)) {
            throw new RuntimeException("No client with name: " + name);
        }
    }

    @Override
    synchronized public void addClient(final Client client) {
        if (this.clientByName.containsKey(client.getName())) {
            throw new RuntimeException("Client with name exists: " + client.getName());
        }
        this.clientByName.put(client.getName(), client);
    }

    @Override
    synchronized public Client getClient(final String name) {
        checkContains(name);
        return this.clientByName.get(name);
    }

    @Override
    synchronized public void addFunds(final String name, final double delta) {
        checkContains(name);
        this.clientByName.get(name).changeMoney(delta);
    }

    @Override
    synchronized public boolean hasStock(final String name, final String stockName, final String corpName, final long quantity) {
        return getClient(name).getClientStocks().stream()
                .filter(
                        stock -> stock.getCorpName().equals(corpName) && stock.getName().equals(stockName)
                )
                .mapToLong(ClientStock::getQuantity).sum()
                >= quantity;
    }

    @Override
    synchronized public void doWork(final String name, final String stockName, final String corpName, final long quantityDelta) {
        if (quantityDelta < 0 && !this.hasStock(name, stockName, corpName, -quantityDelta)) {
            throw new RuntimeException("bad stock");
        }
        final double cost = stockClient.changeStock(stockName, corpName, -quantityDelta, 0.0);
        final Client client = this.getClient(name);
        client.changeMoney(-cost * quantityDelta);
        client.changeStockCount(stockName, corpName, quantityDelta);
    }

    @Override
    public double totalValue(final String name) {
        final Client client = this.getClient(name);
        return client.getMoney() + client.getClientStocks().stream()
                .mapToDouble(stock -> stock.getQuantity() * this.stockClient.queryPrice(stock.getFullName()))
                .sum();
    }

    @Override
    public double queryPrice(final String stockName) {
        return this.stockClient.queryPrice(stockName);
    }
}
