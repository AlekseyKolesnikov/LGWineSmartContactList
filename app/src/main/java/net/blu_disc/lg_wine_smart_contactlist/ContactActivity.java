package net.blu_disc.lg_wine_smart_contactlist;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;

public class ContactActivity extends AppCompatActivity {
    public final static String strFSName = "fontsizeName";
    public final static String strFSDate = "fontsizeDate";
    public final static String strFSPhone = "fontsizePhone";
    public final static int iFSName = 22;
    public final static int iFSDate = 14;
    public final static int iFSPhone = 16;

    private ListView listContacts;
    private String sFilter = "*";
    private String sCaption = "";

    private float fontsizeName = iFSName;
    private float fontsizeDate = iFSDate;
    private float fontsizePhone = iFSPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);

        loadOptions();

        listContacts = findViewById(R.id.conactlist);
        loadContactsFromLog();

        listContacts.setOnKeyListener(new ContactsViewKeyListener());
    }

    private void loadOptions() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        fontsizeName = Float.valueOf(sPref.getString(strFSName, Integer.toString(iFSName)));
        fontsizeDate = Float.valueOf(sPref.getString(strFSDate, Integer.toString(iFSDate)));
        fontsizePhone = Float.valueOf(sPref.getString(strFSPhone, Integer.toString(iFSPhone)));
    }

    private void saveOptions() {
        SharedPreferences sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor ed = sPref.edit();
        ed.putString(strFSName, Float.toString(fontsizeName));
        ed.putString(strFSDate, Float.toString(fontsizeDate));
        ed.putString(strFSPhone, Float.toString(fontsizePhone));
        ed.apply();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Settings");
        return super.onCreateOptionsMenu(menu);
    }

    private float checkFontSize(float fontsize) {
        if (fontsize < 10) fontsize = 10;
        if (fontsize > 50) fontsize = 50;
        return fontsize;
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) return;

        fontsizeName = checkFontSize(Float.valueOf(data.getStringExtra(strFSName)));
        fontsizeDate = checkFontSize(Float.valueOf(data.getStringExtra(strFSDate)));
        fontsizePhone = checkFontSize(Float.valueOf(data.getStringExtra(strFSPhone)));

        saveOptions();

        reloadData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this, SettingsActivity.class);
        intent.putExtra(strFSName, fontsizeName);
        intent.putExtra(strFSDate, fontsizeDate);
        intent.putExtra(strFSPhone, fontsizePhone);
        startActivityForResult(intent, 1);

        return super.onOptionsItemSelected(item);
    }

    private void reloadData() {
        if (sFilter.length() > 1)
            loadContactsFromContacts();
        else
            loadContactsFromLog();

        if (listContacts.getCount() == 0 && sFilter.length() > 1) {
            removeLastFilter();
            if (sFilter.length() > 1)
                loadContactsFromContacts();
            else
                loadContactsFromLog();
        }
    }

    private void loadContactsFromLog() {
        setTitle("Call log");

        String[] projection = new String[] {
                CallLog.Calls._ID,
                CallLog.Calls.CACHED_NAME,
                CallLog.Calls.DATE,
                CallLog.Calls.CACHED_FORMATTED_NUMBER,
                CallLog.Calls.CACHED_NORMALIZED_NUMBER };

        String orderBy = CallLog.Calls.DATE + " DESC";

        String mSelectionClause = CallLog.Calls.DATE + " > ?";

        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        String[] mSelectionArgs = { "" + cal.getTimeInMillis() };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED)
            return;
        Cursor cursor = getContentResolver().query(CallLog.Calls.CONTENT_URI, projection, mSelectionClause, mSelectionArgs, orderBy);
        assert cursor != null;
        if (cursor.getCount() == 0)
            return;

        int idxName = cursor.getColumnIndex(CallLog.Calls.CACHED_NAME);
        int idxDate = cursor.getColumnIndex(CallLog.Calls.DATE);
        int idxNumber = cursor.getColumnIndex(CallLog.Calls.CACHED_FORMATTED_NUMBER);
        int idxUniNumber = cursor.getColumnIndex(CallLog.Calls.CACHED_NORMALIZED_NUMBER);

        fillContactList(cursor, idxName, idxDate, idxUniNumber, idxNumber);
        cursor.close();
    }

    private void loadContactsFromContacts() {
        setTitle(sCaption);

        String[] projection = new String[] {
                ContactsContract.CommonDataKinds.Phone._ID,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED,
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER };//NUMBER, NORMALIZED_NUMBER

        String orderBy = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC";

        String mSelectionClause = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " GLOB '" + sFilter + "*'";

        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, mSelectionClause, null, orderBy);
        assert cursor != null;
        if (cursor.getCount() == 0)
            return;

        int idxName = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
        int idxDate = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LAST_TIME_CONTACTED);
        int idxNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
        int idxUniNumber = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NORMALIZED_NUMBER);

        fillContactList(cursor, idxName, idxDate, idxUniNumber, idxNumber);
        cursor.close();
    }

    private void fillContactList(Cursor cursor, int idxName, int idxDate, int idxUniNumber, int idxNumber) {
        ArrayList<ContactsData> arrayLog = new ArrayList<>();
        ArrayList<String> uniNumbers = new ArrayList<>();

        cursor.moveToFirst();

        do {
            String sNumber = cursor.getString(idxUniNumber);
            if (sNumber == null)
                sNumber = cursor.getString(idxNumber);
            if (sNumber == null)
                sNumber = "";

            boolean found = false;
            for (int i = 0; i < uniNumbers.size(); i++) {
                if (uniNumbers.get(i).equals(sNumber)){
                    found = true;
                    break;
                }
            }

            if (!found) {
                uniNumbers.add(sNumber);
                sNumber = cursor.getString(idxNumber);

                String sName = cursor.getString(idxName);
                if (sName == null || sName.length() == 0)
                    sName = sNumber;

                ContactsData logData = new ContactsData(sName, cursor.getLong(idxDate), sNumber);
                arrayLog.add(logData);
            }
        } while (cursor.moveToNext());

        ListAdapter adapter = new ContactsArrayAdapter(this, R.layout.item, arrayLog, fontsizeName, fontsizeDate, fontsizePhone);
        listContacts.setAdapter(adapter);
    }

    private void removeLastFilter() {
        int iBracket = sFilter.lastIndexOf('[');
        int iOne = sFilter.lastIndexOf('1');
        if ((iOne > -1) && (iBracket > iOne))
            iBracket = iOne;
        if (iBracket == -1)
            iBracket = 1;
        sFilter = sFilter.substring(0, iBracket);
        if (sCaption.length() > 0)
            sCaption = sCaption.substring(0, sCaption.length() - 1);
    }

    private class ContactsViewKeyListener implements View.OnKeyListener {
        final int KEY_DELETE = 174;

        public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
            if (keyCode == KeyEvent.KEYCODE_DEL)
                return true;

            //int eventKeyCode = keyEvent.getKeyCode();
            int keyAction = keyEvent.getAction();
            int scanCode = keyEvent.getScanCode();

            if (keyAction == KeyEvent.ACTION_UP) {
                if (keyCode == KeyEvent.KEYCODE_CALL) {
                    if (listContacts.getSelectedView() != null) {
                        makeCall();
                        return true;
                    }
                } else
                if (keyCode == KeyEvent.KEYCODE_DPAD_CENTER) {
                    if (listContacts.getSelectedView() != null) {
                        openContact();
                        return true;
                    }
                } else
                    return (scanCode == KEY_DELETE);
            }

            Boolean needReload = updateFilterFromKeyCode(keyCode);

            if (scanCode == KEY_DELETE) {
                if (sFilter.length() > 1)
                    removeLastFilter();
                needReload = true;
            }

            if (needReload) {
                reloadData();
                return true;
            }

            return false;
        }

        private Boolean updateFilterFromKeyCode(int keyCode) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_1:
                    sFilter = sFilter + "1";
                    sCaption = sCaption + "1";
                    break;
                case KeyEvent.KEYCODE_2:
                    sFilter = sFilter + "[2aAbBcC]";
                    sCaption = sCaption + "2";
                    break;
                case KeyEvent.KEYCODE_3:
                    sFilter = sFilter + "[3dDeEfF]";
                    sCaption = sCaption + "3";
                    break;
                case KeyEvent.KEYCODE_4:
                    sFilter = sFilter + "[4gGhHiI]";
                    sCaption = sCaption + "4";
                    break;
                case KeyEvent.KEYCODE_5:
                    sFilter = sFilter + "[5jJkKlL]";
                    sCaption = sCaption + "5";
                    break;
                case KeyEvent.KEYCODE_6:
                    sFilter = sFilter + "[6mMnNoO]";
                    sCaption = sCaption + "6";
                    break;
                case KeyEvent.KEYCODE_7:
                    sFilter = sFilter + "[7pPqQrRsS]";
                    sCaption = sCaption + "7";
                    break;
                case KeyEvent.KEYCODE_8:
                    sFilter = sFilter + "[8tTuUvV]";
                    sCaption = sCaption + "8";
                    break;
                case KeyEvent.KEYCODE_9:
                    sFilter = sFilter + "[9wWxXyYzZ]";
                    sCaption = sCaption + "9";
                    break;
                case KeyEvent.KEYCODE_0:
                    sFilter = sFilter + "[0 ]";
                    sCaption = sCaption + "0";
                    break;
                default:
                    return false;
            }
            return true;
        }

        private void makeCall() {
            TextView vPhone = listContacts.getSelectedView().findViewById(R.id.tvPhone);
            if (ActivityCompat.checkSelfPermission(listContacts.getContext(), Manifest.permission.CALL_PHONE) ==
                    PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + vPhone.getText()));
                startActivity(intent);
            }
            finishAffinity();
        }

        private void openContact() {
            String sPhone = ((TextView)listContacts.getSelectedView().findViewById(R.id.tvPhone)).getText().toString();
            Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(sPhone));
            ContentResolver contentResolver = getContentResolver();
            String[] projection = new String[] { ContactsContract.PhoneLookup._ID };

            Cursor cursor = contentResolver.query(uri, projection, null, null, null);

            int contactId = -1;
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    contactId = cursor.getInt(cursor.getColumnIndexOrThrow(ContactsContract.PhoneLookup._ID));
                }
                cursor.close();
            }

            if (contactId > -1) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
                intent.setData(contactUri);
                startActivity(intent);
            } else {
                Intent intent = new Intent(Intent.ACTION_INSERT);
                intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
                intent.putExtra(ContactsContract.Intents.Insert.PHONE, sPhone);
                startActivity(intent);
            }

            //finishAffinity();
        }
    }
}
