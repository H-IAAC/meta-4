package org.eldorado.ui.captura;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import org.tensorflow.lite.examples.transfer.R;

import java.util.ArrayList;

import br.org.eldorado.motionsensors.ItemActivities;
import br.org.eldorado.sensoragent.model.Accelerometer;
import br.org.eldorado.sensoragent.model.SensorBase;
import br.org.eldorado.sensorsdk.listener.SensorSDKListener;

public class CapturaFragment extends Fragment {


    ListView simpleListv;

    public static CapturaFragment newInstance() {
        return new CapturaFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.captura_fragment, container, false);

//Do something when Switch button is on/checked
        ArrayList<ItemActivities> activitieslList = new ArrayList<>();
        simpleListv = (ListView) view.findViewById(R.id.listviewActivities);
        activitieslList.add(new ItemActivities("CORRENDO", true));
        activitieslList.add(new ItemActivities("SENTADO", true));
        activitieslList.add(new ItemActivities("DEITADO", true));
        activitieslList.add(new ItemActivities("DURMINDO", true));
        CapturaFragment.MyAdapterActivities myAdapter = new CapturaFragment.MyAdapterActivities(view.getContext(), R.layout.layout_list_activities, activitieslList);
        simpleListv.setAdapter(myAdapter);
        return view;
    }


    public static class MyAdapterActivities extends ArrayAdapter<ItemActivities> {

        ArrayList<ItemActivities> activitieslList = new ArrayList<>();

        public MyAdapterActivities(Context context, int textViewResourceId, ArrayList<ItemActivities> objects) {
            super(context, textViewResourceId, objects);
            activitieslList = objects;
        }

        @Override
        public int getCount() {
            return super.getCount();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.layout_list_activities, null);
            CheckBox checkbox = null;
            TextView text = null;
            text = (TextView) v.findViewById(R.id.label);
            checkbox = (CheckBox) v.findViewById(R.id.check);
            Button b = (Button) v.findViewById(R.id.button_start_capture);
            text.setText(activitieslList.get(position).getActivityName());
            checkbox.setChecked(activitieslList.get(position).isSelected());
            Accelerometer ac = start_captureAcelerometer();


            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    int getPosition = (Integer) buttonView.getTag();  // Here we get the position that we have set for the checkbox using setTag.

                }
            });

            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        activitieslList.get(position).isSelected();
                        b.setVisibility(View.VISIBLE);

                    } else {
                        b.setVisibility(View.INVISIBLE);

                    }
                    activitieslList.get(position).setSelected(isChecked);

                    Toast.makeText(v.getContext(), activitieslList.get(position).getActivityName() + String.valueOf(isChecked), Toast.LENGTH_SHORT).show();
                }


            });


            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(v.getContext(), activitieslList.get(position).getActivityName(), Toast.LENGTH_SHORT).show();
                    ac.startSensor();


                }


            });
            ;
            return v;
        }

        Accelerometer start_captureAcelerometer() {

            Accelerometer ac = new Accelerometer();
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
                    android.util.Log.d("TAGTAGTAG", String.valueOf(ac.getValuesArray()[0]));
                }
            });
            ac.setFrequency(10);
            ac.stopSensor();
            return ac;
        }

    }


}
