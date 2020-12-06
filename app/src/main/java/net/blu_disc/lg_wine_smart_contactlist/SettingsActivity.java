package net.blu_disc.lg_wine_smart_contactlist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

public class SettingsActivity extends AppCompatActivity {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        getWindow().setLayout((dm.widthPixels), (int)(dm.heightPixels * 0.65));

        EditText edNameFontSize = findViewById(R.id.edNameFontSize);
        EditText edDateFontSize = findViewById(R.id.edDateFontSize);
        EditText edPhoneFontSize = findViewById(R.id.edPhoneFontSize);

        Intent intent = getIntent(); // gets the previously created intent

        edNameFontSize.setText(Integer.toString((int)intent.getFloatExtra(ContactActivity.strFSName, ContactActivity.iFSName)));
        edDateFontSize.setText(Integer.toString((int)intent.getFloatExtra(ContactActivity.strFSDate, ContactActivity.iFSDate)));
        edPhoneFontSize.setText(Integer.toString((int)intent.getFloatExtra(ContactActivity.strFSPhone, ContactActivity.iFSPhone)));
    }

    public void btnOKClick(View view) {
        EditText edNameFontSize = findViewById(R.id.edNameFontSize);
        EditText edDateFontSize = findViewById(R.id.edDateFontSize);
        EditText edPhoneFontSize = findViewById(R.id.edPhoneFontSize);

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
