package me.coucou.nutrition.db.model;

/**
 * Created by matias on 2/11/14.
 */
public class Meal {
    private long id;
    private String description;
    private String date;

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

    public void setDate(String description) {
        this.date = description;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return date +"\n" +description;
    }
}
