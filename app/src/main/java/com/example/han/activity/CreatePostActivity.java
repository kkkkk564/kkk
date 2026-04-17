package com.example.han.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.han.R;
import com.example.han.model.ApiResponse;
import com.example.han.model.PostItem;
import com.example.han.model.PostRequest;
import com.example.han.model.UploadResult;
import com.example.han.network.ApiService;
import com.example.han.network.RetrofitClient;
import com.example.han.util.Constants;
import com.example.han.util.ToastUtils;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePostActivity extends AppCompatActivity {

    private ApiService apiService;
    private EditText etTitle, etContent;
    private ChipGroup chipGroupType;
    private ImageView ivCover;
    private LinearLayout llCoverPlaceholder;
    private Button btnPublish;
    private androidx.cardview.widget.CardView cardCover;

    private String coverUrl = null;
    private Uri selectedImageUri = null;
    private String selectedType = Constants.PostType.HANFU;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImageSelected);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        apiService = RetrofitClient.getApiService(this);
        initViews();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        chipGroupType = findViewById(R.id.chipGroupType);
        ivCover = findViewById(R.id.ivCover);
        llCoverPlaceholder = findViewById(R.id.llCoverPlaceholder);
        btnPublish = findViewById(R.id.btnPublish);
        cardCover = findViewById(R.id.cardCover);

        // Setup type chips
        chipGroupType.check(R.id.chip_hanfu); // default
        chipGroupType.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.chip_hanfu) {
                selectedType = Constants.PostType.HANFU;
            } else if (checkedId == R.id.chip_poetry) {
                selectedType = Constants.PostType.POETRY;
            } else if (checkedId == R.id.chip_music) {
                selectedType = Constants.PostType.MUSIC;
            } else if (checkedId == R.id.chip_etiquette) {
                selectedType = Constants.PostType.ETIQUETTE;
            } else if (checkedId == R.id.chip_solar) {
                selectedType = Constants.PostType.SOLAR;
            } else if (checkedId == R.id.chip_user_post) {
                selectedType = Constants.PostType.USER_POST;
            }
        });

        cardCover.setOnClickListener(v -> imagePicker.launch("image/*"));
        btnPublish.setOnClickListener(v -> publishPost());
    }

    private void onImageSelected(Uri uri) {
        if (uri != null) {
            selectedImageUri = uri;
            Glide.with(this).load(uri).centerCrop().into(ivCover);
            ivCover.setVisibility(View.VISIBLE);
            llCoverPlaceholder.setVisibility(View.GONE);
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri) {
        try {
            File file = createTempFile(uri);
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiService.uploadImage(body, "post").enqueue(new Callback<ApiResponse<UploadResult>>() {
                @Override
                public void onResponse(Call<ApiResponse<UploadResult>> call, Response<ApiResponse<UploadResult>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        coverUrl = response.body().getData().getUrl();
                        ToastUtils.show(CreatePostActivity.this, R.string.upload_success);
                    } else {
                        ToastUtils.show(CreatePostActivity.this, R.string.upload_failed);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UploadResult>> call, Throwable t) {
                    ToastUtils.show(CreatePostActivity.this, R.string.network_error);
                }
            });
        } catch (Exception e) {
            ToastUtils.show(this, R.string.upload_failed);
        }
    }

    private File createTempFile(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("upload_", ".jpg", getCacheDir());
        FileOutputStream fos = new FileOutputStream(tempFile);
        byte[] buffer = new byte[4096];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            fos.write(buffer, 0, read);
        }
        fos.close();
        inputStream.close();
        return tempFile;
    }

    private void publishPost() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() || title.length() < 2) {
            ToastUtils.show(this, R.string.post_title_hint);
            return;
        }
        if (content.isEmpty()) {
            ToastUtils.show(this, R.string.post_content_hint);
            return;
        }
        if (coverUrl == null) {
            ToastUtils.show(this, R.string.upload_cover);
            return;
        }

        String description = content.length() > 50 ? content.substring(0, 50) + "..." : content;
        PostRequest request = new PostRequest(title, description, content, coverUrl, selectedType);

        btnPublish.setEnabled(false);
        apiService.createPost(request).enqueue(new Callback<ApiResponse<PostItem>>() {
            @Override
            public void onResponse(Call<ApiResponse<PostItem>> call, Response<ApiResponse<PostItem>> response) {
                btnPublish.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    ToastUtils.show(CreatePostActivity.this, R.string.publish_success);
                    finish();
                } else {
                    String errorMsg;
                    if (response.body() != null) {
                        errorMsg = response.body().getMessage();
                    } else {
                        try {
                            errorMsg = response.errorBody() != null ? response.errorBody().string() : getString(R.string.publish_failed);
                        } catch (java.io.IOException e) {
                            errorMsg = getString(R.string.publish_failed);
                        }
                    }
                    android.util.Log.e("CreatePost", "Publish failed: code=" +
                            (response.body() != null ? response.body().getCode() : "null") +
                            ", msg=" + errorMsg +
                            ", httpCode=" + response.code());
                    ToastUtils.show(CreatePostActivity.this, errorMsg);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<PostItem>> call, Throwable t) {
                btnPublish.setEnabled(true);
                android.util.Log.e("CreatePost", "Network error", t);
                ToastUtils.show(CreatePostActivity.this, R.string.network_error);
            }
        });
    }
}
