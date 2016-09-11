package com.zhouyiran.mytasks.utils;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

/**
 * Created by zhouyiran on 16/9/11.
 */
public class ActivityUtils {

    public static void addFragmentToActivity(@NonNull FragmentManager fragmentManager,
                                      @NonNull Fragment fragment, int fragId) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.add(fragId, fragment);
        transaction.commit();
    }
}
