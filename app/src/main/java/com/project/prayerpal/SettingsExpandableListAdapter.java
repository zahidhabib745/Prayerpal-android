package com.project.prayerpal;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.appcompat.widget.SwitchCompat;

import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Map;

public class SettingsExpandableListAdapter extends BaseExpandableListAdapter {

    private final Context context;
    private final List<String> groups;
    private final Map<String, List<SettingsModel>> settings;
    private static ListView expandedListView;

    public SettingsExpandableListAdapter(Context context, List<String> groups, Map<String, List<SettingsModel>> settings){

        this.context = context;
        this.groups = groups;
        this.settings = settings;
    }

    public void setExpandedListView(ListView elv){

        expandedListView = elv;
    }

    public int getGroupCount(){

        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {

        String groupName = groups.get(groupPosition);
        return settings.get(groupName).size();
    }

    @Override
    public Object getGroup(int groupPosition) {

        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {

        String groupName = groups.get(groupPosition);

        return settings.get(groupName).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {

        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPositon) {

        return childPositon;
    }

    @Override
    public boolean hasStableIds() {

        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup viewGroup) {

        if(view == null){

            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_group_header, viewGroup, false);
        }

        TextView textGroup = view.findViewById(R.id.text_group);
        textGroup.setText((String) getGroup(groupPosition));

        ImageView list_group_Arrow = view.findViewById(R.id.group_header_arrow_icon);

        if(isExpanded){

            list_group_Arrow.setImageResource(R.drawable.arrow_up);
        }else{

            list_group_Arrow.setImageResource(R.drawable.arrow_down);
        }

        return view;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View view, ViewGroup viewGroup) {

        if(view == null){

            LayoutInflater inflater = LayoutInflater.from(context);
            view = inflater.inflate(R.layout.list_item_setting, viewGroup, false);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

        final SettingsModel settingItem = (SettingsModel) getChild(groupPosition, childPosition);

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch switchComponent = view.findViewById(R.id.toggle_button);

        switchComponent.setText(settingItem.getTitle());

        if(settingItem.getGroupParent().equals("Prayer")){

            switch (settingItem.getSettingId()){

                case 1:

                    switchComponent.setChecked(sharedPreferences.getBoolean("recieveAdhaan", false));
                    break;
                case 2:

                    switchComponent.setChecked(sharedPreferences.getBoolean("amendTo12Hour", false));
                    break;
                case 3:

                    switchComponent.setChecked(sharedPreferences.getBoolean("amendToMadhab", false));
                    break;
            }
        }else if (settingItem.getGroupParent().equals("Notifications")){

            switchComponent.setChecked(sharedPreferences.getBoolean("recieveNotification", false));
        }

        switchComponent.setOnCheckedChangeListener((compoundButton, isChecked) -> {

            SharedPreferences.Editor editor = sharedPreferences.edit();

            if(settingItem.getGroupParent().equals("Prayer")){

                switch (settingItem.getSettingId()){

                    case 1:

                        editor.putBoolean("recieveAdhaan", isChecked);
                        break;
                    case 2:

                        editor.putBoolean("amendTo12Hour", isChecked);
                        break;
                    case 3:

                        editor.putBoolean("amendToMadhab", isChecked);
                        break;
                }
            }else if (settingItem.getGroupParent().equals("Notifications")){

                editor.putBoolean("recieveNotification", isChecked);
            }

            editor.apply();

            if(!sharedPreferences.getBoolean("recieveNotification", false)){

                NotificationScheduler.cancelNotifications(context);

                editor.putBoolean("hasNotificationsBeenScheduled", false);
                editor.apply();

                Log.d("habib", "cancelled notifications");
            }

            Toast.makeText(context, "pressed button", Toast.LENGTH_SHORT).show();
        });

        boolean v = sharedPreferences.getBoolean("recieveNotification", false);

        Toast.makeText(context, Boolean.toString(v), Toast.LENGTH_SHORT).show();

        return view;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}
