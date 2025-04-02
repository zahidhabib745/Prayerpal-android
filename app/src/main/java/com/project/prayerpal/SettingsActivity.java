package com.project.prayerpal;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {//rename this to SettingsActivity

    Toolbar toolBar;

    private ExpandableListView expandableListView;
    private SettingsExpandableListAdapter expandableListAdapter;
    private List<String> groups;
    private Map<String, List<SettingsModel>> settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        addToolbar();

        groups = new ArrayList<>();
        settings = new HashMap<>();

        groups.add(getResources().getString(R.string.prayer_group_setting_item_title));
        groups.add(getResources().getString(R.string.notification_group_setting_item));

        List<SettingsModel> group1Settings = new ArrayList<>();
        group1Settings.add(new SettingsModel(getResources().getString(R.string.adhaan_setting_item), "Description 1", false, 1, "Prayer"));
        group1Settings.add(new SettingsModel(getResources().getString(R.string.amend_hour_setting_item), "Description 2", false, 2, "Prayer"));
        group1Settings.add(new SettingsModel(getResources().getString(R.string.amend_madhab_setting_item), "Description 2", false, 3, "Prayer"));
        settings.put(groups.get(0), group1Settings);

        List<SettingsModel> group2Settings = new ArrayList<>();
        group2Settings.add(new SettingsModel(getResources().getString(R.string.notification_setting_item), "Description 3", true, 1, "Notifications"));
        settings.put(groups.get(1), group2Settings);

        expandableListView = findViewById(R.id.expandable_list_view);
        expandableListAdapter = new SettingsExpandableListAdapter(this, groups, settings);
        expandableListAdapter.setExpandedListView(expandableListView);
        expandableListView.setAdapter(expandableListAdapter);
    }

    public void addToolbar(){

        createToolbar();
        removeUIFromToolbar();
        addBackButtonToToolbar();
    }

    public void createToolbar(){

        toolBar = findViewById(R.id.toolbar_secondary);
        setSupportActionBar(toolBar);
    }

    public void removeUIFromToolbar(){

        CardView settingsImageView = findViewById(R.id.tool_bar_main_card_view);
        settingsImageView.setVisibility(View.GONE);

        ActionBar actionBar = getSupportActionBar();

        if(actionBar != null){

            actionBar.setDisplayShowTitleEnabled(false);
        }
    }

    public void addBackButtonToToolbar(){

        LayoutInflater inflater = LayoutInflater.from(this);
        View customBackButton = inflater.inflate(R.layout.custom_back_button, toolBar, false);

        customBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        toolBar.addView(customBackButton);
    }

}