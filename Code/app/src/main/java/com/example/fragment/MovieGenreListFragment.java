package com.example.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.adapter.MovieGenreListAdapter;
import com.example.dialog.FilterDialog;
import com.example.itemmodels.ItemGenre;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.NetworkUtils;
import com.example.streamingapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;


public class MovieGenreListFragment extends Fragment implements FilterDialog.FilterDialogListener {

    private ArrayList<ItemGenre> mListItem;
    private RecyclerView recyclerView;
    private MovieGenreListAdapter adapter;
    private ProgressBar progressBar;
    private TextView txtNoFound;
    private boolean isVisible = false, isLoaded = false;
    private LinearLayout lytRView;
    private boolean isShow;
    private int selectedFilter = 1;
    private String mFilter = Constant.FILTER_NEWEST;
    private ShowListFragment showListFragment;
    private MovieListFragment movieFragment;

    public static MovieGenreListFragment newInstance(boolean isShow) {
        MovieGenreListFragment f = new MovieGenreListFragment();
        Bundle args = new Bundle();
        args.putBoolean("isShow", isShow);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_language, container, false);
        if (getArguments() != null) {
            isShow = getArguments().getBoolean("isShow", true);
        }
        setHasOptionsMenu(true);
        mListItem = new ArrayList<>();
        progressBar = rootView.findViewById(R.id.progressBar);
        txtNoFound = rootView.findViewById(R.id.textView_mlm);
        lytRView = rootView.findViewById(R.id.lytRView);

        recyclerView = rootView.findViewById(R.id.recyclerView_mlm);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (isVisible && !isLoaded) {
            if (NetworkUtils.isConnected(getActivity())) {
                getGenre();
            } else {
                Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
            }
            isLoaded = true;
        }


        return rootView;
    }

    private void getGenre() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.GENRE_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgress(true);
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                showProgress(false);

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONArray jsonArray = mainJson.getJSONArray(Constant.ARRAY_NAME);
                    JSONObject objJson;
                    if (jsonArray.length() > 0) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            objJson = jsonArray.getJSONObject(i);
                            if (objJson.has(Constant.STATUS)) {
                                txtNoFound.setVisibility(View.VISIBLE);
                            } else {
                                ItemGenre objItem = new ItemGenre();
                                objItem.setGenreId(objJson.getString(Constant.GENRE_ID));
                                objItem.setGenreName(objJson.getString(Constant.GENRE_NAME));

                                if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Drama"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/DRAMA.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Action"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/Action.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Comedy"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/comedy.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Thriller"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/thriller.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Horror"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/horror.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Romance"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/Romance.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Adventure"))
                                    objItem.setGenreImage(" https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/Adventure.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Biopic"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/biography.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Mystry"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Gujarati.jpg");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Historical"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/historical.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Dance"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Gujarati.jpg");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Musical"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/music.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Sports"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/Sports.png");
                                else if (objJson.getString(Constant.GENRE_NAME).equalsIgnoreCase("Animation"))
                                    objItem.setGenreImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/animation.png");
                                else
                                    objItem.setGenreImage(objJson.getString(Constant.GENRE_IMAGE));


                                mListItem.add(objItem);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                displayData();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                showProgress(false);
                txtNoFound.setVisibility(View.VISIBLE);
                lytRView.setVisibility(View.GONE);
            }

        });
    }

    private void displayData() {
        if (mListItem.size() == 0) {
            txtNoFound.setVisibility(View.VISIBLE);
            lytRView.setVisibility(View.GONE);
        } else {
            txtNoFound.setVisibility(View.GONE);
            adapter = new MovieGenreListAdapter(getActivity(), mListItem);
            recyclerView.setAdapter(adapter);
            listByGenre(0);
            adapter.select(0);

            adapter.setOnItemClickListener(position -> {
                listByGenre(position);
                adapter.select(position);
            });
        }
    }


    private void showProgress(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            txtNoFound.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void listByGenre(int position) {
        filterReset();
        String genreName = mListItem.get(position).getGenreName();
        String genreId = mListItem.get(position).getGenreId();
        Bundle bundle = new Bundle();
        bundle.putString("Id", genreId);
        bundle.putBoolean("isLanguage", false);

        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        if (isShow) {
            showListFragment = new ShowListFragment();
            showListFragment.setArguments(bundle);
            ft.replace(R.id.framlayout_sub, showListFragment, genreName);
        } else {
            movieFragment = new MovieListFragment();
            movieFragment.setArguments(bundle);
            ft.replace(R.id.framlayout_sub, movieFragment, genreName);
        }
        ft.addToBackStack(genreName);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        isVisible = isVisibleToUser;
        if (isVisibleToUser && isAdded() && !isLoaded) {
            if (NetworkUtils.isConnected(getActivity())) {
                getGenre();
            } else {
                Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
            }
            isLoaded = true;
        }
        super.setUserVisibleHint(isVisibleToUser);
    }

    @Override
    public void onCreateOptionsMenu(@NotNull Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_filter, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void confirm(String filterTag, int filterPosition) {
        mFilter = filterTag;
        selectedFilter = filterPosition;
        if (isShow) {
            if (showListFragment != null) {
                showListFragment.selectFilter(mFilter);
            }
        } else {
            if (movieFragment != null) {
                movieFragment.selectFilter(mFilter);
            }
        }
    }

    private void filterReset() {
        selectedFilter = 1;
        mFilter = Constant.FILTER_NEWEST;
    }
}
