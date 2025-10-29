package com.example.prm392_cinema;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentWebViewActivity extends AppCompatActivity {

    private WebView paymentWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_web_view);

        paymentWebView = findViewById(R.id.payment_webview);
        paymentWebView.getSettings().setJavaScriptEnabled(true);

        paymentWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d("PaymentWebView", "URL loading: " + url);

                // Intercept the return URL from VNPAY, which might be a localhost URL or a deep link
                if (url.contains("payment-returnURL") || url.startsWith("demozpdk://app")) {
                    Intent returnIntent = new Intent();
                    returnIntent.setData(request.getUrl());

                    String responseCode = request.getUrl().getQueryParameter("vnp_ResponseCode");
                    if ("00".equals(responseCode)) {
                        setResult(RESULT_OK, returnIntent);
                    } else {
                        // Use RESULT_CANCELED for failure or cancellation on the portal
                        setResult(RESULT_CANCELED, returnIntent);
                    }
                    finish(); // Close the WebView
                    return true; // We've handled the URL, don't load it in WebView
                }
                
                // For other URLs, let the WebView load them
                return super.shouldOverrideUrlLoading(view, request);
            }
        });

        String paymentUrl = getIntent().getStringExtra("payment_url");
        if (paymentUrl != null && !paymentUrl.isEmpty()) {
            Log.d("PaymentWebView", "Loading initial payment URL: " + paymentUrl);
            paymentWebView.loadUrl(paymentUrl);
        } else {
            Log.e("PaymentWebView", "Payment URL is null or empty");
            Toast.makeText(this, "Không có URL thanh toán.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        // User pressed back button, treat as cancellation
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        Log.d("PaymentWebView", "Back pressed, setting result to CANCELED.");
        super.onBackPressed();
    }
}
