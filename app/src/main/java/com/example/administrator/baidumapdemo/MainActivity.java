package com.example.administrator.baidumapdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMapOptions;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    Button btn_change;
    Button btn_location;
    MapView mv_main;
    BaiduMap mBaidumap;
    LocationClient locationClient;
    RelativeLayout activity_main;
    LatLng myLocation;
    private BaiduMap.OnMapStatusChangeListener listener = new BaiduMap.OnMapStatusChangeListener() {
        @Override
        public void onMapStatusChangeStart(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChange(MapStatus mapStatus) {

        }

        @Override
        public void onMapStatusChangeFinish(MapStatus mapStatus) {
            Toast.makeText(MainActivity.this, "状态变化：纬度：" + mapStatus.target.latitude + "经度：" + mapStatus.target.longitude, Toast.LENGTH_SHORT).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

        MapStatus mapStatus = new MapStatus.Builder()
                .overlook(0)//俯视角度 -45-0
                .zoom(15)//缩放的级别 3-21
                .build();
        BaiduMapOptions options = new BaiduMapOptions()
                .zoomControlsEnabled(false)//不显示缩放的控件
                .zoomGesturesEnabled(true)//是否允许缩放的收拾
                //具体查看API
                .mapStatus(mapStatus);

        // 目前来说，设置只能通过MapView的构造方法来添加,所以Demo里面是在布局中添加MapView
        // 后面项目实施会动态创建
        mv_main = new MapView(this, options);
        mBaidumap = mv_main.getMap();
        activity_main.addView(mv_main, 0);
        mBaidumap.setOnMapStatusChangeListener(listener);
    }

    private void init() {
        activity_main = (RelativeLayout) findViewById(R.id.activity_main);
        btn_change = (Button) findViewById(R.id.btn_change);
        btn_location = (Button) findViewById(R.id.btn_location);
        btn_change.setOnClickListener(this);
        btn_location.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_change:

                if (mBaidumap.getMapType() == BaiduMap.MAP_TYPE_NORMAL) {
                    mBaidumap.setMapType(BaiduMap.MAP_TYPE_SATELLITE);

                } else {
                    mBaidumap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
                }
                break;
            case R.id.btn_location:
                //定位相关
                /**
                 * 开启定位图层
                 * 初始化LocationClient
                 * 配置一些定位相关的参数LocationClientOption
                 * 设置监听，定位的监听
                 * 开启定位
                 */
                mBaidumap.setMyLocationEnabled(true);//打开定位
                //                初始化
                locationClient = new LocationClient(getApplicationContext());
                //                配置
                LocationClientOption locationClientOption = new LocationClientOption();

                locationClientOption.setOpenGps(true);//打开gps
                locationClientOption.setCoorType("bd09ll");//设置坐标类型，默认gcj02
                locationClientOption.setIsNeedAddress(true);//默认不需要
                locationClientOption.setScanSpan(5000);//设置扫描周期

                locationClient.setLocOption(locationClientOption);
                //设置监听
                locationClient.registerLocationListener(locationListener);

                mBaidumap.setOnMarkerClickListener(markListener);

                //开启定位
                locationClient.start();
                //                为防止定位失败
                locationClient.requestLocation();
                Log.d("111", "nihao");
                break;

        }
    }

    private BDLocationListener locationListener = new BDLocationListener() {

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            //            Toast.makeText(MainActivity.this, "好!!!!!", Toast.LENGTH_SHORT).show();
            if (bdLocation == null) {
                locationClient.requestLocation();
                return;
            }
            Toast.makeText(MainActivity.this, "你好", Toast.LENGTH_SHORT).show();
            double latitude = bdLocation.getLatitude();
            double longitude = bdLocation.getLongitude();
            //            Toast.makeText(MainActivity.this, "经度" + longitude + "，纬度" + latitude, Toast.LENGTH_SHORT).show();

            MyLocationData myLocationData = new MyLocationData.Builder()
                    .latitude(latitude)
                    .longitude(longitude)
                    .accuracy(100f)
                    .build();
            mBaidumap.setMyLocationData(myLocationData);

            myLocation = new LatLng(latitude, longitude);
            moveToMyLocation();
            addMark(new LatLng(latitude + 0.01, longitude));
        }
    };

    private void moveToMyLocation() {

        MapStatus mapStatus = new MapStatus.Builder()
                .target(myLocation)
                .zoom(17)
                .rotate(0)
                .build();
        MapStatusUpdate updata = MapStatusUpdateFactory.newMapStatus(mapStatus);
        mBaidumap.animateMapStatus(updata);
    }

    private BitmapDescriptor dot = BitmapDescriptorFactory.fromResource(R.drawable.treasure_dot);
    private BitmapDescriptor dot_click = BitmapDescriptorFactory.fromResource(R.drawable.treasure_expanded);

    public void addMark(LatLng latLng) {

        MarkerOptions options = new MarkerOptions();
        options.position(latLng);
        options.icon(dot);
        mBaidumap.addOverlay(options);
    }

    private BaiduMap.OnMarkerClickListener markListener = new BaiduMap.OnMarkerClickListener() {
        @Override
        public boolean onMarkerClick(Marker marker) {

            InfoWindow infoWindow = new InfoWindow(dot_click, marker.getPosition(), 0, new InfoWindow.OnInfoWindowClickListener() {
                @Override
                public void onInfoWindowClick() {

                }
            });
            mBaidumap.showInfoWindow(infoWindow);

            return false;
        }
    };
}
