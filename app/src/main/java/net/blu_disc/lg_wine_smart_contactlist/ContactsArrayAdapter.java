package net.blu_disc.lg_wine_smart_contactlist;

import android.app.Activity;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class ContactsArrayAdapter extends ArrayAdapter<ContactsData> {
    private Activity activity = null;

    ContactsArrayAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List<ContactsData> objects) {
        super(activity, resource, objects);
        this.activity = activity;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ContactsData contactsData = getItem(position);

        if (contactsData == null)
            return super.getView(position, convertView, parent);

        // Inflate only once
        if (convertView == null)
            convertView = activity.getLayoutInflater().inflate(R.layout.item, parent, false);

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvDate = convertView.findViewById(R.id.tvCallDateTime);
        TextView tvPhone = convertView.findViewById(R.id.tvPhone);

        tvName.setText(contactsData.getName());

        Date d = new Date(contactsData.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
        tvDate.setText(simpleDateFormat.format(d));

        tvPhone.setText(contactsData.getNumber());

        return convertView;
    }
}
