package com.github.warren_bank.webmonkey.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.github.warren_bank.webmonkey.util.SaveFileDownloadHelper;

public class SaveFileHelper {

  private static final Map<Integer, SaveFileDownloadHelper.Download> downloads = new HashMap<Integer, SaveFileDownloadHelper.Download>();
  private static int lastRequestCode = 0;

  public static void showFilePicker(Activity activity, SaveFileDownloadHelper.Download download, String fileName) {
    if (Build.VERSION.SDK_INT < 19)
      return;

    int requestCode = lastRequestCode + 1;
    lastRequestCode = requestCode;

    downloads.put(requestCode, download);

    Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType(download.mimeType);
    intent.putExtra(Intent.EXTRA_TITLE, fileName);

    activity.startActivityForResult(intent, requestCode);
  }

  public static boolean onActivityResult(Context context, int requestCode, int resultCode, Intent data) {
    SaveFileDownloadHelper.Download download = downloads.remove(requestCode);

    if (download == null)
      return false;

    if (resultCode == Activity.RESULT_OK) {
      Uri uri = data.getData();

      if (uri == null)
        return false;

      try {
        OutputStream out = context.getContentResolver().openOutputStream(uri);
        out.write(download.buffer);
        out.flush();
        out.close();

        return true;
      }
      catch(Exception e) {
        return false;
      }
    }
    return false;
  }

}
