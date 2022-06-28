package org.br.org.eldorado.hiaac.data;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import br.org.eldorado.sensoragent.model.SensorBase;

@Entity
public class SensorFrequency {
    @PrimaryKey(autoGenerate = true)
    public int id;
    @NonNull
    public String label_id;
    @NonNull
    public SensorBase sensor;
    @NonNull
    public int frequency;

    public SensorFrequency(@NonNull String label_id, @NonNull SensorBase sensor, int frequency) {
        this.label_id = label_id;
        this.sensor = sensor;
        this.frequency = frequency;
    }

    @NonNull
    public String getLabel_id() {
        return label_id;
    }
}
