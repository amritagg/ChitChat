package com.amrit.practice.chitchat.Loader;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;

public class ImageLoader extends AsyncTaskLoader<ArrayList<String>> {

    private final Context context;
    private final String LOG_TAG = ImageLoader.class.getSimpleName();

    public ImageLoader(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Nullable
    @Override
    public ArrayList<String> loadInBackground() {
        Log.e(LOG_TAG, "background started");

        ArrayList<String> list = new ArrayList<>();

        // details required for imagefile
        String[] projection = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATE_TAKEN,
                MediaStore.Images.Media.RELATIVE_PATH
        };

        // staring the cursor
        try (Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                MediaStore.Images.Media.DATE_TAKEN + " DESC"
        )) {
            assert cursor != null;
            int idColumn = cursor.getColumnIndex(MediaStore.Images.Media._ID);

            while (cursor.moveToNext()) {
                long id = cursor.getLong(idColumn);
                Uri contentUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id);

                // adding the details in list
                list.add(contentUri.toString());
            }
        } catch (Exception e) {
            Log.e(LOG_TAG, "Can't Load Files");
        }

        Log.e(LOG_TAG, "Back process Done");

        return list;
    }
}
