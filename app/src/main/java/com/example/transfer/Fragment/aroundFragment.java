package com.example.transfer.Fragment;


import android.Manifest;
import android.annotation.TargetApi;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;


import com.example.transfer.Adapter.AdapterSpinner;
import com.example.transfer.Bus.BusDataParsing;
import com.example.transfer.Bus.BusStation;
import com.example.transfer.R;
import com.example.transfer.Subway.SubwayStation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import noman.googleplaces.Place;
import noman.googleplaces.PlacesException;
import noman.googleplaces.PlacesListener;

import static android.content.Context.LOCATION_SERVICE;

public class aroundFragment extends Fragment implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, PlacesListener, GoogleMap.OnMarkerClickListener {
    Context context;


    Document doc;
    ArrayList<String> id_list = new ArrayList<>();
    TextView textView;
    String sid;
    public static String subname = "";

    private GoogleApiClient mGoogleApiClient = null;
    private GoogleMap mGoogleMap = null;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 2002;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초

    private Fragment mContext;
    boolean askPermissionOnceAgain = false;
    boolean mRequestingLocationUpdates = false;
    Location mCurrentLocatiion;
    boolean mMoveMapByUser = true;
    boolean mMoveMapByAPI = true;
    LatLng currentPosition;
    private ArrayList<BusStation> busStation = new ArrayList<BusStation>();
    private ArrayList<SubwayStation> subStation = new ArrayList<SubwayStation>();

    public Spinner distancespn;
    AdapterSpinner adapterSpinner1;
    int distancenum;
    private CardView Info;
    private TextView Num;
    private TextView Name;
    private TextView text2;
    private RadioButton bus;
    private RadioButton subway;
    private ImageView image, downim;
    private LinearLayout linearLayout;
    private ImageView imageView, subimage;

    LocationRequest locationRequest = new LocationRequest().setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY).setInterval(UPDATE_INTERVAL_MS).setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);
    List<Marker> previous_marker = null;

    public aroundFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        View myView = inflater.inflate(R.layout.fragment_around, container, false);

        List<String> data = new ArrayList<>();
        data.add("300m");
        data.add("500m");
        data.add("1km");
        data.add("2km");

        //linearLayout = (LinearLayout)myView.findViewById(R.id.linear);

        distancespn = (Spinner) myView.findViewById(R.id.distancespn);
        adapterSpinner1 = new AdapterSpinner(this, data);
        distancespn.setAdapter(adapterSpinner1);

        text2 = myView.findViewById(R.id.text2);
        Info = myView.findViewById(R.id.info);
        Num = myView.findViewById(R.id.num);
        Name = myView.findViewById(R.id.name);
        bus = myView.findViewById(R.id.bus);
        subway = myView.findViewById(R.id.subway);
        image = myView.findViewById(R.id.image);
        downim = myView.findViewById(R.id.downim);

        Info.setOnClickListener(this);
        bus.setOnClickListener(this);
        subway.setOnClickListener(this);

        previous_marker = new ArrayList<Marker>();

        Log.d(TAG, "onCreate");

        mContext = this;

        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        SupportMapFragment mMapFragment = SupportMapFragment.newInstance();
        FragmentTransaction fragmentTransaction =
                getChildFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.map2, mMapFragment);
        fragmentTransaction.commit();
        mMapFragment.getMapAsync(this);
        context = getActivity();
        linearLayout = (LinearLayout) myView.findViewById(R.id.linear);
        // imageView = new ImageView(getActivity());
        return myView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "onResume : call startLocationUpdates");
            if (!mRequestingLocationUpdates) startLocationUpdates();
        }
        //앱 정보에서 퍼미션을 허가했는지를 다시 검사해봐야 한다.
        if (askPermissionOnceAgain) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                askPermissionOnceAgain = false;
                checkPermissions();
            }
        }
    }

    private void startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {
            if (ActivityCompat.checkSelfPermission(Objects.requireNonNull(getActivity()), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }
            Log.d(TAG, "startLocationUpdates : call FusedLocationApi.requestLocationUpdates");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, locationRequest, this::onLocationChanged);
            mRequestingLocationUpdates = true;
            mGoogleMap.setMyLocationEnabled(true);
        }
    }

    private void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates : LocationServices.FusedLocationApi.removeLocationUpdates");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this::onLocationChanged);
        mRequestingLocationUpdates = false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");
        mGoogleMap = googleMap;
        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();
        //mGoogleMap.getUiSettings().setZoomControlsEnabled(false);
        mGoogleMap.getUiSettings().setMyLocationButtonEnabled(true);
        mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(13));
        mGoogleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {

            @Override
            public boolean onMyLocationButtonClick() {
                Log.d(TAG, "onMyLocationButtonClick : 위치에 따른 카메라 이동 활성화");
                mMoveMapByAPI = true;
                return true;
            }
        });
        mGoogleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng latLng) {
                Log.d(TAG, "onMapClick :");
            }
        });
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {

            @Override
            public void onCameraMoveStarted(int i) {
                if (mMoveMapByUser && mRequestingLocationUpdates) {
                    Log.d(TAG, "onCameraMove : 위치에 따른 카메라 이동 비활성화");
                    mMoveMapByAPI = false;
                }
                mMoveMapByUser = true;
            }
        });
        mGoogleMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
            }
        });
    }

    @Override
    public void onLocationChanged(Location location) {
        currentPosition = new LatLng(location.getLatitude(), location.getLongitude());
        Log.d(TAG, "onLocationChanged : ");
        if (!Objects.requireNonNull(getActivity()).isFinishing()) {
            String markerTitle = getCurrentAddress(currentPosition);
            String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
                    + " 경도:" + String.valueOf(location.getLongitude());
            //현재 위치에 마커 생성하고 이동
            setCurrentLocation(location, markerTitle, markerSnippet);
            mCurrentLocatiion = location;
        }
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Override
    public void onStart() {
        if (mGoogleApiClient != null && !mGoogleApiClient.isConnected()) {
            Log.d(TAG, "onStart: mGoogleApiClient connect");
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        if (mRequestingLocationUpdates) {
            Log.d(TAG, "onStop : call stopLocationUpdates");
            stopLocationUpdates();
        }
        if (mGoogleApiClient.isConnected()) {
            Log.d(TAG, "onStop : mGoogleApiClient disconnect");
            mGoogleApiClient.disconnect();
        }
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (!mRequestingLocationUpdates) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int hasFineLocationPermission = ContextCompat.checkSelfPermission(Objects.requireNonNull(getActivity()),
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (hasFineLocationPermission == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(),
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    Log.d(TAG, "onConnected : 퍼미션 가지고 있음");
                    Log.d(TAG, "onConnected : call startLocationUpdates");
                    startLocationUpdates();
                    mGoogleMap.setMyLocationEnabled(true);
                }
            } else {
                Log.d(TAG, "onConnected : call startLocationUpdates");
                startLocationUpdates();
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed");
        setDefaultLocation();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.d(TAG, "onConnectionSuspended");
        if (cause == CAUSE_NETWORK_LOST)
            Log.e(TAG, "onConnectionSuspended(): Google Play services " +
                    "connection lost.  Cause: network lost.");
        else if (cause == CAUSE_SERVICE_DISCONNECTED)
            Log.e(TAG, "onConnectionSuspended():  Google Play services " +
                    "connection lost.  Cause: service disconnected");
    }

    private String getCurrentAddress(LatLng latlng) {
        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());
        List<Address> addresses;
        try {
            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(getActivity(), "데이터 연결상태를 확인해주세요.", Toast.LENGTH_LONG).show();
            return "데이터 연결상태를 확인해주세요.";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(getActivity(), "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }
        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(getActivity(), "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        } else {
            Address address = addresses.get(0);
            return address.getAddressLine(0).toString();
        }
    }

    private boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }

    private void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        mMoveMapByUser = false;
        if (currentMarker != null) currentMarker.remove();
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(currentLatLng);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        //구글맵의 디폴트 현재 위치는 파란색 동그라미로 표시
        //마커를 원하는 이미지로 변경하여 현재 위치 표
        //
        // 시하도록 수정 fix - 2017. 11.27
//        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.gpspoint));
//        currentMarker = mGoogleMap.addMarker(markerOptions);
        if (mMoveMapByAPI) {
            Log.d(TAG, "setCurrentLocation :  mGoogleMap moveCamera "
                    + location.getLatitude() + " " + location.getLongitude());
            // CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentLatLng, 15);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mGoogleMap.moveCamera(cameraUpdate);
        }
    }

    public void setDefaultLocation() {
        mMoveMapByUser = false;
        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요";
        if (currentMarker != null) currentMarker.remove();
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(DEFAULT_LOCATION);
        markerOptions.title(markerTitle);
        markerOptions.snippet(markerSnippet);
        markerOptions.draggable(true);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        currentMarker = mGoogleMap.addMarker(markerOptions);
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        mGoogleMap.moveCamera(cameraUpdate);
    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    @TargetApi(Build.VERSION_CODES.M)
    private void checkPermissions() {
        boolean fineLocationRationale = ActivityCompat
                .shouldShowRequestPermissionRationale(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION);
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);

        if (hasFineLocationPermission == PackageManager
                .PERMISSION_DENIED && fineLocationRationale)
            showDialogForPermission("앱을 실행하려면 퍼미션을 허가하셔야합니다.");
        else if (hasFineLocationPermission
                == PackageManager.PERMISSION_DENIED && !fineLocationRationale) {
            showDialogForPermissionSetting("퍼미션 거부 + Don't ask again(다시 묻지 않음) " +
                    "체크 박스를 설정한 경우로 설정에서 퍼미션 허가해야합니다.");
        } else if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
            if (mGoogleApiClient.isConnected() == false) {
                Log.d(TAG, "checkPermissions : 퍼미션 가지고 있음");
                mGoogleApiClient.connect();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int permsRequestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (permsRequestCode
                == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION && grantResults.length > 0) {
            boolean permissionAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (permissionAccepted) {
                if (mGoogleApiClient.isConnected() == false) {
                    Log.d(TAG, "onRequestPermissionsResult : mGoogleApiClient connect");
                    mGoogleApiClient.connect();
                }
            } else {
                checkPermissions();
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void showDialogForPermission(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(false);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });
        builder.create().show();
    }

    private void showDialogForPermissionSetting(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("알림");
        builder.setMessage(msg);
        builder.setCancelable(true);
        builder.setPositiveButton("예", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                askPermissionOnceAgain = true;
                Intent myAppSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + getActivity().getPackageName()));
                myAppSettings.addCategory(Intent.CATEGORY_DEFAULT);
                myAppSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getActivity().startActivity(myAppSettings);
            }
        });
        builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                getActivity().finish();
            }
        });
        builder.create().show();
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : 퍼미션 가지고 있음");
                        if (mGoogleApiClient.isConnected() == false) {
                            Log.d(TAG, "onActivityResult : mGoogleApiClient connect ");
                            mGoogleApiClient.connect();
                        }
                        return;
                    }
                }
                break;
        }
    }

    @Override
    public void onPlacesFailure(PlacesException e) {
    }

    @Override
    public void onPlacesStart() {
    }

    @Override
    public void onPlacesSuccess(final List<Place> places) {

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                for (noman.googleplaces.Place place : places) {
                    LatLng latLng = new LatLng(place.getLatitude(), place.getLongitude());
                    if (!Objects.requireNonNull(getActivity()).isFinishing()) {
                        String markerSnippet = getCurrentAddress(latLng);
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng);
                        markerOptions.title(place.getName());
                        markerOptions.snippet(markerSnippet);
                        Marker item = mGoogleMap.addMarker(markerOptions);
                        previous_marker.add(item);
                    }
                }
                //중복 마커 제거
                HashSet<Marker> hashSet = new HashSet<Marker>();
                hashSet.addAll(previous_marker);
                previous_marker.clear();
                previous_marker.addAll(hashSet);
            }

        });
    }

    @Override
    public void onPlacesFinished() {
    }


    public BusStation addBusStation(String id, String name, double lng, double lat) {
        BusStation busStation = new BusStation();
        busStation.setId(id);
        busStation.setName(name);
        busStation.setLng(lng);
        busStation.setLat(lat);
        return busStation;
    }

    public SubwayStation addSubwayStation(String id1, String name1, double lng1, double lat1) {
        SubwayStation subStation = new SubwayStation();
        subStation.setId(id1);
        subStation.setName(name1);
        subStation.setLng(lng1);
        subStation.setLat(lat1);
        return subStation;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.info:
                if (bus.isChecked()) {
                    if (Name.getText().equals("버스정류장")) {
                        Toast.makeText(getActivity(), "버스정류장을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent(getContext(), BusDataParsing.class);
                        intent.putExtra("busId", Num.getText());
                        intent.putExtra("stationId", Name.getText());
                        startActivity(intent);
                    }
                } else if (subway.isChecked()) {
                    if (Name.getText().equals("지하철역")) {
                        Toast.makeText(getActivity(), "지하철역을 먼저 선택해주세요.", Toast.LENGTH_SHORT).show();
                    }
                }

                break;
            case R.id.bus:
                linearLayout.removeAllViews();
                Num.setVisibility(View.VISIBLE);
                //엑셀 읽기
                try {
                    InputStream s = getActivity().getAssets().open("station.xls");
                    Workbook wb = Workbook.getWorkbook(s);

                    if (wb != null) {
                        Sheet sheet = wb.getSheet(0);
                        if (sheet != null) {
                            int colTotal = sheet.getColumns();    // 전체 컬럼
                            int rowIndexStart = 1;                  // row 인덱스 시작
                            int rowTotal = sheet.getColumn(colTotal - 1).length;

                            for (int row = rowIndexStart; row < rowTotal; row++) {
                                String id = "", name = "";
                                double lng = 0, lat = 0;
                                for (int col = 0; col < colTotal; col++) {
                                    String contents = sheet.getCell(col, row).getContents();
                                    switch (col) {
                                        case 0: //id
                                            id = contents;
                                            break;
                                        case 1: //name
                                            name = contents;
                                            break;
                                        case 2: //longitude
                                            lng = Double.parseDouble(contents);
                                            break;
                                        case 3: //latitude
                                            lat = Double.parseDouble(contents);
                                            break;
                                    }
                                }
                                busStation.add(addBusStation(id, name, lng, lat));
                            }
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                }
                //
                if (bus.isChecked()) {
                    distancespn.setVisibility(View.VISIBLE);
                    downim.setVisibility(View.VISIBLE);
                    image.setImageResource(R.drawable.marker_bus);
                    Name.setText("버스정류장");
                    Num.setText("버스정류장 번호");
                    text2.setText("");
                    distancenum = 300;

                    busdatafind();

                    distancespn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            switch (i) {
                                case 0:
                                    mGoogleMap.clear();
                                    distancenum = 300;
                                    busdatafind();
                                    break;

                                case 1:
                                    mGoogleMap.clear();
                                    distancenum = 500;
                                    busdatafind();
                                    break;

                                case 2:
                                    mGoogleMap.clear();
                                    distancenum = 1000;
                                    busdatafind();
                                    break;

                                case 3:
                                    mGoogleMap.clear();
                                    distancenum = 2000;
                                    busdatafind();
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });

                } else {
                    mGoogleMap.clear();
                }
                break;

            case R.id.subway:
                Num.setVisibility(View.GONE);
                //엑셀 읽기
                try {
                    InputStream x = getActivity().getAssets().open("station1.xls");
                    Workbook wb = Workbook.getWorkbook(x);

                    if (wb != null) {
                        Sheet sheet = wb.getSheet(0);
                        if (sheet != null) {
                            int colTotal = sheet.getColumns();    // 전체 컬럼
                            int rowIndexStart = 1;                  // row 인덱스 시작
                            int rowTotal = sheet.getColumn(colTotal - 1).length;

                            for (int row = rowIndexStart; row < rowTotal; row++) {
                                String id = "", name = "";
                                double lng = 0, lat = 0;
                                for (int col = 0; col < colTotal; col++) {
                                    String contents = sheet.getCell(col, row).getContents();
                                    switch (col) {
                                        case 0: //id
                                            id = contents;
                                            break;
                                        case 1: //name
                                            name = contents;
                                            break;
                                        case 2: //longitude
                                            lng = Double.parseDouble(contents);
                                            break;
                                        case 3: //latitude
                                            lat = Double.parseDouble(contents);
                                            break;
                                    }
                                }
                                subStation.add(addSubwayStation(id, name, lng, lat));
                            }
                        }


                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (BiffException e) {
                    e.printStackTrace();
                }
                //

                if (subway.isChecked()) {
                    mGoogleMap.clear();
                    image.setImageResource(R.drawable.marker_sub);
                    distancespn.setVisibility(View.VISIBLE);
                    downim.setVisibility(View.VISIBLE);
                    Name.setText("지하철역");
                    text2.setText("");
                    Num.setText("");


                    distancenum = 300;
                    subdatafind();
                    distancespn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            switch (i) {
                                case 0:
                                    mGoogleMap.clear();
                                    distancenum = 300;
                                    subdatafind();
                                    break;

                                case 1:
                                    mGoogleMap.clear();
                                    distancenum = 500;
                                    subdatafind();
                                    break;

                                case 2:
                                    mGoogleMap.clear();
                                    distancenum = 1000;
                                    subdatafind();
                                    break;

                                case 3:
                                    mGoogleMap.clear();
                                    distancenum = 2000;
                                    subdatafind();
                                    break;
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {
                        }
                    });
                } else {
                    mGoogleMap.clear();
                }
                break;
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (bus.isChecked()) {
            BusStation busStation = (BusStation) marker.getTag();
            Num.setText(busStation.getId());
            Name.setText(busStation.getName());
            // linearLayout.removeAllViews();
        } else {
            SubwayStation subwayStation = (SubwayStation) marker.getTag();
            linearLayout.removeAllViews();
            // imageView= new ImageView(getActivity());
            //linearLayout.removeView(imageView);
            Name.setText(subwayStation.getName() + "역");
            subname = subwayStation.getName();
            text2.setText("(탑승을 희망하는 호선을 눌러주세요)");
            new GetXMLTask().execute();


        }

        return false;
    }

    public void busdatafind() {
        for (int i = 0; i < busStation.size(); i++) {
            double lat = busStation.get(i).getLat();
            double lng = busStation.get(i).getLng();
            // Location sample = new Location(LocationManager.GPS_PROVIDER);
            //sample.setLatitude(37.553836);
            // sample.setLongitude(126.969662);

            Location loc_station = new Location(LocationManager.GPS_PROVIDER);
            loc_station.setLatitude(lat);
            loc_station.setLongitude(lng);

            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_bus);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 160, 160, false);

//            double distance = (double) mCurrentLocatiion.distanceTo(loc_station);
            double distance = (double) mCurrentLocatiion.distanceTo(loc_station);
            if (distance < distancenum) {
                mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .position(new LatLng(lat, lng))).setTag(busStation.get(i));
            }
        }
    }

    public void subdatafind() {
        for (int i = 0; i < subStation.size(); i++) {
            double lat = subStation.get(i).getLat();
            double lng = subStation.get(i).getLng();
//             Location sample = new Location(LocationManager.GPS_PROVIDER);
//            sample.setLatitude(37.554648);
//            sample.setLongitude(126.972559);

            Location loc_station = new Location(LocationManager.GPS_PROVIDER);
            loc_station.setLatitude(lat);
            loc_station.setLongitude(lng);

            BitmapDrawable bitmapdraw = (BitmapDrawable) getResources().getDrawable(R.drawable.marker_sub);
            Bitmap b = bitmapdraw.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 160, 160, false);

            double distance = (double) mCurrentLocatiion.distanceTo(loc_station);
            if (distance < distancenum) {
                mGoogleMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                        .position(new LatLng(lat, lng))).setTag(subStation.get(i));
            }
        }
    }

    public class GetXMLTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            URL url;
            try {
                url = new URL("http://swopenapi.seoul.go.kr/api/subway/6b6e5053646c68773131336d6d5a6471/xml/realtimeStationArrival/0/15/" + subname);
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                doc = db.parse(new InputSource(url.openStream()));
                doc.getDocumentElement().normalize();

            } catch (Exception e) {
                return null;
            }
            return doc;
        }

        @Override
        protected void onPostExecute(Document doc) {
            List<DTO> DTO = new ArrayList<>();

            if (doc == null) {
                Log.e("error", "url error");
                return;
            }
            NodeList nodeList = doc.getElementsByTagName("row");


            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                Element fstElmnt = (Element) node;
                final NodeList subwayId = fstElmnt.getElementsByTagName("subwayId");
                final NodeList updnLine = fstElmnt.getElementsByTagName("updnLine");
                final NodeList trainLineNm = fstElmnt.getElementsByTagName("trainLineNm");
                final NodeList arvlMsg2 = fstElmnt.getElementsByTagName("arvlMsg2");
                final NodeList statnFid = fstElmnt.getElementsByTagName("statnFid");
                final NodeList statnTid = fstElmnt.getElementsByTagName("statnTid");

                // i번째 노드 DTO에 추가
                DTO dto = new DTO();
                dto.setSubwayId(subwayId.item(0).getChildNodes().item(0).getNodeValue());
                dto.setUpdnLine(updnLine.item(0).getChildNodes().item(0).getNodeValue());
                dto.setTrainLineNm(trainLineNm.item(0).getChildNodes().item(0).getNodeValue());
                dto.setArvlMsg2(arvlMsg2.item(0).getChildNodes().item(0).getNodeValue());
                dto.setStatnFid(statnFid.item(0).getChildNodes().item(0).getNodeValue());
                dto.setStatnTid(statnTid.item(0).getChildNodes().item(0).getNodeValue());
                DTO.add(dto);


                if (i == 0) {
                    sid = subwayId.item(0).getChildNodes().item(0).getNodeValue();
                    id_list.add(sid);
                }


                if (!id_list.contains(subwayId.item(0).getChildNodes().item(0).getNodeValue()))
                    id_list.add(subwayId.item(0).getChildNodes().item(0).getNodeValue());
            }


            id_list.sort(Comparator.comparingInt(Integer::valueOf));

            for (int k = 0; k < id_list.size(); k++) {
                imageView = new ImageView(getActivity());
                final Button button = new Button(getActivity());
                switch (id_list.get(k)) {
                    case "1001":
                        imageView.setImageResource(R.drawable.one);
                        break;
                    case "1002":
                        imageView.setImageResource(R.drawable.two);
                        break;
                    case "1003":
                        imageView.setImageResource(R.drawable.three);
                        break;
                    case "1004":
                        imageView.setImageResource(R.drawable.four);
                        break;
                    case "1005":
                        imageView.setImageResource(R.drawable.five);
                        break;
                    case "1006":
                        imageView.setImageResource(R.drawable.six);
                        break;
                    case "1007":
                        imageView.setImageResource(R.drawable.seven);
                        break;
                    case "1008":
                        imageView.setImageResource(R.drawable.eight);
                        break;
                    case "1009":
                        imageView.setImageResource(R.drawable.nine);
                        break;
                    case "1067":
                        imageView.setImageResource(R.drawable.chun);
                        break;
                    case "1063":
                        imageView.setImageResource(R.drawable.center);
                        break;
                    case "1071":
                        imageView.setImageResource(R.drawable.suin);
                        break;
                    case "1065":
                        imageView.setImageResource(R.drawable.airport);
                        break;
                    case "1077":
                        imageView.setImageResource(R.drawable.newbundang);
                        break;
                    case "1075":
                        imageView.setImageResource(R.drawable.bundang);
                        break;

                }

                LinearLayout.LayoutParams params;
                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                imageView.setLayoutParams(params);
//                imageView.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                params.leftMargin = 20; // marginRight
                button.setText(id_list.get(k));

                linearLayout.addView(imageView);

                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        String up1 = ""; // 상행
                        String up2 = ""; // 상행
                        String up3 = ""; // 상행
                        String up4 = ""; // 상행

                        String down1 = ""; // 하행
                        String down2 = ""; // 하행
                        String down3 = ""; // 하행
                        String down4 = ""; // 하행

                        String up_dir = ""; // 상행 방면
                        String compare_dir = ""; // 비교할 방면
                        String down_dir = ""; // 하행 방면
                        String dir = ""; // 무슨행

                        String s = (String) button.getText();

                        int FLAG = 0;
                        int c1 = 0, c2 = 0;
                        for (int z = 0; z < DTO.size(); z++) { // 전체 아이템
                            if (DTO.get(z).subwayId.equals(s)) { // z번째 아이템의 호선이 버튼 호선과 같다면

                                if (FLAG == 0) { // 한 번만 실행
                                    // 해당 아이템의 방면 저장 (상행)
                                    up_dir = DTO.get(z).trainLineNm;

                                    up_dir = up_dir.split("- ")[1];
                                    // Toast.makeText(context, up_dir, Toast.LENGTH_SHORT).show();
                                    FLAG = 1;
                                }
                                dir = DTO.get(z).trainLineNm.split("-")[0];
                                // 현재 아이템의 방면
                                compare_dir = DTO.get(z).trainLineNm;
                                compare_dir = compare_dir.split("- ")[1];

                                if (compare_dir.equals(up_dir)) {// 상행 (현재 아이템 방면이 상행이면)
                                    if (c1 < 2) {
                                        if (c1 == 0) {
                                            if (DTO.get(z).getArvlMsg2().equals(subname + " 진입")) {
                                                up1 += dir;
                                                up2 += "곧 도착";
                                            } else if (DTO.get(z).getArvlMsg2().equals(subname + " 도착")) {
                                                up1 += dir;
                                                up2 += "도착";
                                            } else {
                                                up1 += dir;
                                                up2 += DTO.get(z).getArvlMsg2();
                                            }
                                        }
                                        else if (c1 == 1) {
                                            if (DTO.get(z).getArvlMsg2().equals(subname + " 진입")) {
                                                up3 += dir;
                                                up4 += "곧 도착";
                                            } else if (DTO.get(z).getArvlMsg2().equals(subname + " 도착")) {
                                                up3 += dir;
                                                up4 += "도착";
                                            } else {
                                                up3 += dir;
                                                up4 += DTO.get(z).getArvlMsg2();
                                            }
                                        }
                                        c1++;
                                    }
                                    } else { // 하행 (현재 아이템 방면이 상행이 아니면)
                                        if (c2 < 2) {
                                            if(c2==0) {
                                                if (DTO.get(z).getArvlMsg2().equals(subname + " 진입")) {
                                                    down1 += dir;
                                                    down2 += "곧 도착";
                                                } else if (DTO.get(z).getArvlMsg2().equals(subname + " 도착")) {
                                                    down1 += dir ;
                                                    down2 += "도착";
                                                } else {
                                                    down1 += dir;
                                                    down2 += DTO.get(z).getArvlMsg2();
                                                }
                                            }
                                            else if(c2==1){
                                                if (DTO.get(z).getArvlMsg2().equals(subname + " 진입")) {
                                                    down3 += dir;
                                                    down4 += "곧 도착";
                                                } else if (DTO.get(z).getArvlMsg2().equals(subname + " 도착")) {
                                                    down3 += dir ;
                                                    down4 += "도착";
                                                } else {
                                                    down3 += dir;
                                                    down4 += DTO.get(z).getArvlMsg2();
                                                }
                                            }
                                            down_dir = compare_dir;
                                            c2++;
                                        }
                                    }


                            }
                        }


                        // textView.setText(up_dir + "\n" + up + "\n\n\n\n\n" + down_dir + "\n" + down);

                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        LayoutInflater layoutInflater = getLayoutInflater();
                        View view1 = layoutInflater.inflate(R.layout.item_subdialog, null);

                        builder.setView(view1);
                        subimage = (ImageView) view1.findViewById(R.id.sub);
                        TextView up_dir1 = (TextView) view1.findViewById(R.id.up_dir1);
                        TextView down_dir1 = (TextView) view1.findViewById(R.id.down_dir1);
                        TextView up_1 = (TextView) view1.findViewById(R.id.up1);
                        TextView up_2 = (TextView) view1.findViewById(R.id.up2);
                        TextView up_3 = (TextView) view1.findViewById(R.id.up3);
                        TextView up_4 = (TextView) view1.findViewById(R.id.up4);

                        TextView down_1 = (TextView) view1.findViewById(R.id.down1);
                        TextView down_2 = (TextView) view1.findViewById(R.id.down2);
                        TextView down_3 = (TextView) view1.findViewById(R.id.down3);
                        TextView down_4 = (TextView) view1.findViewById(R.id.down4);

                        TextView subname1 = (TextView) view1.findViewById(R.id.subname);
                        subname1.setText(subname + "역");
                        up_dir1.setText(up_dir);
                        down_dir1.setText(down_dir);
                        up_1.setText(up1);
                        up_2.setText(up2);
                        up_3.setText(up3);
                        up_4.setText(up4);

                        down_1.setText(down1);
                        down_2.setText(down2);
                        down_3.setText(down3);
                        down_4.setText(down4);

                        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        });

                        Dialog dialog1 = builder.create();
                        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                        lp.copyFrom(dialog1.getWindow().getAttributes());
                        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
                        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
                        dialog1.show();
                        Window window = dialog1.getWindow();
                        window.setAttributes(lp);

                    }
                });
                super.

                        onPostExecute(doc);
            }

            id_list.clear();

        }
    }


}

