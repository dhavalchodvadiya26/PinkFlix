package com.example.streamingapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.adapter.MenuCategoryVideoListAdapter;
import com.example.base.BaseActivity;
import com.example.fragment.DownloadedVideosFragment;
import com.example.fragment.HomeFragment;
import com.example.fragment.MoreListFragment;
import com.example.fragment.SettingFragment;
import com.example.fragment.UpcomingListFragment;
import com.example.fragment.WatchListContentFragment;
import com.example.model.menu.MenuCategory;
import com.example.util.API;
import com.example.util.Constant;
import com.example.util.DialogUtils;
import com.example.util.FcmTokenRegistrationService;
import com.example.util.GradientTextView;
import com.example.util.IsRTL;
import com.example.util.Logg;
import com.example.util.NetworkUtils;
import com.example.util.PrettyDialog;
import com.example.util.Remember;
import com.example.util.fullscreen.FullScreenDialogFragment;
import com.example.streamingapp.databinding.ActivityMainBinding;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.ixidev.gdpr.GDPRChecker;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;
import io.github.inflationx.viewpump.ViewPumpContextWrapper;

public class MainActivity extends BaseActivity implements MenuCategoryVideoListAdapter.SetOnExpandClickListener, FullScreenDialogFragment.OnConfirmListener,
        FullScreenDialogFragment.OnDiscardListener,
        FullScreenDialogFragment.OnDiscardFromExtraActionListener,
        DialogUtils.AlertDialogListener {

    Toolbar toolbar;
    boolean doubleBackToExitPressedOnce = false;
    MyApplication myApplication;
    ActivityMainBinding mBinding;
    private FragmentManager fragmentManager;
    private BottomNavigationView bottomNavigationView;
    private FullScreenDialogFragment dialogFragment;
    private GradientTextView toolbarText;
    private boolean isTimerFinished = true;
    private boolean isStopped = false;
    private View.OnClickListener mMenuClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ll_home:
                    HomeFragment homeFragment = new HomeFragment();
                    loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                    break;
                case R.id.ll_watchlist:
                    Intent intentProfile = new Intent(MainActivity.this, WatchScreenActivity.class);
                    intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentProfile);
                    break;
                case R.id.ll_transaction:
                    Intent intenttransaction = new Intent(MainActivity.this, TransactionListActivity.class);
                    intenttransaction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intenttransaction);
                    break;
                case R.id.ll_setting:
                    Intent intenttransaction1 = new Intent(MainActivity.this, SettingActivity.class);
                    intenttransaction1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intenttransaction1);
                    break;
                case R.id.ll_logout:
                    logOut();
                    break;
                case R.id.ll_login:
                    Intent intentSignIn = new Intent(MainActivity.this, SignInScreenActivity.class);
                    intentSignIn.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentSignIn);
                    finish();
                    break;
                case R.id.ll_dashboard:
                    Intent intentDashBoard = new Intent(MainActivity.this, DashboardActivity.class);
                    intentDashBoard.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentDashBoard);
                    break;
            }
//            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
//                drawerLayout.closeDrawer(GravityCompat.START);
//            }
        }


    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;

        getWindow().getDecorView().setSystemUiVisibility(flags);
        sendFcmRegistrationToken();

        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {
                android.Manifest.permission.ACCESS_NETWORK_STATE,
                android.Manifest.permission.INTERNET,
                android.Manifest.permission.FOREGROUND_SERVICE,
                android.Manifest.permission.ACCESS_NOTIFICATION_POLICY,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
        };

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        final View decorView = getWindow().getDecorView();
        decorView
                .setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {

                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                            decorView.setSystemUiVisibility(flags);
                        }
                    }
                });


        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        IsRTL.ifSupported(this);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbarText = toolbar.findViewById(R.id.toolbarText);
//        navigationView = findViewById(R.id.navigation_view);
//        drawerLayout = findViewById(R.id.drawer_layout);
        fragmentManager = getSupportFragmentManager();
        myApplication = MyApplication.getInstance();
        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setItemIconTintList(null);
        bottomNavigationView.setItemTextColor(null);
        bottomNavigationView.setOnNavigationItemSelectedListener(
                item -> {
                    int itemId = item.getItemId();
                    if (!isStopped && isTimerFinished) {
                        if (itemId == R.id.navigation_home) {
                            HomeFragment homeFragment = new HomeFragment();
                            loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);
                            isTimerFinished = false;
                            new Handler().postDelayed(() -> isTimerFinished = true, 500);
                        } else if (itemId == R.id.navigation_movies) {
                            WatchListContentFragment watchListFragment = new WatchListContentFragment();
                            loadFrag(watchListFragment, "WatchList", fragmentManager);
                            isTimerFinished = false;
                            new Handler().postDelayed(() -> isTimerFinished = true, 500);
                        } else if (itemId == R.id.navigation_premium) {
                            DownloadedVideosFragment downloadFragment = new DownloadedVideosFragment();
                            loadFrag(downloadFragment, "Downloads", fragmentManager);
                            isTimerFinished = false;
                            new Handler().postDelayed(() -> isTimerFinished = true, 500);
                        } else if (itemId == R.id.navigation_series) {
                            UpcomingListFragment upcomingFragment = new UpcomingListFragment();
                            loadFrag(upcomingFragment, "Upcoming", fragmentManager);
                            isTimerFinished = false;
                            new Handler().postDelayed(() -> isTimerFinished = true, 500);
                        } else if (itemId == R.id.navigation_more) {
                            openDialog();
                            isTimerFinished = false;
                            new Handler().postDelayed(() -> isTimerFinished = true, 2000);
                        }
                        return true;
                    } else {
                        return false;
                    }

                });

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        new GDPRChecker()
                .withContext(MainActivity.this)
                .withPrivacyUrl(getString(R.string.privacy_url)) // your privacy url
                .withPublisherIds(Constant.adMobPublisherId) // your admob account Publisher id
                .withTestMode("9424DF76F06983D1392E609FC074596C") // remove this on real project
                .check();

        HomeFragment homeFragment = new HomeFragment();
        loadFrag(homeFragment, getString(R.string.menu_home), fragmentManager);


        Bundle bundle = getIntent().getExtras();
        FirebaseMessaging.getInstance().subscribeToTopic("all");
        if (bundle != null) {
            for (String key : bundle.keySet()) {
                Object value = bundle.get(key);
                Log.d("TAG", "Key: " + key + " Value: " + value.toString());
            }
        }
        if (NetworkUtils.isConnected(MainActivity.this)) {
            getCategory();
            getPlanStatus();
        } else {
            dismissProgressDialog();
        }

        ImageView mTitle = toolbar.findViewById(R.id.ic_subscribe);

        mTitle.setOnClickListener(v -> {
            startActivity(new Intent(MainActivity.this, SubscriptionPlanActivity.class));
        });

        final String dialogTag = "dialog";
        if (savedInstanceState != null) {
            dialogFragment =
                    (FullScreenDialogFragment) getSupportFragmentManager().findFragmentByTag(dialogTag);
            if (dialogFragment != null) {
                dialogFragment.setOnConfirmListener(this);
                dialogFragment.setOnDiscardListener(this);
                dialogFragment.setOnDiscardFromExtraActionListener(this);
            }
        }

    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private void openDialog() {
        final Bundle args = new Bundle();
        args.putString("name", "Mehul");


        dialogFragment = new FullScreenDialogFragment.Builder(MainActivity.this)
                .setTitle(R.string.app_name)
                .setConfirmButton(R.string.view_all)
                .setOnConfirmListener(MainActivity.this)
                .setOnDiscardListener(MainActivity.this)
                .setContent(MoreListFragment.class, args)
                .setExtraActions(R.menu.navigation)
                .setOnDiscardFromActionListener(MainActivity.this)
                .build();

        dialogFragment.show(getSupportFragmentManager(), "dialog");
    }


    public void loadFrag(Fragment f1, String name, FragmentManager fm) {
        for (int i = 0; i < fm.getBackStackEntryCount(); ++i) {
            fm.popBackStack();
        }
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.Container, f1, name);
        ft.commit();
        setToolbarTitle(name);
    }

    public void setToolbarTitle(String Title) {
        if (getSupportActionBar() != null) {
            toolbarText.setText(Title);
        }
    }


    public void setHeader() {
    }

    private void logOut() {
        PrettyDialog pDialog = new PrettyDialog(this);
        pDialog
                .setTitle(getString(R.string.menu_logout))
                .setMessage(getString(R.string.logout_msg))
                .setIcon(R.drawable.logout)
                .addButton(
                        getString(R.string.menu_logout),
                        R.color.pdlg_color_white2,
                        R.color.pdlg_color_green,
                        () -> {
                            pDialog.dismiss();
                            myApplication.saveIsLogin(false);
                            Intent intent = new Intent(getApplicationContext(), SignInScreenActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            Remember.clear();
                            finish();
                        }).addButton(
                getString(android.R.string.no),
                R.color.pdlg_color_white2,
                R.color.pdlg_color_red,
                pDialog::dismiss)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setHeader();
//        Logg.d();
//        sendFcmRegistrationToken();
    }

    private void sendFcmRegistrationToken() {
        Intent intent = new Intent(this, FcmTokenRegistrationService.class);
        startService(intent);
    }

    private void checkGoogleApiAvailability() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);

        if (resultCode != ConnectionResult.SUCCESS) {
            if (resultCode == ConnectionResult.SUCCESS) {
                Logg.d("GoogleApi is available");
            } else {
                apiAvailability.getErrorDialog(this, resultCode, 1).show();
            }
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);
        final MenuItem searchMenuItem = menu.findItem(R.id.search);

        final SearchView searchView = (SearchView) searchMenuItem.getActionView();

        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            // TODO Auto-generated method stub
            if (!hasFocus) {
                searchMenuItem.collapseActionView();
                searchView.setQuery("", false);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String arg0) {
                // TODO Auto-generated method stub
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("search", arg0);
                startActivity(intent);
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String arg0) {
                // TODO Auto-generated method stub
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("NewApi")
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() != 0) {
            String tag = fragmentManager.getFragments().get(fragmentManager.getBackStackEntryCount() - 1).getTag();
            setToolbarTitle(tag);
            super.onBackPressed();
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }

            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, getString(R.string.back_key), Toast.LENGTH_SHORT).show();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = fragmentManager.findFragmentByTag(getString(R.string.menu_profile));
        if (fragment != null) {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void getCategory() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.MENU_CATEGORY_URL, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();

                String result = new String(responseBody);
                try {
                    MenuCategory mMenuCategory = gson.fromJson(result, MenuCategory.class);
                    if (mMenuCategory.getStatusCode() == 200) {
                        MenuCategoryVideoListAdapter mMenuCategoryAdapter = new MenuCategoryVideoListAdapter(mMenuCategory.getVideoStreamApp(), MainActivity.this, MainActivity.this);
                    } else {
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    @Override
    public void onExpand(int groupPos, ImageButton ibExp) {
    }

    public void profile(View v) {
        startActivity(new Intent(MainActivity.this, UpdateProfileActivity.class));
    }

    @Override
    public void onConfirm(@Nullable Bundle result) {
        String value;
        if (result != null) {
            value = result.getString("RESULT_FULL_NAME");
            assert value != null;
            if (value.equalsIgnoreCase("WatchList")) {
                if (myApplication.getIsLogin()) {
                    Intent intentProfile = new Intent(MainActivity.this, WatchScreenActivity.class);
                    intentProfile.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intentProfile);
                } else {
                    showAlert();
                }

            } else if (value.equalsIgnoreCase("MyTransaction")) {
                if (myApplication.getIsLogin()) {
                    Intent intenttransaction = new Intent(MainActivity.this, TransactionListActivity.class);
                    intenttransaction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intenttransaction);
                } else
                    showAlert();
            } else if (value.equalsIgnoreCase("Logout")) {
                logOut();
            } else if (value.equalsIgnoreCase("Login")) {
                showAlert();
            } else if (value.equalsIgnoreCase("MyAccount")) {
                if (myApplication.getIsLogin()) {
                    Intent intenttransaction = new Intent(MainActivity.this, DashboardActivity.class);
                    intenttransaction.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intenttransaction);
                } else
                    showAlert();
            } else if (value.equalsIgnoreCase("Membership")) {
                if (myApplication.getIsLogin()) {
                    startActivity(new Intent(MainActivity.this, SubscriptionPlanActivity.class));
                } else
                    showAlert();
            } else if (value.equalsIgnoreCase("Setting")) {
                if (myApplication.getIsLogin()) {
                    SettingFragment settingFragment = new SettingFragment();
                    loadFrag(settingFragment, getString(R.string.menu_setting), fragmentManager);
                } else
                    showAlert();
            } else if (value.equalsIgnoreCase("LiveTv")) {
                Intent intent = new Intent(MainActivity.this, MovieDetailActivity1.class);
                intent.putExtra("Id", "179");
                intent.putExtra("live", true);
                startActivity(intent);
            }
        }

    }

    private void showAlert() {
        PrettyDialog pDialog = new PrettyDialog(this);
        pDialog
                .setTitle(getString(R.string.menu_login))
                .setMessage("Please Login to Enjoy Endless Streaming !!")
                .setIcon(R.drawable.login)
                .addButton(
                        getString(R.string.menu_login),
                        R.color.pdlg_color_white2,
                        R.color.pdlg_color_green,
                        () -> {
                            pDialog.dismiss();
                            login();
                        }).addButton(
                getString(android.R.string.no),
                R.color.pdlg_color_white2,
                R.color.pdlg_color_red,
                pDialog::dismiss)
                .show();
    }

    private void login() {
        startActivity(new Intent(MainActivity.this, SignInScreenActivity.class));
    }

    @Override
    public void onDiscard() {
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    public void onDiscardFromExtraAction(int actionId, @Nullable Bundle result) {
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);
    }

    @Override
    protected void onStop() {
        isStopped = true;
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        isStopped = false;

    }

    @Override
    public void onPositiveClick(int from) {
        if (from == 101)
            login();
    }

    @Override
    public void onNegativeClick(int from) {

    }

    @Override
    public void onNeutralClick(int from) {

    }

    private void getPlanStatus() {
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        JsonObject jsObj = (JsonObject) new Gson().toJsonTree(new API());
        jsObj.addProperty("user_id", myApplication.getIsLogin() ? myApplication.getUserId() : "");
        params.put("data", API.toBase64(jsObj.toString()));
        client.post(Constant.CHECK_USER_PLAN, params, new AsyncHttpResponseHandler() {
            @Override
            public void onStart() {
                super.onStart();
                showProgressDialog();
            }

            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                dismissProgressDialog();

                String result = new String(responseBody);
                try {
                    JSONObject mainJson = new JSONObject(result);
                    JSONObject mainArray = mainJson.getJSONObject(Constant.ARRAY_NAME);
                    System.out.println("MainActivity ==> VIDEO_STREAMING_APP ==> " + mainArray);
                    System.out.println("MainActivity ==> mainJson ==> " + mainJson);
                    String success = mainArray.getString("success");
                    if (success.equals("1")) {
                        String exp_date = mainArray.getString("date");
                        String plan_id = mainArray.getString("plan_id");
                        String plan_name = mainArray.getString("current_plan");
                        String plan_duration = mainArray.getString("plan_duration");
                        String plan_price = mainArray.getString("plan_price");
                        System.out.println("MainActivity ==> exp_date ==> " + exp_date);
                        Date date = Calendar.getInstance().getTime();
                        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                        String current_date = dateFormat.format(date);
                        System.out.println("Converted String: " + current_date);

                        findDifference(current_date, exp_date, plan_id, plan_name, plan_duration, plan_price);

//                        LocalDate dateBefore = LocalDate.parse(current_date);
////                        LocalDate dateAfter = LocalDate.parse(exp_date);
////                        Period period = Period.between(dateBefore, dateAfter);
////                        int days = Math.abs(period.getDays());
//////                        long daysBetween = ChronoUnit.DAYS.between(dateBefore, dateAfter);
//////                        System.out.println ("Days: " + daysBetween);o
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                dismissProgressDialog();
            }

        });
    }

    public void findDifference(String start_date, String end_date,String plan_id, String plan_name, String plan_duration, String plan_price) {

        // SimpleDateFormat converts the
        // string format to date object
        DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time = d2.getTime() - d1.getTime();

            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            long difference_In_Seconds = (difference_In_Time / 1000) % 60;

            long difference_In_Minutes = (difference_In_Time / (1000 * 60)) % 60;

            long difference_In_Hours = (difference_In_Time / (1000 * 60 * 60)) % 24;

            long difference_In_Years = (difference_In_Time / (1000 * 60 * 60 * 24))/365;

            long difference_In_Months = (difference_In_Time / (1000 * 60 * 60 * 24))/30;

            long difference_In_Days = (difference_In_Time / (1000 * 60 * 60 * 24)) % 365;
            // Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds

            if (difference_In_Days<=2){
                showAlert2(plan_id, plan_name, plan_duration, plan_price);
            }

            System.out.print("Difference " + "between two dates is: " +difference_In_Time);

            System.out.println(" "+difference_In_Years + " years, " +difference_In_Months + " Months, " + difference_In_Days + " days, " + difference_In_Hours + " hours, " + difference_In_Minutes + " minutes, " + difference_In_Seconds + " seconds");
        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void showAlert2(String plan_id,String plan_name, String plan_duration, String plan_price) {
        PrettyDialog pDialog = new PrettyDialog(this);
        pDialog
                .setTitle("Alert!!")
                .setMessage("Your subscription is going to expire soon !! Please renew it.")
                .setIcon(R.drawable.ic_more_member)
                .addButton(
                        "Renew Now",
                        R.color.pdlg_color_white,
                        R.color.pdlg_color_green,
                        () -> {
                            pDialog.dismiss();
                            Intent intent = new Intent(getApplicationContext(), SelectSubscriptionPlanActivity.class);
                            intent.putExtra("planId", plan_id);
                            intent.putExtra("planName", plan_name);
                            intent.putExtra("promoCode", "");
                            intent.putExtra("planPrice", plan_price);
                            intent.putExtra("planDuration", plan_duration);
                            startActivity(intent);
                            finish();
                        }).addButton(
                getString(android.R.string.no),
                R.color.pdlg_color_white,
                R.color.pdlg_color_red,
                pDialog::dismiss)
                .show();
    }


}
