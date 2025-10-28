package com.example.prm392_cinema;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.appcompat.app.AppCompatActivity;

public class PaymentWebViewActivity extends AppCompatActivity {

    private static final String DEEP_LINK_SCHEME = "demozpdk";
    private String transactionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        WebView webView = findViewById(R.id.payment_webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);

        String paymentUrl = getIntent().getStringExtra("payment_url");
        transactionId = getIntent().getStringExtra("transactionId");

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url == null) {
                    return false;
                }

                Uri uri = Uri.parse(url);
                if (DEEP_LINK_SCHEME.equals(uri.getScheme()) || "localhost".equals(uri.getHost())) {
                    navigateToSuccessScreen();
                    return true;
                }
                
                return super.shouldOverrideUrlLoading(view, url);
            }
        });

        if (paymentUrl != null) {
            webView.loadUrl(paymentUrl);
        }
    }

    private void navigateToSuccessScreen() {
        Intent intent = new Intent(PaymentWebViewActivity.this, PaymentNotification.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("transactionId", transactionId);
        startActivity(intent);
        finish();
    }
}
