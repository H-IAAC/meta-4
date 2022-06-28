package org.tensorflow.lite.examples.transfer;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import org.eldorado.ui.treinamento.TransferLearningModelWrapper1;
import org.tensorflow.lite.examples.transfer.databinding.ActivityMainHarBinding;


public class MainActivity_har extends AppCompatActivity {
    private AppBarConfiguration appBarConfiguration;
    private ActivityMainHarBinding binding;
  /*  private CameraFragmentViewModel viewModel;
    private TransferLearningModelWrapper1 tlModel;*/


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainHarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);
   /*     tlModel = new TransferLearningModelWrapper1(binding.getRoot().getContext());
        viewModel = ViewModelProviders.of(this).get(CameraFragmentViewModel.class);
        viewModel.setTrainBatchSize(tlModel.getTrainBatchSize());*/
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_har);
        appBarConfiguration = new AppBarConfiguration.Builder(navController.getGraph()).build();
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_activity_har);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}