package me.coucou.nutrition.model;

/**
 * Created by matias on 2/11/14.
 */
public class Meal {
    private long id;
    private String description;
    private String date;
    private String time;
    private String imagePath;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public void setDate(String value) {
        this.date = value;
    }

    public void setTime(String value) {
        this.time = value;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String value) {
        this.imagePath = value;
    }

    public String getFullDateTime(){
        return date +" "+time;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return date+" - "+ time +"\n" +description +"\n"+imagePath;
    }
}
