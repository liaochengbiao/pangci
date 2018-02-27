package com.hasee.pangci.X5;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hasee.pangci.R;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

public class WebviewVideoActivity extends AppCompatActivity {
//    private String url="http://www.suomusic.com/dryCargoInfo.htm?mt=show&id=2";  //测试网址选择1
//        private String url="https://xinjiexi.cn/app/index.php?i=80&c=entry&do=index&m=super_mov";//测试网址选择2腾讯视频

    private String url = "http://api2.my230.com/?vid=goGA1UERkK104wtz";//测试网址选择2腾讯视频
    private WebView webView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview_video);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        APIWebviewTBS.getAPIWebview().initTBSActivity(this);   //api借口注册二次封装
        loadWebvieUrl(url);
    }
//    private void initdata() {//获取手机版本信息
//        int tbsVersion = QbSdk.getTbsVersion(this);
//        String TID = QbSdk.getTID();
//        String qBVersion = QbSdk.getMiniQBVersion(this);
//        tvStatus.setText("TbsVersion:" + tbsVersion + "\nTID:" + TID + "\nMiniQBVersion:" + qBVersion);
//    }

    private void loadWebvieUrl(String url) {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        webView = (WebView) findViewById(R.id.webview_wechat);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onReceivedError(WebView var1, int var2, String var3, String var4) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(WebviewVideoActivity.this, "视频加载失败", Toast.LENGTH_LONG);
            }
        });
        //进度条
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //  确保注销配置能够被释放
//        if(this.webView!=null){
//            webView.destroy();
//        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
