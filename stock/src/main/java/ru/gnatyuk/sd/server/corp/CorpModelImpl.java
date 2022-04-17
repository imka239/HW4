package ru.gnatyuk.sd.server.corp;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CorpModelImpl implements CorpModel {
    private final List<Corp> companies = new ArrayList<>();

    private Stream<Corp> getCorpByName(final String name) {
        return companies.stream().filter(c -> c.getName().equals(name));
    }

    @Override
    public void addCorp(final Corp corp) {
        getCorpByName(corp.getName())
                .findAny().ifPresent(c -> {
                    throw new IllegalArgumentException("Corp " + corp.getName() + " is already registered.");
                });
        this.companies.add(corp);
    }

    @Override
    public Corp getCorp(final String name) {
        return getCorpByName(name).findAny().orElseThrow();
    }
}
