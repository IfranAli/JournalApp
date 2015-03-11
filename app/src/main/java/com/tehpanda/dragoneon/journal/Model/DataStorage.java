package com.tehpanda.dragoneon.journal.Model;

import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DataStorage {
	private Document document = null;
    // Folder Structure.
    private static final String documents = Environment.getExternalStorageDirectory() + "/Documents";
    private static final String journals = documents + "/Journals";
    private static final String books = journals + "/Books";

    private static DataStorage _DataStorage;
    public static DataStorage GetInstance(){
        if(_DataStorage == null){
            _DataStorage = new DataStorage();
        }
        return _DataStorage;
    }

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

    public void SaveDocument(ArrayList<JournalEntry> notes, String filename){
        newDocument();
        saveData(notes);
        saveDocument(filename, books);
    }

    private void newDocument(){
        try {
            this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        }catch(Exception ex){
            Log.d("ERROR", ex.getMessage());
        }
    }

	private void saveDocument(String FileName, String SaveDir) {
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

    private void saveData(ArrayList<JournalEntry> entries){
        Element root = document.createElement("Journal"); // root element.
        document.appendChild(root); // Append root element.

        for(JournalEntry j : entries){
            Element entry = document.createElement("Entry");
            root.appendChild(entry);

            Element e = document.createElement("title");
            e.appendChild(document.createTextNode(j.getTitle()));
            entry.appendChild(e);

            e = document.createElement("data");
            e.appendChild(document.createTextNode(j.getData()));
            entry.appendChild(e);

            e = document.createElement("date");
            e.appendChild(document.createTextNode(j.date.toString()));
            entry.appendChild(e);
        }
    }

    public ArrayList<JournalEntry> loadData(String fileName) throws FileNotFoundException{
        ArrayList<JournalEntry> entries = new ArrayList<JournalEntry>();
        try {
            DocumentBuilderFactory dbFactory =  DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(new File(books + "/" + fileName));

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("Entry");
            Node n = null;
            for (int i = 0; i < nList.getLength(); i++) {
                n = nList.item(i);
                if(n.getNodeType() == Node.ELEMENT_NODE){
                    Element e = (Element)n;
                    String title = e.getElementsByTagName("title").item(0).getTextContent();
                    String date = e.getElementsByTagName("date").item(0).getTextContent();
                    String data = e.getElementsByTagName("data").item(0).getTextContent();
                    entries.add(new JournalEntry(title, data, date));
                }
            }
        } catch (FileNotFoundException e){
            throw e;
        } catch (Exception e){
            Log.wtf("XML PARSER", e.getMessage());
        }
        return entries;
    }

    // Load Book objects from Books.xml.
    public ArrayList<Book> GetBooks(){
        ArrayList<Book> books = new ArrayList<Book>();
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
                    //String entries = e.getElementsByTagName("Entries").item(0).getTextContent(); // derived.
                    books.add(new Book(fileName, bookName));
                }
            }
        } catch (Exception ex){
            Log.e("ERROR", ex.getMessage());
        }
        return books;
    }

    // Removes a journal.
    public void RemoveBook( Book b){
        File f = new File(books + "/" + b.getFileName());
        f.delete();
    }

    // Creates Books index file.
    public void CreateBooksInfo(ArrayList<Book> entries){
        // index any unIndexed books as well.
        for(String s : getUnprocessedBooks(entries)){
            entries.add(new Book(s, s));
        }

        newDocument();
        Element root = document.createElement("Books"); // root element.
        document.appendChild(root); // Append root element.

        for(Book b : entries) {
            try {
                b.loadXML();

                Element entry = document.createElement("Book");
                root.appendChild(entry);

                Element e = document.createElement("BookName");
                e.appendChild(document.createTextNode(b.getBookName()));
                entry.appendChild(e);

                e = document.createElement("FileName");
                e.appendChild(document.createTextNode(b.getFileName()));
                entry.appendChild(e);

                e = document.createElement("Entries");
                e.appendChild(document.createTextNode(String.valueOf(b.NumOfEntries())));
                entry.appendChild(e);
            } catch (FileNotFoundException e) {
                Log.e("DATASTORAGE", "Skipping: " + b.getFileName());
            }
        }
        saveDocument("Books.xml", journals);
    }

    // Gets any unprocessed books.
    ArrayList<String> getUnprocessedBooks(ArrayList<Book> books){
        ArrayList<String> filenames = PopulateBooksArray();
        for(Book b : books){
            if(filenames.contains(b.getFileName()))
                filenames.remove(b.getFileName());
        }
        Log.e("DATASTORAGE_UNPROCESSED:", String.valueOf(filenames.size()));
        return filenames;
    }

    // Scans directory for books and reruns their filenames.
    ArrayList<String> PopulateBooksArray(){
        ArrayList<String> fileNames = new ArrayList<String>();
        File dir = new File(books);
        if(!dir.exists()){
            Log.e("DataStorage", dir.getPath());
            Log.e("DataStorage", "Save Dir not exist");
        } else{
            for (File f : dir.listFiles()){
                if (f.getName().endsWith(".xml")){
                    fileNames.add(f.getName());
                }
            }
        }
        return fileNames;
    }

    public String ExportBookAsText(Book book) {
        File outputfile = new File(documents + "/" + book.getBookName() + ".txt");
        try {
            FileWriter fw = new FileWriter(outputfile);
            BufferedWriter bw = new BufferedWriter(fw);
            for (JournalEntry j : book.getListOfEntries()) {
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
