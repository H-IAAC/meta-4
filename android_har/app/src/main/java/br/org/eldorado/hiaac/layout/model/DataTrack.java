package br.org.eldorado.hiaac.layout.model;

import androidx.annotation.NonNull;

import org.br.org.eldorado.hiaac.data.SensorFrequency;

import java.util.ArrayList;
import java.util.List;


public class DataTrack {

    private List<SensorFrequency> sensorList;
    private String label;
    private int stopTime;

    public DataTrack() {
        sensorList = new ArrayList<SensorFrequency>();
    }

    public int getStopTime() {
        return stopTime;
    }

    public void setStopTime(int stp) {
        this.stopTime = stp;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String lbl) {
        this.label = lbl;
    }

    public void addSensorList(List<SensorFrequency> sensorList) {
        this.sensorList = sensorList;
    }

    public List<SensorFrequency> getSensorList() {
        return sensorList;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof DataTrack) {
            DataTrack dt = (DataTrack) obj;
            if (dt.getLabel().equals(getLabel())) {
                return true;
            }
        }
        return false;
    }

    @NonNull
    @Override
    public String toString() {
        return getLabel() + " " + getStopTime();
    }
}
