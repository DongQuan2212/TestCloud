package hcmute.edu.vn.could;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private List<ImageModel> imageList;
    private ImageClickListener listener;

    public interface ImageClickListener {
        void onShareClick(ImageModel image);
        void onDeleteClick(ImageModel image);
    }

    public ImageAdapter(List<ImageModel> imageList, ImageClickListener listener) {
        this.imageList = imageList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_image, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        ImageModel image = imageList.get(position);
        holder.tvUrl.setText(image.getUrl());
        Glide.with(holder.itemView.getContext())
                .load(image.getUrl())
                .into(holder.imageView);

        holder.btnShare.setOnClickListener(v -> {
            if (listener != null) {
                listener.onShareClick(image);
            }
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(image);
            }
        });
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    public void updateImages(List<ImageModel> newImages) {
        this.imageList = newImages;
        notifyDataSetChanged();
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView tvUrl;
        ImageButton btnShare;
        ImageButton btnDelete;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvUrl = itemView.findViewById(R.id.tvUrl);
            btnShare = itemView.findViewById(R.id.btnShare);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
