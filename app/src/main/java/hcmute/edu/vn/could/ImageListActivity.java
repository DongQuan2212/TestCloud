package hcmute.edu.vn.could;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ImageListActivity extends AppCompatActivity implements ImageAdapter.ImageClickListener {
    private static final String PREF_NAME = "CloudinaryPrefs";
    private static final String KEY_IMAGES = "uploaded_images";

    private RecyclerView recyclerView;
    private ImageAdapter adapter;
    private List<ImageModel> imageList;
    private SharedPreferences sharedPreferences;
    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);

        // Hiển thị nút back
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Ảnh Đã Upload");
        }

        // Khởi tạo SharedPreferences và Gson
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        gson = new Gson();
        loadUploadedImages();

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ImageAdapter(imageList, this);
        recyclerView.setAdapter(adapter);

        if (imageList.isEmpty()) {
            Toast.makeText(this, "Chưa có ảnh nào được upload", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUploadedImages() {
        String json = sharedPreferences.getString(KEY_IMAGES, null);
        if (json != null) {
            Type type = new TypeToken<List<ImageModel>>(){}.getType();
            imageList = gson.fromJson(json, type);
        }
    }

    private void saveUploadedImages() {
        String json = gson.toJson(imageList);
        sharedPreferences.edit().putString(KEY_IMAGES, json).apply();
    }

    @Override
    public void onShareClick(ImageModel image) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Xem ảnh của tôi: " + image.getUrl());
        startActivity(Intent.createChooser(shareIntent, "Chia sẻ ảnh"));
    }

    @Override
    public void onDeleteClick(ImageModel image) {
        imageList.remove(image);
        saveUploadedImages();
        adapter.updateImages(imageList);
        Toast.makeText(this, "Đã xóa ảnh", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadUploadedImages();
        adapter.updateImages(imageList);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}