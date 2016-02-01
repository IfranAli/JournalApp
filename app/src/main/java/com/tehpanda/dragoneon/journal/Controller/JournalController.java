package com.tehpanda.dragoneon.journal.Controller;

import android.util.Log;

import com.tehpanda.dragoneon.journal.Model.Book;
import com.tehpanda.dragoneon.journal.Model.IJournalEntry;
import com.tehpanda.dragoneon.journal.Model.JournalEncodedEntry;
import com.tehpanda.dragoneon.journal.Model.Note;

import java.util.List;

public class JournalController implements IJournalController {
    //private DataStorage dataStorage = DataStorage.GetInstance();
    //private ArrayList<Book> Books;
    private BookLibrary mBookLibrary;

    // Singleton.
    private static IJournalController journalController;
    public static IJournalController GetInstance(){
        if (journalController == null)
            journalController = new JournalController();
        return journalController;
    }

    // Constructor.
    JournalController() {
        mBookLibrary = new BookLibrary();
        //Books = dataStorage.GetBooks();
        Log.e("alert", "recreating books.xml");
        //dataStorage.CreateBooksInfo(Books, false);
    }

    @Override
    public boolean HasBooks() {
        return !mBookLibrary.GetAllBooks().isEmpty();
    }

    // Adding note to a book.
    @Override
    public void addItem(int currentBook, String title, String data) {
        mBookLibrary.GetBook(currentBook).AddNewNote(title, data);
    }
    // Delete note in specified book.
    @Override
    public void delete(int currentBook, int index) {
        //Books.get(currentBook).Delete(index);
        mBookLibrary.GetBook(currentBook).Delete(index);
    }

    // Get note from a book.
    @Override
    public IJournalEntry getEntry(int currentBook, int index) {
        //return Books.get(currentBook).GetEntry(index);
        return mBookLibrary.GetBook(currentBook).GetEntry(index);
    }
    // Convert to password protected note.
    @Override
    public JournalEncodedEntry makePasswordProtected(int currentBook, int position) {
        Note e = mBookLibrary.GetBook(currentBook).GetEntry(position);
        JournalEncodedEntry ee = mBookLibrary.getmDataStorage().GetProtectedNote(e);
        mBookLibrary.GetBook(currentBook).SetEntry(position, ee);
        return ee;
    }
    // Get all notes from specified book.
    @Override
    public List<Note> getListOfEntries(int currentBook) {
        //return Books.get(currentBook).getListOfEntries();
        return mBookLibrary.GetBook(currentBook).getListOfEntries();
    }

    @Override
    public List<Note> GetRecents() {
        return mBookLibrary.GetRecentNotes();
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
        mBookLibrary.GenerateSettingsFile();
    }
    // Get a Book Object.
    @Override
    public Book getBook(int currentBook) {
        return mBookLibrary.GetBook(currentBook);
    }
    // Delete a Book.
    @Override
    public void DeleteBook(Book book){
        mBookLibrary.DeleteBook(book);
    }
    // Add a Book.
    @Override
    public Boolean AddNewBook(String bookname){
        return mBookLibrary.CreateBook(bookname);
    }

    // Save Load
    @Override
    public void saveXML(int currentBook) {
        mBookLibrary.GetBook(currentBook).SaveBook();
        mBookLibrary.GenerateSettingsFile(); // <-- this should be done in datastorage class.
    }

    @Override
    public List<Book> GetBooks() {
        return mBookLibrary.GetAllBooks();
    }

    @Override
    public String ExportBookAsText(int position) {
        return mBookLibrary.GetBook(position).ExportToTextFile();
        // Might want to return a boolean confirmation.
    }

    @Override
    public void RebuildBooksXml() {
        mBookLibrary.GenerateSettingsFile();
    }
}