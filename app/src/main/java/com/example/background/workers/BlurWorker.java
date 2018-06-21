package com.example.background.workers;

import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.background.Constants;
import com.example.background.R;

import java.io.FileNotFoundException;

import androidx.work.Data;
import androidx.work.Worker;

import static com.example.background.Constants.KEY_IMAGE_URI;

public class BlurWorker extends Worker {

    private static final String TAG = BlurWorker.class.getSimpleName();

    @NonNull
    @Override
    public Result doWork() {
        final String resourceUri = getInputData().getString(KEY_IMAGE_URI, null);
        final Context context = getApplicationContext();

        try {
            if (TextUtils.isEmpty(resourceUri)) {
                Log.e(TAG, "Invalid input uri");
                throw new IllegalArgumentException("Invalid input uri");
            }

            final ContentResolver resolver = context.getContentResolver();

            // create a bitmap
            final Bitmap picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(Uri.parse(resourceUri)));
            final Bitmap pictureBlur = WorkerUtils.blurBitmap(picture, context);

            Uri fileUri = WorkerUtils.writeBitmapToFile(context, pictureBlur);

            setOutputData(new Data.Builder().putString(KEY_IMAGE_URI, fileUri.toString()).build());

            WorkerUtils.makeStatusNotification("Output is " + fileUri.toString(), context);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error applying blur", e);
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }
}
