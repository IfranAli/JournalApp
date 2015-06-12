package com.tehpanda.dragoneon.journal.Model;

/**
 * Created by panda on 9/12/14.
 */
public interface IJournalEntry {
    public Boolean isEncrypted();
    public Boolean Decrypt(String key);
    public String getData();
    public void setData(String data);
    public String getTitle();
    public void setTitle(String title);

    public int GetDayColor();
    public String GetDay();
    public String GetMonth();
    public String GetTitlePreview();
    public String GetDataPreview();
    public String GetTime();
}
