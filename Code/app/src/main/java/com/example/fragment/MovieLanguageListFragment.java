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

import com.example.adapter.MovieLanguageListAdapter;
import com.example.dialog.FilterDialog;
import com.example.itemmodels.ItemLanguage;
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


public class MovieLanguageListFragment extends Fragment implements FilterDialog.FilterDialogListener {

    private ArrayList<ItemLanguage> mListItem;
    private RecyclerView recyclerView;
    private MovieLanguageListAdapter adapter;
    private ProgressBar progressBar;
    private TextView txtNoFound,title;
    private boolean isShow;
    private String position;
    private LinearLayout lytRView;
    private int selectedFilter = 1;
    private String mFilter = Constant.FILTER_NEWEST;
    private ShowListFragment showListFragment;
    private MovieListFragment movieFragment;

    public static MovieLanguageListFragment newInstance(boolean isShow, String position) {
        MovieLanguageListFragment f = new MovieLanguageListFragment();
        Bundle args = new Bundle();
        args.putBoolean("isShow", isShow);
        args.putString("position", position);
        f.setArguments(args);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_language, container, false);
        if (getArguments() != null) {
            isShow = getArguments().getBoolean("isShow", true);
            position = getArguments().getString("position");
        }
        setHasOptionsMenu(true);
        mListItem = new ArrayList<>();
        progressBar = rootView.findViewById(R.id.progressBar);
        txtNoFound = rootView.findViewById(R.id.textView_mlm);
        lytRView = rootView.findViewById(R.id.lytRView);
        lytRView.setVisibility(View.GONE);
        title=rootView.findViewById(R.id.text1);
        title.setText("Movies");

        recyclerView = rootView.findViewById(R.id.recyclerView_mlm);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        if (NetworkUtils.isConnected(getActivity())) {
            getLanguage();
        } else {
            Toast.makeText(getActivity(), getString(R.string.conne_msg1), Toast.LENGTH_SHORT).show();
        }

        return rootView;
    }

    private void getLanguage() {

        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.LANGUAGE_URL, params, new AsyncHttpResponseHandler() {
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
                                ItemLanguage objItem = new ItemLanguage();
                                objItem.setLanguageId(objJson.getString(Constant.LANGUAGE_ID));
                                objItem.setLanguageName(objJson.getString(Constant.LANGUAGE_NAME));
                                if (objJson.getString(Constant.LANGUAGE_NAME).equalsIgnoreCase("Hindi"))
                                    objItem.setLanguageImage("https://moviesy.s3.ap-south-1.amazonaws.com/Hindi.jpg");
                                else if (objJson.getString(Constant.LANGUAGE_NAME).equalsIgnoreCase("English"))
                                    objItem.setLanguageImage("https://moviesy.s3.ap-south-1.amazonaws.com/English.png");
                                else if (objJson.getString(Constant.LANGUAGE_NAME).equalsIgnoreCase("Gujarati"))
                                    objItem.setLanguageImage("https://moviesy.s3.ap-south-1.amazonaws.com/Gujarati.jpg");
                                else if (objJson.getString(Constant.LANGUAGE_NAME).equalsIgnoreCase("Telugu"))
                                    objItem.setLanguageImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/Telugu.png");
                                else if (objJson.getString(Constant.LANGUAGE_NAME).equalsIgnoreCase("Tamil"))
                                    objItem.setLanguageImage("https://moviesy.s3.ap-south-1.amazonaws.com/Genre+icons/tamil.jpg");
                                else
                                    objItem.setLanguageImage(objJson.getString(Constant.LANGUAGE_IMAGE));

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
            adapter = new MovieLanguageListAdapter(getActivity(), mListItem);
            recyclerView.setAdapter(adapter);
            listByLanguage(Integer.parseInt(position));
            adapter.select(Integer.parseInt(position));


            adapter.setOnItemClickListener(position -> {
                listByLanguage(position);
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

    private void listByLanguage(int position) {
        filterReset();
        String languageName = mListItem.get(position).getLanguageName();
        String languageId = mListItem.get(position).getLanguageId();
        Bundle bundle = new Bundle();
        bundle.putString("Id", languageId);
        bundle.putBoolean("isLanguage", true);
        FragmentManager fm = getChildFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();


        if (isShow) {
            showListFragment = new ShowListFragment();
            title.setText("Series");
            showListFragment.setArguments(bundle);
            ft.replace(R.id.framlayout_sub, showListFragment, languageName);
        } else {
            movieFragment = new MovieListFragment();
            movieFragment.setArguments(bundle);
            ft.replace(R.id.framlayout_sub, movieFragment, languageName);
        }
        ft.addToBackStack(languageName);
        ft.commitAllowingStateLoss();
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
