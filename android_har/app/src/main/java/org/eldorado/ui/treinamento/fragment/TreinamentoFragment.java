package org.eldorado.ui.treinamento.fragment;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.commons.io.FilenameUtils;

import org.eldorado.ui.datasetsview.embeddings.ImagesColorMap;
import org.eldorado.ui.inferencia.capturerealtime.CaptureRealtime;
import org.eldorado.ui.treinamento.ImagesViewModel;
import org.eldorado.ui.treinamento.LoggingBenchmarkImages;
import org.eldorado.ui.treinamento.TransferLearningModelWrapper1;
import org.tensorflow.lite.examples.transfer.CameraFragmentViewModel;
import org.tensorflow.lite.examples.transfer.R;
import org.tensorflow.lite.examples.transfer.api.TransferLearningModel;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class TreinamentoFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {
    public final static String SELECTED_FILES = "selected_files";
    private static final int LOWER_BYTE_MASK = 0xFF;
    public static List<String> labelsArray = new ArrayList<String>();
    public static ImagesViewModel viewModel;
    public static TransferLearningModelWrapper1 tlModel;
    public static LoggingBenchmarkImages inferenceBenchmark = new LoggingBenchmarkImages("InferenceBench");
    public List<File> selectedFiles;
    public TextView texviewResult;
    String[] datasets = {"meu dataset", "Ku-har", "Extrasensory"};
    String[] embebedingArray = {"colormap images", "RMS", "UMAP"};
    String[] trainTypeArray = {"Tensorflow train", "Flower", ".."};
    RecyclerView activitiesView;
    Button buttonTrain;

    public static TreinamentoFragment newInstance() {
        return new TreinamentoFragment();
    }

    public static final void inferenceAnalyzer(Bitmap bitmap, String s, TextView texviewResult) {

        final String imageId = UUID.randomUUID().toString();

        inferenceBenchmark.startStage(imageId, "preprocess");
        String sampleClass = s; //addSampleRequests.poll();
        // //  Bitmap bitmap1 = Bitmap.createBitmap(5, 50, Bitmap.Config.RGB_565);


        //   float[][][] rgbImage =
        //    prepareCameraImage(yuvCameraImageToBitmap(imageProxy), rotationDegrees);

        float[][][] rgbImage =
                prepareCameraImage(bitmap);
        inferenceBenchmark.endStage(imageId, "preprocess");

        // Adding samples is also handled by inference thread / use case.
        // We don't use CameraX ImageCapture since it has very high latency (~650ms on Pixel 2 XL)
        // even when using .MIN_LATENCY.

        if (sampleClass != null) {
            inferenceBenchmark.startStage(imageId, "addSample");
            try {
                tlModel.addSample(rgbImage, sampleClass).get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Failed to add sample to model", e.getCause());
            } catch (InterruptedException e) {
                // no-op
            }

            viewModel.increaseNumSamples(sampleClass);
            inferenceBenchmark.endStage(imageId, "addSample");

        } else {
            // We don't perform inference when adding samples, since we should be in capture mode
            // at the time, so the inference results are not actually displayed.
            inferenceBenchmark.startStage(imageId, "predict");
            TransferLearningModel.Prediction[] predictions = tlModel.predict(rgbImage);
            if (predictions == null) {
                return;
            }
            inferenceBenchmark.endStage(imageId, "predict");

            for (TransferLearningModel.Prediction prediction : predictions) {
                viewModel.setConfidence(prediction.getClassName(), prediction.getConfidence());
                texviewResult.setText(prediction.getClassName() + " ac=" + prediction.getConfidence());
                Log.d("resultado", prediction.getClassName());
                break;
            }
        }

        inferenceBenchmark.finish(imageId);


    }

    private static float[][][] prepareCameraImage(Bitmap bitmap) {
        int modelImageSize = TransferLearningModelWrapper1.IMAGE_SIZE;

        Bitmap paddedBitmap = padToSquare(bitmap);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                paddedBitmap, modelImageSize, modelImageSize, true);

        float[][][] normalizedRgb = new float[modelImageSize][modelImageSize][3];
        for (int y = 0; y < modelImageSize; y++) {
            for (int x = 0; x < modelImageSize; x++) {
                int rgb = scaledBitmap.getPixel(x, y);


                float r = ((rgb >> 16) & LOWER_BYTE_MASK) * (1 / 255.f);
                float g = ((rgb >> 8) & LOWER_BYTE_MASK) * (1 / 255.f);
                float b = (rgb & LOWER_BYTE_MASK) * (1 / 255.f);

                normalizedRgb[y][x][0] = r;
                normalizedRgb[y][x][1] = g;
                normalizedRgb[y][x][2] = b;
            }
        }


        return normalizedRgb;
    }

    private static Bitmap padToSquare(Bitmap source) {
        int width = source.getWidth();
        int height = source.getHeight();

        int paddingX = width < height ? (height - width) / 2 : 0;
        int paddingY = height < width ? (width - height) / 2 : 0;
        Bitmap paddedBitmap = Bitmap.createBitmap(
                width + 2 * paddingX, height + 2 * paddingY, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);
        canvas.drawBitmap(source, paddingX, paddingY, null);
        return paddedBitmap;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.treinamento_fragment, container, false);
        Spinner spinDatasets = (Spinner) view.findViewById(R.id.spinner);
        spinDatasets.setOnItemSelectedListener(this);
        spinDatasets.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner, datasets));

        Spinner spinEmbebeding = (Spinner) view.findViewById(R.id.spinnerEmbebeding);
        spinEmbebeding.setOnItemSelectedListener(this);
        spinEmbebeding.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner, embebedingArray));

        Spinner spinTrainType = (Spinner) view.findViewById(R.id.spinnertypeTrain);
        spinTrainType.setOnItemSelectedListener(this);
        spinTrainType.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner, trainTypeArray));

        texviewResult = (TextView) view.findViewById(R.id.texviewResult);
        activitiesView = (RecyclerView) view.findViewById(R.id.file_selector_recycle_view_train);
        activitiesView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        FileSelectorViewAdapter adapter = new FileSelectorViewAdapter(view.getContext());
        activitiesView.setAdapter(adapter);
        selectedFiles = new ArrayList<>();


        tlModel = new TransferLearningModelWrapper1(view.getContext());
        viewModel = ViewModelProviders.of(this).get(ImagesViewModel.class);
        viewModel.setTrainBatchSize(tlModel.getTrainBatchSize());

        Button buttonTest = (Button) view.findViewById(R.id.buttonTest);
        buttonTrain = (Button) view.findViewById(R.id.buttontrain);
        buttonTrain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String selectedembebeding = String.valueOf(spinEmbebeding.getSelectedItem());
                if (selectedembebeding.contains("colormap images")) {
                    ImagesColorMap imagescolorMap = new ImagesColorMap(selectedFiles);
                    int count = 1;
                    for (File file : selectedFiles) {
                        String name = FilenameUtils.removeExtension(String.valueOf(count));
                        labelsArray.add(name);
                        ArrayList<Bitmap> arrayImages = imagescolorMap.getArrayByActivity(file);
                        for (Bitmap image : arrayImages) {
                            inferenceAnalyzer(image, name, texviewResult);
                        }
                        count = count + 1;
                    }
                }
                tlModel.enableTraining((epoch, loss) -> viewModel.setLastLoss(loss));
                if (!viewModel.getInferenceSnackbarWasDisplayed().getValue()) {
                    Snackbar.make(
                            view,
                            R.string.switch_to_inference_hint,
                            BaseTransientBottomBar.LENGTH_LONG)
                            .show();
                    viewModel.markInferenceSnackbarWasCalled();
                }
                buttonTest.setVisibility(View.VISIBLE);
            }
        });

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                CaptureRealtime captureRealtime = new CaptureRealtime(50, texviewResult);
                captureRealtime.startSensors();
            }
        });

        return view;
    }

    public void changeView(View view) {
        new Thread(new Runnable() {
            public void run() {

                Button buttonTrain = (Button) view.findViewById(R.id.buttontrain);
                buttonTrain.setText("treinando");
            }
        }).start();
    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
        String path = view.getContext().getFilesDir().getAbsolutePath() + File.separator;
        File directory = new File(view.getContext().getFilesDir().getAbsolutePath() + File.separator + "Datasets");
        FileSelectorViewAdapter adapter = new FileSelectorViewAdapter(view.getContext());
        activitiesView.setAdapter(adapter);
        selectedFiles = new ArrayList<>();
        List<File> allFiles = new ArrayList<File>();
        if (datasets[position] == "meu dataset") {
            allFiles = getActivitiesNames(directory);
            adapter.setFiles(allFiles.toArray(new File[0]));
            selectedFiles.clear();
        } else if (datasets[position] == "Ku-har") {

        }
        adapter.setListener(new FileSelectorViewAdapter.FileSelectedListener() {
            @Override
            public void onFileChecked(File file, boolean ischecked) {
                if (ischecked) {
                    selectedFiles.add(file);
                    // Toast.makeText(getView().getContext(), file.getAbsolutePath().toString(), Toast.LENGTH_LONG).show();

                } else {
                    selectedFiles.remove(file);
                }
            }
        });
        //  Toast.makeText(getView().getContext(), datasets[position] + Integer.toString(allFiles.size()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public List<File> getActivitiesNames(File directory) {
        List<File> allFilesc = new ArrayList<File>();
        File[] files1 = directory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.contains(".txt");
            }
        });

        allFilesc.addAll(Arrays.asList(files1));
        return allFilesc;
    }

}

