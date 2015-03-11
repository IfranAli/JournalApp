package com.tehpanda.dragoneon.journal.Model;

import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.logging.SimpleFormatter;

public class JournalEntry implements IJournalEntry {
	private String data; // Text Entry.
    private String title; // Text Entry.
	private static final String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
	public Date date;

	// CONSTRUCTOR...
	public JournalEntry(String title, String data, String date) {
		this.data = data;
		this.title = title;
		this.date = parseCalendar(date);
	}

    // From saved state to object
    private static Date parseCalendar(String calendar) {
        String[] split = calendar.split("\\:");
        return new Date(Integer.parseInt(split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]),Integer.parseInt(split[3]),Integer.parseInt(split[4]), Integer.parseInt(split[5]));
    }

    private String getPreviewText(String data, int cutOff) {
        if(data.length() > cutOff) {
            String mod = data.substring(0, cutOff).replaceAll("\\n", " ");
            return mod + "...";
        }
        else {
            return data.replaceAll("\\n", " ");
        }
    }

    public String GetDataPreview() {
        return getPreviewText(data, 200);
    }
    public String GetTitlePreview() {
        //return title;
        return getPreviewText(title, 50);
    }
	// Getters and Setters.
	public String getData() {
		return data;
	}
	public void setData(String data){
		this.data = data;
	}
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String GetDay() {
		return String.valueOf(date.GetDay());
	}
	public String GetMonth() {
		return monthNames[date.GetMonth() -1];
	}
    public String GetTime() {
        return date.GetTime();
    }
    public int GetDayColor(){
        return date.GetDayOfWeek();
    }

    public long DateAsLong(){
        return date.GetDateAsLong();
    }

    int randomWithRange(int min, int max)
    {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    // Comparator - By Colour
    public static Comparator<JournalEntry> ByColor = new Comparator<JournalEntry>() {
        @Override
        public int compare(JournalEntry journalEntry, JournalEntry journalEntry2) {
            return journalEntry.GetDayColor() - journalEntry2.GetDayColor();
        }
    };
    // Comparator - By Date/Time (Descending)
    public static Comparator<JournalEntry> ByDate = new Comparator<JournalEntry>() {
        @Override
        public int compare(JournalEntry journalEntry, JournalEntry journalEntry2) {
            return (int) (journalEntry2.DateAsLong() - journalEntry.DateAsLong());
        }
    };

}
