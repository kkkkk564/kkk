package com.example.han.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.han.R;
import com.example.han.activity.PostDetailActivity;
import com.example.han.adapter.PostAdapter;
import com.example.han.model.ApiResponse;
import com.example.han.model.Post;
import com.example.han.model.PostPage;
import com.example.han.network.ApiService;
import com.example.han.network.RetrofitClient;
import com.example.han.util.Constants;
import com.example.han.util.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

public class HomeFragment extends Fragment {

    private ApiService apiService;
    private PostAdapter postAdapter;
    private SwipeRefreshLayout swipeRefresh;
    private RecyclerView rvPosts;
    private Spinner spinnerType;

    private int currentPage = 0;
    private boolean isLoading = false;
    private boolean hasMore = true;
    private String currentType = Constants.PostType.ALL;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        apiService = RetrofitClient.getApiService(requireContext());

        swipeRefresh = view.findViewById(R.id.swipeRefresh);
        rvPosts = view.findViewById(R.id.rvPosts);
        spinnerType = view.findViewById(R.id.spinnerType);

        postAdapter = new PostAdapter();
        rvPosts.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvPosts.setAdapter(postAdapter);

        postAdapter.setOnItemClickListener(post -> {
            Intent intent = new Intent(requireContext(), PostDetailActivity.class);
            intent.putExtra("postId", post.getId());
            startActivity(intent);
        });

        // Setup type spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, Constants.PostType.getDisplayNames());
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerType.setAdapter(adapter);
        spinnerType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                currentType = Constants.PostType.getValues()[position];
                refreshPosts();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        swipeRefresh.setOnRefreshListener(this::refreshPosts);

        rvPosts.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

        refreshPosts();
    }

    private void refreshPosts() {
        currentPage = 0;
        hasMore = true;
        postAdapter.clear();
        loadPosts();
    }

    private void loadMore() {
        if (isLoading || !hasMore) return;
        currentPage++;
        loadPosts();
    }

    private void loadPosts() {
        isLoading = true;
        apiService.getPosts(currentType, currentPage, Constants.PAGE_SIZE, Constants.SortType.CREATED_AT)
                .enqueue(new retrofit2.Callback<ApiResponse<PostPage>>() {
                    @Override
                    public void onResponse(retrofit2.Call<ApiResponse<PostPage>> call,
                                           retrofit2.Response<ApiResponse<PostPage>> response) {
                        isLoading = false;
                        swipeRefresh.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            PostPage page = response.body().getData();
                            List<Post> posts = page.getContent();
                            if (posts != null && !posts.isEmpty()) {
                                if (currentPage == 0) {
                                    postAdapter.setPosts(posts);
                                } else {
                                    postAdapter.addPosts(posts);
                                }
                                hasMore = page.getCurrentPage() < page.getTotalPages() - 1;
                            } else {
                                hasMore = false;
                            }
                        } else {
                            ToastUtils.show(requireContext(), "加载失败");
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<ApiResponse<PostPage>> call, Throwable t) {
                        isLoading = false;
                        swipeRefresh.setRefreshing(false);
                        ToastUtils.show(requireContext(), R.string.network_error);
                    }
                });
    }
}
