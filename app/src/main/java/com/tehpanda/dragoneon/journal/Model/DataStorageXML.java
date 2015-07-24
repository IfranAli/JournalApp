package com.tehpanda.dragoneon.journal.Model;

import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class DataStorageXML extends DataStorage implements IDataStorage {

    @Override
    public List<Note> GetBook(String filename) throws FileNotFoundException {
        ArrayList<Note> entries = new ArrayList<>();
        try {
            Document doc = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder()
                    .parse(new File(books + "/" + filename));

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

    @Override
    public List<Book> GetBooks() {
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
                    books.add(new Book(fileName, bookName, Integer.parseInt(entries), this));
                }
            }
        } catch (Exception ex){
            Log.e("ERROR", ex.getMessage());
        }
        return books;
    }

    @Override
    public void SaveBooks(List<Note> notes, String filename) {
        // New Document.
        Document document = newDocument();

        // Define Root element.
        Element root = document.createElement("Journal"); // root element.
        document.appendChild(root); // Append root element.

        // Iterate though each note.
        for(Note j : notes){
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

    @Override
    public void CreateIndexFile(List<Book> books, boolean calculateTrueTotal) {
        // index any un-Indexed books as well.
        for(String s : getUnprocessedBooks(books)){
            books.add(new Book(s, s, this));
        }

        Document document = newDocument();
        Element root = document.createElement("Books"); // root element.
        document.appendChild(root); // Append root element.

        for(Book b : books) {
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
                if (calculateTrueTotal){
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
}