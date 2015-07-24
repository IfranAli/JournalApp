package com.tehpanda.dragoneon.journal.Controller;

import com.tehpanda.dragoneon.journal.Model.Book;
import com.tehpanda.dragoneon.journal.Model.DataStorageJson;
import com.tehpanda.dragoneon.journal.Model.IDataStorage;
import com.tehpanda.dragoneon.journal.Model.Note;

import java.util.List;

/**
 * Created by panda on 7/24/15.
 */
public class BookLibrary {
    private List<Book> mBooks;
    private IDataStorage mDataStorage;

    public BookLibrary() {
        mDataStorage = new DataStorageJson();
    }

    // Public Methods.
    public Boolean CreateBook (String nameOfBook) {
        Boolean ok =  mBooks.add(new Book(nameOfBook, mDataStorage));
        mDataStorage.CreateIndexFile(mBooks, false);
        return ok;
    }

    public Book GetBook(int bookIndex) {
        return mBooks.get(bookIndex);
    }

    public void DeleteBook(Book book) {
        mDataStorage.RemoveBook(book);
        mDataStorage.CreateIndexFile(mBooks, false);
    }

    // Misc
    public List<Book> GetAllBooks() {
        if (mBooks == null) {
            mBooks = mDataStorage.GetBooks();
            mDataStorage.CreateIndexFile(mBooks, false);
        }
        return mBooks;
    }

    public List<Note> GetRecentNotes() {
        Book b = new Book("test", mDataStorage);
        b.AddNewNote("Testing", "Recents");
        b.AddNewNote("Testing", "1");
        b.AddNewNote("Testing", "2");
        b.AddNewNote("Testing", "3");
        b.AddNewNote("Testing", "4");
        return b.getListOfEntries();
    }

    // SettingsFile
    public void GenerateSettingsFile() {
        mDataStorage.CreateIndexFile(mBooks, false);
    }
}
