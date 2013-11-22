package backpack.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import backpack.misc.Constants;
import cpw.mods.fml.common.Loader;

public class Version implements Runnable {
    protected static Version instance = null;
    public static String newestVersion = "";
    public static boolean seen = false;

    public static void checkForUpdate() {
        if(instance == null) {
            instance = new Version();
        }
        new Thread(instance).start();
    }

    @Override
    public void run() {
        newestVersion = "";
        int count = 0;

        try {
            while(count < 3) {
                getVersionFromServer();

                if(!newestVersion.isEmpty()) {
                    count = 3;
                } else {
                    count++;
                    Thread.sleep(10000L);
                }
            }
        }
        catch (InterruptedException ie) {}
    }

    protected static void getVersionFromServer() {
        BufferedReader in = null;
        try {
            URL url = new URL("http://www.eydamos.de/minecraft/backpack/versions.txt");
            URLConnection urlConn = url.openConnection();
            in = new BufferedReader(new InputStreamReader(urlConn.getInputStream()));

            String version = "";
            String inputLine;
            String mcversion = Loader.instance().getMinecraftModContainer().getVersion();
            while((inputLine = in.readLine()) != null) {
                if(inputLine.startsWith(mcversion)) {
                    version = inputLine.split(":")[1];
                    break;
                }
            }
            if(!version.isEmpty()) {
                newestVersion = version;
            }
            in.close();
        }
        catch (MalformedURLException e) {}
        catch (IOException ioe) {
            if(in != null) {
                try {
                    in.close();
                }
                catch (IOException e) {}
            }
        }
    }

    public static boolean isOutdated() {
        return newestVersion == "" ? false : !newestVersion.equals(Constants.MOD_VERSION);
    }
}