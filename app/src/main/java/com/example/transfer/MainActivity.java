package com.example.transfer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;


import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transfer.Card.CardInfoUpdate;
import com.example.transfer.Card.CardRegistration;
import com.example.transfer.Fragment.ViewPagerAdapter;
import com.example.transfer.Fragment.recentFragment;
import com.example.transfer.Model.CardInfo;
import com.example.transfer.Prevalent.Prevalent;
import com.example.transfer.Service.ForegroundService;
import com.example.transfer.Service.NotificationCancel;
import com.example.transfer.Setting.SettingsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ViewPager viewPager;

    private TabLayout tabLayout;
    //    private ArrayList<String> tabNames = new ArrayList<>();
//    private String[] pageTitle = {"최근사용내역", "주변 정보","환승관련정보"};
    private TextView tagText, welcome;
    private Button join;
    private static final String TAG = "MainActivity";
    private NfcAdapter mAdapter;
    private PendingIntent mPendingIntent;
    private String TagIdStr, TagID, TagID2;
    private String parentDbName = "Transfer Users";
    private ProgressDialog loadingBar;
    private ImageButton MyCardInfo, NfcSet;
    private DatabaseReference infoRef;
    private String DateRandomkey;
    public static Context mContext;
    public static boolean onoffb, alarm5b, alarm10b, alarm15b;
    public boolean isRunning;
    private String saveCurrentTime, saveCurrentDate, saveAfterTime;
    private String saveCurrentTime3;
    private long limit = 0;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;

        onoffb = getIntent().getBooleanExtra("onoffb", false);
        alarm5b = getIntent().getBooleanExtra("alarm5b", false);
        alarm10b = getIntent().getBooleanExtra("alarm10b", false);
        alarm15b = getIntent().getBooleanExtra("alarm15b", false);

        infoRef = FirebaseDatabase.getInstance().getReference().child("Transfer Users");

        TagID2 = getIntent().getStringExtra("TagID");

        tagText = (TextView) findViewById(R.id.tagText);
        join = (Button) findViewById(R.id.join);
        welcome = (TextView) findViewById(R.id.welcome);

        NfcSet = (ImageButton) findViewById(R.id.NfcSet);
        MyCardInfo = (ImageButton) findViewById(R.id.MyCardInfo);

        loadingBar = new ProgressDialog(this);

        mAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mAdapter == null) { // NFC 미지원단말
            Toast.makeText(getApplicationContext(), "NFC를 지원하지 않는 단말기입니다.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        IntentFilter tagDetected = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        tagDetected.addCategory(Intent.CATEGORY_DEFAULT);
        mPendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        viewPager = (ViewPager) findViewById(R.id.view_pager);

        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

        //set gravity for tab bar
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

//        if (toolbar != null) {
//            toolbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_gradient));
//        }

//        loadTabName();

        setTabLayout();

        //set viewpager adapter
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //change Tab selection when swipe ViewPager
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        //change ViewPager page when tab selected
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());

                if (tab.getPosition() == 0) {
                    tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#4e8e7a"), PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#4e8e7a"), PorterDuff.Mode.SRC_IN);
                } else if (tab.getPosition() == 1) {
                    tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#4e8e7a"), PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#4e8e7a"), PorterDuff.Mode.SRC_IN);
                } else if (tab.getPosition() == 2) {
                    tabLayout.getTabAt(0).getIcon().setColorFilter(Color.parseColor("#4e8e7a"), PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(1).getIcon().setColorFilter(Color.parseColor("#4e8e7a"), PorterDuff.Mode.SRC_IN);
                    tabLayout.getTabAt(2).getIcon().setColorFilter(Color.parseColor("#ffffff"), PorterDuff.Mode.SRC_IN);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        join.setOnClickListener(this);
        MyCardInfo.setOnClickListener(this);
        NfcSet.setOnClickListener(this);
    }

    @TargetApi(Build.VERSION_CODES.N)
    private void setTabLayout() {
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("하차내역").setIcon(R.drawable.list_pick));
        tabLayout.addTab(tabLayout.newTab().setText("주변정보").setIcon(R.drawable.gps));
        tabLayout.addTab(tabLayout.newTab().setText("유용한정보").setIcon(R.drawable.information));
//        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.option:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;

            case R.id.help:
                Intent intent2 = new Intent(MainActivity.this, HelpActivity.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent2);
                break;

            default:
                return super.onOptionsItemSelected(item);
        }

        return true;

    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume() called");
        super.onResume();

        if (mAdapter != null) {

            mAdapter.enableForegroundDispatch(this, mPendingIntent, null, null);

        }
    }

    @Override
    protected void onPause() {

        if (mAdapter != null) {

            mAdapter.disableForegroundDispatch(this);
        }
        super.onPause();
    }

    @Override

    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);

        Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
        if(tag == null){
            return;
        }
        byte[] tagId = tag.getId();

        if (tag != null) {

            TagIdStr = toHexString(tagId);

        } else {
            TagIdStr = TagID2;
        }

        MyCardInfo.setVisibility(View.INVISIBLE);
        welcome.setVisibility(View.INVISIBLE);

        TagID = toHexString(tagId);
        cardinfo(TagID);

//        ((recentFragment)recentFragment.mContext).startService();
    }

    public static final String CHARS = "0123456789ABCDEF";

    public static String toHexString(byte[] data) {

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < data.length; ++i) {

            sb.append(CHARS.charAt((data[i] >> 4) & 0x0F))

                    .append(CHARS.charAt(data[i] & 0x0F));

        }

        return sb.toString();

    }

    private void cardinfo(String TagID) {

        final DatabaseReference RootRef;
        RootRef = FirebaseDatabase.getInstance().getReference();

        RootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {

                    if (dataSnapshot.child(parentDbName).child(TagID).exists()) {
                        CardInfo userData = dataSnapshot.child(parentDbName).child(TagID).getValue(CardInfo.class);
                        Prevalent.currentOnlineUser = userData;

                        if (userData.getTagID().equals(TagID)) {
                            tagText.setVisibility(View.VISIBLE);
                            MyCardInfo.setVisibility(View.VISIBLE);
                            join.setVisibility(View.INVISIBLE);
                            userInfoDisplay(welcome);
                            Toast.makeText(MainActivity.this, "카드정보 읽기를 성공했습니다.", Toast.LENGTH_SHORT).show();
                            loadingBar.dismiss();
                            setToFirebase(TagID);
                        }
                    } else {
                        String noTagId = "";
                        ToTransInfo(noTagId);
                        tagText.setVisibility(View.INVISIBLE);
                        join.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this, "등록되지않은 NFC카드입니다. 정보를 먼저 등록해주세요.", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void userInfoDisplay(final TextView welcome) {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Transfer Users").child(Prevalent.currentOnlineUser.getTagID());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    if (dataSnapshot.child("tagID").exists()) {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String nickname = dataSnapshot.child("nickname").getValue().toString();
                        tagText.setText(nickname);
                        welcome.setText(name + " 님 환영합니다." + "\n하차 시 카드를 태그하면 알림이 시작됩니다.");
                        welcome.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setToFirebase(String TagID) {

        String info = "하차";
        Calendar calForDate = Calendar.getInstance();

        Date date = new Date();

        SimpleDateFormat currentDate = new SimpleDateFormat("yyyy, MMM dd", Locale.KOREA);
        saveCurrentDate = currentDate.format(calForDate.getTime());

        SimpleDateFormat currentTime = new SimpleDateFormat("HH:mm:ss a", Locale.KOREA);
        SimpleDateFormat currentTime3 = new SimpleDateFormat("yyyy, MMM dd HH:mm:ss a", Locale.KOREA);
        saveCurrentTime = currentTime.format(calForDate.getTime()); // 현재 시간
        saveCurrentTime3 = currentTime3.format(calForDate.getTime());

        SimpleDateFormat currentTime2 = new SimpleDateFormat("HH", Locale.KOREA);
        String saveCurrentTime2 = currentTime2.format(calForDate.getTime());

        calForDate.setTime(date);

        if (saveCurrentTime2.equals("21") == true || saveCurrentTime2.equals("22") == true
                || saveCurrentTime2.equals("23") == true || saveCurrentTime2.equals("00") == true
                || saveCurrentTime2.equals("01") == true || saveCurrentTime2.equals("02") == true
                || saveCurrentTime2.equals("03") == true || saveCurrentTime2.equals("04") == true
                || saveCurrentTime2.equals("05") == true || saveCurrentTime2.equals("06") == true) {

            calForDate.add(Calendar.HOUR, 1);
        } else {

            calForDate.add(Calendar.MINUTE, 30);

        }

        saveAfterTime = currentTime.format(calForDate.getTime());
        DateRandomkey = saveCurrentDate + "," + saveCurrentTime;

        HashMap<String, Object> recentMap = new HashMap<>();
        recentMap.put("info", info);
        recentMap.put("date", saveCurrentDate);
        recentMap.put("time", saveCurrentTime);
        recentMap.put("aftertime", saveAfterTime);

        FirebaseDatabase.getInstance().getReference()
                .child(Prevalent.currentOnlineUser.getTagID() + "Info")
                .child(DateRandomkey)
                .updateChildren(recentMap)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {

                            ToTransInfo(TagID);

                            load();
                            if (!isRunning) {
                                try {
                                    ((recentFragment) recentFragment.mContext).startService(onoffb, alarm5b, alarm10b, alarm15b, saveCurrentTime3);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            } else
                                Toast.makeText(MainActivity.this, "시간 갱신 희망 시, 기존의 알림을 제거해주세요.\n알림 메시지 옆 버튼을 통하여 알림을 종료할 수 있습니다.", Toast.LENGTH_LONG).show();


                        }
                    }
                });
    }

    private void ToTransInfo(String TagID) {

        ((recentFragment) recentFragment.mContext).refresh(TagID);

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.join:
                Intent intent = new Intent(MainActivity.this, CardRegistration.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("TagIdStr", TagIdStr);
                startActivity(intent);
                break;
            case R.id.MyCardInfo:
                Intent intent2 = new Intent(MainActivity.this, CardInfoUpdate.class);
                intent2.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent2.putExtra("TagIdStr", TagIdStr);
                startActivity(intent2);
                break;
            case R.id.NfcSet:
                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
        }
    }

    public void save(boolean isRunning) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        editor = sharedPreferences.edit();
        editor.putBoolean("isRunning", isRunning);
        editor.apply();
    }

    public void load() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        isRunning = sharedPreferences.getBoolean("isRunning", false);
    }

    public boolean checkRunning() {
        load();
        return this.isRunning;
    }

    public boolean[] checkSetting() {
        return new boolean[]{onoffb, alarm5b, alarm10b, alarm15b};
    }


}
