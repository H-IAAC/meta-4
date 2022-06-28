package org.eldorado.ui.inferencia.capturerealtime;

import android.graphics.Bitmap;
import android.widget.TextView;

import org.apache.commons.lang3.ArrayUtils;
import org.eldorado.ui.datasetsview.embeddings.ImagesColorMap;
import org.eldorado.ui.treinamento.fragment.TreinamentoFragment;

import java.util.ArrayList;
import java.util.List;

import br.org.eldorado.sensoragent.model.Accelerometer;
import br.org.eldorado.sensoragent.model.Gyroscope;
import br.org.eldorado.sensoragent.model.SensorBase;
import br.org.eldorado.sensorsdk.listener.SensorSDKListener;

public class CaptureRealtime {
    List<float[]> rowsAcc = new ArrayList<float[]>();
    List<float[]> rowsGyro = new ArrayList<float[]>();
    Gyroscope gr = new Gyroscope();
    Accelerometer ac = new Accelerometer();
    List<float[]> allSensors = new ArrayList<float[]>();
    TextView texviewResult;
    boolean running = false;
    Object[] rowsSensors;

    public CaptureRealtime(int freq, TextView texviewResult) {
        accelerometer();
        gyroscopeS();
        setFrequency(freq);
        this.texviewResult = texviewResult;

    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;

    }

    public Object[] getRowsSensors() {
        return rowsSensors;
    }

    void accelerometer() {

        ac.registerListener(new SensorSDKListener() {
            @Override
            public void onSensorStarted(SensorBase sensorBase) {
                android.util.Log.d("TAGTAGTAG", "sensor started");
            }

            @Override
            public void onSensorStopped(SensorBase sensorBase) {
                android.util.Log.d("TAGTAGTAG", "sensor stoped");
            }

            @Override
            public void onSensorChanged(SensorBase sensorBase) {
                rowsAcc.add(ac.getValuesArray());
                allSensors.add(ArrayUtils.addAll(ac.getValuesArray(), gr.getValuesArray()));
                android.util.Log.d("TAGTAGTAG", String.valueOf(ac.getValuesArray()[0]));
                if (allSensors.size() == 50) {
                    getResult();
                }
            }
        });

    }

    public void getResult() {
        new Thread(new Runnable() {
            public void run() {
                mixSensors();
                allSensors = new ArrayList<float[]>();
                android.util.Log.d("TAGTAGTAG1", "XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
            }
        }).start();
    }


    void gyroscopeS() {


        gr.registerListener(new SensorSDKListener() {
            @Override
            public void onSensorStarted(SensorBase sensorBase) {
                android.util.Log.d("TAGTAGTAG", "sensor started");
            }

            @Override
            public void onSensorStopped(SensorBase sensorBase) {
                android.util.Log.d("TAGTAGTAG", "sensor stoped");
            }

            @Override
            public void onSensorChanged(SensorBase sensorBase) {
                rowsGyro.add(gr.getValuesArray());
                android.util.Log.d("TAGTAGTAG", String.valueOf(gr.getValuesArray()[0]));
            }
        });


    }

    void mixSensors() {

        Bitmap bitmap = ImagesColorMap.getBitmapObject(allSensors);
        TreinamentoFragment.inferenceAnalyzer(bitmap, null, this.texviewResult);
    }


    public void startSensors() {
        gr.startSensor();
        ac.startSensor();
        this.running = true;
    }

    public void stopSensors() {
        gr.stopSensor();
        ac.stopSensor();
        this.running = false;
    }

    public void setFrequency(int freq) {
        gr.setFrequency(freq);
        ac.setFrequency(freq);
    }


}
