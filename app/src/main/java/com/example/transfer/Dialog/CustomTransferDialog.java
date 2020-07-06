package com.example.transfer.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.transfer.R;

import org.w3c.dom.Text;

public class CustomTransferDialog extends Dialog {

    private ImageView Icon;
    private int mIcon;
    private Button okButton;
    private String mInfoText;
    private TextView InfoText;

    private View.OnClickListener InfookClickListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 다이얼로그 외부 화면 흐리게 표현
        WindowManager.LayoutParams lpWindow = new WindowManager.LayoutParams();
        lpWindow.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        lpWindow.dimAmount = 0.8f;
        getWindow().setAttributes(lpWindow);

        setContentView(R.layout.infodialog_custom);

        InfoText = (TextView) findViewById(R.id.InfoText);

        Icon = (ImageView) findViewById(R.id.AlertIcon);

        okButton = (Button) findViewById(R.id.InfookBtn);

        // 제목과 내용을 생성자에서 셋팅한다.
        Icon.setImageResource(mIcon);

        InfoText.setText(mInfoText);

        // 클릭 이벤트 셋팅
        if (InfookClickListener != null) {
            okButton.setOnClickListener(InfookClickListener);
        }

    }

    // 클릭버튼이 하나일때 생성자 함수로 클릭이벤트를 받는다.
    public CustomTransferDialog(Context context,
                                int mIcon,
                                String mInfoText,
                                View.OnClickListener InfosingleListener)
    {

        super(context, android.R.style.Theme_Translucent_NoTitleBar);

        this.mIcon = mIcon;

        this.mInfoText = mInfoText;

        this.InfookClickListener = InfosingleListener;
    }

}