package ru.gnatyuk.sd.server.corp;

public interface CorpModel {
    void addCorp(final Corp corp);

    Corp getCorp(final String name);
}
