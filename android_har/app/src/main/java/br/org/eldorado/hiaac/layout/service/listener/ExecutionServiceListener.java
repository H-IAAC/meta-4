package br.org.eldorado.hiaac.layout.service.listener;


import br.org.eldorado.hiaac.layout.model.DataTrack;

public interface ExecutionServiceListener {

    DataTrack getDataTrack();

    void onRunning(long remainingTime);

    void onStopped();

    void onStarted();
}
