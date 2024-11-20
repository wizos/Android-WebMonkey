package at.pardus.android.webview.gm.util;

import android.content.Context;
import android.util.Base64;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class CacheFileHelper {

  // multiple of 3, so base64 encoded string doesn't require padding
  public static final int READ_BYTES_PER_CHUNK = 1050;

  private static final String FILENAME_PREFIX   = "download_";
  private static final String FILENAME_SUFFIX   = ".tmp";

  private static String getFileName(String UUID) {
    return FILENAME_PREFIX + UUID + FILENAME_SUFFIX;
  }

  private static File getFile(Context context, String UUID) {
    String fileName = getFileName(UUID);
    File file = new File(context.getCacheDir(), fileName);
    return file;
  }

  public static boolean write(Context context, String UUID, String chunkBase64) {
    return write(context, UUID, Base64.decode(chunkBase64, Base64.DEFAULT));
  }

  public static boolean write(Context context, String UUID, byte[] buffer) {
    return write(context, UUID, buffer, 0, buffer.length);
  }

  public static boolean write(Context context, String UUID, byte[] buffer, int offset, int length) {
    File file = getFile(context, UUID);
    boolean OK = false;

    try {
      FileOutputStream fos = new FileOutputStream(file, true);
      fos.write(buffer, offset, length);
      fos.close();
      OK = true;
    }
    catch(Exception e) {
      OK = false;
    }

    return OK;
  }

  public static String read(Context context, String UUID, long byteOffset) {
    File file = getFile(context, UUID);
    String chunkBase64 = null;

    try {
      FileInputStream fis = new FileInputStream(file);
      long skipped = fis.skip(byteOffset);
      if (skipped > byteOffset) throw new Exception();
      int remaining = (int) (byteOffset - skipped);
      byte[] buffer = new byte[READ_BYTES_PER_CHUNK + remaining];
      int bytesRead = fis.read(buffer);
      if (bytesRead != -1) {
        chunkBase64 = Base64.encodeToString(buffer, remaining, (bytesRead - remaining), (Base64.DEFAULT | Base64.NO_WRAP));
      }
    }
    catch(Exception e) {
      chunkBase64 = null;
    }

    return chunkBase64;
  }

  public static boolean save(Context context, String UUID, OutputStream out) {
    File file = getFile(context, UUID);
    boolean OK = false;
    int bytesRead;

    try {
      FileInputStream fis = new FileInputStream(file);
      byte[] buffer = new byte[1024];

      while((bytesRead = fis.read(buffer)) != -1) {
        out.write(buffer, 0, bytesRead);
        out.flush();
      }

      out.close();
      OK = true;
    }
    catch(Exception e) {
      OK = false;
    }

    if (!OK) {
      try {
        out.close();
      }
      catch(Exception e) {}
    }

    return OK;
  }

  public static boolean delete(Context context, String UUID) {
    File file = getFile(context, UUID);
    boolean OK = false;

    try {
      OK = file.delete();
    }
    catch(Exception e) {
      OK = false;
    }

    return OK;
  }

  public static void deleteAll(Context context) {
    File[] downloads = null;

    try {
      downloads = context.getCacheDir().listFiles(new FilenameFilter() {
        public boolean accept(File dir, String name) {
          return (name.startsWith(FILENAME_PREFIX) && name.endsWith(FILENAME_SUFFIX));
        }
      });
    }
    catch(Exception e) {
      downloads = null;
    }

    if (downloads != null) {
      for (File download : downloads) {
        try {
          download.delete();
        }
        catch(Exception e) {}
      }
    }
  }

}
