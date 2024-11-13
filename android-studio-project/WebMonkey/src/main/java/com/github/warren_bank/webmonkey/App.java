package com.github.warren_bank.webmonkey;

import com.github.warren_bank.webmonkey.extras.TLSSocketFactory;

import android.app.Application;
import android.database.CursorWindow;
import android.os.Build;

import java.lang.reflect.Field;
import javax.net.ssl.HttpsURLConnection;

public class App extends Application {
  @Override
  public void onCreate() {
    super.onCreate();

    if (
      (Build.VERSION.SDK_INT >= 16) &&
      (Build.VERSION.SDK_INT <  20)
    ) {
      try {
        TLSSocketFactory socketFactory = new TLSSocketFactory();

        HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
      }
      catch(Exception e) {}
    }

    try {
      Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
      field.setAccessible(true);
      field.set(null, 250 * 1024 * 1024); //250MB
    }
    catch(Exception e) {}

  }
}
