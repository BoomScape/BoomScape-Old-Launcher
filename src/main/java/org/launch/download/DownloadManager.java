package org.launch.download;

import com.google.inject.Inject;
import org.launch.Settings;
import org.launch.download.os.OperatingSystem;
import org.launch.frame.Frame;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class DownloadManager {
    @Inject
    private Settings settings;
    @Inject
    private Frame frame;
    public static boolean NEEDS_REDOWNLOAD = false;

    private static int numberOfUpdates = 0;
    private static int currentlyUpdated = 0;


    public void init() throws IOException {
        System.out.println("DownloadManager#init for OS " + settings.getOperatingSystem());
        loadAllData();
        for(DownloadType cacheDownloadType : DownloadType.values()) {
            if(cacheDownloadType.getOperatingSystem() == settings.getOperatingSystem() || cacheDownloadType.getOperatingSystem() == OperatingSystem.ANY) {
                System.out.println("Name: " + cacheDownloadType.getFileName());
                download(cacheDownloadType);
            }
        }
        frame.setTask("Verifying files..");
        for(DownloadType cacheDownloadType : DownloadType.values()) {
            if(cacheDownloadType.getOperatingSystem() == settings.getOperatingSystem() || cacheDownloadType.getOperatingSystem() == OperatingSystem.ANY) {
                System.out.println("Name: " + cacheDownloadType.getFileName());
                download(cacheDownloadType);
            }
        }
    }

    public void loadAllData() throws MalformedURLException {
        loadSizes();
        for(DownloadType cacheDownloadType : DownloadType.values()) {
            cacheDownloadType.remoteSize = getFileSize(new URL(cacheDownloadType.getUrl()));
            cacheDownloadType.remoteLastModified = getLastModified(new URL(cacheDownloadType.getUrl()));
            if(needsUpdate(cacheDownloadType))
                numberOfUpdates++;
        }
    }

    /**
     * Starts the initial download process
     * @param type
     */
    public void download(DownloadType type) {
        if(!needsUpdate(type)) {
            return;
        }

        try {
            download(type.getFileName(), type.getUrl());

            type.personalSize = type.remoteSize;
            type.personalLastModified = type.remoteLastModified;

            NEEDS_REDOWNLOAD = false;

           /* if(type == DownloadType.OSRS_MAPS || type == DownloadType.REG_MAPS || type == DownloadType.REG_MODELS) {
                type.repack = true;
            }*/

            saveSizes();

            currentlyUpdated++;
            return;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return;
    }

    public boolean needsUpdate(DownloadType type) {
        if(NEEDS_REDOWNLOAD) {
            return true;
        }
        if(!new File(settings.getGameDirectory() + type.getFileName()).exists() && !type.getFileName().contains("zip"))
            return true;

        return (((type.remoteSize != type.personalSize) || (type.remoteLastModified != type.personalLastModified)));
    }
    public double calculatePercentage(double currentlyDownloaded, double totalUpdates) {
        return currentlyDownloaded * 100 / totalUpdates;
    }
    /**
     * Downloads the cache
     * @param fileName
     * @param downloadUrl
     * @throws IOException
     */
    public void download(String fileName, String downloadUrl) throws IOException {
        URL url = new URL(downloadUrl);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        httpConn.addRequestProperty("User-Agent", "Mozilla/4.76");
        int responseCode = httpConn.getResponseCode();

        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = settings.getGameDirectory() + File.separator + fileName;

            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);

            int bytesRead = -1;
            byte[] buffer = new byte[4096];
            long startTime = System.currentTimeMillis();
            int downloaded = 0;
            long numWritten = 0;
            int length = httpConn.getContentLength();
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                numWritten += bytesRead;
                downloaded += bytesRead;
                int percentage = (int)(((double)numWritten / (double)length) * 100D);
                @SuppressWarnings("unused")
                int downloadSpeed = (int) ((downloaded / 1024) / (1 + ((System.currentTimeMillis() - startTime) / 1000)));
                frame.getProgressBar().setValue(percentage);
                frame.setTask("Downloading "+DownloadType.getTypeFromFileName(fileName.toLowerCase()).getFileDescription()+" ("+downloadSpeed+"kb/s): "+percentage+"%");
            }

            outputStream.close();
            inputStream.close();

        } else {
            System.out.println(downloadUrl + " returned code + " + responseCode);
        }
        httpConn.disconnect();
        if(fileName.contains("zip"))
            unZip(fileName);
    }

    /**
     * Starts the intiial file unzipping process
     * @param fileName
     */
    public void unZip(String fileName) {
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(settings.getGameDirectory() + fileName));
            ZipInputStream zin = new ZipInputStream(in);
            ZipEntry e;
            while ((e = zin.getNextEntry()) != null) {
                if (e.isDirectory()) {
                    (new File(settings.getGameDirectory() + e.getName())).mkdir();
                } else {
                    if (e.getName().equals(settings.getGameDirectory() + fileName)) {
                        unzip(zin, settings.getGameDirectory() + fileName);
                        break;
                    }
                    unzip(zin, settings.getGameDirectory() + e.getName());
                }
            }
            zin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Actually unzips the file...
     * @param zin
     * @param s
     * @throws IOException
     */
    public void unzip(ZipInputStream zin, String s) throws IOException {
        FileOutputStream out = new FileOutputStream(s);
        byte[] b = new byte[1024];
        int len = 0;
        while ((len = zin.read(b)) != -1) {
            out.write(b, 0, len);
        }
        out.close();
    }

    public long getFileSize(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getContentLengthLong();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public long getLastModified(URL url) {
        HttpURLConnection conn = null;
        try {
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("HEAD");
            return conn.getLastModified();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }
    }

    public void saveSizes() {
        try {
            PrintWriter writer = new PrintWriter(settings.getGameDirectory() + "versions", "UTF-8");
            for(DownloadType cacheDownloadType : DownloadType.values()) {
                writer.println(cacheDownloadType.toString()+":"+cacheDownloadType.personalSize+":size");
                writer.println(cacheDownloadType.toString()+":"+cacheDownloadType.personalLastModified+":modified");
            }
            writer.println(DownloadType.MAIN_FILE.toString()+":redownload:"+NEEDS_REDOWNLOAD+"");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadSizes() {
        File file = new File(settings.getGameDirectory() + "versions");
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch(FileNotFoundException e) {
            return;
        }
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                String[] args = line.split(":");
                if (args.length <= 1)
                    continue;
                String token = args[0], value = args[1], type = args[2];
                if(token.contains("USER_OPTION"))
                    continue;
                DownloadType cacheType = DownloadType.valueOf(token);


                switch(type) {
                    case "size":
                        long size = Long.parseLong(value);
                        cacheType.personalSize = size;
                        break;
                    case "modified":
                        long modified = Long.parseLong(value);
                        cacheType.personalLastModified = modified;
                        break;
                    case "redownload":
                        NEEDS_REDOWNLOAD = Boolean.parseBoolean(value);
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

