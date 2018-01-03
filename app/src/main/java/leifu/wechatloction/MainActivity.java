package leifu.wechatloction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.model.MyLocationStyle;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.help.Tip;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import leifu.shapelibrary.ShapeView;

public class MainActivity extends AppCompatActivity implements AMapLocationListener, LocationSource, PoiSearch.OnPoiSearchListener {

    MyLocationStyle myLocationStyle;
    //声明mlocationClient对象
    public AMapLocationClient mlocationClient;
    //声明mLocationOption对象
    public AMapLocationClientOption mLocationOption = null;
    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private boolean isFirstLoc = true;
    private AMap aMap;
    private OnLocationChangedListener mListener = null;//定位监听器
    private PoiSearch.Query query;
    private PoiSearch poiSearch;
    private double mCurrentLat;
    private double mCurrentLng;
    private ListView listView;
    ArrayList<PoiItem> arrayList = new ArrayList<>();
    ArrayList<PoiItem> poiItemsList;
    private MapView mapView;
    private SearchAdapter searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);// 此方法必须重写
        aMap = mapView.getMap();

        //设置显示定位按钮 并且可以点击
        UiSettings settings = aMap.getUiSettings();
        //设置定位监听
        aMap.setLocationSource(this);
        // 是否显示定位按钮
        settings.setMyLocationButtonEnabled(true);
        // 是否可触发定位并显示定位层
        aMap.setMyLocationEnabled(true);


        init();
        final ShapeView shapeView = (ShapeView) findViewById(R.id.et_input);
        listView = (ListView) findViewById(R.id.listView);
        shapeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, SearchResultActivity.class), 0);
            }
        });
        findViewById(R.id.btn_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                searchPoi(editText.getText().toString(), 0, "", false);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                searchAdapter.setSelectedPosition(i);
                searchAdapter.notifyDataSetChanged();
                PoiItem poiItem = poiItemsList.get(i);
                //设置图钉选项
                MarkerOptions markerOptions = new MarkerOptions();
//                options.icon(BitmapDescriptorFactory.defaultMarker());//默认图标
                markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_market)); //图标
                markerOptions.position(new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude())); //位置
                markerOptions.title(poiItem.getProvinceName() + "" + poiItem.getCityName() + "" + poiItem.getAdName()); //标题
                markerOptions.snippet(poiItem.getSnippet()); //子标题
                markerOptions.period(60); //设置多少帧刷新一次图片资源
                aMap.clear();
                CameraUpdate cu = CameraUpdateFactory.newLatLng(new LatLng(poiItem.getLatLonPoint().getLatitude(), poiItem.getLatLonPoint().getLongitude()));
                aMap.animateCamera(cu);
                aMap.addMarker(markerOptions);
                aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
            }
        });

    }

    /**
     * 搜索poi
     *
     * @param key      关键字
     * @param pageNum  页码
     * @param cityCode 城市代码，或者城市名称
     * @param nearby   是否搜索周边
     */
    void searchPoi(String key, int pageNum, String cityCode, boolean nearby) {

        query = new PoiSearch.Query(key, "", cityCode);
        //keyWord表示搜索字符串，
        //第二个参数表示POI搜索类型，二者选填其一，
        //POI搜索类型共分为以下20种：汽车服务|汽车销售|
        //汽车维修|摩托车服务|餐饮服务|购物服务|生活服务|体育休闲服务|医疗保健服务|
        //住宿服务|风景名胜|商务住宅|政府机构及社会团体|科教文化服务|交通设施服务|
        //金融保险服务|公司企业|道路附属设施|地名地址信息|公共设施
        //cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
        query.setPageSize(50);// 设置每页最多返回多少条poiitem
        query.setPageNum(pageNum);//设置查询页码
        poiSearch = new PoiSearch(this, query);
        poiSearch.setOnPoiSearchListener(this);
        if (nearby)
            poiSearch.setBound(new PoiSearch.SearchBound(new LatLonPoint(mCurrentLat,
                    mCurrentLng), 1500));//设置周边搜索的中心点以及半径
        poiSearch.searchPOIAsyn();
    }


    private void init() {
        mlocationClient = new AMapLocationClient(this);
//初始化定位参数
        mLocationOption = new AMapLocationClientOption();
//设置定位监听
        mlocationClient.setLocationListener(this);
//设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setInterval(2000);
//设置定位参数
        mlocationClient.setLocationOption(mLocationOption);
// 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
// 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
// 在定位结束后，在合适的生命周期调用onDestroy()方法
// 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
//启动定位
        mlocationClient.startLocation();

    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //存储定位数据
                mCurrentLat = amapLocation.getLatitude();
                mCurrentLng = amapLocation.getLongitude();
                //定位成功回调信息，设置相关消息
                amapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                amapLocation.getLatitude();//获取纬度
                amapLocation.getLongitude();//获取经度
                amapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date(amapLocation.getTime());
                df.format(date);//定位时间
                if (isFirstLoc) {
                    //设置缩放级别
                    aMap.moveCamera(CameraUpdateFactory.zoomTo(17));
                    //将地图移动到定位点
                    aMap.moveCamera(CameraUpdateFactory.changeLatLng(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())));
                    //点击定位按钮 能够将地图的中心移动到定位点
                    mListener.onLocationChanged(amapLocation);
                    //添加图钉
                    aMap.addMarker(getMarkerOptions(amapLocation));
                    searchPoi("", 2, "", true);
                    isFirstLoc = false;
                }
            }
        } else {
            //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
            Log.e("AmapError", "location Error, ErrCode:"
                    + amapLocation.getErrorCode() + ", errInfo:"
                    + amapLocation.getErrorInfo());
        }
    }

    //自定义一个图钉，并且设置图标，当我们点击图钉时，显示设置的信息
    private MarkerOptions getMarkerOptions(AMapLocation amapLocation) {
        MarkerOptions markerOptions = new MarkerOptions(); //设置图钉选项
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_market)); //图标
        markerOptions.position(new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude())); //位置
//        new StringBuffer().append(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getDistrict() + "" + amapLocation.getStreet() + "" + amapLocation.getStreetNum());
        markerOptions.title(amapLocation.getCountry() + "" + amapLocation.getProvince() + "" + amapLocation.getCity() + "" + amapLocation.getDistrict()); //标题
        markerOptions.snippet(amapLocation.getStreet() + "" + amapLocation.getStreetNum()); //子标题
        markerOptions.period(60);  //设置多少帧刷新一次图片资源
        return markerOptions;

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;
    }

    @Override
    public void deactivate() {
        mListener = null;
    }

    @Override
    public void onPoiSearched(PoiResult poiResult, int i) {
        //填充数据，并更新listview
        poiItemsList = poiResult.getPois();
        searchAdapter = new SearchAdapter(poiItemsList, MainActivity.this);
        listView.setAdapter(searchAdapter);
        for (PoiItem item : poiResult.getPois()) {
            Log.e("aaa", item.toString());
            Log.e("aaa", item.getDirection());
            Log.e("aaa", item.getAdName());
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {
        Log.e("bbb+++++", poiItem.getAdName() + poiItem.getCityName());
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mapView.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mapView.onSaveInstanceState(outState);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == 1) {
            Tip tip = data.getParcelableExtra("tip");
            searchPoi(tip.getName(), 0, "", true);
        }
    }
}
