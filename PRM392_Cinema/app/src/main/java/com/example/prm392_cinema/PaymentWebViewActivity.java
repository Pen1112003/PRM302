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

                if (url.startsWith("demozpdk://app")) {
                    // Payment is complete, return the result URL to the calling activity.
                    Intent returnIntent = new Intent();
                    returnIntent.setData(Uri.parse(url));
                    setResult(RESULT_OK, returnIntent);
                    finish(); // Close the WebView
                    return true; // We've handled the URL
                }
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
        Intent returnIntent = new Intent();
        setResult(RESULT_CANCELED, returnIntent);
        Log.d("PaymentWebView", "Back pressed, setting result to CANCELED.");
        super.onBackPressed();
    }
}
