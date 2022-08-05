package org.launch.download;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.launch.download.os.OperatingSystem;

@Getter
@RequiredArgsConstructor
public enum  DownloadType {
    MAIN_FILE("client.rsps", "YOUR .JAR FILE", "Game Client", OperatingSystem.ANY),
    MAC_JAVA("java for mac.zip", "OPENJDK FOR MAC URL", "Java for MAC", OperatingSystem.MAC),
    LINUX_JAVA("java for linux.zip", "OPENJDK FOR LINUX URL", "Java for Linux", OperatingSystem.LINUX),
    WINDOWS_JAVA("java for windows.zip", "OPENJDK FOR WINDOWS URL", "Java for Windows", OperatingSystem.WINDOWS);

    private final String fileName, url, fileDescription;
    private final OperatingSystem operatingSystem;
    public long personalSize, remoteSize, personalLastModified, remoteLastModified;
    public boolean repack;
    public static DownloadType getTypeFromFileName(String name) {
        for(DownloadType type : DownloadType.values()) {
            if(type.getFileName().equalsIgnoreCase(name))
                return type;
        }
        return null;
    }
}
