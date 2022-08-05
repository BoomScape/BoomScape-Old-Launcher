package org.launch;

import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.Getter;
import lombok.SneakyThrows;
import org.launch.download.DownloadManager;
import org.launch.frame.Frame;

import java.io.File;

public class Launch {
    @Getter
    private static Injector injector;

    @Inject
    private Frame frame;
    @Inject
    private DownloadManager downloadManager;

    @Inject
    private Settings settings;
    public static void main(String[] args) {
            injector = Guice.createInjector(new Binder());
            injector.getInstance(Launch.class).start();
    }

    @SneakyThrows
    public void start() {
        settings.setOperatingSystem();
        frame.setVisible(true);
        File dir = new File(settings.getGameDirectory());
        if(!dir.exists())
            dir.mkdirs();

        Thread.sleep(300);
        downloadManager.init();
        switch (settings.getOperatingSystem()) {
            case WINDOWS:
                settings.launchGame("openlogic-openjdk-8u262-b10-win-64/bin", "javaw.exe");
                break;
            case MAC:
                settings.launchGame("openlogic-openjdk-8u262-b10-mac-x64/jdk1.8.0_262.jdk/Contents/Home/bin", "java");
                break;
            case LINUX:
                settings.launchGame("openlogic-openjdk-8u262-b10-linux-64/bin", "java");
                break;
        }
    }
}
