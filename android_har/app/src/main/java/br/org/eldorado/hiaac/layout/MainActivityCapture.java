package br.org.eldorado.hiaac.layout;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.br.org.eldorado.hiaac.data.LabelConfig;
import org.br.org.eldorado.hiaac.data.LabelConfigViewModel;
import org.br.org.eldorado.hiaac.data.SensorFrequency;
import org.tensorflow.lite.examples.transfer.MainActivity_har;
import org.tensorflow.lite.examples.transfer.R;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import br.org.eldorado.hiaac.layout.adapter.LabelRecyclerViewAdapter;


public class MainActivityCapture extends AppCompatActivity {
    public static final int NEW_LABEL_CONFIG_ACTIVITY = 1;
    public static final int UPDATE_LABEL_CONFIG_ACTIVITY = 2;
    public static final String LABEL_CONFIG_ACTIVITY_TYPE = "label_config_type";
    public static final String LABEL_CONFIG_ACTIVITY_ID = "label_config_id";

    private FloatingActionButton mAddButton;
    private LabelConfigViewModel mLabelConfigViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.label_recycle_view);
        LabelRecyclerViewAdapter adapter = new LabelRecyclerViewAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mLabelConfigViewModel = ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()).create(LabelConfigViewModel.class);
        mLabelConfigViewModel.getAllLabels().observe(this, new Observer<List<LabelConfig>>() {
            @Override
            public void onChanged(List<LabelConfig> labels) {
                adapter.setLabelConfigs(labels);
            }
        });

        mLabelConfigViewModel.getAllSensorFrequencies().observe(this, new Observer<List<SensorFrequency>>() {
            @Override
            public void onChanged(List<SensorFrequency> sensorFrequencies) {
                Map<String, List<SensorFrequency>> sensorFrequencyMap = sensorFrequencies.stream()
                        .collect(Collectors.groupingBy(SensorFrequency::getLabel_id));
                adapter.setSensorFrequencyMap(sensorFrequencyMap);
            }
        });

        mAddButton = findViewById(R.id.add_new_label);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), LabelOptionsActivity.class);
                intent.putExtra(LABEL_CONFIG_ACTIVITY_TYPE, NEW_LABEL_CONFIG_ACTIVITY);
                startActivity(intent);
            }
        });


        FloatingActionButton mBackButton = findViewById(R.id.return_main);
        mBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity_har.class);
                startActivity(intent);

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_har);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }

}