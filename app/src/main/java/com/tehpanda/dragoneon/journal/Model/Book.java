package com.tehpanda.dragoneon.journal.Model;

import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Book {
    private final DataStorage dataStorage;
    private String FileName;
    private String BookName;
    private final ArrayList<JournalEntry> entries = new ArrayList<>();

    //Todo: Clean up this class, fix constructor member initialization.
    //Todo: Check if fileName exists.
    // Ctor for creating a new book.
    public Book(String bookName){
        this.setFileName(generateFileName(bookName));
        this.dataStorage = DataStorage.GetInstance();
        this.setBookName(bookName);
    }
    // Ctor for restoring created Book from DB.
    public Book(String fileName, String bookName) {
        this.setFileName(fileName);
        this.setBookName(bookName);
        this.dataStorage = DataStorage.GetInstance();
    }

    public void AddEntry(String title, String data, String date){
        entries.add(new JournalEntry(title, data, date));
    }
    public void Delete(int index){
        entries.remove(index);
    }
    public JournalEntry GetEntry(int index){
        return entries.get(index);
    }
    public List<JournalEntry> getListOfEntries(){
        return entries;
    }
    public int NumOfEntries(){
        return entries.size();
    }

    // Getters and setters.
    public String getFileName() {
        return FileName;
    }
    void setFileName(String fileName) {
        FileName = fileName;
    }
    public String getBookName() {
        return BookName;
    }
    public void setBookName(String bookName) {
        BookName = bookName;
    }

    // Save Load
    public void saveXML(){
        try {
            dataStorage.SaveBook(entries, FileName);
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }
    public void loadXML() throws FileNotFoundException {
        entries.clear();
        entries.addAll(dataStorage.loadData(getFileName()));
    }

    public static final int MAX_LENGTH = 10;
    private static String generateFileName(String input) {
        // Remove all non-alphabetical characters.
        input = input.replaceAll("[^a-zA-Z0-9]", "");
        // Shrink text if it's too long.
        if(input.length() > MAX_LENGTH) {
            input = input.substring(0, MAX_LENGTH);
        }
        return input + ".xml";
    }
}
