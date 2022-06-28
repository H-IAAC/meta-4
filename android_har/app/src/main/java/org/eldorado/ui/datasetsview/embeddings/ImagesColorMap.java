package org.eldorado.ui.datasetsview.embeddings;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;

import com.opencsv.CSVParser;
import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ImagesColorMap {


    public ImagesColorMap(List<File> selectedFiles) {

    }

    public ImagesColorMap() {

    }

    public static Bitmap getBitmapObject(List<float[]> allSensors) {
        int w = 5;
        int h = 50;
        int[] rawData = new int[w * h];

        for (int y = 0; y < w; y++) {
            for (int x = 0; x < h; x++) {
                float hue = (allSensors.get(x)[y] + 1) * 180;
                float hsv[] = new float[3];
                hsv[0] = hue;
                hsv[1] = 1f;
                hsv[2] = 1f;
                int color = Color.HSVToColor(hsv);
                rawData[y + x * w] = color;
            }
        }
        Log.d("d", String.valueOf(rawData.length));
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }

    public ArrayList<Bitmap> getArrayByActivity(File file) {
        ArrayList<Bitmap> bitmapArray = new ArrayList<Bitmap>();
        try {
            List<String[]> all_array = getarray_by_category(file.getAbsolutePath());

            List<String[]> all_array1 = new ArrayList<>();
            for (int y = 0; y < (all_array.size() / 50) - 1; y++) {
                all_array1 = new ArrayList<>();
                all_array1.addAll(all_array.subList(y * 50, y * 50 + 50));
                Bitmap bitmap = getBitmap(all_array1);
                bitmapArray.add(bitmap); // Add a bitmap
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmapArray;
    }

    private List<String[]> getarray_by_category(String file) throws IOException {
        List<String[]> allData = null;
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
            allData = csvReader.readAll();
            // Read all data at once
            csvReader.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

        return allData;
    }

    public Bitmap getBitmap(List<String[]> rows) throws IOException {
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
        Log.d("d", String.valueOf(rawData.length));
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.RGB_565);
        bitmap.setPixels(rawData, 0, w, 0, 0, w, h);
        return bitmap;
    }
}