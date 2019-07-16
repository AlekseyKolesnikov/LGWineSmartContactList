package net.blu_disc.lg_wine_smart_contactlist;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

class ContactsArrayAdapter extends ArrayAdapter<ContactsData> {
    private Activity activity;
    private float fontSizeName;
    private float fontSizeDate;
    private float fontSizePhone;

    ContactsArrayAdapter(@NonNull Activity activity, @LayoutRes int resource, @NonNull List<ContactsData> objects,
                         float fsName, float fsDate, float fsPhone) {
        super(activity, resource, objects);
        this.activity = activity;
        this.fontSizeName = fsName;
        this.fontSizeDate = fsDate;
        this.fontSizePhone = fsPhone;
    }


    /**
     * This method converts dp unit to equivalent pixels, depending on device density.
     *
     * @param dp A value in dp (density independent pixels) unit. Which we need to convert into pixels
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent px equivalent to dp depending on device density
     */
    private static float convertDpToPixel(float dp, Context context){
        return dp * ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    /**
     * This method converts device specific pixels to density independent pixels.
     *
     * @param px A value in px (pixels) unit. Which we need to convert into db
     * @param context Context to get resources and device specific display metrics
     * @return A float value to represent dp equivalent to px value
     */
    private static float convertPixelsToDp(float px, Context context){
        return px / ((float) context.getResources().getDisplayMetrics().densityDpi / DisplayMetrics.DENSITY_DEFAULT);
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

        ViewGroup.LayoutParams lp = convertView.getLayoutParams();
        lp.height = Math.round(convertDpToPixel((float)((fontSizeName + fontSizePhone) * 1.42), activity));
        convertView.setLayoutParams(lp);

        tvName.setTextSize(fontSizeName);
        tvDate.setTextSize(fontSizeDate);
        tvPhone.setTextSize(fontSizePhone);

        tvName.setText(contactsData.getName());

        Date aDate = new Date(contactsData.getDate());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM HH:mm", Locale.getDefault());
        tvDate.setText(simpleDateFormat.format(aDate));

        tvPhone.setText(contactsData.getNumber());

        return convertView;
    }
}
