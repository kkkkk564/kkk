package com.example.han.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.han.R;
import com.example.han.activity.EditProfileActivity;
import com.example.han.activity.LoginActivity;
import com.example.han.model.ApiResponse;
import com.example.han.model.User;
import com.example.han.network.ApiService;
import com.example.han.network.RetrofitClient;
import com.example.han.network.TokenManager;
import com.example.han.util.ToastUtils;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {

    private ApiService apiService;
    private TokenManager tokenManager;
    private CircleImageView ivAvatar;
    private TextView tvName, tvBio, tvMyPosts, tvLogout;
    private View btnEditProfile;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService(requireContext());
        tokenManager = TokenManager.getInstance(requireContext());

        ivAvatar = view.findViewById(R.id.ivAvatar);
        tvName = view.findViewById(R.id.tvName);
        tvBio = view.findViewById(R.id.tvBio);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);
        tvMyPosts = view.findViewById(R.id.tvMyPosts);
        tvLogout = view.findViewById(R.id.tvLogout);

        btnEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(requireContext(), EditProfileActivity.class));
        });

        tvMyPosts.setOnClickListener(v -> {
            MyPostsFragment fragment = new MyPostsFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit();
        });

        tvLogout.setOnClickListener(v -> logout());

        loadProfile();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadProfile();
    }

    private void loadProfile() {
        apiService.getProfile().enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    User user = response.body().getData();
                    tvName.setText(user.getName());
                    tvBio.setText(user.getBio() != null ? user.getBio() : "这个人很懒，什么都没留下");
                    tokenManager.saveUserInfo(user.getId(), user.getName());

                    if (user.getAvatar() != null && !user.getAvatar().isEmpty()) {
                        Glide.with(requireContext())
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
                ToastUtils.show(requireContext(), R.string.network_error);
            }
        });
    }

    private void logout() {
        apiService.logout().enqueue(new Callback<ApiResponse<Void>>() {
            @Override
            public void onResponse(Call<ApiResponse<Void>> call, Response<ApiResponse<Void>> response) {
                // Always clear token regardless of response
                tokenManager.clearAll();
                ToastUtils.show(requireContext(), R.string.logout_success);
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }

            @Override
            public void onFailure(Call<ApiResponse<Void>> call, Throwable t) {
                tokenManager.clearAll();
                ToastUtils.show(requireContext(), R.string.logout_success);
                Intent intent = new Intent(requireActivity(), LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }
}
