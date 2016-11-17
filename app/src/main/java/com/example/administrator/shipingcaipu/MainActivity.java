package com.example.administrator.shipingcaipu;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ListView lvlist;
    private List<ShiPingCaiPuDataInfo.ListBean> list=new ArrayList<>();
    private ShiPingCaiPuAdapter adapter;
    String index="";
    private  Boolean flag=false;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1:
                    adapter.notifyDataSetChanged();
            }

        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        setData();
        adapter=new ShiPingCaiPuAdapter(list,this);
        lvlist.setAdapter(adapter);
        setListener();
    }

    private void setListener() {
        lvlist.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
                if (flag  && i==SCROLL_STATE_IDLE){
                    flag=false;
                    setData();
                }
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
            if (i+i1==i2){
                flag=true;
            }
            }
        });
        lvlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent=new Intent();
              //  intent.setClass(MainActivity.this,IdActivity);
                intent.putExtra("id",list.get(i).getId());
                startActivity(intent);

            }
        });
    }

    private void setData() {


        new Thread(){
            @Override
            public void run() {
                super.run();

//http://api.ecook.cn/public/getRecommendContentsByType.shtml?machine=d0eab938324d212a6d577de6751d9685&version=12.9.0&id=78361&type=video_recipe
                String uri="http://api.ecook.cn/public/getRecommendContentsByType.shtml?machine=d0eab938324d212a6d577de6751d9685&version=12.9.0&type=video_recipe&id="+index;
            //    String uri="http://api.ecook.cn/public/getRecommendContentsByType.shtml";
                RequestParams entity=new RequestParams(uri);
//                entity.addBodyParameter("machine","d0eab938324d212a6d577de6751d9685");
//                entity.addBodyParameter("version","12.9.0");
//                entity.addBodyParameter("id","78361");
//                entity.addBodyParameter("type","video_recipe");
                x.http().post(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.d("test","*******"+result);
                        Gson gson=new Gson();
                        ShiPingCaiPuDataInfo shiPingCaiPuDataInfo = gson.fromJson(result, ShiPingCaiPuDataInfo.class);
                        list.addAll(shiPingCaiPuDataInfo.getList());
                        index = list.get(list.size()-1).getId();

                        handler.sendEmptyMessage(1);
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        Toast.makeText(MainActivity.this,"亲！数据获取异常！请等待",Toast.LENGTH_SHORT).show();
                        Log.d("test","***请求失败***"+ex);
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }
                    @Override
                    public void onFinished() {

                    }
                });
            }
        }.start();
    }

    private void initView() {
        lvlist= (ListView) findViewById(R.id.lvlist);
    }
}
