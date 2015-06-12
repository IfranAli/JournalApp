package com.tehpanda.dragoneon.journal.Model;

import android.os.Environment;
import android.util.JsonReader;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringReader;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DataStorage {
    // Folder Structure.
    private static final String documents = Environment.getExternalStorageDirectory() + "/Documents";
    private static final String journals = documents + "/Journals";
    private static final String books = journals + "/Books";

    // Singleton.
    private static DataStorage _DataStorage;
    public static DataStorage GetInstance(){
        if(_DataStorage == null){
            _DataStorage = new DataStorage();
        }
        return _DataStorage;
    }

    // Check folders upon initialization.
	DataStorage() {
        CheckFolders();
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
    private Book readBooksJson(JsonReader r) {
        String title = null;
        String data = null;
        String date = null;

        try{
            r.beginObject();
            while (r.hasNext()) {
                String name = r.nextName();
                if (name.equals("BookName")) {
                    title = r.nextString();
                } else if (name.equals("FileName")) {
                    date = r.nextString();
                } else if (name.equals("Entries")) {
                    data = r.nextString();
                } else {
                    r.skipValue();
                }
            }
            r.endObject();
        } catch (Exception ex){
            ex.printStackTrace();
        }
        return new Book(date, title, Integer.parseInt(data));
    }

    // Load Book from given filename.
    public ArrayList<Note> loadBookXML(String fileName) throws FileNotFoundException{
        loadBookJSON(fileName);
        ArrayList<Note> entries = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new File(books + "/" + fileName));

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Entry");
            Node n;
            for (int i = 0; i < nList.getLength(); i++) {
                n = nList.item(i);
                if(n.getNodeType() == Node.ELEMENT_NODE){
                    Element e = (Element)n;
                    Note y;

                    Boolean x = null;
                    try {
                        x = Boolean.parseBoolean(e.getElementsByTagName("encrypted").item(0).getTextContent());
                    } catch (Exception ex) {
                        //Log.e("Error", "" + ex.getMessage());
                    }
                    if(x != null) {
                        y = new JournalEncodedEntry(
                                e.getElementsByTagName("title").item(0).getTextContent(),
                                e.getElementsByTagName("data").item(0).getTextContent(),
                                e.getElementsByTagName("date").item(0).getTextContent()
                        );
                        y.isEncrypt = true;
                    } else {
                        y = new JournalEntry(
                                e.getElementsByTagName("title").item(0).getTextContent(),
                                e.getElementsByTagName("data").item(0).getTextContent(),
                                e.getElementsByTagName("date").item(0).getTextContent()
                        );
                    }
                    entries.add(y);
                }
            }
        } catch (FileNotFoundException e){
            throw e;
        } catch (Exception e){
            Log.wtf("XML PARSER", e.getMessage());
        }
        return entries;
    }
    public ArrayList<Note> loadBookJSON(String fileName) {
        File file = new File(books + "/" + fileName);

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
    public ArrayList<Note> loadBook(String fileName) throws FileNotFoundException{
        return loadBookJSON(fileName);
    }

    // Load Book objects from Books.xml.
    public ArrayList<Book> GetBooks(){
        return getBooksJSON();
    }
    public ArrayList<Book> getBooksXML(){
        ArrayList<Book> books = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(journals + "/Books.xml"));
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Book");
            Node n = null;
            for (int i = 0; i < nList.getLength(); i++) {
                n = nList.item(i);
                if(n.getNodeType() == Node.ELEMENT_NODE){
                    Element e = (Element)n;
                    String bookName = e.getElementsByTagName("BookName").item(0).getTextContent();
                    String fileName = e.getElementsByTagName("FileName").item(0).getTextContent();
                    String entries = e.getElementsByTagName("Entries").item(0).getTextContent();
                    books.add(new Book(fileName, bookName, Integer.parseInt(entries)));
                }
            }
        } catch (Exception ex){
            Log.e("ERROR", ex.getMessage());
        }
        return books;
    }
    public ArrayList<Book> getBooksJSON(){
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

        JsonReader reader = new JsonReader(new StringReader(jsonText.toString()));
        try {
            reader.beginArray();
            while(reader.hasNext()) {
                entries.add(readBooksJson(reader));
            }
            reader.endArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return entries;
    }

    // Removes a journal.
    public void RemoveBook( Book b){
        File f = new File(books + "/" + b.getFileName());
        f.delete();
    }

    // Gets any unprocessed books.
    private ArrayList<String> getUnprocessedBooks(ArrayList<Book> bookList){
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

    //region XML saving stuff.
    // Creates Books index file.
    public void CreateBooksInfo(ArrayList<Book> entries, boolean getTrueTotal){
        createBooksInfoJSON(entries, getTrueTotal);
    }
    public void createBooksInfoXML(ArrayList<Book> entries, boolean getTrueTotal){
        // index any un-Indexed books as well.
        for(String s : getUnprocessedBooks(entries)){
            entries.add(new Book(s, s));
        }

        Document document = newDocument();
        Element root = document.createElement("Books"); // root element.
        document.appendChild(root); // Append root element.

        for(Book b : entries) {
            try {
                //b.loadXML();

                Element entry = document.createElement("Book");
                root.appendChild(entry);

                Element e = document.createElement("BookName");
                e.appendChild(document.createTextNode(b.getBookName()));
                entry.appendChild(e);

                e = document.createElement("FileName");
                e.appendChild(document.createTextNode(b.getFileName()));
                entry.appendChild(e);

                e = document.createElement("Entries");
                if (getTrueTotal){
                    b.LoadBook();
                    e.appendChild(document.createTextNode(String.valueOf(b.NumOfEntries())));
                } else {
                    e.appendChild(document.createTextNode(String.valueOf(b.getTotalNotes())));
                }
                entry.appendChild(e);
            } catch (FileNotFoundException e) {
                Log.e("DATASTORAGE", "404-Skipping: " + b.getFileName());
            }
        }
        saveDocument(document, "Books.xml", journals);
    }
    public void createBooksInfoJSON(ArrayList<Book> entries, boolean getTrueTotal){
        // index any un-Indexed books as well.
        for(String s : getUnprocessedBooks(entries)){
            entries.add(new Book(s, s));
        }

        JSONArray jsonob = new JSONArray();

        for(Book b : entries) {
            JSONObject n = new JSONObject();
            try {
                n.put("BookName", b.getBookName());
                n.put("FileName", b.getFileName());

                if (getTrueTotal) {
                    b.LoadBook();
                    n.put("Entries", b.NumOfEntries());
                } else {
                    n.put("Entries", b.getTotalNotes());
                }
                jsonob.put(n);
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        try {
            OutputStream os = new FileOutputStream(new File(journals + "/Books.json"));
            OutputStreamWriter osw = new OutputStreamWriter(os);
            osw.write(jsonob.toString());
            osw.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Save a Book into a XML document with the given filename.
    public void SaveBook(ArrayList<Note> journalEntries, String filename){
        saveBookJSON(journalEntries, filename);
    }
    private void saveBookJSON(ArrayList<Note> journalEntries, String filename) {
        // New JSon Object.
        JSONArray jsonob = new JSONArray();

        for(Note j : journalEntries) {
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

        String jsoncoded = jsonob.toString();
    }
    private void saveBookXML(ArrayList<Note> journalEntries, String filename){
        saveBookJSON(journalEntries, filename);
        // New Document.
        Document document = newDocument();

        // Define Root element.
        Element root = document.createElement("Journal"); // root element.
        document.appendChild(root); // Append root element.

        // Iterate though each note.
        for(Note j : journalEntries){
            Element entry = document.createElement("Entry");
            root.appendChild(entry);

            Element e = document.createElement("title");
            e.appendChild(document.createTextNode(j.getTitle()));
            entry.appendChild(e);

//            e = document.createElement("data");
//            e.appendChild(document.createTextNode(j.getData()));
//            entry.appendChild(e);

            e = document.createElement("date");
            e.appendChild(document.createTextNode(j.date.toString()));
            entry.appendChild(e);

            if(j.isEncrypted()) {
                JournalEncodedEntry je = (JournalEncodedEntry)j;
                Log.d("ENCRYPTION", "found note");
                // Append Encrypted Tag.
                e = document.createElement("encrypted");
                e.appendChild(document.createTextNode("True"));
                entry.appendChild(e);
                // Make sure encrypted data was modified.
                if((je).modified == true) {
                    Log.d("ENCRYPTION", "Was Modified. Updating key..");
                    e = document.createElement("data");
                    // Encrypt data before saving it using last used passphrase.
                    String encryptdata = Encryption.Encrypt(je.getData(), je.decryptionkey);
                    e.appendChild(document.createTextNode(encryptdata));

                    entry.appendChild(e);
                    Log.d("ENCRYPTION", "Encryption + Save Complete.");
                    // clear decryption key so the note isn't flagged as modified next save iteration.
                    //je.decryptionkey = null; // Should find a better way to do this.
                    je.modified = false;
                } else {
                    e = document.createElement("data");
                    if(je.decryptionkey != null) {
                        String encryptdata = Encryption.Encrypt(je.getData(), je.decryptionkey);
                        e.appendChild(document.createTextNode(encryptdata));
                    } else {
                        e.appendChild(document.createTextNode(je.getEncoded()));
                    }
                    entry.appendChild(e);
                }
                // Encrypt data then append.
            } else {
                e = document.createElement("data");
                e.appendChild(document.createTextNode(j.getData()));
                entry.appendChild(e);
            }
        }

        // Save the document.
        saveDocument(document, filename, books);
    }

    // Returns a Document Object.
    private Document newDocument(){
        try {
            return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }catch(Exception ex){
            Log.d("ERROR", ex.getMessage());
        }
        return null;
    }

    // Saves a Document Obj to path specified.
    private void saveDocument(Document document, String FileName, String SaveDir) {
        try {
            // Write Content.
            Transformer tf = TransformerFactory.newInstance().newTransformer();

            // Indentation.
            tf.setOutputProperty(OutputKeys.INDENT, "yes");
            tf.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

            DOMSource source = new DOMSource(document);
            StreamResult result = new StreamResult(new File(SaveDir + "/" + FileName));
            tf.transform(source, result);
        } catch (Exception e){
            Log.d("error", e.getMessage());
        }
    }
    //endregion
}
