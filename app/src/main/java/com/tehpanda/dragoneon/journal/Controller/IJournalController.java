package com.tehpanda.dragoneon.journal.Controller;

import com.tehpanda.dragoneon.journal.Model.Book;
import com.tehpanda.dragoneon.journal.Model.IJournalEntry;
import com.tehpanda.dragoneon.journal.Model.JournalEntry;

import java.util.List;

/**
 * Created by panda on 2/24/15.
 */
public interface IJournalController {
    // Adding note to a book.
    void addItem(int currentBook, String title, String data);

    // Delete note in specified book.
    void delete(int currentBook, int index);

    // Get note from a book.
    IJournalEntry getEntry(int currentBook, int index);

    // Get all notes from specified book.
    List<JournalEntry> getListOfEntries(int currentBook);

    // Book Public Methods.
    String GetBookName(int bookIndex);

    // Set Book Name.
    void SetBookName(int bookIndex, String bookName);

    // Get a Book Object.
    Book getBook(int currentBook);

    // Delete a Book.
    void DeleteBook(Book book);

    // Add a Book.
    Book AddNewBook(String bookname);

    // Save Load
    void saveXML(int currentBook);

    List<Book> GetBooks();

    String ExportBookAsText(int position);
}
