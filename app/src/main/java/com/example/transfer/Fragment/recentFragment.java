package com.example.transfer.Fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.transfer.MainActivity;
import com.example.transfer.Model.Notice;
import com.example.transfer.Service.ForegroundService;
import com.example.transfer.R;
import com.example.transfer.ViewHolder.NoticeViewHolder;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class recentFragment extends Fragment {
    private static long START_TIME_IN_MILLIS = 60000 * 30;
    private String time;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    public static CountDownTimer timer;
    public static Fragment mContext;
    private MainActivity activity;
    private String TagID = "";
    private String tag = "";
    private DatabaseReference deleteRef;
    private RecyclerView recyclerView;
    private LinearLayout recentli;
    private Vibrator vibrator;

    public recentFragment() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mContext = this;
        activity = (MainActivity) getActivity();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recentli = (LinearLayout) view.findViewById(R.id.recentli);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_menu);
        vibrator = (Vibrator) Objects.requireNonNull(getActivity()).getSystemService(Context.VIBRATOR_SERVICE);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (!tag.equals("")) {

            TagID = tag;

            recentli.setBackgroundColor(Color.WHITE);

            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            ((LinearLayoutManager) layoutManager).setReverseLayout(true);
            ((LinearLayoutManager) layoutManager).setStackFromEnd(true);
            recyclerView.setLayoutManager(layoutManager);

            DatabaseReference infoRef = FirebaseDatabase.getInstance().getReference().child(TagID + "Info");

            FirebaseRecyclerOptions<Notice> options =
                    new FirebaseRecyclerOptions.Builder<Notice>()
                            .setQuery(infoRef, Notice.class)
                            .build();

            FirebaseRecyclerAdapter<Notice, NoticeViewHolder> adapter =
                    new FirebaseRecyclerAdapter<Notice, NoticeViewHolder>(options) {
                        @Override
                        protected void onBindViewHolder(@NonNull NoticeViewHolder holder, int position, @NonNull Notice model) {
                            holder.transText.setText(model.getInfo());
                            holder.dateText.setText(model.getDate() + "일");
                            holder.timeText.setText(model.getTime());
                            holder.aftetimeText.setText(model.getAftertime());

                            holder.delete.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    deleteRef = FirebaseDatabase.getInstance().getReference().child(TagID + "Info")
                                            .child(model.getDate() + "," + model.getTime());

                                    deleteRef.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
//                                                Toast.makeText(getActivity(), position + 1 + " 번 하차내역 삭제", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });

                            holder.start.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    MainActivity mainActivity = new MainActivity();
                                    boolean isRunning = mainActivity.checkRunning();
                                    boolean[] setting = mainActivity.checkSetting();

                                    if (!isRunning) {
                                        try {
                                            startService(setting[0], setting[1], setting[2], setting[3], model.getDate()+" "+model.getTime());
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }

                                    } else
                                        Toast.makeText(getActivity(), "시간 갱신 희망 시, 기존의 알림을 제거해주세요.\n알림 메시지 옆 버튼을 통하여 알림을 종료할 수 있습니다.", Toast.LENGTH_LONG).show();
                                }

                            });
                        }

                        @NonNull
                        @Override
                        public NoticeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recent_listitem, parent, false);
                            return new NoticeViewHolder(view);
                        }
                    };

            recyclerView.setHasFixedSize(true);
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }

    public void startService(boolean onoffb, boolean alarm5b, boolean alarm10b, boolean alarm15b, String start) throws ParseException {

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentTime = new SimpleDateFormat("HH", Locale.KOREA);
        Date start_time = currentTime.parse(start);

        assert start_time != null;
        String saveCurrentTime = currentTime.format(calForDate.getTime());
        calForDate.setTime(start_time);

        if (saveCurrentTime.equals("21") || saveCurrentTime.equals("22")
                || saveCurrentTime.equals("23") || saveCurrentTime.equals("00")
                || saveCurrentTime.equals("01") || saveCurrentTime.equals("02")
                || saveCurrentTime.equals("03") || saveCurrentTime.equals("04")
                || saveCurrentTime.equals("05") || saveCurrentTime.equals("06")) {

            START_TIME_IN_MILLIS = gap(start, 60);
            // 60000 = 1분

        } else START_TIME_IN_MILLIS = gap(start, 30);

        if (START_TIME_IN_MILLIS < 0) {
            Toast.makeText(getContext(), "환승 가능 시간이 지났습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        timer = new CountDownTimer(START_TIME_IN_MILLIS, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                mTimeLeftInMillis = millisUntilFinished;

                time = String.format(Locale.KOREA, "%02d", (mTimeLeftInMillis / 1000) / 60) + ":" +
                        String.format(Locale.KOREA, "%02d", (mTimeLeftInMillis / 1000) % 60);

                if (onoffb) {

                    if (alarm5b) {
                        if (time.equals("05:00")) {
                            vibrator.vibrate(2000);
                        }
                    }
                    if (alarm10b) {
                        if (time.equals("10:00")) {
                            vibrator.vibrate(2000);
                        }
                    }
                    if (alarm15b) {
                        if (time.equals("15:00")) {
                            vibrator.vibrate(2000);
                        }
                    }

                }

                Intent serviceIntent = new Intent(activity, ForegroundService.class);
                serviceIntent.putExtra("time", time);
                serviceIntent.putExtra("TagID", TagID);
                ContextCompat.startForegroundService(activity, serviceIntent);
            }

            @Override
            public void onFinish() {

                timer.cancel();
                ((MainActivity) MainActivity.mContext).save(false);
                vibrator.vibrate(2500);
                stopService();

            }
        };

        timer.start();
        ((MainActivity) MainActivity.mContext).save(true);
    }

    private void stopService() {

        Intent serviceIntent = new Intent(activity, ForegroundService.class);
        Objects.requireNonNull(getActivity()).stopService(serviceIntent);

    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = (MainActivity) getActivity();
        mContext = this;
    }

    public void refresh(String tag) {

        assert getFragmentManager() != null;
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        transaction.detach(this).attach(this).commit();

        this.tag = tag;

    }

    private long gap(String time, int add_time) throws ParseException {
        SimpleDateFormat f = new SimpleDateFormat("yyyy, MMM dd HH:mm:ss a", Locale.KOREA);
        Date start_time = f.parse(time);
        Calendar cal = Calendar.getInstance();
        assert start_time != null;
        cal.setTime(start_time);
        cal.add(Calendar.MINUTE, add_time);
        String transfer = f.format(cal.getTime());
        Date transfer_time = f.parse(transfer);

        Date date = new Date();
        String cur = f.format(date);
        Date current_time = f.parse(cur);

        assert transfer_time != null;
        assert current_time != null;
        return transfer_time.getTime() - current_time.getTime();
    }

}