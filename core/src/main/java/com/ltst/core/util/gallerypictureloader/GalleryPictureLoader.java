package com.ltst.core.util.gallerypictureloader;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.ImageColumns;
import android.provider.MediaStore.Images.Media;
import android.support.annotation.WorkerThread;

import com.ltst.core.R;
import com.ltst.core.util.FilePathUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import timber.log.Timber;

/**
 * http://stackoverflow.com/a/21488062/1852397
 */
public class GalleryPictureLoader {

    public static final int MAX_PHOTO_SIZE = 1024;

    private static final String CATALOG_NAME = "yotask";
    private static final String CATALOG_PATH = Environment.DIRECTORY_PICTURES + "/" + CATALOG_NAME + "/";
    private static final String TEMP_CATALOG_NAME = "tmp";
    private static final String TEMP_CATALOG_PATH = Environment.getExternalStorageDirectory() + "/" + TEMP_CATALOG_NAME + "/";
    private static final String TEMP_FORMAT_FILE_NAME = "yyyyMMddHHmmss";

    private final Context context;
    private final ContentResolver resolver;
    private Uri uri;
    private String path;
    private Matrix orientation;
    int storedHeight;
    int storedWidth;

    private Uri lastFileImage;

    public GalleryPictureLoader(Context context) {
        this.context = context;
        this.resolver = context.getContentResolver();
    }

    private boolean getInformation() throws IOException {
        if (getInformationFromMediaDatabase())
            return true;

        if (getInformationFromFileSystem())
            return true;

        return false;
    }

    /* Support for gallery apps and remote ("picasa") images */
    private boolean getInformationFromMediaDatabase() {
        String[] fields = {Media.DATA, ImageColumns.ORIENTATION};
        Cursor cursor = resolver.query(uri, fields, null, null, null);

        if (cursor == null)
            return false;

        cursor.moveToFirst();
        path = cursor.getString(cursor.getColumnIndex(Media.DATA));
        int orientation = cursor.getInt(cursor.getColumnIndex(ImageColumns.ORIENTATION));
        this.orientation = new Matrix();
//        this.orientation.setRotate(orientation);
        cursor.close();

        return true;
    }

    /* Support for file managers and dropbox */
    private boolean getInformationFromFileSystem() throws IOException {
        path = uri.getPath();

        if (path == null)
            return false;

        ExifInterface exif = new ExifInterface(path);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        this.orientation = new Matrix();
        switch (orientation) {
            case ExifInterface.ORIENTATION_NORMAL:
                /* Identity matrix */
                break;
            case ExifInterface.ORIENTATION_FLIP_HORIZONTAL:
                this.orientation.setScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                this.orientation.setRotate(180);
                break;
            case ExifInterface.ORIENTATION_FLIP_VERTICAL:
                this.orientation.setScale(1, -1);
                break;
            case ExifInterface.ORIENTATION_TRANSPOSE:
                this.orientation.setRotate(90);
                this.orientation.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                this.orientation.setRotate(90);
                break;
            case ExifInterface.ORIENTATION_TRANSVERSE:
                this.orientation.setRotate(-90);
                this.orientation.postScale(-1, 1);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                this.orientation.setRotate(-90);
                break;
        }

        return true;
    }

    private boolean getStoredDimensions() throws IOException {
        InputStream input = resolver.openInputStream(uri);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);

        /* The input stream could be reset instead of closed and reopened if it were possible
           to reliably wrap the input stream on a buffered stream, but it's not possible because
           decodeStream() places an upper read limit of 1024 bytes for a reset to be made (it calls
           mark(1024) on the stream). */
        input.close();

        if (options.outHeight <= 0 || options.outWidth <= 0)
            return false;

        storedHeight = options.outHeight;
        storedWidth = options.outWidth;

        return true;
    }

    @WorkerThread public Bitmap getBitmap(Uri uri, int maxSize) throws IOException {
        return getBitmap(uri, maxSize, true);
    }

    @WorkerThread
    private Bitmap getBitmap(Uri uri, int maxSize, boolean needRotate) throws IOException {
        this.uri = uri;
        if (!getInformation())
            throw new FileNotFoundException();

        if (!getStoredDimensions())
            throw new InvalidObjectException(null);

        RectF rect = new RectF(0, 0, storedWidth, storedHeight);
        orientation.mapRect(rect);
        int width = (int) rect.width();
        int height = (int) rect.height();
        int subSample = 1;

        while (width > maxSize || height > maxSize) {
            width /= 2;
            height /= 2;
            subSample *= 2;
        }

        if (width == 0 || height == 0)
            throw new InvalidObjectException(null);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = subSample;
        Bitmap subSampled = BitmapFactory.decodeStream(resolver.openInputStream(uri), null, options);
        int rotationOfPhoto = new ImageHeaderParser(resolver.openInputStream(uri)).getOrientation();
        int angle;
        switch (rotationOfPhoto) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                angle = 90;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                angle = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                angle = 270;
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                angle = 0;
                break;
        }
        if (needRotate) {
            orientation.postRotate(angle);
        }
        Bitmap picture;
        if (!orientation.isIdentity()) {
            picture = Bitmap.createBitmap(subSampled, 0, 0, options.outWidth, options.outHeight, orientation, false);
            subSampled.recycle();
        } else
            picture = subSampled;

        return picture;
    }

    public File getFileWithRotate(Intent data, int maxSize) {
        try {
            Bitmap bitmap = getBitmap(data.getData(), GalleryPictureLoader.MAX_PHOTO_SIZE, true);
            File file = FilePathUtil.getCacheDir(context);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public File getFile(Intent data) {
        try {
            Bitmap bitmap = getBitmap(data.getData(), GalleryPictureLoader.MAX_PHOTO_SIZE, false);
            File file = FilePathUtil.getCacheDir(context);
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            return file;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    private boolean clearTmpCatalog() {
        File storageDir = context.getExternalFilesDir(TEMP_CATALOG_PATH);
        if (storageDir == null || !storageDir.exists()) return false;
        return storageDir.delete();
    }

    @WorkerThread
    public File createTempImageFile() throws IOException {
        // Create an image file name

        clearTmpCatalog();

        String timeStamp = new SimpleDateFormat(TEMP_FORMAT_FILE_NAME).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/yotask/");
        File storageDir = context.getExternalFilesDir(TEMP_CATALOG_PATH);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        lastFileImage = Uri.fromFile(image);

        Timber.d("CreateImageFile: lastFileImage = " + lastFileImage);

        // Save a file: ic_path for use with ACTION_VIEW intents
//        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

//    @WorkerThread
//    public File createImageFile() throws IOException {
//        // Create an image file name
//        String timeStamp = new SimpleDateFormat(TEMP_FORMAT_FILE_NAME).format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
////        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES + "/yotask/");
//        File storageDir = context.getExternalFilesDir(TEMP_CATALOG_PATH);
//        // TODO remove all previous files;
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );
//
//        lastFileImage = Uri.fromFile(image);
//
//        Timber.d("CreateImageFile: lastFileImage = " + lastFileImage);
//
//        // Save a file: ic_path for use with ACTION_VIEW intents
////        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
//        return image;
//    }

    @WorkerThread
    public File saveImageToGallery(Uri uri) throws IOException {
        int size = context.getResources().getInteger(R.integer.message_photo_size);
        Bitmap bitmap = getBitmap(uri, size, true);
        return saveImageToGallery(bitmap);
    }

    @WorkerThread
    public File saveImageToGallery(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat(TEMP_FORMAT_FILE_NAME).format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return saveImageToGallery(bitmap, imageFileName);
    }

    @WorkerThread
    public File saveImageToGallery(Bitmap bitmap, String imageFileName) {

        try {
            File storageDir = context.getExternalFilesDir(CATALOG_PATH);
            boolean isCreated = storageDir.mkdirs();
            File image = new File(storageDir, imageFileName + ".jpg");
            FileOutputStream outStream = new FileOutputStream(image);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();

            // TODO Provide test to see how it'll work on different versions of Android;
            // For gallery on Xaiomi devices;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                Uri contentUri = Uri.fromFile(storageDir);
                mediaScanIntent.setData(contentUri);
                context.sendBroadcast(mediaScanIntent);
            } else {
                context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.fromFile(storageDir)));
            }

            // For gallery and Google Photos on other devices;
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, imageFileName);
            values.put(MediaStore.Images.Media.DESCRIPTION, "Image from YoTask");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, image.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, image.getName().toLowerCase(Locale.US));
            values.put("_data", image.getAbsolutePath());

            ContentResolver cr = context.getContentResolver();
            cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

            return image;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Uri getLastFileImage() {
        return lastFileImage;
    }
}
