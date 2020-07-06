package com.example.transfer.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.transfer.R;

public class CustomCallDialog extends Dialog implements View.OnClickListener{

    private ImageView Icon;
    private int mIcon;

    private TextView tmoney, kb, samsung, ibk, bc, we, shinhan;
    private String mTmoney, mKb, mSamsung, mIbk, mBc, mWe, mShinhan;

    private TextView Tnumber, Knumber, Snumber, Inumber, Bnumber, Wnumber, Shnumber;

    private Button okButton;

    private View.OnClickListener CallokClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.callcenter_custom);

        Icon = (ImageView) findViewById(R.id.AlertIcon);

        tmoney = (TextView) findViewById(R.id.tmoney);
        kb = (TextView) findViewById(R.id.kb);
        samsung = (TextView) findViewById(R.id.samsung);
        ibk = (TextView) findViewById(R.id.ibk);
        bc = (TextView) findViewById(R.id.bc);
        we = (TextView) findViewById(R.id.we);
        shinhan = (TextView) findViewById(R.id.shinhan);

        Tnumber = (TextView) findViewById(R.id.Tnumber);
        Tnumber.setPaintFlags(Tnumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Knumber = (TextView) findViewById(R.id.Knumber);
        Knumber.setPaintFlags(Knumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Snumber = (TextView) findViewById(R.id.Snumber);
        Snumber.setPaintFlags(Snumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Inumber = (TextView) findViewById(R.id.Inumber);
        Inumber.setPaintFlags(Inumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Bnumber = (TextView) findViewById(R.id.Bnumber);
        Bnumber.setPaintFlags(Bnumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Wnumber = (TextView) findViewById(R.id.Wnumber);
        Wnumber.setPaintFlags(Wnumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        Shnumber = (TextView) findViewById(R.id.Shnumber);
        Shnumber.setPaintFlags(Shnumber.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        okButton = (Button) findViewById(R.id.CallokBtn);

        // 제목과 내용을 생성자에서 셋팅한다.
        Icon.setImageResource(mIcon);

        tmoney.setText(mTmoney);
        kb.setText(mKb);
        samsung.setText(mSamsung);
        ibk.setText(mIbk);
        bc.setText(mBc);
        we.setText(mWe);
        shinhan.setText(mShinhan);

        // 클릭 이벤트 셋팅
        if (CallokClickListener != null) {
            okButton.setOnClickListener(CallokClickListener);
        }

        Tnumber.setOnClickListener(this);
        Knumber.setOnClickListener(this);
        Snumber.setOnClickListener(this);
        Inumber.setOnClickListener(this);
        Bnumber.setOnClickListener(this);
        Wnumber.setOnClickListener(this);
        Shnumber.setOnClickListener(this);

//        Tnumber.setOnClickListener(CallListener);

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CustomCallDialog(Context context,

                            int mIcon,
                            String mTmoney,
                            String mKb,
                            String mSamsung,
                            String mIbk,
                            String mBc,
                            String mWe,
                            String mShinhan,

                            View.OnClickListener CallsingleListener) {

        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.mIcon = mIcon;

        this.mTmoney = mTmoney;

        this.mKb= mKb;

        this.mSamsung= mSamsung;

        this.mIbk= mIbk;

        this.mBc= mBc;

        this.mWe= mWe;

        this.mShinhan= mShinhan;

        this.CallokClickListener = CallsingleListener;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {

            case R.id.Tnumber:
                Linkify.addLinks(Tnumber,Linkify.PHONE_NUMBERS); // 전화연결
                break;

            case R.id.Knumber:
                Linkify.addLinks(Knumber,Linkify.PHONE_NUMBERS); // 전화연결
                break;

            case R.id.Snumber:
                Linkify.addLinks(Snumber,Linkify.PHONE_NUMBERS); // 전화연결
                break;

            case R.id.Inumber:
                Linkify.addLinks(Inumber,Linkify.PHONE_NUMBERS); // 전화연결
                break;

            case R.id.Bnumber:
                Linkify.addLinks(Bnumber,Linkify.PHONE_NUMBERS); // 전화연결
                break;

            case R.id.Wnumber:
                Linkify.addLinks(Wnumber,Linkify.PHONE_NUMBERS); // 전화연결
                break;

            case R.id.Shnumber:
                Linkify.addLinks(Shnumber,Linkify.PHONE_NUMBERS); // 전화연결
                break;

        }
    }

}