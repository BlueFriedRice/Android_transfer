package com.example.transfer.Card;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.transfer.MainActivity;
import com.example.transfer.Prevalent.Prevalent;
import com.example.transfer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.HashMap;

public class CardInfoUpdate extends AppCompatActivity implements View.OnClickListener {

    private EditText NameEditText, PhoneEditText, NickNameEditText;
    private Button updateBtn;
    private TextView nfcID;
    private String TagIdStr;
    private ImageButton back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card_info_update);

        TagIdStr = getIntent().getStringExtra("TagIdStr");

        nfcID = (TextView)findViewById(R.id.nfcID);
        nfcID.setText("NFC ID : " + TagIdStr);

        NameEditText = (EditText)findViewById(R.id.update_username_input);
        PhoneEditText = (EditText)findViewById(R.id.update_phone_number_input);
        NickNameEditText = (EditText)findViewById(R.id.update_cardnickname_input);

        back = (ImageButton) findViewById(R.id.backBtn);
        updateBtn = (Button) findViewById(R.id.update_btn);

        back.setOnClickListener(this);
        updateBtn.setOnClickListener(this);

        userInfoDisplay(NameEditText, PhoneEditText, NickNameEditText);
    }

    private void userInfoDisplay(final EditText NameEditText, final EditText PhoneEditText, final EditText NickNameEditText)
    {
        DatabaseReference UsersRef = FirebaseDatabase.getInstance().getReference().child("Transfer Users").child(Prevalent.currentOnlineUser.getTagID());

        UsersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.exists())
                {
                    if (dataSnapshot.child("tagID").exists())
                    {
                        String name = dataSnapshot.child("name").getValue().toString();
                        String phone = dataSnapshot.child("phone").getValue().toString();
                        String nickname = dataSnapshot.child("nickname").getValue().toString();

                        NameEditText.setText(name);
                        PhoneEditText.setText(phone);
                        NickNameEditText.setText(nickname);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateOnlyUserInfo()
    {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Transfer Users");

        HashMap<String, Object> userMap = new HashMap<>();
        userMap. put("name", NameEditText.getText().toString());
        userMap. put("phone", PhoneEditText.getText().toString());
        userMap. put("nickname", NickNameEditText.getText().toString());
        ref.child(Prevalent.currentOnlineUser.getTagID()).updateChildren(userMap);

        startActivity(new Intent(CardInfoUpdate.this, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        Toast.makeText(CardInfoUpdate.this, "카드정보 수정완료", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.update_btn:
                updateOnlyUserInfo();
                break;
            case R.id.backBtn:
                Intent intent = new Intent(CardInfoUpdate.this,MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                break;
        }
    }
}
