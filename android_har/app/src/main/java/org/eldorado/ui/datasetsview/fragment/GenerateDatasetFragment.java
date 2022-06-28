package org.eldorado.ui.datasetsview.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import org.eldorado.ui.treinamento.fragment.FileSelectorViewAdapter;
import org.tensorflow.lite.examples.transfer.R;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.IntStream;
import java.util.stream.Stream;


public class GenerateDatasetFragment extends Fragment implements
        AdapterView.OnItemSelectedListener {


    String[] datasets_view = {"Tempo", "Frequencia"};
    RecyclerView activitiesView;

    public static GenerateDatasetFragment newInstance() {
        return new GenerateDatasetFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generate_dataset_fragment, container, false);
        activitiesView = (RecyclerView) view.findViewById(R.id.file_selector_recycle_view);
        activitiesView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        String path = view.getContext().getFilesDir().getAbsolutePath() + File.separator;
        File directory = new File(path);
        FileSelectorViewAdapter adapter = new FileSelectorViewAdapter(view.getContext());
        activitiesView.setAdapter(adapter);
        List<File> selectedFiles = new ArrayList<>();
        List<File> allFiles = new ArrayList<File>();
        allFiles = getActivitiesNames(directory);
        adapter.setFiles(allFiles.toArray(new File[0]));
        selectedFiles.clear();
        adapter.setListener(new FileSelectorViewAdapter.FileSelectedListener() {
            @Override
            public void onFileChecked(File file, boolean ischecked) {
                if (ischecked) {
                    selectedFiles.add(file);
                } else {
                    selectedFiles.remove(file);
                }
            }
        });


        Spinner spinDatasetVision = (Spinner) view.findViewById(R.id.spinner_datasetvision);
        spinDatasetVision.setAdapter(new ArrayAdapter<String>(getContext(), R.layout.custom_spinner, datasets_view));
        Button button_generate_dataset_view = (Button) view.findViewById(R.id.button_generate_dataset_view);
        button_generate_dataset_view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                File folder = new File(view.getContext().getFilesDir().getAbsolutePath() + File.separator + "Datasets");
                boolean success = true;
                if (!folder.exists()) {
                    success = folder.mkdirs();
                }
                if (success) {
                    writeFilesToDatasets(selectedFiles, folder);
                } else {
                    // Do something else on failure
                }

            }
        });
        return view;
    }


    void writeFilesToDatasets(List<File> selectedFiles, File folder) {
        for (File file : selectedFiles) {

            try {
                // Create an object of file reader class with CSV file as a parameter.
                FileReader filereader = new FileReader(file);
                // create csvParser object with
                // custom separator semi-colon
                CSVParser parser = new CSVParserBuilder().withSeparator(';').build();
                // create csvReader object with parameter
                // filereader and parser
                CSVReader csvReader = new CSVReaderBuilder(filereader)
                        .withCSVParser(parser)
                        .build();
                List<String[]> allData = csvReader.readAll();
                // Read all data at once
                getAllArraySensors(allData, folder);
                csvReader.close();
                Toast.makeText(getView().getContext(), " Arquivos criados com sucesso", Toast.LENGTH_LONG).show();

            } catch (Exception e) {
                Toast.makeText(getView().getContext(), " não foi possivel criar os arquivos", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }


        }

    }

    void getAllArraySensors(List<String[]> allData, File folder) {
        List<String> rowsAcc = new ArrayList<>();
        List<String> rowsGyro = new ArrayList<>();
        Stream<String> rowsconcat;
        // Print Data.
        for (String[] row : allData) {
            if (row[1].contains("Accelerometer")) {
                // System.out.print(row[0] + " \t" +row[1] + " \t" + row[2] + " \t" + row[3] + " \t" + row[4] + " \t" + row[5]+ " \t" + row[6]+ " \t" + row[6]);
                rowsAcc.add(Float.parseFloat(row[5]) + ";" + Float.parseFloat(row[6]) + ";" + Float.parseFloat(row[7]));
            }
            if (row[1].contains("Gyroscope")) {
                // System.out.print(row[0] + " \t" +row[1] + " \t" + row[2] + " \t" + row[3] + " \t" + row[4] + " \t" + row[5]+ " \t" + row[6]+ " \t" + row[6]);
                rowsGyro.add(";" + Float.parseFloat(row[5]) + ";" + Float.parseFloat(row[6]) + ";" + Float.parseFloat(row[7]));

            }
            rowsconcat = IntStream
                    .range(0, Math.min(rowsAcc.size(), rowsGyro.size()))
                    .mapToObj(i -> rowsAcc.get(i) + rowsGyro.get(i));

            try {
                File file = new File(folder.getAbsolutePath() + File.separator + allData.get(1)[0] + ".txt");
                if (!file.exists()) {
                    file.createNewFile();
                }
                FileWriter fw = new FileWriter(file.getAbsoluteFile());
                BufferedWriter bw = new BufferedWriter(fw);


                for (Object row1 : rowsconcat.toArray()) {
                    bw.write(row1.toString());
                    bw.newLine();
                }

                bw.close();

            } catch (IOException e) {

                e.printStackTrace();
            }

        }

    }

    @Override
    public void onItemSelected(AdapterView<?> arg0, View view, int position, long id) {
        String path = view.getContext().getFilesDir().getAbsolutePath() + File.separator;
        File directory = new File(path);
        FileSelectorViewAdapter adapter = new FileSelectorViewAdapter(view.getContext());
        activitiesView.setAdapter(adapter);
        List<File> selectedFiles = new ArrayList<>();
        List<File> allFiles = new ArrayList<File>();
        allFiles = getActivitiesNames(directory);
        adapter.setFiles(allFiles.toArray(new File[0]));
        selectedFiles.clear();


        adapter.setListener(new FileSelectorViewAdapter.FileSelectedListener() {
            @Override
            public void onFileChecked(File file, boolean ischecked) {
                if (ischecked) {
                    selectedFiles.add(file);
                } else {
                    selectedFiles.remove(file);
                }
            }
        });

        Toast.makeText(getView().getContext(),
                Integer.toString(allFiles.size()), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    List<File> getActivitiesNames(File directory) {
        List<File> allFilesc = new ArrayList<File>();
        Queue<File> dirs = new LinkedList<File>();
        dirs.add(directory);
        while (!dirs.isEmpty()) {
            for (File f : dirs.poll().listFiles()) {
                if (f.isDirectory()) {
                    dirs.add(f);
                    File[] files = f.listFiles(new FilenameFilter() {
                        @Override
                        public boolean accept(File dir, String name) {
                            return name.contains(".csv");
                        }
                    });
                    allFilesc.addAll(Arrays.asList(files));
                }

            }
        }
        return allFilesc;
    }
}