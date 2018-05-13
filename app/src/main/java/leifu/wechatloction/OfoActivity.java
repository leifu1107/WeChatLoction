package leifu.wechatloction;

import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.Circle;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;

/**
 * 创建人: 雷富
 * 创建时间: 2018/5/11 15:59
 * 描述:
 */

public class OfoActivity extends AppCompatActivity implements AMap.OnMyLocationChangeListener {
    MapView mMapView = null;
    AMap aMap;
    private Circle circle;
    private boolean isFirst = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ofo);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        //初始化地图控制器对象

        if (aMap == null) {
            aMap = mMapView.getMap();
        }
//        实现定位蓝点
        aMap.setOnMyLocationChangeListener(this);
        initLocation();

    }

    private void initLocation() {
        MyLocationStyle myLocationStyle;
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_FOLLOW);//连续定位、且将视角移动到地图中心点，定位蓝点跟随设备移动。（1秒1次定位）
        myLocationStyle.myLocationIcon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_market));
        myLocationStyle.anchor(0.5f, 0.5f);
        myLocationStyle.strokeColor(0xffffff);
        myLocationStyle.strokeWidth(0);
        myLocationStyle.radiusFillColor(0xffffff);

        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }

    @Override
    public void onMyLocationChange(Location location) {

        if (isFirst) {
            LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
            CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(
                    latLng, 15);
            aMap.animateCamera(cameraUpate);

            circle = aMap.addCircle(new CircleOptions().
                    center(latLng).
                    radius(1000).
                    fillColor(Color.argb(11, 1, 1, 1)).
                    strokeColor(Color.argb(11, 1, 1, 1)).
                    strokeWidth(15));
            isFirst = false;
            MarkerOptions markerOption2 = new MarkerOptions();

            markerOption2.position(new LatLng((location.getLatitude()), location.getLongitude()));
            markerOption2.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_launcher_round));
            aMap.addMarker(markerOption2);
            Log.e("aaa", "onMyLocationChange: " + location.toString());
            for (int i = 1; i < 10; i++) {
                MarkerOptions markerOption = new MarkerOptions();

                markerOption.position(new LatLng((location.getLatitude() + i / 1000.1), location.getLongitude()));
                markerOption.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_hook));
                aMap.addMarker(markerOption).setVisible(true);
                // 定义 Marker 点击事件监听
                AMap.OnMarkerClickListener markerClickListener = new AMap.OnMarkerClickListener() {
                    // marker 对象被点击时回调的接口
                    // 返回 true 则表示接口已响应事件，否则返回false
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Log.e("aaa", "onMarkerClick: " + marker.getPosition().toString());
                        return false;
                    }
                };
                // 绑定 Marker 被点击事件
                aMap.setOnMarkerClickListener(markerClickListener);
            }
        }
    }
}
