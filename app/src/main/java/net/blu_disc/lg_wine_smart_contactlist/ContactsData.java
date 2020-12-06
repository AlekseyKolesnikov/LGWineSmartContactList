package net.blu_disc.lg_wine_smart_contactlist;

class ContactsData {
    private final String mName;
    private final long mDate;
    private final String mNumber;

    ContactsData(String name, long date, String number) {
        mName = name;
        mDate = date;
        mNumber = number;
    }

    String getName() {
        return mName;
    }

    long getDate() {
        return mDate;
    }

    String getNumber() {
        return mNumber;
    }
}
