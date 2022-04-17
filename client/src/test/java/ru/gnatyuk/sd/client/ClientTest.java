package ru.gnatyuk.sd.client;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import ru.gnatyuk.sd.client.client.Client;
import ru.gnatyuk.sd.client.client.ClientModel;
import ru.gnatyuk.sd.client.client.ClientModelImpl;
import ru.gnatyuk.sd.client.config.LocalStockClient;
import ru.gnatyuk.sd.client.stock.StockClient;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class ClientTest {
    protected static class Stock {
        final String name;
        final String corpName;
        final double price;

        private Stock(String name, String corpName, double price) {
            this.name = name;
            this.corpName = corpName;
            this.price = price;
        }
    }

    @ClassRule
    public static GenericContainer stockWebServer
            = new FixedHostPortGenericContainer("stock:1.0-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);

    protected static final StockClient stockClient = new LocalStockClient().stockClient();

    protected static final List<String> corpNames = List.of("steam", "twitch", "yandex");

    protected static final Map<String, List<Stock>> stocks = new HashMap<>();

    static {
        stocks.put("steam", List.of(
                new Stock("s1", "steam", 200.0),
                new Stock("s2", "steam", 1000.0)
        ));
        stocks.put("twitch", List.of(
                new Stock("s2", "twitch", 20.0),
                new Stock("s3", "twitch", 100.0)
        ));
        stocks.put("yandex", List.of(
                new Stock("s3", "yandex", 1500.0),
                new Stock("s4", "yandex", 50.0)
        ));
    }

    @BeforeClass
    public static void fillMarket() {
        for (final String corpName : corpNames) {
            stockClient.doPostRequest("new-corp", Map.of("name", corpName));
        }
        stocks.forEach((corpName, stocksList) -> {
            stocksList.forEach(stock ->
                    stockClient.doPostRequest("new-stock", Map.of(
                            "name", stock.name,
                            "corp", stock.corpName,
                            "quantity", String.valueOf(10),
                            "price", String.valueOf(stock.price)
                    ))
            );
        });
    }

    private static final ClientModel clientModel = new ClientModelImpl(stockClient);;
    private static final Random random = new Random(4);

    private static String newRandomName() {
        byte[] array = new byte[16];
        random.nextBytes(array);
        return new String(array, StandardCharsets.UTF_8);
    }

    private Client newClient(final String name, final double funds) {
        clientModel.addClient(new Client(name, funds));
        final Client client = clientModel.getClient(name);
        Assert.assertEquals(client.getName(), name);
        Assert.assertEquals(client.getMoney(), funds, 0.0);
        Assert.assertEquals(client.getClientStocks(), List.of());
        return client;
    }

    @Test
    public void testNewClient() {
        newClient(newRandomName(), 10.0);
    }

    @Test
    public void testNewClientAlreadyExists() {
        final String name = newRandomName();
        newClient(name, 10.0);
        Assert.assertThrows(RuntimeException.class, () -> newClient(name, 200.0));
    }

    @Test
    public void testAddFunds() {
        final String name = newRandomName();
        newClient(name, 50.0);
        clientModel.addFunds(name, 200.0);
        Assert.assertEquals(250.0, clientModel.getClient(name).getMoney(), 0.0);
    }

    @Test
    public void testdoWorkStocks() {
        final String name = newRandomName();
        final Client client = newClient(name, 4000.0);
        clientModel.doWork(name, "s1", "steam", 10);
        Assert.assertEquals(1, client.getClientStocks().size());
        Assert.assertEquals(10, client.getClientStocks().get(0).getQuantity());
        Assert.assertEquals(2000.0, client.getMoney(), 5.0);
        Assert.assertEquals(4000.0, clientModel.totalValue(name), 5.0);
        clientModel.doWork(name, "s1", "steam", -3);
        Assert.assertEquals(1, client.getClientStocks().size());
        Assert.assertEquals(7, client.getClientStocks().get(0).getQuantity());
        Assert.assertEquals(2600.0, client.getMoney(), 5.0);
        Assert.assertEquals(4000.0, clientModel.totalValue(name), 5.0);
        clientModel.doWork(name, "s1", "steam", -7);
        Assert.assertEquals(0, client.getClientStocks().size());
        Assert.assertEquals(4000.0, client.getMoney(), 5.0);
        Assert.assertEquals(4000.0, clientModel.totalValue(name), 5.0);
    }

    @Test
    public void testBuyStocksNotEnoughStocksInMarket() {
        final String name = newRandomName();
        final Client client = newClient(name, 100.0);
        Assert.assertThrows(RuntimeException.class, () -> clientModel.doWork(name, "s1", "steam", 1000));
        Assert.assertEquals(100.0, client.getMoney(), 0.0);
    }

    @Test
    public void testBuyStocksNotEnoughStocksInClient() {
        final String name = newRandomName();
        final Client client = newClient(name, 100.0);
        Assert.assertThrows(RuntimeException.class, () -> clientModel.doWork(name, "s1", "steam", -1000));
        Assert.assertEquals(100.0, client.getMoney(), 0.0);
    }

    @Test
    public void testQueryPriceNoSuchStock() {
        Assert.assertThrows(IllegalArgumentException.class, () -> stockClient.queryPrice("no-such-stock"));
    }

    @Test
    public void testChangeStockNoSuchStock() {
        Assert.assertThrows(RuntimeException.class, () -> stockClient.changeStock("no-stock", "no-corp", 0, 0));
    }

    @Test
    public void testQueryPrice() {
        Assert.assertEquals(200.0, stockClient.queryPrice("s1:steam"), 5.0);
        Assert.assertEquals(20.0, stockClient.queryPrice("s2:twitch"), 5.0);
        Assert.assertEquals(1500.0, stockClient.queryPrice("s3:yandex"), 5.0);
        Assert.assertThrows(IllegalArgumentException.class, () -> stockClient.queryPrice("s4:steam"));
    }

    @Test
    public void testChange() {
        stockClient.changeStock("s1", "steam", -2, 100.0);
        Assert.assertEquals(300.0, stockClient.queryPrice("s1:steam"), 5.0);
    }

    @Test
    public void testChangeTooLowQuantity() {
        Assert.assertThrows(RuntimeException.class, () -> stockClient.changeStock("s3", "twitch", -1000, 0.0));
    }

    @Test
    public void testChangeTooLowPrice() {
        Assert.assertThrows(RuntimeException.class, () -> stockClient.changeStock("s3", "twitch", 0, -1000.0));
    }
}
