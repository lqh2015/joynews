package com.hzpd.ui.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hzpd.hflt.R;
import com.hzpd.ui.App;
import com.hzpd.utils.Log;

/**
 * WevView third url
 */
public class WebActivity extends MBaseActivity {

    @Override
    protected int getOnResumeApi() {
        return 114;
    }

    @Override
    protected int getOnStopApi() {
        return 115;
    }


    private View app_progress_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity_layout);
        app_progress_bar = findViewById(R.id.app_progress_bar);
        app_progress_bar.setVisibility(View.VISIBLE);
        checkIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent();
    }

    private void checkIntent() {
        loading = true;
        Intent intent = getIntent();
        url = intent.getStringExtra(KEY_URL);
        if (TextUtils.isEmpty(url)) {
            finish();
        }
        initViews();
        urlView.setText(url);
    }

    @Override
    protected void onDestroy() {
        webView.loadUrl("about:blank");
        webView.destroy();
        webView = null;
        super.onDestroy();
    }

    public final static String KEY_URL = "key_url";
    private String url;
    private WebView webView;
    private ProgressBar load_progress_bar;
    private View background_empty;
    private View backView;
    private TextView urlView;

    private void initViews() {
        webView = (WebView) findViewById(R.id.webview);
        urlView = (TextView) findViewById(R.id.news_url);
        load_progress_bar = (ProgressBar) findViewById(R.id.load_progress_bar);
        background_empty = findViewById(R.id.background_empty);
        backView = findViewById(R.id.news_detail_bak);
        backView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //load_progress_bar.postDelayed(runnable, 50);
        load_progress_bar.setVisibility(View.VISIBLE);
        load_progress_bar.setProgress(0);
        webView.loadUrl(url);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webSettings.setDomStorageEnabled(true);

        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(App.getInstance().getAllDiskCacheDir());
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAllowFileAccess(true);


        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
                //background_empty.setVisibility(View.GONE);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                loading = true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                background_empty.setVisibility(View.GONE);
                wProgress = 100;
                load_progress_bar.setVisibility(View.GONE);
                app_progress_bar.setVisibility(View.GONE);
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                Log.e("test", description + ";" + errorCode + ":" + failingUrl);
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                if (error != null) {
                    Log.e("test", "News:  error is null ");
                } else {
                    Log.e("test", "News: " + error.toString() + ":" + error.getCertificate());
                }
                if (error != null && error.getCertificate() != null) {
                    handler.proceed();
                } else {
                    handler.cancel();
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                Log.e("test", newProgress);
                if (newProgress > progress) {
                    progress = newProgress;
                    load_progress_bar.setProgress(progress);
                }
                if (progress > 99) {
                    background_empty.setVisibility(View.GONE);
                    wProgress = 100;
                    load_progress_bar.setVisibility(View.GONE);
                    app_progress_bar.setVisibility(View.GONE);
                }
            }
        });
    }

    boolean loading = true;
    int wProgress = 0;
    int MIDDLE_PROGRESS = 95;
    int progress = 0;

    @Override
    public void finish() {
        super.finish();
        if (isTaskRoot()) {
            Intent in = new Intent();
            in.setClass(this, MainActivity.class);
            startActivity(in);
        }
    }
}
