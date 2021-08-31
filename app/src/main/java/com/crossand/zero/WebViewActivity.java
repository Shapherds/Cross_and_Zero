package com.crossand.zero;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.onesignal.OneSignal;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class WebViewActivity extends AppCompatActivity {

    private android.webkit.WebView webView;

    private FrameLayout customViewContainer;
    private WebChromeClient.CustomViewCallback customViewCallback;
    private View mCustomView;
    private WebViewChromeClient mWebChromeClient;
    public ValueCallback<Uri[]> uploadMessage;
    private ValueCallback<Uri> mUploadMessage;
    public static final int REQUEST_SELECT_FILE = 100;
    private final static int FILECHOOSER_RESULTCODE = 1;
    private String mCurrentUrl;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_content);
        OneSignal.setLogLevel(OneSignal.LOG_LEVEL.VERBOSE, OneSignal.LOG_LEVEL.NONE);
        // OneSignal Initialization
        OneSignal.initWithContext(this);
        OneSignal.setAppId(Configuration.ONESIGNAL_APP_ID.getData());
        customViewContainer = findViewById(R.id.customViewContainer);
        webView = findViewById(R.id.webView);
        initWebView();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        WebSettings settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);
        settings.setAllowContentAccess(true);
        settings.setAllowUniversalAccessFromFileURLs(false);
        settings.setLoadsImagesAutomatically(true);
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setMediaPlaybackRequiresUserGesture(true);
        settings.setUseWideViewPort(true);
        settings.setDisplayZoomControls(false);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setLoadWithOverviewMode(false);
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true);

        WebViewClient webViewClient = new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, String url) {
                if (url != null && url.equals(mCurrentUrl)) {
                    webView.goBack();
                    return true;
                }

                view.loadUrl(url);
                mCurrentUrl = url;
                return true;
            }

            @TargetApi(Build.VERSION_CODES.N)
            @Override
            public boolean shouldOverrideUrlLoading(android.webkit.WebView view, WebResourceRequest request) {
                if (mCurrentUrl != null && request.getUrl().toString() != null && request.getUrl().toString().equals(mCurrentUrl)) {
                    webView.goBack();
                    return true;
                }

                view.loadUrl(request.getUrl().toString());
                mCurrentUrl = request.getUrl().toString();
                return true;
            }

            @Override
            public void onReceivedSslError(android.webkit.WebView view, final SslErrorHandler handler, SslError error) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(WebViewActivity.this);
                String message = "SSL Certificate error.";
                switch (error.getPrimaryError()) {
                    case SslError.SSL_UNTRUSTED:
                        message = "The certificate authority is not trusted.";
                        break;
                    case SslError.SSL_EXPIRED:
                        message = "The certificate has expired.";
                        break;
                    case SslError.SSL_IDMISMATCH:
                        message = "The certificate Hostname mismatch.";
                        break;
                    case SslError.SSL_NOTYETVALID:
                        message = "The certificate is not yet valid.";
                        break;
                }
                message += "Do you want to continue anyway?";
                builder.setTitle("SSL Certificate Error");
                builder.setMessage(message);
                builder.setPositiveButton("continue", (dialog, which) -> handler.proceed());
                builder.setNegativeButton("cancel", (dialog, which) -> handler.cancel());
                final AlertDialog dialog = builder.create();
                dialog.show();
            }
        };

        mWebChromeClient = new WebViewChromeClient();
        new WebChromeClient() {
            @Override
            public boolean onShowFileChooser(android.webkit.WebView mWebView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                if (uploadMessage != null) {
                    uploadMessage.onReceiveValue(null);
                    uploadMessage = null;
                }
                uploadMessage = filePathCallback;
                Intent intent = new Intent("android.intent.action.GET_CONTENT").addCategory(Intent.CATEGORY_OPENABLE).setType("image/*");
                try {
                    startActivityForResult(intent, REQUEST_SELECT_FILE);
                } catch (ActivityNotFoundException e) {
                    uploadMessage = null;
                    Toast.makeText(WebViewActivity.this, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                    return false;
                }
                return true;
            }
        };

        webView.setWebChromeClient(mWebChromeClient);
        webView.setWebViewClient(webViewClient);

        Log.e("finalUrl", " URL : " + Objects.requireNonNull(getIntent().getStringExtra("Links")));
        webView.loadUrl(Objects.requireNonNull(getIntent().getStringExtra("Links")));
    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode == REQUEST_SELECT_FILE) {
                if (uploadMessage == null)
                    return;
                uploadMessage.onReceiveValue(WebChromeClient.FileChooserParams.parseResult(resultCode, intent));
                uploadMessage = null;
            }
        } else if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null : intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        } else
            Toast.makeText(this, "Failed to Upload Image", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onSaveInstanceState(@NotNull Bundle outState) {
        super.onSaveInstanceState(outState);
        webView.saveState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NotNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        webView.restoreState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        if (inCustomView()) {
            hideCustomView();
            return;
        }
        if ((mCustomView == null) && webView.canGoBack()) {
            webView.goBack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        webView.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        webView.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (inCustomView()) {
            hideCustomView();
        }
    }

    private class WebViewChromeClient extends WebChromeClient {
        private View mVideoProgressView;

        @Override
        public boolean onShowFileChooser(android.webkit.WebView mWebView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (uploadMessage != null) {
                uploadMessage.onReceiveValue(null);
                uploadMessage = null;
            }
            uploadMessage = filePathCallback;
            Intent intent = new Intent("android.intent.action.GET_CONTENT").addCategory(Intent.CATEGORY_OPENABLE).setType("image/*");
            try {
                startActivityForResult(intent, REQUEST_SELECT_FILE);
            } catch (ActivityNotFoundException e) {
                uploadMessage = null;
                Toast.makeText(WebViewActivity.this, "Cannot Open File Chooser", Toast.LENGTH_LONG).show();
                return false;
            }
            return true;
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            mCustomView = view;
            webView.setVisibility(View.GONE);
            customViewContainer.setVisibility(View.VISIBLE);
            customViewContainer.addView(view);
            customViewCallback = callback;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getVideoLoadingProgressView() {
            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(WebViewActivity.this);
                mVideoProgressView = inflater.inflate(R.layout.video_prog, null);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            super.onHideCustomView();
            if (mCustomView == null)
                return;

            webView.setVisibility(View.VISIBLE);
            customViewContainer.setVisibility(View.GONE);

            mCustomView.setVisibility(View.GONE);

            customViewContainer.removeView(mCustomView);
            customViewCallback.onCustomViewHidden();

            mCustomView = null;
        }
    }
}