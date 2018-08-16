package com.alexx_bo.privatatm;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class AtmRender extends DefaultClusterRenderer<AtmItem> {
    Context context;

    public AtmRender(Context context, GoogleMap map, ClusterManager<AtmItem> clusterManager) {
        super(context, map, clusterManager);
        this.context = context;
    }

    @Override
    protected void onBeforeClusterItemRendered(AtmItem item, MarkerOptions markerOptions) {
        if (item.getStatus().equals("Close")) {
            markerOptions.icon(bitmapDescriptorFromVector(context, R.drawable.ic_atm_marker_close));
        } else {
            markerOptions.icon(bitmapDescriptorFromVector(context, R.drawable.ic_atm_marker_open));
        }
        markerOptions.title(item.getPlaceName());
        markerOptions.snippet(item.getAddress());
        super.onBeforeClusterItemRendered(item, markerOptions);
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
