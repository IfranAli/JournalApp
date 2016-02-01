package com.tehpanda.dragoneon.journal.Model;

import java.io.FileNotFoundException;
import java.util.List;

public interface IDataStorage {

    List<Note> GetBook(String filename) throws FileNotFoundException;
    List<Book> GetBooks();
    void SaveBooks(List<Note> notes, String filename);
    void CreateIndexFile(List<Book> books, boolean calculateTrueTotal);

    void RemoveBook(Book book);
    String ExportBookAsText(Book book);

    JournalEncodedEntry GetProtectedNote(Note n);
}
