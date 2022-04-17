package ru.gnatyuk.sd.server.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.gnatyuk.sd.server.corp.CorpModel;
import ru.gnatyuk.sd.server.stock.StockModel;
import ru.gnatyuk.sd.server.corp.Corp;
import ru.gnatyuk.sd.server.stock.Stock;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;

@RestController
public class StockController {
    private final StockModel stockDao;
    private final CorpModel corpDao;

    private ResponseEntity<?> execute(final Callable<String> callable) {
        try {
            return new ResponseEntity<>(callable.call() + System.lineSeparator(), HttpStatus.OK);
        } catch (final IllegalArgumentException e) {
            return new ResponseEntity<>("An error occurred: " + e.getMessage() + System.lineSeparator(), HttpStatus.BAD_REQUEST);
        } catch (final Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public StockController(final StockModel stockDao, final CorpModel corpDao) {
        this.stockDao = stockDao;
        this.corpDao = corpDao;
    }

    @RequestMapping("/new-corp")
    public ResponseEntity<?> newCorp(@RequestParam("name") final String name) {
        return execute(() -> {
            corpDao.addCorp(new Corp(name));
            return "Corp '" + name + "' has been successfully added.";
        });
    }

    @RequestMapping("/new-stock")
    public ResponseEntity<?> newStock(@RequestParam("name") final String name,
                                      @RequestParam("corp") final String corpName,
                                      @RequestParam("price") final double price,
                                      @RequestParam("quantity") final long quantity) {
        return execute(() -> {
            this.stockDao.addStock(new Stock(name, corpName, quantity, price));
            return "New stock '" + name + "' by '" + corpName + "' has been successfully added.";
        });
    }

    @RequestMapping("/stock-info")
    public ResponseEntity<?> stockInfo() {
        return execute(() ->
                this.stockDao.getAllStocks().stream()
                        .map(stock -> "'" + stock.getName() + ":" + stock.getCorpName()
                                + "', quantity: " + stock.getQuantity() + ", price: " + stock.getPrice())
                        .collect(Collectors.joining(System.lineSeparator()))
        );
    }

    @RequestMapping("/change-stock")
    public ResponseEntity<?> changeStock(@RequestParam("name") final String name,
                                         @RequestParam("corp") final String corpName,
                                         @RequestParam(name = "qdelta", required = false, defaultValue = "0") final long quantityDelta,
                                         @RequestParam(name = "pdelta", required = false, defaultValue = "0") final double priceDelta) {
        return execute(() -> {
            final double price = this.stockDao.changeStock(name, corpName, quantityDelta, priceDelta);
            return "Successfully modified stock '" + name + "' by '" + corpName + "', price: " + price;
        });
    }
}
