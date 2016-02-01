package com.tehpanda.dragoneon.journal.Model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;


public class DataStorageJson extends DataStorage implements IDataStorage {

    @Override
    public List<Note> GetBook(String filename) throws FileNotFoundException {
        File file = new File(books + "/" + filename);

        StringBuilder jsonText = new StringBuilder("");
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                jsonText.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Note> entries = new ArrayList<>();
        JSONArray arry = null;
        try {
            arry = new JSONArray(jsonText.toString());
            for(int i = 0; i < arry.length(); i ++ ) {
                JSONObject obj = arry.getJSONObject(i);
                entries.add(readNoteJson(obj));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entries;
    }

    @Override
    public List<Book> GetBooks() {
        File file = new File(journals + "/Books.json");

        StringBuilder jsonText = new StringBuilder("");
        String line = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            while ((line = br.readLine()) != null) {
                jsonText.append(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ArrayList<Book> entries = new ArrayList<>();

        JSONObject obj;
        JSONArray recents = new JSONArray();
        JSONArray books = new JSONArray();
        try {
            obj = new JSONObject(String.valueOf(jsonText));
            recents = obj.getJSONArray("Recents");
            books = obj.getJSONArray("Books");

            int lengthOfBooks = books.length();
            for(int i = 0; i < lengthOfBooks; i++) {
                JSONObject currentBook = books.getJSONObject(i);
                entries.add(new Book(currentBook.getString("FileName"),
                        currentBook.getString("BookName"),
                        currentBook.getInt("Entries"),
                        this));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entries;
    }

    @Override
    public void SaveBooks(List<Note> notes, String filename) {
        // New JSon Object.
        JSONArray jsonob = new JSONArray();

        for(Note j : notes) {
            JSONObject n = new JSONObject();
            try {
                n.put("title", j.title);
                n.put("date", j.date.toString());

                if(j.isEncrypt)
                {
                    JournalEncodedEntry je = (JournalEncodedEntry)j;
                    n.put("encrypted", true);
                    if(je.decryptionkey != null) {
                        String encryptdata = Encryption.Encrypt(je.getData(), je.decryptionkey);
                        n.put("data", encryptdata);
                        je.modified = false;
                    } else {
                        n.put("data", je.getEncoded());
                    }
                } else {
                    n.put("data", j.data);
                }
                jsonob.put(n);
            } catch (JSONException e1) {
                e1.printStackTrace();
            }
        }

        try {
            OutputStream os = new FileOutputStream(new File(books + "/" + filename));
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(jsonob.toString());
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //String jsoncoded = jsonob.toString();
    }

    @Override
    public void CreateIndexFile(List<Book> books, boolean calculateTrueTotal) {
        // index any un-Indexed books as well.
        for(String s : getUnprocessedBooks(books)){
            books.add(new Book(s, s, this));
        }

        JSONObject settingsFile = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        JSONArray recents = new JSONArray();

        for(Book b : books) {
            JSONObject n = new JSONObject();
            try {
                n.put("BookName", b.getBookName());
                n.put("FileName", b.getFileName());

                if (calculateTrueTotal) {
                    b.LoadBook();
                    n.put("Entries", b.NumOfEntries());
                } else {
                    n.put("Entries", b.getTotalNotes());
                }
                jsonArray.put(n);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        JSONArray obj;
        try {
            obj = new JSONArray();
            for (int count = 0; count < 10; count++) {
                JSONObject ob = new JSONObject();
                ob.put("BIndex", count);
                ob.put("NIndex", count);
                obj.put(ob);
                recents.put(obj);
            }
        } catch (JSONException js) {

        }

        try {
            settingsFile.put("Recents", recents);
            settingsFile.put("Books", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            OutputStream os = new FileOutputStream(new File(journals + "/Books.json"));
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(settingsFile.toString());
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Note readNoteJson(JSONObject r) {
        String title = null;
        String data = null;
        String date = null;
        Boolean isencoded = false;
        try{
            title = r.getString("title");
            date = r.getString("date");
            data = r.getString("data");
            isencoded = r.getBoolean("encrypted");
        } catch (Exception ex){
            ex.printStackTrace();
        }
        if (isencoded) {
            JournalEncodedEntry c = new JournalEncodedEntry(title, data, date);
            c.isEncrypt = true;
            return c;
        } else {
            return new JournalEntry(title, data, date);
        }
    }

    @Override
    public JournalEncodedEntry GetProtectedNote(Note n) {
        JournalEncodedEntry je = new JournalEncodedEntry(n.title, n.data, n.date.toString());
        je.decryptionkey = "abcd";
        je.isEncrypt = true;
        je.modified = true;
        return je;
    }
}
