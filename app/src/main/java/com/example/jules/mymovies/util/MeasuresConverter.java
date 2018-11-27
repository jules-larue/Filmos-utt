package com.example.jules.mymovies.util;

import android.content.res.Resources;
import android.util.TypedValue;

public class MeasuresConverter {

    public static int dpToPx(Resources resources, int dp) {
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics()
        ));
    }
}
