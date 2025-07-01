package ani.example.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

public class MainActivity extends Activity {

    private WebView mWebView;
    private final String mainUrl = "https://info.dtech24.co.za";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mWebView = findViewById(R.id.activity_main_webview);

        WebSettings webSettings = mWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);
        webSettings.setSupportMultipleWindows(false);

        mWebView.addJavascriptInterface(new AdJsInterface(this), "Android");
        mWebView.setWebChromeClient(new WebChromeClient());

        mWebView.setWebViewClient(new WebViewClient() {

            // For Android 6.0+ (API 23+)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                return handleCustomUrlLogic(url);
            }

            // For older Android versions
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return handleCustomUrlLogic(url);
            }

            private boolean handleCustomUrlLogic(String url) {
                if (url.startsWith("https://x24em.github.io/Ani/") ||
                    url.contains("databasegdriveplayer.xyz/player.php")) {
                    return false; // allow WebView to load it
                }

                if (url.startsWith("https://drive.google.com/drive")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    return true;
                }

                Toast.makeText(MainActivity.this, "Blocked navigation attempt.", Toast.LENGTH_SHORT).show();
                return true; // block everything else
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                showOfflineDialog();
            }
        });

        if (isConnected()) {
            mWebView.loadUrl(mainUrl);
        } else {
            showOfflineDialog();
        }

        enterImmersiveMode();
    }

    // JavaScript interface class
    public class AdJsInterface {
        Activity activity;

        AdJsInterface(Activity act) {
            this.activity = act;
        }

        @JavascriptInterface
        public void openAdWindow() {
            activity.runOnUiThread(() -> {
                Intent intent = new Intent(activity, PopupWebViewActivity.class);
                intent.putExtra("url", "https://cafewarriors.com/spymr251ew?key=ce76edf7e5c6e4907177e712dc143365");
                activity.startActivity(intent);
            });
        }
    }

    private void showOfflineDialog() {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("No Internet Connection")
                .setMessage("Please check your connection and try again.")
                .setCancelable(false)
                .setPositiveButton("OK", (dialog, which) -> finish())
                .show();
    }

    private boolean isConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
        } else {
            super.onBackPressed();
        }
    }

    private void enterImmersiveMode() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        );
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            enterImmersiveMode();
        }
    }
                                  }
