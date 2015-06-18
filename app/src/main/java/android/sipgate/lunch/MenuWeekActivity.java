package android.sipgate.lunch;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.schafm.helper.DateHelper;
import android.sipgate.lunch.sipgatelunch.R;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.samples.apps.iosched.ui.widget.DrawShadowFrameLayout;

import android.sipgate.lunch.data.RefreshDataTask;
import android.sipgate.lunch.ormlite.Meal;
import android.sipgate.lunch.data.GoogleImageSearch;
import android.sipgate.lunch.ui.DayHeaderView;
import android.sipgate.lunch.ui.MealView;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import android.schafm.helper.UiHelper;


/**
 * Activity to show the menu of the current week.
 *
 * @author schafm
 */
public class MenuWeekActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, GoogleImageSearch.ImageSearchResponse {
    private static final String TAG = MenuWeekActivity.class.getSimpleName();
    private static final String ACTION_FOR_INTENT_CALLBACK = "REFRESH_DATA";
    private static final int[] RES_IDS_ACTION_BAR_SIZE = {R.attr.actionBarSize};
    private SwipeRefreshLayout mSwipeLayout;
    private LinearLayout mMealsLayout;
    private RelativeLayout mProgressLayout;
    private ScrollView mScrollView;
    private GoogleImageSearch mGoogleImageSearch;
    private boolean mGoogleImageSearchEnabled;
    private List<Meal> mMealsList;
    private DayHeaderView mTodayHeaderView;
    private Date mSyncDate;
    private boolean mTransculentNavBar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.week_layout);
        mMealsLayout = (LinearLayout) findViewById(R.id.mealsLayout);
        mProgressLayout = (RelativeLayout) findViewById(R.id.progressLayout);
        mScrollView = (ScrollView) findViewById(R.id.scrollView);
        mSwipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeLayout.setOnRefreshListener(this);
        mSwipeLayout.setColorScheme(R.color.sipgate_blue);
        DrawShadowFrameLayout drawShadowFrameLayout = (DrawShadowFrameLayout) findViewById(R.id.headerLayout);
        drawShadowFrameLayout.setShadowTopOffset(UiHelper.calculateActionBarSize(this, RES_IDS_ACTION_BAR_SIZE));
        setSupportActionBar(getActionBarToolbar());

        checkForFirstRun();
        mGoogleImageSearchEnabled = CustomApplication.getPrefs().getBoolean("enableGoogleImageSearch",
                CustomPreferenceActivity.DEFAULT_ENABLE_GOOGLE_IMAGE_SEARCH);

        loadUiContent();
        loadData(true);
    }

    private void initTransculentNavBar(boolean transculentNavBar) {
        mTransculentNavBar = transculentNavBar;
        if (mTransculentNavBar && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setNavigationBarTintColor(Color.LTGRAY);
            tintManager.setTintAlpha(0.2f);
            tintManager.setNavigationBarTintEnabled(true);
            findViewById(android.R.id.content).setPadding(0, getStatusBarHeight(), 0, 0);
            mMealsLayout.setPadding(0, 0, 0, UiHelper.calculateActionBarSize(this, RES_IDS_ACTION_BAR_SIZE));
        } else if (!mTransculentNavBar && (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            findViewById(android.R.id.content).setPadding(0, 0, 0, 0);
            mMealsLayout.setPadding(0, 0, 0, UiHelper.dpToPx(20));
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    private void checkForFirstRun() {
        if (CustomApplication.getPrefs().getBoolean("firstRun", true)) {
            CustomPreferenceActivity.initFirstRun(this);
            CustomApplication.getPrefs().edit().putBoolean("firstRun", false).commit();
        }
    }

    private void loadUiContent() {
        mMealsLayout.removeAllViews();
        mMealsList = new ArrayList<>();
        try {
            mMealsList = CustomApplication.getDbHelper().getMealDao().queryForAll();
        } catch (SQLException e) {
            Log.e(TAG, "error trying to receive weekly meals from database!", e);
            Toast.makeText(this, "Database error!", Toast.LENGTH_LONG).show();
            return;
        }

        if (mMealsList.isEmpty()) {
            hideProgressLoader();
            return;
        }

        Date lastDate = null;
        Collections.sort(mMealsList);
        for (final Meal meal : mMealsList) {
            if (lastDate == null || lastDate.getTime() != meal.getDate().getTime()) {
                lastDate = meal.getDate();
                DayHeaderView dayHeaderView = new DayHeaderView(this, meal.getDate());
                addViewToMealsView(dayHeaderView);
                if (DateHelper.isToday(meal.getDate())) {
                    mTodayHeaderView = dayHeaderView;
                }
            }

            final MealView mealView = new MealView(this);
            mealView.setMealTitle(meal.getName());
            mealView.setType(meal.getType());
            final String url = "http://altepost.sipgate.net/showPic.php?meal=" + meal.getId() + "&pic=image.jpg";
            loadMealPhoto(mealView, url, new Callback() {
                @Override
                public void onSuccess() {
                }

                @Override
                public void onError() {
                    if (mGoogleImageSearchEnabled) {
                        if (mGoogleImageSearch == null) {
                            mGoogleImageSearch = new GoogleImageSearch();
                        }
                        mGoogleImageSearch.requestImageSearch(mealView, MenuWeekActivity.this);
                    }
                }
            });

            mealView.getImageView().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showFullscreenImage(mealView.getUrl());
                }
            });
            addViewToMealsView(mealView);
            hideProgressLoader();
        }

        mScrollView.post(new Runnable() {
            public void run() {
                scrollToToday();
            }
        });
    }

    private void scrollToToday() {
        final int xScrollPosition;
        if (mTodayHeaderView != null) {
            xScrollPosition = mTodayHeaderView.getTop();
        } else {
            xScrollPosition = 0;
        }

        // scroll animation
//        if (xScrollPosition > 0) {
//            ObjectAnimator objAnim = ObjectAnimator.ofInt(
//                    mScrollView, "scrollY", 0, xScrollPosition);
//            objAnim.setDuration(2000L);
//            objAnim.start();
//        }
        mScrollView.scrollTo(0, xScrollPosition);
    }

    private void addViewToMealsView(final View view) {
        mMealsLayout.addView(view);
        view.setVisibility(View.INVISIBLE);
        view.post(new Runnable() {
            @Override
            public void run() {
                view.setVisibility(View.VISIBLE);
                YoYo.with(Techniques.FadeIn)
                        .duration(1000)
                        .playOn(view);
            }
        });
    }

    private void showFullscreenImage(String url) {
        Intent mIntent = new Intent(MenuWeekActivity.this, FullscreenActivity.class);
        mIntent.putExtra("image_url", url);
        startActivity(mIntent);
    }

    private void hideProgressLoader() {
        mProgressLayout.setVisibility(View.GONE);
        mSwipeLayout.setVisibility(View.VISIBLE);
    }

    private void onSettingsAction() {
        Intent mIntent = new Intent(MenuWeekActivity.this, CustomPreferenceActivity.class);
        startActivity(mIntent);
    }

    private void loadData(boolean showLargeProgress) {
        mMealsLayout.removeAllViews();
        if (showLargeProgress) {
            mProgressLayout.setVisibility(View.VISIBLE);
        }
        mSwipeLayout.setVisibility(View.GONE);
        RefreshDataTask dataTask = new RefreshDataTask(this, ACTION_FOR_INTENT_CALLBACK);
        dataTask.execute();
    }

    private void loadMealPhoto(final MealView mealView, final String url) {
        loadMealPhoto(mealView, url, null);
    }

    private void loadMealPhoto(final MealView mealView, final String url, final Callback callback) {
        runOnUiThread(new Runnable() {
            public void run() {
                int photoWidth = UiHelper.getDisplayWidth();
                int photoHeight = (int) getResources().getDimension(R.dimen.mealview_height);
                mealView.setUrl(url);
                if (callback == null) {
                    Picasso.with(MenuWeekActivity.this).load(url)
                            .placeholder(R.drawable.meal_template)
                            .error(R.drawable.meal_template)
                            .resize(photoWidth, photoHeight).centerCrop()
                            .into(mealView.getImageView());
                } else {
                    Picasso.with(MenuWeekActivity.this).load(url)
                            .placeholder(R.drawable.meal_template)
                            .error(R.drawable.meal_template)
                            .resize(photoWidth, photoHeight).centerCrop()
                            .into(mealView.getImageView(), callback);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_overview, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            onSettingsAction();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(mDataRefreshReceiver, new IntentFilter(ACTION_FOR_INTENT_CALLBACK));
        boolean transculentNavBar = CustomApplication.getPrefs().getBoolean("transculentNavBar",
                CustomPreferenceActivity.DEFAULT_TRANSCULENT_NAV_BAR);
        mGoogleImageSearchEnabled = CustomApplication.getPrefs().getBoolean("enableGoogleImageSearch",
                CustomPreferenceActivity.DEFAULT_ENABLE_GOOGLE_IMAGE_SEARCH);

        if (transculentNavBar != mTransculentNavBar) {
            initTransculentNavBar(transculentNavBar);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(mDataRefreshReceiver);
    }

    private BroadcastReceiver mDataRefreshReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int result = intent.getIntExtra("result", 0);
            if (result == RefreshDataTask.RESPONSE_REFRESH_FINISHED) {
                mSyncDate = new Date();
            } else if (result == RefreshDataTask.RESPONSE_REFRESH_ERROR) {
                String errorMsg = intent.getStringExtra("error_msg");
                Toast.makeText(MenuWeekActivity.this, errorMsg, Toast.LENGTH_LONG).show();
            }
            mSwipeLayout.setRefreshing(false);
            mProgressLayout.setVisibility(View.GONE);
            loadUiContent();
        }
    };

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        if (mSyncDate != null) {
            savedInstanceState.putLong("timestamp", mSyncDate.getTime());
        }
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (!savedInstanceState.containsKey("timestamp")) {
            return;
        }

        mSyncDate = new Date(savedInstanceState.getLong("timestamp"));
        if (!DateHelper.isToday(mSyncDate)) {
            loadData(true);
        }
    }

    @Override
    public void onRefresh() {
        loadData(false);
    }

    @Override
    public void onSearchResponse(final MealView mealView, final String url) {
        loadMealPhoto(mealView, url);
    }
}
