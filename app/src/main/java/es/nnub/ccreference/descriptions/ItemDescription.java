package es.nnub.ccreference.descriptions;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import es.nnub.ccreference.R;
import es.nnub.ccreference.services.FloatingWidgetService;

public class ItemDescription extends BaseDescriptionAdapter {

    private Drawable image;
    private int summary;
    private int stats;
    private int description;

    public ItemDescription(@NonNull Context context) {
        super(context);
    }

    public void loadResourcesFromName(String resourceName) {
        Context context = getContext();
        Resources res = context.getResources();

        image = res.getDrawable(
                res.getIdentifier(resourceName + "_icon", "drawable", context.getPackageName()),
                null
        );
        image.setFilterBitmap(false);
        summary = res.getIdentifier(resourceName + "_summary", "string", context.getPackageName());
        stats = res.getIdentifier(resourceName + "_stats", "string", context.getPackageName());
        description = res.getIdentifier(resourceName + "_description", "string", context.getPackageName());
    }

    @Override
    public View initializeView(@NonNull Context context, @NonNull ViewGroup container, FloatingWidgetService.FloatingWidgetCallbacks floatingWidgetCallbacks) {
        LayoutInflater li = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        View convertView = li.inflate(R.layout.item_description, container, true);

        ImageView imageView = convertView.findViewById(R.id.image1);
        TextView summaryView = convertView.findViewById(R.id.text_summary);
        TextView statsView = convertView.findViewById(R.id.text_stats);
        TextView descriptionView = convertView.findViewById(R.id.text_description);

        imageView.setImageDrawable(image);
        summaryView.setText(summary);
        statsView.setText(stats);
        descriptionView.setText(description);

        return convertView;
    }
}
