package com.tehpanda.dragoneon.journal.Model;

import java.util.Comparator;

/**
 * Created by panda on 5/20/15.
 */
public abstract class Note implements IJournalEntry {
    protected String data; // Text Entry.
    protected String title; // Text Entry.
    protected static final String[] monthNames = {
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    };
    protected Date date;
    protected Boolean isEncrypt = false;

    public Boolean isEncrypted() {
        return isEncrypt;
    }

    // From saved state to object
    protected static Date parseCalendar(String calendar) {
        String[] split = calendar.split("\\:");
        return new Date(Integer.parseInt(
                split[0]),Integer.parseInt(split[1]),Integer.parseInt(split[2]),
                Integer.parseInt(split[3]),Integer.parseInt(split[4]), Integer.parseInt(split[5]));
    }

    // Default Behaviour.
    protected String getPreviewText(String data, int cutOff) {
        if(data.length() > cutOff) {
            String mod = data.substring(0, cutOff).replaceAll("\\n", " ");
            return mod + "...";
        }
        else {
            return data.replaceAll("\\n", " ");
        }
    }
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
    public String GetDataPreview() {
        return getPreviewText(data, 200);
    }
    public String GetTitlePreview() {
        //return title;
        return getPreviewText(title, 50);
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

    int randomWithRange(int min, int max) {
        int range = (max - min) + 1;
        return (int)(Math.random() * range) + min;
    }

    // Comparator - By Colour
    public static Comparator<Note> ByColor = new Comparator<Note>() {
        @Override
        public int compare(Note journalEntry, Note journalEntry2) {
            return journalEntry.GetDayColor() - journalEntry2.GetDayColor();
        }
    };
    // Comparator - By Date/Time (Descending)
    public static Comparator<Note> ByDate = new Comparator<Note>() {
        @Override
        public int compare(Note journalEntry, Note journalEntry2) {
            return (int) (journalEntry2.date.GetDateAsLong() - journalEntry.date.GetDateAsLong());
        }
    };
}
