package com.example.background.workers;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.background.R;

import java.io.FileNotFoundException;

import androidx.work.Worker;

public class BlurWorker extends Worker {

    private static final String TAG = BlurWorker.class.getSimpleName();

    @NonNull
    @Override
    public Result doWork() {
        final Context context = getApplicationContext();

        final Bitmap picture = BitmapFactory.decodeResource(
                context.getResources(),
                R.drawable.test);
        final Bitmap pictureBlur = WorkerUtils.blurBitmap(picture, context);

        try {
            Uri fileUri = WorkerUtils.writeBitmapToFile(context, pictureBlur);
            WorkerUtils.makeStatusNotification("Output is " + fileUri.toString(), context);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error applying blur", e);
            return Result.FAILURE;
        }
        return Result.SUCCESS;
    }
}
