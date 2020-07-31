package es.nnub.ccreference;

import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class CustomListActivity extends BaseActionBarActivity {
    String[] myLabels;
    TypedArray myImages;
    String[] myLinks;

    private class ImageTitlePair {
        public int image;
        public String title;

        public ImageTitlePair(int image, String title) {
            this.image = image;
            this.title = title;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void autoFillListViewWithPassedData() {
        try {
            Bundle extras = getIntent().getExtras();
            String list = extras.getString("category");
            //System.out.println(list);
            int labelsRef = getResources().getIdentifier(list, "array", getPackageName());
            int iconsRef = getResources().getIdentifier(list + "_icons", "array", getPackageName());
            int linksRef = getResources().getIdentifier(list + "_links", "array", getPackageName());
            fillListView(labelsRef, iconsRef, linksRef);
        } catch(RuntimeException e) {
            e.printStackTrace();
        }
    }

    protected void fillListView(int labelsRes, int iconsRes, int linksRes) {
        myLabels = getResources().getStringArray(labelsRes);
        myImages = getResources().obtainTypedArray(iconsRes);
        myLinks = getResources().getStringArray(linksRes);

        ArrayList<ImageTitlePair> myListData = new ArrayList<>();

        for (int i = 0; i < myLabels.length; i++) {
            myListData.add(new ImageTitlePair(myImages.getResourceId(i, 0), myLabels[i]));
        }

        ImageTitlePair[] myData = new ImageTitlePair[myListData.size()];
        myListData.toArray(myData);

        ListView listView = findViewById(R.id.mainContentView);
        listView.setAdapter(new CustomArrayAdapter<ImageTitlePair>(this, R.layout.index_single_item, myData) {
            public View getFilledLayoutForData(ImageTitlePair item, @NonNull View convertView, int position) {
                TextView tv = convertView.findViewById(R.id.text1);
                ImageView iv = convertView.findViewById(R.id.image1);
                try {
                    tv.setText(item.title);
                    Drawable image = getResources().getDrawable(item.image, null);
                    image.setFilterBitmap(false);
                    iv.setImageDrawable(image);
                } catch (NullPointerException e) {
                    tv.setText(R.string.customarrayadapter_error);
                }

                return convertView;
            }
        });
        listView.setOnItemClickListener(new ListView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                onListItemClick((ListView) parent, v, position, id);
            }
        });
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        String link = myLinks[position];

        if (link.startsWith("list_")) {
            String listName = link.substring(5);
            Intent i = new Intent(this, CategoryActivity.class);
            i.putExtra("itemName", myLabels[position]);
            i.putExtra("category", listName);
            startActivity(i);
        } else if (link.startsWith("desc_")) {
            String listName = link.substring(5);
            Intent i = new Intent(this, DescriptionActivity.class);
            i.putExtra("itemName", myLabels[position]);
            i.putExtra("name", listName);
            startActivity(i);
        }
    }
}
