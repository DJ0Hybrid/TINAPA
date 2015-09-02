package com.tinapaproject.tinapa;

import android.app.Application;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

public class TinapaApplication extends Application {
    public static Bus bus = new Bus(ThreadEnforcer.MAIN);


}
