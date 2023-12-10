package com.example.breakingnews;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class News {
    private String title;
    private String details;
    public String getTitle() { return title; }
    public String getDetails() { return details; }

    public News(String _title , String description) {
        title = _title;
        details = description;
    }
    @Override
    public String toString() {

        return title;
    }
}
