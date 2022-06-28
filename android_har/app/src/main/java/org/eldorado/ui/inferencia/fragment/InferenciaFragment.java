package org.eldorado.ui.inferencia.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.eldorado.ui.datasetsview.embeddings.ImagesColorMap;
import org.eldorado.ui.inferencia.capturerealtime.CaptureRealtime;
import org.tensorflow.lite.examples.transfer.R;

public class InferenciaFragment extends Fragment {

    public static TextView texviewResult;

    public static InferenciaFragment newInstance() {
        return new InferenciaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.inferencia_fragment, container, false);
        Button buttonStart = (Button) view.findViewById(R.id.buttonstartinferencia);
        Button buttonStop = (Button) view.findViewById(R.id.buttonstopinferencia);
        ImagesColorMap imcm = new ImagesColorMap();
        texviewResult = (TextView) view.findViewById(R.id.texviewResult);
        CaptureRealtime captureRealtime = new CaptureRealtime(50, texviewResult);
        captureRealtime.stopSensors();

        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (captureRealtime.isRunning() == false) {
                    captureRealtime.stopSensors();


                    captureRealtime.startSensors();

                    Object[] data = captureRealtime.getRowsSensors();


                }

                buttonStart.setVisibility(View.INVISIBLE);
                buttonStop.setVisibility(View.VISIBLE);
            }
        });

        buttonStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (captureRealtime.isRunning() == true) {
                    captureRealtime.stopSensors();
                    buttonStop.setVisibility(View.INVISIBLE);
                    buttonStart.setVisibility(View.VISIBLE);
                }
            }
        });


        return view;
    }


}