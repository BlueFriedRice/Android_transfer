package com.example.transfer.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.example.transfer.MainActivity;
import com.example.transfer.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener {

    private Switch onOff;
    private Switch alarm5;
    private Switch alarm10;
    private Switch alarm15;

    private TextView toolbar_title;

    private Boolean onoffb = false;
    private Boolean alarm5b = false;
    private Boolean alarm10b = false;
    private Boolean alarm15b = false;

    private SharedPreferences SettingData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        toolbar_title = (TextView) findViewById(R.id.settoolbar_title);

        Toolbar toolbar = (Toolbar) findViewById(R.id.setToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_title.setText("알림설정");

        onOff = (Switch) findViewById(R.id.on_off);
        alarm5 = (Switch) findViewById(R.id.alarm_5);
        alarm10 = (Switch) findViewById(R.id.alarm_10);
        alarm15 = (Switch) findViewById(R.id.alarm_15);

        alarm5.setEnabled(false);
        alarm10.setEnabled(false);
        alarm15.setEnabled(false);

        SettingData = getSharedPreferences("SettingData", MODE_PRIVATE);

        LoadSetting();

        if(onoffb) {
            onOff.setChecked(onoffb);

            alarm5.setEnabled(true);
            alarm10.setEnabled(true);
            alarm15.setEnabled(true);

            alarm5.setChecked(alarm5b);
            alarm10.setChecked(alarm10b);
            alarm15.setChecked(alarm15b);
        }

        onOff.setOnCheckedChangeListener(this);
        alarm5.setOnCheckedChangeListener(this);
        alarm10.setOnCheckedChangeListener(this);
        alarm15.setOnCheckedChangeListener(this);

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

        switch (compoundButton.getId()) {
            case R.id.on_off:
                if (isChecked) {
                    onoffb = true;

                    alarm5.setChecked(true);

                    alarm5.setEnabled(true);
                    alarm10.setEnabled(true);
                    alarm15.setEnabled(true);

                } else {
                    onoffb = false;

                    alarm5.setChecked(false);
                    alarm10.setChecked(false);
                    alarm15.setChecked(false);

                    alarm5.setEnabled(false);
                    alarm10.setEnabled(false);
                    alarm15.setEnabled(false);
                }
                break;

            case R.id.alarm_5:
                if (isChecked) {

                        alarm10.setChecked(false);
                        alarm15.setChecked(false);

                    alarm5b = true;
                } else {
                    alarm5b = false;
                }
                break;

            case R.id.alarm_10:
                if (isChecked) {

                        alarm5.setChecked(false);
                        alarm15.setChecked(false);

                    alarm10b = true;
                } else {
                    alarm10b = false;
                }
                break;

            case R.id.alarm_15:
                if (isChecked) {

                        alarm5.setChecked(false);
                        alarm10.setChecked(false);

                    alarm15b = true;
                } else {
                    alarm15b = false;
                }
                break;

        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                SaveSetting();
                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("onoffb", onoffb);
                intent.putExtra("alarm5b", alarm5b);
                intent.putExtra("alarm10b", alarm10b);
                intent.putExtra("alarm15b", alarm15b);
                startActivity(intent);
                break;
        }

        return true;
    }

    public void SaveSetting() {

        SharedPreferences.Editor editor = SettingData.edit();

        editor.putBoolean("onoffb", onoffb);
        editor.putBoolean("alarm5b", alarm5b);
        editor.putBoolean("alarm10b", alarm10b);
        editor.putBoolean("alarm15b", alarm15b);

        editor.commit(); //완료한다.

    }

    public void LoadSetting() {

        onoffb = SettingData.getBoolean("onoffb", false);
        alarm5b = SettingData.getBoolean("alarm5b", false);
        alarm10b = SettingData.getBoolean("alarm10b", false);
        alarm15b = SettingData.getBoolean("alarm15b", false);

    }

    @Override
    public void onBackPressed()
    {
        SaveSetting();
        Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("onoffb", onoffb);
        intent.putExtra("alarm5b", alarm5b);
        intent.putExtra("alarm10b", alarm10b);
        intent.putExtra("alarm15b", alarm15b);
        startActivity(intent);
        super.onBackPressed();
    }

}