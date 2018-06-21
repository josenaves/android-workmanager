package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.work.Data;
import androidx.work.Worker;

import static com.example.background.Constants.KEY_IMAGE_URI;

public class SaveImageToFileWorker extends Worker {

    private static final String TAG = SaveImageToFileWorker.class.getSimpleName();

    private static final String TITLE = "Blurred Image";
    private static final SimpleDateFormat DATE_FORMATTER =
            new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm:ss z", Locale.getDefault());

    @NonNull
    @Override
    public Result doWork() {
        final Context context = getApplicationContext();
        ContentResolver resolver = context.getContentResolver();

        try {
            // get the input URI from the data object
            final String resourceUri =  getInputData().getString(KEY_IMAGE_URI, null);
            Bitmap bitmap = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));
            String imageUrl = MediaStore.Images.Media.insertImage(
                    resolver, bitmap, TITLE, DATE_FORMATTER.format(new Date()));
            if (TextUtils.isEmpty(imageUrl)) {
                Log.e(TAG, "Writing to MediaStore failed");
                return Result.FAILURE;
            }
            // create and set the output data object with the imageUri
            final Data output = new Data.Builder()
                    .putString(KEY_IMAGE_URI, imageUrl)
                    .build();
            setOutputData(output);
            return Result.SUCCESS;
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Unable to save image to Gallery", e);
            return Result.FAILURE;
        }
    }
}
