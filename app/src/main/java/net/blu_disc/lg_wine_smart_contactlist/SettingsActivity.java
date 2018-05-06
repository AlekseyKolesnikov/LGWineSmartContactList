package net.blu_disc.lg_wine_smart_contactlist;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        EditText edNameFontSize = (EditText) findViewById(R.id.edNameFontSize);
        EditText edDateFontSize = (EditText) findViewById(R.id.edDateFontSize);
        EditText edPhoneFontSize = (EditText) findViewById(R.id.edPhoneFontSize);

        Intent intent = getIntent(); // gets the previously created intent

        edNameFontSize.setText(Integer.toString((int)intent.getFloatExtra(ContactActivity.strFSName, ContactActivity.iFSName)));
        edDateFontSize.setText(Integer.toString((int)intent.getFloatExtra(ContactActivity.strFSDate, ContactActivity.iFSDate)));
        edPhoneFontSize.setText(Integer.toString((int)intent.getFloatExtra(ContactActivity.strFSPhone, ContactActivity.iFSPhone)));
    }

    public void btnOKClick(View view) {
        EditText edNameFontSize = (EditText) findViewById(R.id.edNameFontSize);
        EditText edDateFontSize = (EditText) findViewById(R.id.edDateFontSize);
        EditText edPhoneFontSize = (EditText) findViewById(R.id.edPhoneFontSize);

        Intent intent = new Intent();
        intent.putExtra(ContactActivity.strFSName, edNameFontSize.getText().toString());
        intent.putExtra(ContactActivity.strFSDate, edDateFontSize.getText().toString());
        intent.putExtra(ContactActivity.strFSPhone, edPhoneFontSize.getText().toString());
        setResult(RESULT_OK, intent);

        this.finish();
    }

    public void btnCancelClick(View view) {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED, intent);
        this.finish();
    }
}
