package com.tehpanda.dragoneon.journal.Model;

import android.os.Environment;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
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

    // Load Book from given filename.
    public ArrayList<JournalEntry> loadBook(String fileName) throws FileNotFoundException{
        ArrayList<JournalEntry> entries = new ArrayList<>();
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
                    entries.add(new JournalEntry(
                            e.getElementsByTagName("title").item(0).getTextContent(),
                            e.getElementsByTagName("data").item(0).getTextContent(),
                            e.getElementsByTagName("date").item(0).getTextContent()
                    ));
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
                if (f.getName().endsWith(".xml")){
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

    //region XML saving stuff.
    // Creates Books index file.
    public void CreateBooksInfo(ArrayList<Book> entries, boolean getTrueTotal){
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

    // Save a Book into a XML document with the given filename.
    public void SaveBook(ArrayList<JournalEntry> journalEntries, String filename){
        // New Document.
        Document document = newDocument();

        // Define Root element.
        Element root = document.createElement("Journal"); // root element.
        document.appendChild(root); // Append root element.

        // Iterate though each note.
        for(JournalEntry j : journalEntries){
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
