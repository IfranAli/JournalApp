package com.tehpanda.dragoneon.journal.Model;

/**
 * Created by panda on 5/20/15.
 */
public class JournalEncodedEntry extends Note implements IJournalEntry {
    public String decryptionkey = null;
    public Boolean modified = false;

    public JournalEncodedEntry(String title, String data, String date) {
        this.data = data;
        this.title = title;
        this.date = parseCalendar(date);
    }

    @Override
    public Boolean isEncrypted() {
        return isEncrypt;
    }

    @Override
    public Boolean Decrypt(String key) {
        try {
            data = Encryption.Decrypt(this.data.getBytes(), key);
            decryptionkey = key;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public String getEncoded() {
        return  data;
    }

    @Override
    public String getData() {
        if(decryptionkey != null) {
            return data;
        }
        return "##Encrypted##";
    }

    @Override
    public void setData(String data) {
        if(decryptionkey != null) {
            this.data = data;
            modified = true;
        }
    }

    @Override
    protected String getPreviewText(String data, int cutOff) {
        if(decryptionkey != null) {
            return super.getPreviewText(data, cutOff);
        }
        return "##Encrypted##";
    }

    @Override
    public String GetDataPreview() {
        if(decryptionkey != null) {
            return super.GetDataPreview();
        }
        return "##Encrypted##";
    }

    public void RemovePassword() {
        isEncrypt = false;
    }
}
