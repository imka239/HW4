package ru.gnatyuk.sd.client.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Client {
    private final String name;
    private double money;
    private final Map<String, ClientStock> stockByName = new HashMap<>();

    public Client(final String name, final double money) {
        this.name = name;
        this.money = money;
    }

    public void changeStockCount(final String stockName, final String corpName, final long quantityDelta) {
        if (quantityDelta <= 0) {
            this.removeClientStocks(stockName, corpName, -quantityDelta);
        } else {
            this.addClientStock(new ClientStock(stockName, corpName, quantityDelta));
        }
    }

    public void addClientStock(final ClientStock stock) {
        if (!this.stockByName.containsKey(stock.getFullName())) {
            this.stockByName.put(stock.getFullName(), stock);
        } else {
            final ClientStock clientStock = this.stockByName.get(stock.getFullName());
            clientStock.changeQuantity(stock.getQuantity());
        }
    }

    public void removeClientStocks(final String stockName, final String corpName, final long quantity) {
        final ClientStock stock = this.stockByName.get(ClientStock.getFullName(stockName, corpName));
        stock.changeQuantity(-quantity);
        if (stock.getQuantity() == 0) {
            this.stockByName.remove(ClientStock.getFullName(stockName, corpName));
        }
    }

    public String getName() {
        return name;
    }

    public double getMoney() {
        return money;
    }

    public void changeMoney(final double delta) {
        this.money += delta;
    }

    public List<ClientStock> getClientStocks() {
        return new ArrayList<>(stockByName.values());
    }
}
