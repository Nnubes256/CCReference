package es.nnub.ccreference;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

public class CustomArrayAdapter<T> extends ArrayAdapter<T> {
    private Context context;
    private int itemRes;

    public CustomArrayAdapter(@NonNull Context context, int resource, @NonNull T[] objects) {
        super(context, android.R.layout.simple_list_item_1, objects);

        this.context = context;
        this.itemRes = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup container) {
        if (convertView == null) {
            LayoutInflater li = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            convertView = li.inflate(itemRes, container, false);
        }

        T item = this.getItem(position);
        convertView = this.getFilledLayoutForData(item, convertView, position);

        return convertView;
    }

    @NonNull
    public View getFilledLayoutForData(T item, @NonNull View convertView, int position) {
        TextView tv = convertView.findViewById(R.id.text1);
        try {
            System.out.println(position);
            tv.setText(item.toString());
        } catch (NullPointerException e) {
            tv.setText(R.string.customarrayadapter_error);
        }

        return convertView;
    }
}
