package br.org.eldorado.motionsensors;

public class ItemActivities {

    String activityName;
    int activityImage;
    private boolean selected;

    public ItemActivities(String activityName, boolean selected) {
        this.selected = selected;
        this.activityName = activityName;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getActivityName() {
        return activityName;
    }

    public int getActivityImage() {
        return activityImage;
    }
}