package com.itapp.placeit;

import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import com.getcapacitor.BridgeActivity;

public class MainActivity extends BridgeActivity {
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Android 15+ forces edge-to-edge. Capacitor 6's WebView does not turn the
    // system bar insets into CSS env(safe-area-inset-*), so pad the root view
    // natively: status bar, nav bar, display cutout and keyboard (IME).
    WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

    View root = findViewById(android.R.id.content);
    ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
      Insets bars = windowInsets.getInsets(
          WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
      Insets ime = windowInsets.getInsets(WindowInsetsCompat.Type.ime());
      // The IME inset already includes the nav bar height, so take the larger.
      v.setPadding(bars.left, bars.top, bars.right, Math.max(bars.bottom, ime.bottom));
      return WindowInsetsCompat.CONSUMED;
    });

    applySystemBarIconsSoon();
  }

  @Override
  public void onStart() {
    super.onStart();
    // Disable pinch-to-zoom and double-tap zoom at the WebView level.
    WebSettings settings = this.bridge.getWebView().getSettings();
    settings.setSupportZoom(false);
    settings.setBuiltInZoomControls(false);
    settings.setDisplayZoomControls(false);
    applySystemBarIcons();
  }

  @Override
  public void onResume() {
    super.onResume();
    applySystemBarIconsSoon();
  }

  @Override
  public void onWindowFocusChanged(boolean hasFocus) {
    super.onWindowFocusChanged(hasFocus);
    if (hasFocus) applySystemBarIconsSoon();
  }

  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    applySystemBarIconsSoon();
  }

  // The Capacitor StatusBar plugin applies its own style on the main thread
  // shortly AFTER onCreate/onStart/onResume have run, which used to win and
  // leave dark icons on the dark strip. So apply now and again a moment later.
  private void applySystemBarIconsSoon() {
    applySystemBarIcons();
    View decor = getWindow().getDecorView();
    decor.post(this::applySystemBarIcons);
    decor.postDelayed(this::applySystemBarIcons, 400);
    decor.postDelayed(this::applySystemBarIcons, 1500);
  }

  // Light icons on the dark strip in dark mode, dark icons on white in light mode.
  private void applySystemBarIcons() {
    boolean night = (getResources().getConfiguration().uiMode
        & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;
    WindowInsetsControllerCompat c =
        WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
    c.setAppearanceLightStatusBars(!night);
    c.setAppearanceLightNavigationBars(!night);
  }
}
