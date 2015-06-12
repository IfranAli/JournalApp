package com.tehpanda.dragoneon.journal.Model;

public class JournalEntry extends Note implements IJournalEntry {

	public JournalEntry(String title, String data, String date) {
		this.data = data;
		this.title = title;
		this.date = parseCalendar(date);
	}

    @Override
    public Boolean isEncrypted() {
        return isEncrypt;
    }

    public String decryptionkey = null;
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
}
