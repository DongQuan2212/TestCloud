package hcmute.edu.vn.could;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cloudinary.Cloudinary;
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String PREF_NAME = "CloudinaryPrefs";
    private static final String KEY_IMAGES = "uploaded_images";
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;

    private ImageView imageView;
    private Button btnSelectImage, btnUpload, btnViewImages;
    private TextView tvResult;
    private Uri selectedImageUri;
    private SharedPreferences sharedPreferences;
    private Gson gson;
    private List<ImageModel> uploadedImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Khởi tạo SharedPreferences và Gson
        sharedPreferences = getSharedPreferences(PREF_NAME, MODE_PRIVATE);
        gson = new Gson();
        loadUploadedImages();

        // Khởi tạo views
        imageView = findViewById(R.id.imageView);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUpload = findViewById(R.id.btnUpload);
        btnViewImages = findViewById(R.id.btnViewImages);
        tvResult = findViewById(R.id.tvResult);

        // Khởi tạo Cloudinary
        initCloudinary();

        // Xử lý sự kiện chọn ảnh
        btnSelectImage.setOnClickListener(v -> {
            if (checkPermission()) {
                openImagePicker();
            } else {
                requestPermission();
            }
        });

        // Xử lý sự kiện upload ảnh
        btnUpload.setOnClickListener(v -> {
            if (selectedImageUri != null) {
                uploadImage();
            } else {
                Toast.makeText(this, "Vui lòng chọn ảnh trước khi upload", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện xem danh sách ảnh
        btnViewImages.setOnClickListener(v -> {
            Intent intent = new Intent(this, ImageListActivity.class);
            startActivity(intent);
        });
    }

    private void initCloudinary() {
        Map config = new HashMap();
        config.put("cloud_name", "dgpcjnxg0");
        config.put("api_key", "173525473881339");
        config.put("api_secret", "y5BIb8vjW3Sd2nvwoSJVIUn3vSk");
        config.put("secure", true);

        try {
            MediaManager.init(this, config);
            Log.d(TAG, "Cloudinary initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Cloudinary: " + e.getMessage());
            Toast.makeText(this, "Lỗi khởi tạo Cloudinary: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private boolean checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    PERMISSION_REQUEST_CODE);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openImagePicker();
            } else {
                Toast.makeText(this, "Cần quyền truy cập thư viện ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            imageView.setImageURI(selectedImageUri);
            btnUpload.setEnabled(true);
            Toast.makeText(this, "Đã chọn ảnh", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage() {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh trước khi upload", Toast.LENGTH_SHORT).show();
            return;
        }

        btnUpload.setEnabled(false);
        String requestId = MediaManager.get().upload(selectedImageUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                        Log.d(TAG, "Upload started");
                        runOnUiThread(() -> {
                            tvResult.setText("Đang upload...");
                        });
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                        Log.d(TAG, "Upload progress: " + bytes + "/" + totalBytes);
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Log.d(TAG, "Upload success: " + resultData);
                        runOnUiThread(() -> {
                            String imageUrl = (String) resultData.get("secure_url");
                            String publicId = (String) resultData.get("public_id");
                            tvResult.setText("Upload thành công!\nURL: " + imageUrl);
                            
                            // Lưu thông tin ảnh đã upload
                            ImageModel imageModel = new ImageModel(imageUrl, publicId);
                            uploadedImages.add(imageModel);
                            saveUploadedImages();
                            
                            Toast.makeText(MainActivity.this, "Upload thành công!", Toast.LENGTH_SHORT).show();
                            btnUpload.setEnabled(true);
                        });
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Log.e(TAG, "Upload error: " + error.getDescription());
                        runOnUiThread(() -> {
                            tvResult.setText("Upload thất bại: " + error.getDescription());
                            Toast.makeText(MainActivity.this, "Upload thất bại: " + error.getDescription(), Toast.LENGTH_LONG).show();
                            btnUpload.setEnabled(true);
                        });
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        Log.d(TAG, "Upload rescheduled: " + error.getDescription());
                    }
                })
                .dispatch();
    }

    private void loadUploadedImages() {
        String json = sharedPreferences.getString(KEY_IMAGES, null);
        if (json != null) {
            Type type = new TypeToken<List<ImageModel>>(){}.getType();
            uploadedImages = gson.fromJson(json, type);
        } else {
            uploadedImages = new ArrayList<>();
        }
    }

    private void saveUploadedImages() {
        String json = gson.toJson(uploadedImages);
        sharedPreferences.edit().putString(KEY_IMAGES, json).apply();
    }
}