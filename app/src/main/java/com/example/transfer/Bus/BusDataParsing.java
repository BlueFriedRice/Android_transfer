package com.example.transfer.Bus;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.transfer.R;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

public class BusDataParsing extends AppCompatActivity {

    final String TAG = ".BusDataParsing";

    private String requestUrl;
    ArrayList<BusDataItem> list = null;
    BusDataItem bus = null;
    RecyclerView recyclerView;
    private TextView toolbar_title;
    private String busId, stationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busdataparsing);

        busId = getIntent().getStringExtra("busId");
        stationId  = getIntent().getStringExtra("stationId");

        toolbar_title = (TextView)findViewById(R.id.toolbar_title);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        toolbar_title.setText(stationId);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipe);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                Intent intent = getIntent();

                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);
                swipeRefreshLayout.setRefreshing(false);
            }

        });


//        AsyncTask
        MyAsyncTask myAsyncTask = new MyAsyncTask();
        myAsyncTask.execute();
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:{
                finish();
                break;

            }
            case  R.id.action_refresh:{

                Intent intent = getIntent();
                finish();
                startActivity(intent);
                overridePendingTransition(0, 0);

                break;
            }
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.refresh_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    public class MyAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {

            requestUrl = "requestUrl" + busId;
            try {
                boolean b_stnNm = false;
                boolean b_rtNm =false;
                boolean b_arrmsg1 = false;
                boolean b_arrmsg2 = false;

                URL url = new URL(requestUrl);
                InputStream is = url.openStream();
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser parser = factory.newPullParser();
                parser.setInput(new InputStreamReader(is, "UTF-8"));

                String tag;
                int eventType = parser.getEventType();

                while(eventType != XmlPullParser.END_DOCUMENT){
                    switch (eventType){
                        case XmlPullParser.START_DOCUMENT:
                            list = new ArrayList<BusDataItem>();
                            break;
                        case XmlPullParser.END_DOCUMENT:
                            break;
                        case XmlPullParser.END_TAG:
                            if(parser.getName().equals("itemList") && bus != null) {
                                list.add(bus);
                            }
                            break;
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("itemList")){
                                bus = new BusDataItem();
                            }
                            if (parser.getName().equals("adirection")) b_stnNm = true;
                            if (parser.getName().equals("rtNm")) b_rtNm = true;
                            if (parser.getName().equals("arrmsg1")) b_arrmsg1 = true;
                            if (parser.getName().equals("arrmsg2")) b_arrmsg2 = true;
                            break;
                        case XmlPullParser.TEXT:
                            if(b_stnNm){
                                bus.setStnNm(parser.getText());
                                b_stnNm = false;
                            } else if(b_rtNm) {
                                bus.setRtNm(parser.getText());
                                b_rtNm = false;
                            } else if (b_arrmsg1) {
                                bus.setArrmsg1(parser.getText());
                                b_arrmsg1 = false;
                            } else if(b_arrmsg2) {
                                bus.setArrmsg2(parser.getText());
                                b_arrmsg2 = false;
                            }
                            break;
                    }
                    eventType = parser.next();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //어답터 연결
            BusAdapter adapter = new BusAdapter(getApplicationContext(), list);
            recyclerView.setAdapter(adapter);
        }
    }
}
