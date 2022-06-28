/* Copyright 2019 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package org.tensorflow.lite.examples.transfer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.SystemClock;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Rational;
import android.util.Size;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.camera.core.CameraX;
import androidx.camera.core.CameraX.LensFacing;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageAnalysis.ImageReaderMode;
import androidx.camera.core.ImageAnalysisConfig;
import androidx.camera.core.Preview;
import androidx.camera.core.PreviewConfig;
import androidx.databinding.BindingAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.eldorado.ui.menuP.help.HelpDialog;
import org.eldorado.ui.treinamento.TransferLearningModelWrapper1;
import org.tensorflow.lite.examples.transfer.CameraFragmentViewModel.TrainingState;
import org.tensorflow.lite.examples.transfer.api.TransferLearningModel.Prediction;
import org.tensorflow.lite.examples.transfer.databinding.CameraFragmentBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;

import br.org.eldorado.motionsensors.CSVReader;


/**
 * The main fragment of the classifier.
 * <p>
 * Camera functionality (through CameraX) is heavily based on the official example:
 * https://github.com/android/camera/tree/master/CameraXBasic.
 */
public class CameraFragment extends Fragment {

    private static final int LOWER_BYTE_MASK = 0xFF;

    private static final String TAG = CameraFragment.class.getSimpleName();

    private static final LensFacing LENS_FACING = LensFacing.BACK;

    private static final int LONG_PRESS_DURATION = 500;
    private static final int SAMPLE_COLLECTION_DELAY = 300;
    private final Handler sampleCollectionHandler = new Handler(Looper.getMainLooper());
    private final HelpDialog helpDialog = new HelpDialog();
    // When the user presses the "add sample" button for some class,
    // that class will be added to this queue. It is later extracted by
    // InferenceThread and processed.
    private final ConcurrentLinkedQueue<String> addSampleRequests = new ConcurrentLinkedQueue<>();
    private final LoggingBenchmark inferenceBenchmark = new LoggingBenchmark("InferenceBench");
    public String classId;
    public final View.OnClickListener onAddSampleClickListener =
            view -> {
                String className = getClassNameFromResourceId(view.getId());
                classId = className;
                addSampleRequests.add(className);
            };
    private Context context;
    private TextureView viewFinder;
    private Integer viewFinderRotation = null;
    private Size bufferDimens = new Size(0, 0);
    private Size viewFinderDimens = new Size(0, 0);
    private CameraFragmentViewModel viewModel;
    private TransferLearningModelWrapper1 tlModel;
    private long sampleCollectionButtonPressedTime;
    private boolean isCollectingSamples = false;
    public final View.OnTouchListener onAddSampleTouchListener =
            (view, motionEvent) -> {
                switch (motionEvent.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        isCollectingSamples = true;
                        sampleCollectionButtonPressedTime = SystemClock.uptimeMillis();
                        sampleCollectionHandler.post(
                                new Runnable() {
                                    @Override
                                    public void run() {
                                        long timePressed =
                                                SystemClock.uptimeMillis() - sampleCollectionButtonPressedTime;
                                        view.findViewById(view.getId()).performClick();
                                        if (timePressed < LONG_PRESS_DURATION) {
                                            sampleCollectionHandler.postDelayed(this, LONG_PRESS_DURATION);
                                        } else if (isCollectingSamples) {
                                            String className = getClassNameFromResourceId(view.getId());
                                            viewModel.setNumCollectedSamples(
                                                    viewModel.getNumSamples().getValue().get(className) + 1);
                                            sampleCollectionHandler.postDelayed(this, SAMPLE_COLLECTION_DELAY);
                                            viewModel.setSampleCollectionLongPressed(true);
                                        }
                                    }
                                });
                        break;
                    case MotionEvent.ACTION_UP:
                        sampleCollectionHandler.removeCallbacksAndMessages(null);
                        isCollectingSamples = false;
                        viewModel.setSampleCollectionLongPressed(false);
                        break;
                    default:
                        break;
                }
                return true;
            };
    private String class_idT = "2";
    private final ImageAnalysis.Analyzer inferenceAnalyzer =
            (imageProxy, rotationDegrees) -> {
                final String imageId = UUID.randomUUID().toString();

                inferenceBenchmark.startStage(imageId, "preprocess");
                String sampleClass = addSampleRequests.poll();
                Bitmap bitmap1 = Bitmap.createBitmap(5, 50, Bitmap.Config.RGB_565);
                try {

                    if (sampleClass != null) {
                        if (sampleClass == "1") {
                            bitmap1 = getarray("data/eldorado/X_CORRENDO.txt");

                        }
                        if (sampleClass == "2") {
                            bitmap1 = getarray("data/eldorado/X_CAMINHANDO.txt");
                        }

                        if (sampleClass == "3") {
                            bitmap1 = getarray("data/eldorado/X_DEITADO.txt");
                        }
                        if (sampleClass == "4") {
                            bitmap1 = getarray("data/eldorado/X_SENTADO.txt");
                        }


                    } else {

                        if (class_idT == "1") {
                            bitmap1 = getarray("data/eldorado/X_CORRENDO.txt");

                        }
                        if (class_idT == "2") {
                            bitmap1 = getarray("data/eldorado/X_CAMINHANDO.txt");
                        }

                        if (class_idT == "3") {
                            bitmap1 = getarray("data/eldorado/X_DEITADO.txt");
                        }
                        if (class_idT == "4") {
                            bitmap1 = getarray("data/eldorado/X_SENTADO.txt");
                        }

                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }


                //   float[][][] rgbImage =
                //    prepareCameraImage(yuvCameraImageToBitmap(imageProxy), rotationDegrees);

                float[][][] rgbImage =
                        prepareCameraImage(bitmap1, rotationDegrees);
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
                    Prediction[] predictions = tlModel.predict(rgbImage);
                    if (predictions == null) {
                        return;
                    }
                    inferenceBenchmark.endStage(imageId, "predict");

                    for (Prediction prediction : predictions) {
                        viewModel.setConfidence(prediction.getClassName(), prediction.getConfidence());
                    }
                }

                inferenceBenchmark.finish(imageId);
            };

    private static Integer getDisplaySurfaceRotation(Display display) {
        if (display == null) {
            return null;
        }

        switch (display.getRotation()) {
            case Surface.ROTATION_0:
                return 0;
            case Surface.ROTATION_90:
                return 90;
            case Surface.ROTATION_180:
                return 180;
            case Surface.ROTATION_270:
                return 270;
            default:
                return null;
        }
    }

    /**
     * Normalizes a camera image to [0; 1], cropping it to size expected by the model and adjusting
     * for camera rotation.
     */
    private static float[][][] prepareCameraImage(Bitmap bitmap, int rotationDegrees) {
        int modelImageSize = TransferLearningModelWrapper1.IMAGE_SIZE;

        Bitmap paddedBitmap = padToSquare(bitmap);
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(
                paddedBitmap, modelImageSize, modelImageSize, true);

        Matrix rotationMatrix = new Matrix();
        rotationMatrix.postRotate(rotationDegrees);
        Bitmap rotatedBitmap = Bitmap.createBitmap(
                scaledBitmap, 0, 0, modelImageSize, modelImageSize, rotationMatrix, false);

        float[][][] normalizedRgb = new float[modelImageSize][modelImageSize][3];
        for (int y = 0; y < modelImageSize; y++) {
            for (int x = 0; x < modelImageSize; x++) {
                int rgb = rotatedBitmap.getPixel(x, y);


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
                width + 2 * paddingX, height + 2 * paddingY, Config.ARGB_8888);
        Canvas canvas = new Canvas(paddedBitmap);
        canvas.drawARGB(0xFF, 0xFF, 0xFF, 0xFF);
        canvas.drawBitmap(source, paddingX, paddingY, null);
        return paddedBitmap;
    }

    @BindingAdapter({"captureMode", "inferenceText", "captureText"})
    public static void setClassSubtitleText(
            TextView view, boolean captureMode, Float inferenceText, Integer captureText) {
        if (captureMode) {
            view.setText(captureText != null ? Integer.toString(captureText) : "0");
        } else {
            view.setText(
                    String.format(Locale.getDefault(), "%.2f", inferenceText != null ? inferenceText : 0.f));
        }
    }

    @BindingAdapter({"android:visibility"})
    public static void setViewVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);

    }

    @BindingAdapter({"highlight"})
    public static void setClassButtonHighlight(View view, boolean highlight) {
        int drawableId;
        if (highlight) {
            drawableId = R.drawable.btn_default_highlight;
        } else {
            drawableId = R.drawable.btn_default;
        }
        view.setBackground(AppCompatResources.getDrawable(view.getContext(), drawableId));
    }

    /**
     * Set up a responsive preview for the view finder.
     */
    private void startCamera() {

        DisplayMetrics metrics = new DisplayMetrics();
        viewFinder.getDisplay().getRealMetrics(metrics);
        Rational screenAspectRatio = new Rational(metrics.widthPixels, metrics.heightPixels);

        PreviewConfig config = new PreviewConfig.Builder()
                .setLensFacing(LENS_FACING)
                .setTargetAspectRatio(screenAspectRatio)
                .setTargetRotation(viewFinder.getDisplay().getRotation())
                .build();

        Preview preview = new Preview(config);

        preview.setOnPreviewOutputUpdateListener(previewOutput -> {
            ViewGroup parent = (ViewGroup) viewFinder.getParent();
            parent.removeView(viewFinder);
            parent.addView(viewFinder, 0);


            Integer rotation = getDisplaySurfaceRotation(viewFinder.getDisplay());
            updateTransform(rotation, previewOutput.getTextureSize(), viewFinderDimens);
        });

        viewFinder.addOnLayoutChangeListener((
                view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            Size newViewFinderDimens = new Size(right - left, bottom - top);
            Integer rotation = getDisplaySurfaceRotation(viewFinder.getDisplay());
            updateTransform(rotation, bufferDimens, newViewFinderDimens);
        });

        HandlerThread inferenceThread = new HandlerThread("InferenceThread");
        inferenceThread.start();
        ImageAnalysisConfig analysisConfig = new ImageAnalysisConfig.Builder()
                .setLensFacing(LENS_FACING)
                .setCallbackHandler(new Handler(inferenceThread.getLooper()))
                .setImageReaderMode(ImageReaderMode.ACQUIRE_LATEST_IMAGE)
                .setTargetRotation(viewFinder.getDisplay().getRotation())
                .build();

        ImageAnalysis imageAnalysis = new ImageAnalysis(analysisConfig);
        imageAnalysis.setAnalyzer(inferenceAnalyzer);

        CameraX.bindToLifecycle(this, preview, imageAnalysis);
    }

    private String getClassNameFromResourceId(int id) {
        String className;
        if (id == R.id.class_btn_1) {
            className = "1";
        } else if (id == R.id.class_btn_2) {
            className = "2";
        } else if (id == R.id.class_btn_3) {
            className = "3";
        } else if (id == R.id.class_btn_4) {
            className = "4";
        } else {
            throw new RuntimeException("Listener called for unexpected view");
        }
        return className;
    }

    private String getRandomString(int id) {


        List<String> myList = Arrays.asList("1", "2", "3", "4");
        Random r = new Random();

        int randomitem = r.nextInt(myList.size());
        String randomElement = myList.get(randomitem);
        return randomElement;
    }

    /**
     * Fit the camera preview into [viewFinder].
     *
     * @param rotation            view finder rotation.
     * @param newBufferDimens     camera preview dimensions.
     * @param newViewFinderDimens view finder dimensions.
     */
    private void updateTransform(Integer rotation, Size newBufferDimens, Size newViewFinderDimens) {
        if (Objects.equals(rotation, viewFinderRotation)
                && Objects.equals(newBufferDimens, bufferDimens)
                && Objects.equals(newViewFinderDimens, viewFinderDimens)) {
            return;
        }

        if (rotation == null) {
            return;
        } else {
            viewFinderRotation = rotation;
        }

        if (newBufferDimens.getWidth() == 0 || newBufferDimens.getHeight() == 0) {
            return;
        } else {
            bufferDimens = newBufferDimens;
        }

        if (newViewFinderDimens.getWidth() == 0 || newViewFinderDimens.getHeight() == 0) {
            return;
        } else {
            viewFinderDimens = newViewFinderDimens;
        }

        Log.d(TAG, String.format("Applying output transformation.\n"
                + "View finder size: %s.\n"
                + "Preview output size: %s\n"
                + "View finder rotation: %s\n", viewFinderDimens, bufferDimens, viewFinderRotation));
        Matrix matrix = new Matrix();

        float centerX = viewFinderDimens.getWidth() / 2f;
        float centerY = viewFinderDimens.getHeight() / 2f;

        matrix.postRotate(-viewFinderRotation.floatValue(), centerX, centerY);

        float bufferRatio = bufferDimens.getHeight() / (float) bufferDimens.getWidth();

        int scaledWidth;
        int scaledHeight;
        if (viewFinderDimens.getWidth() > viewFinderDimens.getHeight()) {
            scaledHeight = viewFinderDimens.getWidth();
            scaledWidth = Math.round(viewFinderDimens.getWidth() * bufferRatio);
        } else {
            scaledHeight = viewFinderDimens.getHeight();
            scaledWidth = Math.round(viewFinderDimens.getHeight() * bufferRatio);
        }

        float xScale = scaledWidth / (float) viewFinderDimens.getWidth();
        float yScale = scaledHeight / (float) viewFinderDimens.getHeight();

        matrix.preScale(xScale, yScale, centerX, centerY);

        viewFinder.setTransform(matrix);
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);

        tlModel = new TransferLearningModelWrapper1(getActivity());
        viewModel = ViewModelProviders.of(this).get(CameraFragmentViewModel.class);
        viewModel.setTrainBatchSize(tlModel.getTrainBatchSize());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        CameraFragmentBinding dataBinding =
                DataBindingUtil.inflate(inflater, R.layout.camera_fragment, container, false);
        dataBinding.setLifecycleOwner(getViewLifecycleOwner());
        dataBinding.setVm(viewModel);
        View rootView = dataBinding.getRoot();

        for (int buttonId : new int[]{
                R.id.class_btn_1, R.id.class_btn_2, R.id.class_btn_3, R.id.class_btn_4}) {
            rootView.findViewById(buttonId).setOnClickListener(onAddSampleClickListener);
            rootView.findViewById(buttonId).setOnTouchListener(onAddSampleTouchListener);
        }


        if (viewModel.getCaptureMode().getValue()) {
            ((RadioButton) rootView.findViewById(R.id.capture_mode_button)).setChecked(true);
        } else {
            ((RadioButton) rootView.findViewById(R.id.inference_mode_button)).setChecked(true);
        }

        RadioGroup toggleButtonGroup = rootView.findViewById(R.id.mode_toggle_button_group);
        toggleButtonGroup.setOnCheckedChangeListener(
                (radioGroup, checkedId) -> {
                    if (viewModel.getTrainingState().getValue() == TrainingState.NOT_STARTED) {
                        ((RadioButton) rootView.findViewById(R.id.capture_mode_button)).setChecked(true);
                        ((RadioButton) rootView.findViewById(R.id.inference_mode_button)).setChecked(false);

                        Snackbar.make(
                                requireActivity().findViewById(R.id.classes_bar),
                                "Inference can only start after training is done.",
                                BaseTransientBottomBar.LENGTH_LONG)
                                .show();
                    } else {
                        if (checkedId == R.id.capture_mode_button) {
                            viewModel.setCaptureMode(true);
                        } else {
                            viewModel.setCaptureMode(false);
                            Snackbar.make(
                                    requireActivity().findViewById(R.id.classes_bar),
                                    "Point your camera at one of the trained objects.",
                                    BaseTransientBottomBar.LENGTH_LONG)
                                    .show();
                        }
                    }
                });

        Button helpButton = rootView.findViewById(R.id.help_button);
        helpButton.setOnClickListener(
                (button) -> {
                    helpDialog.show(requireActivity().getSupportFragmentManager(), "Help Dialog");
                });
        // Display HelpDialog when opened.
        helpDialog.show(requireActivity().getSupportFragmentManager(), "Help Dialog");

        Button testButton = rootView.findViewById(R.id.button);
        testButton.setOnClickListener(
                (button) -> {
                    class_idT = getRandomString(0);
                    testButton.setText(class_idT);
                });


        return dataBinding.getRoot();
    }

    // Binding adapters:

    @Override
    public void onViewCreated(View view, Bundle bundle) {
        super.onViewCreated(view, bundle);
        this.context = view.getContext();
        viewFinder = requireActivity().findViewById(R.id.view_finder);
        viewFinder.post(this::startCamera);

        viewModel
                .getTrainingState()
                .observe(
                        getViewLifecycleOwner(),
                        trainingState -> {
                            switch (trainingState) {
                                case STARTED:
                                    tlModel.enableTraining((epoch, loss) -> viewModel.setLastLoss(loss));
                                    if (!viewModel.getInferenceSnackbarWasDisplayed().getValue()) {
                                        Snackbar.make(
                                                requireActivity().findViewById(R.id.classes_bar),
                                                R.string.switch_to_inference_hint,
                                                BaseTransientBottomBar.LENGTH_LONG)
                                                .show();
                                        viewModel.markInferenceSnackbarWasCalled();
                                    }
                                    break;
                                case PAUSED:
                                    tlModel.disableTraining();
                                    break;
                                case NOT_STARTED:
                                    break;
                            }
                        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        tlModel.close();
        tlModel = null;
    }

    private Bitmap getarray(String file) throws IOException {
        List<String[]> rows = new ArrayList<>();
        CSVReader csvReader = new CSVReader(this.context, file);

        rows = csvReader.readCSV();
        int w = 5;
        int h = 50;
        int[] rawData = new int[w * h];

        for (int y = 0; y < w; y++) {
            for (int x = 0; x < h; x++) {


                float hue = Float.parseFloat((rows.get(x)[y] + 1)) * 180;
                float hsv[] = new float[3];
                hsv[0] = hue;
                hsv[1] = 1f;
                hsv[2] = 1f;

                int color = Color.HSVToColor(hsv);
                rawData[y + x * w] = color;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }
}
