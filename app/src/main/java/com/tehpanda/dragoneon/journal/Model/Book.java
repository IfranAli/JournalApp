package com.tehpanda.dragoneon.journal.Model;

import android.util.Log;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class Book {
    private final DataStorage mDataStorage;
    private String mFileName;
    private String mBookName;
    private ArrayList<JournalEntry> mNotes;
    private int mTotalNotes;

    //Todo: Clean up this class, fix constructor member initialization.
    //Todo: Check if fileName exists.
    // Ctor for creating a new book.
    public Book(String bookName){
        this.setFileName(generateFileName(bookName));
        this.setBookName(bookName);
        this.mDataStorage = DataStorage.GetInstance();
    }
    // Ctor for restoring created Book from DB.
    public Book(String fileName, String bookName) {
        this.setFileName(fileName);
        this.setBookName(bookName);
        this.mDataStorage = DataStorage.GetInstance();
    }
    // Ctor for restoring created Book from DB.
    public Book(String fileName, String bookName, int totalNotes) {
        this(fileName, bookName);
        this.mTotalNotes = totalNotes;
    }

    public void AddEntry(String title, String data, String date){
        mNotes.add(new JournalEntry(title, data, date));
        mTotalNotes++;
    }
    public void Delete(int index){
        mNotes.remove(index);
        mTotalNotes--;
    }

    public JournalEntry GetEntry(int index){
        return mNotes.get(index);
    }

    public List<JournalEntry> getListOfEntries(){
        if(mNotes == null){
            try {
                LoadBook();
            } catch (FileNotFoundException e) {
                Log.e("Error", e.getMessage());
            }
        }
        return mNotes;
    }

    public int getTotalNotes() {
        return mTotalNotes;
    }
    public int NumOfEntries(){
        return mNotes.size();
    }

    // Getters and setters.
    public String getFileName() {
        return mFileName;
    }
    void setFileName(String fileName) {
        mFileName = fileName;
    }
    public String getBookName() {
        return mBookName;
    }
    public void setBookName(String bookName) {
        mBookName = bookName;
    }

    // Save Load
    public void SaveBook(){
        try {
            mDataStorage.SaveBook(mNotes, mFileName);
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }
    public void LoadBook() throws FileNotFoundException {
        if(mNotes == null){
            mNotes = new ArrayList<>();
        }
        mNotes.clear();
        mNotes.addAll(mDataStorage.loadBook(getFileName()));
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
