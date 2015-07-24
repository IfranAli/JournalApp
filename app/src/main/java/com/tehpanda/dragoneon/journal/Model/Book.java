package com.tehpanda.dragoneon.journal.Model;

import android.util.Log;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Book {
    private final IDataStorage mDataStorage;
    private String mFileName;
    private String mBookName;
    private ArrayList<Note> mNotes;
    private int mTotalNotes;

    //Todo: Clean up this class, fix constructor member initialization.
    //Todo: Check if fileName exists.
    // Ctor for creating a new book.
    public Book(String bookName, IDataStorage dataStorage){
        this.mDataStorage = dataStorage;
        this.setFileName(generateFileName(bookName));
        this.setBookName(bookName);
        mNotes = new ArrayList<Note>();
    }
    // Ctor for restoring created Book from DB.
    public Book(String fileName, String bookName, IDataStorage dataStorage) {
        this(bookName, dataStorage);
        this.setFileName(fileName);
    }
    // Ctor for restoring created Book from DB.
    public Book(String fileName, String bookName, int totalNotes, IDataStorage dataStorage) {
        this(fileName, bookName, dataStorage);
        this.mTotalNotes = totalNotes;
    }

    public void AddEntry(String title, String data, String date){
        JournalEntry journal = new JournalEntry(title, data, date);
        mNotes.add(journal);
        mTotalNotes++;
    }
    public void AddNewNote(String title, String data) {
        mNotes.add(new JournalEntry(title, data, currDateFormatted()));
        mTotalNotes++;
    }
    public void Delete(int index){
        mNotes.remove(index);
        mTotalNotes--;
    }

    public Note GetEntry(int index){
        return mNotes.get(index);
    }

    public List<Note> getListOfEntries(){
        if(mNotes == null || mNotes.isEmpty()){
            try {
                LoadBook();
            } catch (FileNotFoundException e) {
                Log.e("Error", e.getMessage());
            }
        }
        return mNotes;
    }
    // Current Date formatted for saving.
    private static String currDateFormatted(){
        return new SimpleDateFormat("dd:MM:yyyy:HH:mm:s").format(Calendar.getInstance().getTime());
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

    // Export
    public String ExportToTextFile() {
        return mDataStorage.ExportBookAsText(this);
    }

    // Save Load
    public void SaveBook(){
        try {
            mDataStorage.SaveBooks(mNotes, mFileName);
        } catch (Exception e) {
            Log.d("ERROR", e.getMessage());
        }
    }
    public void LoadBook() throws FileNotFoundException {
        if(mNotes == null){
            mNotes = new ArrayList<>();
        }
        mNotes.clear();
        mNotes.addAll(mDataStorage.GetBook(getFileName()));
    }

    public static final int MAX_LENGTH = 10;
    private static String generateFileName(String input) {
        // Remove all non-alphabetical characters.
        input = input.replaceAll("[^a-zA-Z0-9]", "");
        // Shrink text if it's too long.
        if(input.length() > MAX_LENGTH) {
            input = input.substring(0, MAX_LENGTH);
        }
        return input + ".json";
    }
}
