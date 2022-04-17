package ru.gnatyuk.sd.client.client;

public class ClientStock {
    private final String name;
    private final String corpName;
    private long quantity;

    public ClientStock(final String name, final String corpName, final long quantity) {
        this.name = name;
        this.corpName = corpName;
        this.quantity = quantity;
    }

    public String getFullName() {
        return getFullName(this.name, this.corpName);
    }

    public String getName() {
        return name;
    }

    public String getCorpName() {
        return corpName;
    }

    public long getQuantity() {
        return quantity;
    }

    public void changeQuantity(final long quantityDelta) {
        this.quantity += quantityDelta;
    }

    public static String getFullName(final String stockName, final String corpName) {
        return stockName + ":" + corpName;
    }
}
