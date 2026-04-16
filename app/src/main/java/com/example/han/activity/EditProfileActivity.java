package com.example.han.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.han.R;
import com.example.han.model.ApiResponse;
import com.example.han.model.ProfileUpdate;
import com.example.han.model.UploadResult;
import com.example.han.model.User;
import com.example.han.network.ApiService;
import com.example.han.network.RetrofitClient;
import com.example.han.network.TokenManager;
import com.example.han.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditProfileActivity extends AppCompatActivity {

    private ApiService apiService;
    private TokenManager tokenManager;
    private CircleImageView ivAvatar;
    private com.google.android.material.textfield.TextInputEditText etName, etBio;
    private Button btnSave;

    private String avatarUrl = null;
    private boolean isUploading = false;

    private final ActivityResultLauncher<String> imagePicker =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::onImageSelected);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        apiService = RetrofitClient.getApiService(this);
        tokenManager = TokenManager.getInstance(this);

        initViews();
        loadProfile();
    }

    private void initViews() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> finish());

        ivAvatar = findViewById(R.id.ivAvatar);
        etName = findViewById(R.id.etName);
        etBio = findViewById(R.id.etBio);
        btnSave = findViewById(R.id.btnSave);

        ivAvatar.setOnClickListener(v -> imagePicker.launch("image/*"));
        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void loadProfile() {
        apiService.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    etName.setText(user.getName());
                    etBio.setText(user.getBio());
                    avatarUrl = user.getAvatar();
                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Glide.with(EditProfileActivity.this)
                                .load(user.getAvatar())
                                .circleCrop()
                                .placeholder(R.drawable.ic_avatar_default)
                                .error(R.drawable.ic_avatar_default)
                                .into(ivAvatar);
                    } else {
                        ivAvatar.setImageResource(R.drawable.ic_avatar_default);
                    }
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                ToastUtils.show(EditProfileActivity.this, R.string.network_error);
            }
        });
    }

    private void onImageSelected(Uri uri) {
        if (uri != null) {
            Glide.with(this).load(uri).circleCrop().into(ivAvatar);
            uploadImage(uri);
        }
    }

    private void uploadImage(Uri uri) {
        try {
            isUploading = true;
            btnSave.setEnabled(false);
            File file = createTempFile(uri);
            RequestBody requestFile = RequestBody.create(file, MediaType.parse("image/*"));
            MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

            apiService.uploadImage(body, "avatar").enqueue(new Callback<ApiResponse<UploadResult>>() {
                @Override
                public void onResponse(Call<ApiResponse<UploadResult>> call, Response<ApiResponse<UploadResult>> response) {
                    isUploading = false;
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        avatarUrl = response.body().getData().getUrl();
                        btnSave.setEnabled(true);
                        ToastUtils.show(EditProfileActivity.this, R.string.upload_success);
                    } else {
                        btnSave.setEnabled(true);
                        String msg = response.body() != null ? response.body().getMessage() : "上传失败";
                        ToastUtils.show(EditProfileActivity.this, msg);
                    }
                }

                @Override
                public void onFailure(Call<ApiResponse<UploadResult>> call, Throwable t) {
                    isUploading = false;
                    btnSave.setEnabled(true);
                    ToastUtils.show(EditProfileActivity.this, R.string.network_error);
                }
            });
        } catch (Exception e) {
            isUploading = false;
            btnSave.setEnabled(true);
            ToastUtils.show(this, R.string.upload_failed);
        }
    }

    private File createTempFile(Uri uri) throws Exception {
        InputStream inputStream = getContentResolver().openInputStream(uri);
        File tempFile = File.createTempFile("avatar_", ".jpg", getCacheDir());
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

    private void saveProfile() {
        if (isUploading) {
            ToastUtils.show(this, "头像上传中，请稍候");
            return;
        }

        String name = etName.getText().toString().trim();
        String bio = etBio.getText().toString().trim();

        if (name.isEmpty()) {
            ToastUtils.show(this, R.string.please_input_username);
            return;
        }

        btnSave.setEnabled(false);
        ProfileUpdate update = new ProfileUpdate(avatarUrl, bio);
        apiService.updateProfile(update).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    tokenManager.saveUserInfo(user.getId(), user.getName());
                    ToastUtils.show(EditProfileActivity.this, R.string.profile_update_success);
                    finish();
                } else {
                    ToastUtils.show(EditProfileActivity.this, R.string.profile_update_failed);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                btnSave.setEnabled(true);
                ToastUtils.show(EditProfileActivity.this, R.string.network_error);
            }
        });
    }
}
