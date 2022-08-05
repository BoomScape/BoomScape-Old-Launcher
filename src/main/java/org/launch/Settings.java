package org.launch;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import org.launch.download.os.OperatingSystem;

import java.io.File;
import java.io.IOException;
import java.util.Locale;

@Getter
public class Settings {
    private String gameDirectory = System.getProperty("user.home") + "/YOURRSPS Data/";

    public OperatingSystem getOperatingSystem() {
        OperatingSystem operatingSystem;
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        System.out.println("Found OS: " + OS);
        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            operatingSystem = OperatingSystem.MAC;
        } else if (OS.indexOf("win") >= 0) {
            operatingSystem = OperatingSystem.WINDOWS;
        } else if (OS.indexOf("nux") >= 0) {
            operatingSystem = OperatingSystem.LINUX;
        } else {
            operatingSystem = OperatingSystem.ANY;
        }
        return operatingSystem;
    }

    private OperatingSystem operatingSystem = OperatingSystem.WINDOWS;
    @SneakyThrows
    public void setOperatingSystem() {
        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
        System.out.println("Found OS: " + OS);
        if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
            operatingSystem = OperatingSystem.MAC;
        } else if (OS.indexOf("win") >= 0) {
            operatingSystem = OperatingSystem.WINDOWS;
        } else if (OS.indexOf("nux") >= 0) {
            operatingSystem = OperatingSystem.LINUX;
        } else {
            operatingSystem = OperatingSystem.ANY;
        }
    }
    @SneakyThrows
    public void launchGame(String folderName, String executableName) {
        File file = new File(getGameDirectory() + folderName + "/" + executableName);
        if(file.exists()) {
            file.setExecutable(true, false);
            file.setReadable(true, false);
            file.setWritable(true, false);
        }
        String[] command = new String[] {getGameDirectory() + folderName + "/" + executableName, "-jar", getGameDirectory() + "client.rsps"};
        try {
            Runtime.getRuntime().exec(command);
            Thread.sleep(150);
            System.exit(0);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }
}
