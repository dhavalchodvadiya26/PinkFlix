package com.example.adapter;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fragment.WatchListContentFragment;
import com.example.itemmodels.ItemMovie;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.PopUpAds;
import com.example.util.PrettyDialog;
import com.example.util.RvOnClickListener;
import com.example.streamingapp.MyApplication;
import com.example.streamingapp.R;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class WatchMovieVideoListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<ItemMovie> dataList;
    private Context mContext;
    private RvOnClickListener clickListener;
    private int columnWidth;
    private boolean isRTL;
    private boolean isHomeMore;
    MyApplication myApplication;
    ProgressBar mProgressBar;


    public WatchMovieVideoListAdapter(Context context, ArrayList<ItemMovie> dataList, boolean isHomeMore) {
        this.dataList = dataList;
        this.mContext = context;
        isRTL = false;
        this.isHomeMore = isHomeMore;
        DisplayMetrics displayMetrics = new DisplayMetrics();
        columnWidth = displayMetrics.widthPixels*2;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.home_row_watch_list_item, parent, false);
        myApplication = MyApplication.getInstance();
        return new ItemRowHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder viewHolder, final int position) {
        final ItemRowHolder holder = (ItemRowHolder) viewHolder;
        final ItemMovie singleItem = dataList.get(position);

        holder.image.setLayoutParams(new RelativeLayout.LayoutParams(250, 250));
        holder.txtLanguage.setText(singleItem.getMovieLanguage());
        holder.txtDuration.setText(singleItem.getMovieDuration());
        holder.txtMovieName.setText(singleItem.getMovieName());

        Picasso.with(mContext).load(singleItem.getMovieImage()).into(holder.image);
        holder.cardView.setOnClickListener(v -> PopUpAds.showInterstitialAds(mContext, holder.getAdapterPosition(), clickListener));

        String movie_id = singleItem.getMovieId();

        holder.imgremove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                PrettyDialog pDialog = new PrettyDialog(mContext);
                pDialog
                        .setTitle("PinkFlix")
                        .setMessage("Are you sure to remove this movie from WatchList?")
                        .setIcon(R.drawable.watchlist)
                        .addButton(
                                "YES",
                                R.color.pdlg_color_white2,
                                R.color.pdlg_color_green,
                                () -> {
                                    pDialog.dismiss();
                                    AsyncHttpClient client = new AsyncHttpClient();
                                    RequestParams params = new RequestParams();
                                    JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
                                    params.put("movie_videos_id", movie_id);
                                    params.put("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
                                    client.post(Constant.REMOVE_TO_WATCHLIST, params, new AsyncHttpResponseHandler() {
                                        @Override
                                        public void onStart() {
                                            super.onStart();
                                            mProgressBar.setVisibility(View.VISIBLE);

                                        }

                                        @Override
                                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                            mProgressBar.setVisibility(View.GONE);

                                            AppCompatActivity activity = (AppCompatActivity) view.getContext();
                                            Fragment myFragment = new WatchListContentFragment();
                                            activity.getSupportFragmentManager().beginTransaction().replace(R.id.Container, myFragment).addToBackStack(null).commit();


                                            String result = new String(responseBody);
                                            try {
                                                JSONObject mainJson = new JSONObject(result);
                                                if (mainJson.optString("code").equalsIgnoreCase("200")) {

                                                    PrettyDialog pDialog = new PrettyDialog(mContext);
                                                    pDialog
                                                            .setTitle("PinkFlix")
                                                            .setMessage(mainJson.getString("message"))
                                                            .setIcon(R.drawable.watchlist)
                                                            .addButton(
                                                                    "OK",
                                                                    R.color.pdlg_color_white2,
                                                                    R.color.pdlg_color_red,
                                                                    pDialog::dismiss)
                                                            .show();

                                                } else {
                                                    mProgressBar.setVisibility(View.GONE);
                                                }

                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                            mProgressBar.setVisibility(View.GONE);
                                        }
                                    });
                                }).addButton(
                        "CANCEL",
                        R.color.pdlg_color_white2,
                        R.color.pdlg_color_green,
                        pDialog::dismiss)
                        .show();
            }
        });
    }


    @Override
    public int getItemCount() {
        return (null != dataList ? dataList.size() : 0);
    }


    public void setOnItemClickListener(RvOnClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class ItemRowHolder extends RecyclerView.ViewHolder {
        ImageView image, imgremove;
        TextView txtLanguage, txtDuration, txtMovieName;
        CardView cardView;
        RelativeLayout rootLayout;

        ItemRowHolder(View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.image);
            txtLanguage = itemView.findViewById(R.id.txtLanguage);
            txtDuration = itemView.findViewById(R.id.txtDuration);
            imgremove = itemView.findViewById(R.id.imgRemove);
            cardView = itemView.findViewById(R.id.cardView);
            rootLayout = itemView.findViewById(R.id.rootLayout);
            txtMovieName = itemView.findViewById(R.id.txtMovieName);
            mProgressBar = itemView.findViewById(R.id.progressBar1);
        }
    }
}
