package com.example.transfer.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import com.example.transfer.Dialog.CustomCallDialog;
import com.example.transfer.Dialog.CustomTransferDialog;
import com.example.transfer.R;

public class infoFragment extends Fragment implements View.OnClickListener {

    private CardView callCenter, seoulInfo, transferInfo;
    private CustomCallDialog call_dialog;
    private CustomTransferDialog trans_dialog;
    private ImageButton tmoney, cashbee, railplus, hanway;

    public infoFragment()
    {
    }
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View myView = inflater.inflate(R.layout.fragment_info, container, false);

        callCenter = (CardView) myView.findViewById(R.id.callCenter);
        seoulInfo = (CardView) myView.findViewById(R.id.seoulInfo);
        transferInfo = (CardView) myView.findViewById(R.id.transferInfo);

        tmoney = (ImageButton) myView.findViewById(R.id.tmoney);
        cashbee = (ImageButton) myView.findViewById(R.id.cashb);
        railplus = (ImageButton) myView.findViewById(R.id.railplus);
        hanway = (ImageButton) myView.findViewById(R.id.hanway);

        tmoney.setOnClickListener(this);
        cashbee.setOnClickListener(this);
        railplus.setOnClickListener(this);
        hanway.setOnClickListener(this);

        seoulInfo.setOnClickListener(this);

        transferInfo.setOnClickListener(this);
        callCenter.setOnClickListener(this);

        return myView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.callCenter:
                CallDialog();
                break;
            case R.id.transferInfo:
                InfoDialog();
                break;
            case R.id.tmoney:
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.t-money.co.kr/ncs/pct/cardrgt/ReadDscdRgtDvs.dev"));
//                intent.setPackage("com.android.chrome");   // 브라우저가 여러개 인 경우 콕 찍어서 크롬을 지정할 경우
                startActivity(intent);
                break;
            case R.id.cashb:
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.cashbee.co.kr/cb/register/kidDcRegView.do"));
                startActivity(intent1);
                break;
            case R.id.railplus:
                Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://railplus.korail.com/index.do?menuId=0000000014"));
                startActivity(intent2);
                break;
            case R.id.hanway:
                Intent intent3 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.hanpay.net/portal2nd/myhanpay/displayCardRegist.do?p=1.5&type=1"));
                startActivity(intent3);
                break;
            case R.id.seoulInfo:
                Intent intent4 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://tearstop.seoul.go.kr/mulga/info/charge01.jsp"));
                startActivity(intent4);
                break;

        }
    }

    public void InfoDialog() {
        trans_dialog = new CustomTransferDialog(getActivity(),
                R.drawable.info_dialog,
                "◆ 대상\n" +
                        "  - 서울버스 (간선, 지선, 순환, 광역, 마을)\n" +
                        "  - 인천버스 (간선, 지선, 좌석, 광역)\n" +
                        "  - 경기버스 (일반, 좌석, 직행좌석, 마을)\n" +
                        "  - 수도권전철\n" +
                        "\n" +
                        "◆ 환승 통행시 요금\n" +
                        "  - 기본 구간 내 기본요금 (일반, 마을버스는 10km, 좌석버스는 30km)\n" +
                        "  - 초과 시 매 5km당 100원의 추가 요금 발생 (청소년 80원, 어린이 50원)\n" +
                        "  - 40km 초과 시 거리와 상관없이 100원만 추가되고 더 이상의 추가 요금은 없습니다.\n" +
                        "\n" +
                        "◆ 버스환승제도 이용방법\n" +
                        "  - 승차, 하차 시 단말기에 교통카드를 접촉해야 합니다.\n" +
                        "    ㆍ만약 환승할인을 받는 환승통행자일 때 하차 시 교통카드를 찍지 않고 내리면\n" +
                        "      다음 대중교통 승차 시 기본요금과 추가요금이 부과됩니다.\n" +
                        "\n" +
                        "  - 환승 유효시간\n" +
                        "    ㆍ하차 후 30분 이내 (금일 21시~ 익일 07시는 1시간)\n" +
                        "\n" +
                        "  - 횟수\n" +
                        "    ㆍ4회 환승으로 5개 수단 탑승까지 환승할인 혜택 적용, 환승 5회부터는(탑승 6회차)\n" +
                        "      비용이 발생합니다.\n" +
                        "\n" +
                        "  - 동일 노선 환승 시 할인혜택을 받을 수 없습니다.\n" +
                        "    ㆍ같은 방향의 똑같은 차량번호를 가진 버스를 탑승할 경우 환승이 되지 않고, \n" +
                        "      요금이 부가됩니다.\n" +
                        "\n" +
                        "  - 선불교통카드의 경우 최소 250원의 잔액이 남아있어야 환승이 가능합니다.\n" +
                        "   ㆍ기본 구간 초과 시 최소 250원 이상의 금액이 있어야 가능하기 때문입니다.\n" +
                        "\n" +
                        "  - 다인승 환승이 가능합니다.\n" +
                        "    ㆍ승ㆍ하차 인원이 동일할 경우 환승 시 할인혜택을 받을 수 있습니다.",
                InfookListener
                );

        trans_dialog.setCancelable(true);
        trans_dialog.getWindow().setGravity(Gravity.CENTER);
        trans_dialog.show();

    }

    public void CallDialog(){
        call_dialog = new CustomCallDialog(getActivity(),
                R.drawable.call,
                "티머니", "KB국민은행",
                "삼성카드", "IBK기업은행",
                "BC카드", "우리은행",
                "신한카드",
                CallokListener); // 확인 버튼 이벤트

        //요청 이 다이어로그를 종료할 수 있게 지정함
        call_dialog.setCancelable(true);
        call_dialog.getWindow().setGravity(Gravity.CENTER);
        call_dialog.show();

    }

    //다이얼로그 클릭이벤트
    private View.OnClickListener InfookListener = new View.OnClickListener() {
        public void onClick(View v) {
//            Toast.makeText(getActivity(), "버튼을 클릭하였습니다.", Toast.LENGTH_SHORT).show();
            trans_dialog.dismiss();
        }
    };

    private View.OnClickListener CallokListener = new View.OnClickListener() {
        public void onClick(View v) {
//            Toast.makeText(getActivity(), "버튼을 클릭하였습니다.", Toast.LENGTH_SHORT).show();
            call_dialog.dismiss();
        }
    };

}