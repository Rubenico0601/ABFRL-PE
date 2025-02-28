package com.example.ctpecommerce;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

public class CarouselAdapter extends RecyclerView.Adapter<CarouselAdapter.ViewHolder> {

    private List<Integer> imageList;
    private List<String> labelList;
    private boolean showLabels;

    // Constructor for Circular images with labels
    public CarouselAdapter(List<Integer> imageList, List<String> labelList) {
        this.imageList = imageList;
        this.labelList = labelList;
        this.showLabels = true;
    }

    // Constructor for Square images without labels
    public CarouselAdapter(List<Integer> imageList) {
        this.imageList = imageList;
        this.showLabels = false;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView imageLabel;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image_view);
            imageLabel = itemView.findViewById(R.id.image_label);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.carousel_item, parent, false);

        // Ensure the view fills the ViewPager2 page
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
        view.setLayoutParams(layoutParams);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        int actualPosition = position % imageList.size();

        // Load the image using Glide
        Glide.with(holder.imageView.getContext())
                .load(imageList.get(actualPosition))
                .override(300, 300)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .into(holder.imageView);

        // Show labels for circular images only
        if (showLabels && labelList != null && actualPosition < labelList.size()) {
            holder.imageLabel.setText(labelList.get(actualPosition));
            holder.imageLabel.setVisibility(View.VISIBLE);
        } else {
            holder.imageLabel.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }
}
