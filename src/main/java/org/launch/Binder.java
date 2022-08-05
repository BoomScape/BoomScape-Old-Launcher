package org.launch;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import org.launch.download.DownloadManager;
import org.launch.frame.Frame;

import javax.swing.*;

public class Binder extends AbstractModule {

    @Override
    protected void configure() {
    bind(DownloadManager.class);
    }
    @Singleton
    @Provides
    Settings settingsProvider() {
        return new Settings();
    }
    @Singleton
    @Provides
    Frame frameProvider() {
        return new Frame();
    }
}
