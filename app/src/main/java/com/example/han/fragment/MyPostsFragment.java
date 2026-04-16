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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.han.R;
import com.example.han.activity.PostDetailActivity;
import com.example.han.adapter.PostAdapter;
import com.example.han.model.ApiResponse;
import com.example.han.model.PostPage;
import com.example.han.network.ApiService;
import com.example.han.network.RetrofitClient;
import com.example.han.util.Constants;
import com.example.han.util.ToastUtils;

import java.util.List;

public class MyPostsFragment extends Fragment {

    private ApiService apiService;
    private PostAdapter postAdapter;
    private RecyclerView rvMyPosts;
    private TextView tvEmpty;

    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMore = true;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_my_posts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService(requireContext());

        rvMyPosts = view.findViewById(R.id.rvMyPosts);
        tvEmpty = view.findViewById(R.id.tvEmpty);

        postAdapter = new PostAdapter();
        rvMyPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvMyPosts.setAdapter(postAdapter);

        postAdapter.setOnItemClickListener(post -> {
            Intent intent = new Intent(requireContext(), PostDetailActivity.class);
            intent.putExtra("postId", post.getId());
            startActivity(intent);
        });

        rvMyPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (layoutManager != null) {
                    int lastVisible = layoutManager.findLastVisibleItemPosition();
                    int total = layoutManager.getItemCount();
                    if (!isLoading && hasMore && lastVisible >= total - 3 && dy > 0) {
                        loadMore();
                    }
                }
            }
        });

        loadMyPosts();
    }

    private void loadMyPosts() {
        isLoading = true;
        apiService.getMyPosts(currentPage, Constants.PAGE_SIZE)
                .enqueue(new retrofit2.Callback<ApiResponse<PostPage>>() {
                    @Override
                    public void onResponse(retrofit2.Call<ApiResponse<PostPage>> call,
                                           retrofit2.Response<ApiResponse<PostPage>> response) {
                        isLoading = false;
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            PostPage page = response.body().getData();
                            List<com.example.han.model.Post> posts = page.getContent();
                            if (posts != null && !posts.isEmpty()) {
                                if (currentPage == 0) {
                                    postAdapter.setPosts(posts);
                                    tvEmpty.setVisibility(View.GONE);
                                } else {
                                    postAdapter.addPosts(posts);
                                }
                                hasMore = page.getCurrentPage() < page.getTotalPages() - 1;
                            } else {
                                hasMore = false;
                                if (currentPage == 0) {
                                    tvEmpty.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {
                            ToastUtils.show(requireContext(), "加载失败");
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ApiResponse<PostPage>> call, Throwable t) {
                        isLoading = false;
                        ToastUtils.show(requireContext(), R.string.network_error);
                    }
                });
    }

    private void loadMore() {
        if (isLoading || !hasMore) return;
        currentPage++;
        loadMyPosts();
    }
}
