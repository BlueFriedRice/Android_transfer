package com.example.transfer.Card;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transfer.MainActivity;
import com.example.transfer.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CardRegistration extends AppCompatActivity {

    private String TagIdStr;
    private EditText InputName, InputPhoneNumber, InputNickName;
    private TextView nfcID;
    private ProgressDialog loadingBar;
    private Button CreateCardButton;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_registration);

        TagIdStr = getIntent().getStringExtra("TagIdStr");

        nfcID = (TextView) findViewById(R.id.nfcID);
        nfcID.setText("NFC ID : " + TagIdStr);

        InputName = (EditText) findViewById(R.id.register_username_input);
        InputPhoneNumber = (EditText) findViewById(R.id.register_phone_number_input);
        InputNickName = (EditText) findViewById(R.id.register_cardnickname_input);

        back = (ImageButton) findViewById(R.id.backBtn);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CardRegistration.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        });

        CreateCardButton = (Button) findViewById(R.id.register_btn);

        loadingBar = new ProgressDialog(this);

        CreateCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateAccount();
            }
        });
    }

        private void CreateAccount()
        {
            String tagID = TagIdStr;
            String name = InputName.getText().toString();
            String phone = InputPhoneNumber.getText().toString();
            String nickname = InputNickName.getText().toString();

            if(TextUtils.isEmpty(name))
            {
                Toast.makeText(this, "이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(phone))
            {
                Toast.makeText(this, "핸드폰 번호를 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
            else if(TextUtils.isEmpty(nickname))
            {
                Toast.makeText(this, "카드별명을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
            else
            {
                loadingBar.setTitle("NFC카드 등록");
                loadingBar.setMessage("잠시만 기다려주세요. NFC카드 정보를 등록중입니다.");
                loadingBar.setCanceledOnTouchOutside(false);
                loadingBar.show();

                ValidateTagId(tagID, name, phone, nickname);
            }

        }

        private void ValidateTagId (String tagID, String name, String phone, String nickname)
        {

            final DatabaseReference RootRef;
            RootRef = FirebaseDatabase.getInstance().getReference();

            RootRef.addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (!(dataSnapshot.child("Transfer Users").child(TagIdStr).exists()))
                    {
                        HashMap<String, Object> userdataMap = new HashMap<>();
                        userdataMap.put("tagID",tagID);
                        userdataMap.put("name",name);
                        userdataMap.put("phone",phone);
                        userdataMap.put("nickname",nickname);

                        RootRef.child("Transfer Users").child(TagIdStr).updateChildren(userdataMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>()
                                {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task)
                                    {
                                        if (task.isSuccessful())
                                        {
                                            Toast.makeText(CardRegistration.this, "정보 저장을 완료했습니다.", Toast.LENGTH_SHORT).show();
                                            Toast.makeText(CardRegistration.this, "카드를 다시 태그해주세요.", Toast.LENGTH_SHORT).show();
                                            loadingBar.dismiss();

                                            Intent intent = new Intent(CardRegistration.this, MainActivity.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                            startActivity(intent);
                                        }
                                        else
                                        {
                                            loadingBar.dismiss();
                                            Toast.makeText(CardRegistration.this, "데이터 연결상태를 확인해주세요.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                    else
                    {
                        Toast.makeText(CardRegistration.this, TagIdStr+"\n해당 ID가 존재합니다", Toast.LENGTH_SHORT).show();
                        loadingBar.dismiss();
                        Toast.makeText(CardRegistration.this, "다른 카드를 등록해주세요.", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(CardRegistration.this,MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });
        }
}
