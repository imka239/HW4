package ru.gnatyuk.sd.server.stock;

public class Stock {
    private final String name;
    private final String corpName;
    private long quantity;
    private double price;

    public Stock(final String name, final String corpName, final long quantity, final double price) {
        this.name = name;
        this.corpName = corpName;
        this.quantity = quantity;
        this.price = price;
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

    public void setQuantity(final long quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(final double price) {
        this.price = price;
    }
}
