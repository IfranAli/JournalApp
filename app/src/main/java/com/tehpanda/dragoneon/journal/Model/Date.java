package com.tehpanda.dragoneon.journal.Model;
/**
 * Created by dragoneon on 31/08/14.
 */
public class Date {
    //day, month, year, hour, minute
    private final int day;
    private final int month;
    private final int year;
    private final int hour;
    private final int minute;
    private final int second;

    public Date(int day, int month, int year, int hour, int minute, int second){
        this.day = day;
        this.month = month;
        this.year = year;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    @Override
    public String toString(){
        return String.format("%s:%s:%s:%s:%s:%s", day, month, year, hour, minute, second);
    }

    public int GetDayOfWeek(){
        // Method to calculate the day of the week.
        int t[] = {0, 3, 2, 5, 0, 3, 5, 1, 4, 6, 2, 4};
        int y = year;
        y -= month < 3 ? 1 : 0;
        return (y + y/4 - y/100 + y/400 + t[month-1] + day) % 7;
    }

    public int GetDay() {
        return day;
    }
    public int GetMonth() {
        return month;
    }
    public int GetYear() {
        return year;
    }

    // In string format.
    public String GetTime(){
        String suffix = hour >= 12 ? "pm" : "am";
        return String.format("%02d:%02d%s", suffix == "pm" ? hour - 12 : hour, minute, suffix);
    }
    public int GetHour() {
        return hour;
    }
    public int GetMinute() {
        return minute;
    }

    public long GetDateAsLong(){
        return Long.parseLong(String.format("%s%02d%02d%02d%02d%02d", year, month, day, hour, minute, second));
    }

    public String GetDateStandard(){
        return String.format("%02d/%02d/%s %s", day, month, year, GetTime());
    }
}
