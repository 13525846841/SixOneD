package com.yksj.consultation.im;

import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.library.base.base.BaseTitleActivity;
import com.yksj.consultation.sonDoc.R;

/**
 * 聊天地图详情界面
 */
public class ChatMapActivity extends BaseTitleActivity {

    private MapView mMapView;
    private BaiduMap mBaiduMap;

    @Override
    public int createLayoutRes() {
        return R.layout.chat_map_layout;
    }

    @Override
    public void initialize(Bundle bundle) {
        super.initialize(bundle);
        setTitle("位置信息");
        initView();
    }

    private void initView() {
        mMapView = findViewById(R.id.mmap);
        mBaiduMap = mMapView.getMap();
        Double lng = Double.valueOf(getIntent().getStringExtra("lo"));
        Double lat = Double.valueOf(getIntent().getStringExtra("la"));
        LatLng point = new LatLng(lng, lat);
        MapStatus mapStatus = new MapStatus.Builder()
                .target(point)
                .zoom(16.0f)
                .build();
        MapStatusUpdate msu = MapStatusUpdateFactory.newMapStatus(mapStatus);
        BitmapDescriptor bitmap = BitmapDescriptorFactory
                .fromResource(R.drawable.icon_marka);
        mBaiduMap.animateMapStatus(msu);
        MarkerOptions markerOptions = new MarkerOptions().position(point).icon(bitmap);
        mBaiduMap.clear();
        mBaiduMap.addOverlay(markerOptions);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
        mMapView.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
        mMapView.onPause();
    }
}
