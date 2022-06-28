package br.org.eldorado.motionsensors;

public class ItemDatasets {

    String datasetName;
    int datasetImage;


    public ItemDatasets(String datasetName, int datasetImage) {
        this.datasetImage = datasetImage;
        this.datasetName = datasetName;
    }

    public int getDatasetImage() {
        return datasetImage;
    }

    public void setDatasetImage(int datasetImage) {
        this.datasetImage = datasetImage;
    }

    public String getDatasetName() {
        return datasetName;
    }

    public void setDatasetName(String datasetName) {
        this.datasetName = datasetName;
    }

}