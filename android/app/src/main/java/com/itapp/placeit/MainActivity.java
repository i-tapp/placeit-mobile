package com.itapp.placeit;

import android.os.Bundle;
import android.webkit.WebSettings;
import androidx.core.view.WindowCompat;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    // Android 15 (SDK 35+) forces edge-to-edge drawing with no opt-out, so
    // the WebView now draws under the status bar AND the navigation bar
    // regardless of any app setting. This line is what actually makes the
    // WebView aware of those system bar insets, so env(safe-area-inset-top)
    // and env(safe-area-inset-bottom) in the site's CSS resolve to real
    // values instead of silently falling back to 0 - without it, the
    // status bar overlaps the header and the nav bar overlaps the app's
    // own bottom tab bar, no matter what the web-side CSS says.
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
  }

  @Override
  public void onStart() {
    super.onStart();
    // Disable pinch-to-zoom and double-tap zoom at the WebView level.
    // The viewport meta tag on the web side (maximumScale/userScalable)
    // covers most browsers, but Android's WebView has its own zoom
    // controls that can override the page unless turned off here too.
    WebSettings settings = this.bridge.getWebView().getSettings();
    settings.setSupportZoom(false);
    settings.setBuiltInZoomControls(false);
    settings.setDisplayZoomControls(false);
  }
}

