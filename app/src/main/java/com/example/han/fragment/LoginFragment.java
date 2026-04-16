package com.example.han.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.han.R;
import com.example.han.activity.MainActivity;
import com.example.han.model.ApiResponse;
import com.example.han.model.AuthData;
import com.example.han.model.LoginRequest;
import com.example.han.network.ApiService;
import com.example.han.network.RetrofitClient;
import com.example.han.network.TokenManager;
import com.example.han.util.ToastUtils;
import com.google.android.material.textfield.TextInputEditText;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginFragment extends Fragment {

    private TextInputEditText etUsername, etPassword;
    private Button btnAction;
    private ApiService apiService;
    private TokenManager tokenManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etUsername = view.findViewById(R.id.etUsername);
        etPassword = view.findViewById(R.id.etPassword);
        btnAction = view.findViewById(R.id.btnAction);

        apiService = RetrofitClient.getApiServiceNoAuth(requireContext());
        tokenManager = TokenManager.getInstance(requireContext());

        btnAction.setOnClickListener(v -> login());
    }

    private void login() {
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (username.isEmpty()) {
            ToastUtils.show(requireContext(), R.string.please_input_username);
            return;
        }
        if (password.isEmpty()) {
            ToastUtils.show(requireContext(), R.string.please_input_password);
            return;
        }

        btnAction.setEnabled(false);
        apiService.login(new LoginRequest(username, password))
                .enqueue(new Callback<ApiResponse<AuthData>>() {
                    @Override
                    public void onResponse(Call<ApiResponse<AuthData>> call, Response<ApiResponse<AuthData>> response) {
                        btnAction.setEnabled(true);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            AuthData data = response.body().getData();
                            tokenManager.saveToken(data.getToken());
                            tokenManager.saveRefreshToken(data.getRefreshToken());
                            if (data.getUser() != null) {
                                tokenManager.saveUserInfo(data.getUser().getId(), data.getUser().getName());
                            }
                            ToastUtils.show(requireContext(), R.string.login_success);
                            startActivity(new Intent(requireActivity(), MainActivity.class));
                            requireActivity().finish();
                        } else {
                            String msg = response.body() != null ? response.body().getMessage() : getString(R.string.login_failed);
                            ToastUtils.show(requireContext(), msg);
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiResponse<AuthData>> call, Throwable t) {
                        btnAction.setEnabled(true);
                        ToastUtils.show(requireContext(), R.string.network_error);
                    }
                });
    }
}
