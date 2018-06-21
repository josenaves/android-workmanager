package com.example.background.workers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;

import com.example.background.Constants;

import java.io.File;

import androidx.work.Worker;

import static com.example.background.Constants.OUTPUT_PATH;

public class CleanupWorker extends Worker {

    private static final String TAG = CleanupWorker.class.getSimpleName();

    @NonNull
    @Override
    public Result doWork() {
        final Context context = getApplicationContext();

        try {
            File outputDir = new File(context.getFilesDir(), OUTPUT_PATH);
            if (outputDir.exists()) {
                File[] entries = outputDir.listFiles();
                if (entries != null && entries.length > 0) {
                    for (File entry : entries) {
                        String name = entry.getName();
                        if (!TextUtils.isEmpty(name) && name.endsWith(".png")) {
                            boolean deleted = entry.delete();
                            Log.i(TAG, String.format("Deleted %s - %s", name, deleted));
                        }
                    }
                }
            }
            return Result.SUCCESS;
        } catch (Exception e) {
            Log.e(TAG, "Error cleaning up", e);
            return Result.FAILURE;
        }
    }
}
