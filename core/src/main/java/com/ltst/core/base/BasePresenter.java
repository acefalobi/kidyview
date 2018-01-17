package com.ltst.core.base;

import android.os.Bundle;
import android.support.annotation.NonNull;

public interface BasePresenter {

    void firstStart();

    void start();

    void stop();

    void onRestore(@NonNull Bundle savedInstanceState);

    void onSave(@NonNull Bundle outState);

}
