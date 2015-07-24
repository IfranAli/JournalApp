package com.tehpanda.dragoneon.journal.Model;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//public class DataStorage {
//    private static DataStorage _DataStorage;
//    public static DataStorage GetInstance(){
//        if(_DataStorage == null){
//            _DataStorage = new DataStorageJson();
//        }
//        return _DataStorage;
//    }
//}

abstract class DataStorage implements IDataStorage {
    protected static final String documents = Environment.getExternalStorageDirectory() + "/Documents";
    protected static final String journals = documents + "/Journals";
    protected static final String books = journals + "/Books";

    public DataStorage() {
        CheckFolders();
    }

    protected ArrayList<String> getUnprocessedBooks(List<Book> bookList){
        ArrayList<String> filenames = new ArrayList<>();
        File dir = new File(books);
        if(!dir.exists()){
            Log.e("DataStorage", "Save " + dir.getPath() + " not exist");
        } else{
            for (File f : dir.listFiles()){
                if (f.getName().endsWith(".json")){
                    filenames.add(f.getName());
                }
            }
        }

        for(Book b : bookList){
            if(filenames.contains(b.getFileName()))
                filenames.remove(b.getFileName());
        }
        Log.e("DATASTORAGE_UNPROC:", String.valueOf(filenames.size()));
        return filenames;
    }

    private void CheckFolders(){
        // Create folders if they don't exist.
        File documentsFolder = new File(documents);
        File journalFolder = new File(journals);
        File bookFolder = new File(books);
        if(!documentsFolder.exists()){
            documentsFolder.mkdir();
            Log.e("Creating", "docFolder");
        }
        if(!journalFolder.exists()) {
            journalFolder.mkdir();
            Log.e("Creating", "jFolder");
        }
        if(!bookFolder.exists()){
            bookFolder.mkdirs();
            Log.e("Creating", bookFolder.getPath());
        }
    }

    @Override
    public void RemoveBook( Book b){
        File f = new File(books + "/" + b.getFileName());
        f.delete();
    }
    @Override
    public String ExportBookAsText(Book book) {
        File outputfile = new File(documents + "/" + book.getBookName() + ".txt");
        try {
            FileWriter fw = new FileWriter(outputfile);
            BufferedWriter bw = new BufferedWriter(fw);
            for (Note j : book.getListOfEntries()) {
                bw.write("#" + j.getTitle() + " : " + j.date.GetDateStandard());
                bw.newLine();
                bw.write(j.getData());
                bw.newLine();
                bw.newLine();
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return outputfile.getAbsolutePath();
    }
}