package com.tehpanda.dragoneon.journal.Controller;
import android.app.Activity;
import android.content.Context;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ArrayAdapter;

import com.tehpanda.dragoneon.journal.Model.*;
import com.tehpanda.dragoneon.journal.View.item_adapter_book;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class JournalController implements IJournalController {
    private DataStorage dataStorage = DataStorage.GetInstance();
    private ArrayList<Book> Books;

    // Singleton.
    private static IJournalController journalController;
    public static IJournalController GetInstance(){
        if (journalController == null)
            journalController = new JournalController();
        return journalController;
    }

    // Constructor.
    JournalController() {
        Books = dataStorage.GetBooks();
        Log.e("alert", "recreating books.xml");
        dataStorage.CreateBooksInfo(Books);
    }

    // Current Date formatted for saving.
    private static String currDateFormatted(){
        return new SimpleDateFormat("dd:MM:yyyy:HH:mm:s").format(Calendar.getInstance().getTime());
    }
    // Adding note to a book.
    @Override
    public void addItem(int currentBook, String title, String data) {
        Books.get(currentBook).AddEntry(title, data, currDateFormatted());
    }
    // Delete note in specified book.
    @Override
    public void delete(int currentBook, int index) {
        Books.get(currentBook).Delete(index);
    }

    // Get note from a book.
    @Override
    public IJournalEntry getEntry(int currentBook, int index) {
        return Books.get(currentBook).GetEntry(index);
    }
    // Get all notes from specified book.
    @Override
    public List<JournalEntry> getListOfEntries(int currentBook) {
        return Books.get(currentBook).getListOfEntries();
    }

    // Book Public Methods.
    @Override
    public String GetBookName(int bookIndex){
        return getBook(bookIndex).getBookName();
    }
    // Set Book Name.
    @Override
    public void SetBookName(int bookIndex, String bookName){
        getBook(bookIndex).setBookName(bookName);
        // Save Changes.
        regenerateBooksXML();
    }
    // Get a Book Object.
    @Override
    public Book getBook(int currentBook) {
        return Books.get(currentBook);
    }
    // Delete a Book.
    @Override
    public void DeleteBook(Book book){
        dataStorage.RemoveBook(book);
        Books.remove(book);
        regenerateBooksXML();
    }
    // Add a Book.
    @Override
    public Book AddNewBook(String bookname){
        Book book = new Book(bookname);
        Books.add(book);
        regenerateBooksXML();
        Log.e("BookFileName", book.getFileName());
        return book;
    }

    // Book Private Methods.
    private void regenerateBooksXML(){
        dataStorage.CreateBooksInfo(Books);
    }
    // Save Load
    @Override
    public void saveXML(int currentBook) {
        Books.get(currentBook).saveXML();
    }

    @Override
    public List<Book> GetBooks() {
        return Books;
    }

    @Override
    public String ExportBookAsText(int position) {
        return dataStorage.ExportBookAsText(getBook(position));
    }
}