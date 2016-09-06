package com.pogofications.activities;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ListViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import com.onesignal.OneSignal;
import com.pogofications.App;
import com.pogofications.constants.Config;
import com.pogofications.R;
import com.yarolegovich.lovelydialog.LovelyInfoDialog;
import com.yarolegovich.lovelydialog.LovelyStandardDialog;

import org.json.JSONObject;


public class HomeActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private Toolbar toolbar;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private AppBarLayout appBar;
    private FloatingActionButton fab;
    private SwipeRefreshLayout swipeRefresh;
    private ListViewCompat lvLocations;
    private String titleLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //Call setupview
        setupView();

        //Call onclick listeners
        onClickListener();
    }

    @Override
    public void onResume() {
        super.onResume();
        String message = ((App) getApplication()).getMessage();
        if (message != null) {
            displayNotification(message);
        }

        // Register mMessageReceiver to receive messages.
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver,
                new IntentFilter(Config.NOTIFICATION_INTENT_FILTER_TAG));
    }

    // handler for received Intents for the "my-event" event
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Extract data included in the Intent
            String title = intent.getStringExtra(Config.NOTIFICATION_TITLE_TAG);
            String message = intent.getStringExtra(Config.NOTIFICATION_MESSAGE_TAG);
            displayNotification(message);
            Log.d("receiver", "Got message: " + message);
        }
    };

    @Override
    protected void onPause() {
        // Unregister since the activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
        super.onPause();
    }

    /**
     * Setup View components
     */
    private void setupView() {
        //Set toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_home);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab_home);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh_home);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        appBar = (AppBarLayout) findViewById(R.id.appbar);
        appBar.setExpanded(false);
        lvLocations = (ListViewCompat) findViewById(R.id.lv_locations);
        swipeRefresh.setEnabled(false);
        getLocationTag();
        displayAgreement();
    }

    /**
     * On click listeners
     */
    private void onClickListener() {
        lvLocations.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                onCreateDialog(getResources().getStringArray(R.array.address)[i]);
            }
        });
    }

    /**
     * Get the assigned tags
     */
    private void getLocationTag() {
        if (isOnline()) {
            OneSignal.getTags(new OneSignal.GetTagsHandler() {
                @Override
                public void tagsAvailable(JSONObject tags) {
                    if (tags.optString(Config.LOCATION_TAG).toString() != null) {
                        titleLocation = tags.optString(Config.LOCATION_TAG).toString();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                collapsingToolbarLayout.setTitle(titleLocation);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                collapsingToolbarLayout.setTitle(getResources().getString(R.string.select_adress));
                                onLocationNotSelected();
                            }
                        });
                    }
                }
            });
        } else {
            Toast.makeText(HomeActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Create dialog to select address
     *
     * @return
     */
    private void onCreateDialog(final String locationName) {
        swipeRefresh.setRefreshing(true);
        String message = getResources().getString(R.string.location_update);
        new LovelyStandardDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                .setMessage(getResources().getString(R.string.location_update) + "'" + locationName + "'")
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLocationSelected(locationName);
                    }
                })
                .setNegativeButton(R.string.cancel, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // User cancelled the dialog
                        swipeRefresh.setRefreshing(false);
                    }
                })
                .show();
    }

    /**
     * Show agreemnet dialog
     */
    private void displayAgreement() {
        new LovelyInfoDialog(this)
                .setTopColorRes(R.color.colorPrimary)
                //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                .setNotShowAgainOptionEnabled(0)
                .setCancelable(false)
                .setTitle(R.string.agreement_title)
                .setMessage(R.string.agreement_text)
                .setIconTintColor(R.color.colorAccent)
                .show();
    }

    private void onLocationNotSelected() {
        new Handler().postDelayed(new Runnable() {
            // Showing splash screen with a timer.
            @Override
            public void run() {
                new LovelyStandardDialog(HomeActivity.this)
                        .setTopColorRes(R.color.colorPrimary)
                        //This will add Don't show again checkbox to the dialog. You can pass any ID as argument
                        .setCancelable(false)
                        .setTitle(R.string.location_not_selected_title)
                        .setMessage(R.string.location_not_selected_description)
                        .setIconTintColor(R.color.colorAccent)
                        .show();
            }
        }, Config.SPLASH_TIME_OUT);
    }

    /**
     * Show notification via dialog method
     *
     * @param message
     */
    private void displayNotification(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false)
                .setMessage(message)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Ok clickec
                    }
                });
        Dialog dialog = builder.create();
        dialog.show();
        Linkify.addLinks((TextView) dialog.findViewById(android.R.id.message), Linkify.ALL);
    }

    @Override
    public void onRefresh() {

    }

    /**
     * Location selected method
     */
    private void onLocationSelected(String location) {
        if (isOnline()) {
            collapsingToolbarLayout.setTitle(location);
            OneSignal.sendTag(Config.LOCATION_TAG, location);
            swipeRefresh.setRefreshing(false);
        } else {
            Toast.makeText(HomeActivity.this, R.string.no_internet, Toast.LENGTH_LONG).show();
        }
    }

    /**
     * Check network connection
     *
     * @return
     */
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
