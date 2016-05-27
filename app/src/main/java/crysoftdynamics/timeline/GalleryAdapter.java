package crysoftdynamics.timeline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by Maxx on 5/26/2016.
 */
public class GalleryAdapter extends ArrayAdapter<GalleryItem> {
    private Context context;
    private int resourceId;

    public GalleryAdapter(Context context, int resourceId, List<GalleryItem> items, boolean useList) {
        super(context, resourceId, items);
        this.context = context;
        this.resourceId = resourceId;
    }

    /*Java Geeks say this improves the speed and reduces the calls to findViewById. Smart Chap so we'll go with his idea **/
    private class ViewHolder {
        ImageView snapImageView;
    }

    /**
     * Let's populate the View Holder we just created
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        //
        ViewHolder viewHolder = null;
        GalleryItem galleryItem = getItem(position);
        View viewToBeUsed = null;
        //Let's CONDITIONALLY infLate the list view based on whether we want a Grid or List View
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null){
            viewHolder = new ViewHolder();
            viewToBeUsed = mInflater.inflate(resourceId,null);
            viewHolder.snapImageView = (ImageView) viewToBeUsed.findViewById(R.id.imageView);
            viewToBeUsed.setTag(viewHolder);
        }else{
            viewToBeUsed = convertView;
            viewHolder = (ViewHolder) viewToBeUsed.getTag();
        }
        viewHolder.snapImageView.setImageURI(galleryItem.getThumbnailUri());
                return viewToBeUsed;

    }
}
