package com.github.warren_bank.webmonkey.util;

import at.pardus.android.webview.gm.store.ScriptStoreSQLite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class BackupRestoreHelper {

  private static int backupRequestCode  = 1;
  private static int restoreRequestCode = 2;

  public static void chooseBackupFile(Activity activity, String fileName) {
    chooseFile(activity, fileName, backupRequestCode, Intent.ACTION_CREATE_DOCUMENT);
  }

  public static void chooseRestoreFile(Activity activity, String fileName) {
    chooseFile(activity, fileName, restoreRequestCode, Intent.ACTION_OPEN_DOCUMENT);
  }

  private static void chooseFile(Activity activity, String fileName, int requestCode, String action) {
    if (Build.VERSION.SDK_INT < 19)
      return;

    Intent intent = new Intent(action);
    intent.addCategory(Intent.CATEGORY_OPENABLE);
    intent.setType("application/octet-stream");
    intent.putExtra(Intent.EXTRA_TITLE, fileName);

    activity.startActivityForResult(intent, requestCode);
  }

  public static boolean onActivityResult(Context context, ScriptStoreSQLite scriptStore, int requestCode, int resultCode, Intent data) {
    boolean handled = false;

    if (requestCode == backupRequestCode) {
      handled = true;

      if ((resultCode == Activity.RESULT_OK) && (data != null)) {
        Uri uri = data.getData();

        if (uri != null) {
          boolean OK = false;

          if ((context != null) && (scriptStore != null)) {
            OK = doBackup(context, scriptStore, uri);
          }

          if (!OK && (context != null)) {
            // remove new file for failed backup
            context.getContentResolver().delete(uri, null, null);
          }
        }
      }
    }

    if (requestCode == restoreRequestCode) {
      handled = true;

      if ((resultCode == Activity.RESULT_OK) && (data != null)) {
        Uri uri = data.getData();

        if (uri != null) {
          boolean OK = false;

          if ((context != null) && (scriptStore != null)) {
            OK = doRestore(context, scriptStore, uri);
          }
        }
      }
    }

    return handled;
  }

  private static boolean doBackup(Context context, ScriptStoreSQLite scriptStore, Uri uri) {
    boolean OK = false;

    try {
      scriptStore.open();
      String filepath = scriptStore.getDbPath();
      if (filepath == null) throw new Exception();
      scriptStore.close();

      InputStream  in  = new FileInputStream(filepath);
      OutputStream out = context.getContentResolver().openOutputStream(uri);
      OK = copy(in, out);
    }
    catch(Exception e) {
      OK = false;
    }
    finally {
      scriptStore.open();
    }
    return OK;
  }

  private static boolean doRestore(Context context, ScriptStoreSQLite scriptStore, Uri uri) {
    boolean OK = false;

    try {
      scriptStore.open();
      String filepath = scriptStore.getDbPath();
      if (filepath == null) throw new Exception();
      scriptStore.close();

      InputStream  in  = context.getContentResolver().openInputStream(uri);
      OutputStream out = new FileOutputStream(filepath);
      OK = copy(in, out);
    }
    catch(Exception e) {
      OK = false;
    }
    finally {
      scriptStore.open();
    }
    return OK;
  }

  private static boolean copy(InputStream in, OutputStream out) {
    boolean OK = false;
    byte[] buffer = new byte[1024];
    int bytesRead;

    try {
      while((bytesRead = in.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
        out.flush();
      }
      OK = true;
    }
    catch(Exception e) {
      OK = false;
    }
    try {  in.close(); } catch(Exception e) {}
    try { out.close(); } catch(Exception e) {}
    return OK;
  }

}
