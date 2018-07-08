package com.lamont.demo.itunetop100;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;

import android.support.v4.app.ActivityCompat;
import static android.Manifest.permission.*;

import com.lamont.demo.itunetop100.Utils.NetUtil;
import com.lamont.demo.itunetop100.database.Top100Table;
import com.lamont.demo.itunetop100.database.iTuneDBHelper;
import com.lamont.demo.itunetop100.database.model.ITuneItem;
import com.lamont.demo.itunetop100.itunes.IiTunesRss;
import com.lamont.demo.itunetop100.itunes.RssResult;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/*   get iTunes Top 100 from iTunes Rss */
public class iTuneTop100Activity extends AppCompatActivity implements NetBroadcastReceiver.INetEvevtLister {
    @BindView(R.id.fab) FloatingActionButton refreshBtn;
    @BindView(R.id.rootView) View rootView;
    @BindView(R.id.toolbar ) Toolbar toolbar;
    @BindView(R.id.itune_item_list)RecyclerView mRvList; // 列表

    private static String TAG = "iTuneTop100Activity";
    private static int WRITE_EXTERNAL_STORAGE_REQUEST_CODE = 100;
    private static int READ_EXTERNAL_STORAGE_REQUEST_CODE = 101;
    private int netStatus;
    private ColorStateList csDisable;
    private ColorStateList csEnable;

    private Top100Table mTop100Table;
    private String query_country = "tw";
    private String country_tw = "tw";
    private String country_us = "us";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_i_tune_top100);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        // for permission
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            //request WRITE_EXTERNAL_STORAGE
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE},
                    WRITE_EXTERNAL_STORAGE_REQUEST_CODE);
        } else {
            mTop100Table = new Top100Table(getApplicationContext());
        }


        // button color state
        Resources resource=(Resources)getBaseContext().getResources();
        csDisable = (ColorStateList)resource.getColorStateList(R.color.refresh_btn_disable);
        csEnable = (ColorStateList)resource.getColorStateList(R.color.refresh_btn_enable);

        // for network status
        NetBroadcastReceiver.registerNetListener(this);
        inspectNet();
        setRefreshBtn();

        // for list

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvList.setLayoutManager(layoutManager);
        setList();
    }

    private void setList() {
        if (null != mTop100Table) {
            final List<ITuneItem> iTuneItemList = mTop100Table.getAll();
            if (iTuneItemList.size() > 0) {
                this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(TAG, "setList --- > ");
                        ituneAdapter adapter = new ituneAdapter(getApplicationContext(), iTuneItemList);
                        mRvList.setAdapter(adapter);
                    }
                });
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        doNext(requestCode, grantResults);
    }

    private void doNext(int requestCode, int[] grantResults) {
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission Granted
                Log.d(TAG, "get WRITE PERMISSION_GRANTED");
                mTop100Table = new Top100Table(this);

            } else {
                // Permission Denied
                Snackbar.make(rootView, "正常來說 會再問一次", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        }
    }



    @Override
    protected void onDestroy(){
        super.onDestroy();
        NetBroadcastReceiver.unregisterNetListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_i_tune_top100, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            if (query_country.equalsIgnoreCase(country_us)) {
                query_country = country_tw;
            } else {
                query_country = country_us;
            }
            Snackbar.make(rootView, "set query country to " + query_country + " ! ", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab) void refreshBtn(View view) {
        // TODO call server...
        Animation aniRotate = new RotateAnimation(0.0f, 360.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                0.5f);
        aniRotate.setRepeatCount(1);
        aniRotate.setDuration(1000);
        refreshBtn.setAnimation(aniRotate);
        Snackbar.make(view, "Loading Top 100 Music...", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        requestiTuneRss();
    }

    private void requestiTuneRss() {

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(IiTunesRss.ENDPOINT) // 设置 网络请求 Url
                .addConverterFactory(GsonConverterFactory.create()) //设置使用Gson解析(记得加入依赖)
                .build();


        IiTunesRss request = retrofit.create(IiTunesRss.class);
        Call<RssResult> call = request.getTop100ByCountry(query_country);


        call.enqueue(new Callback<RssResult>() {
            @Override
            public void onResponse(Call<RssResult> call, Response<RssResult> response) {
                RssResult.result[] results =  response.body().getResults();
                mTop100Table.saveItuneItems(ITuneItem.convertITuneItemList(results));
                setList();
            }

            @Override
            public void onFailure(Call<RssResult> call, Throwable throwable) {
                System.out.println("Get RSS Fail!");
            }
        });
    }

    public void onNetChange(int netMobile) {
        Log.d(TAG, "onNetChange : " + netMobile);
        this.netStatus = netMobile;
        setRefreshBtn();
    }

    /**
     *  初始化時判斷有沒有網絡
     */
    private boolean inspectNet() {
        this.netStatus = NetUtil.getNetWorkState(this);
        return isNetConnect();
    }
    /**
     *判斷有無網絡。
     *
     * @return true有網，false沒有網絡。
     */
    private boolean isNetConnect() {
        if (netStatus == NetUtil.NETWORK_WIFI) {
            return true;
        } else if (netStatus == NetUtil.NETWORK_MOBILE) {
            return true;
        } else if (netStatus == NetUtil.NETWORK_NONE) {
            return false;
        }
        return false;
    }

    private void setRefreshBtn() {
        boolean isConnected = isNetConnect();
        refreshBtn.setEnabled(isConnected);
        if (isConnected) {
            refreshBtn.setSupportBackgroundTintList(csEnable);
        } else{
            refreshBtn.setSupportBackgroundTintList(csDisable);
        }
    }
}
