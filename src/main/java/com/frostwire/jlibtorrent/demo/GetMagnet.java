package com.frostwire.jlibtorrent.demo;

import com.frostwire.jlibtorrent.Downloader;
import com.frostwire.jlibtorrent.Entry;
import com.frostwire.jlibtorrent.Session;
import com.frostwire.jlibtorrent.Utils;

import java.io.File;

/**
 * @author gubatron
 * @author aldenml
 */
public final class GetMagnet {

    public static void main(String[] args) throws Throwable {

        String uri = "magnet:?xt=urn:btih:86d0502ead28e495c9e67665340f72aa72fe304e&dn=Frostwire.5.3.6.+%5BWindows%5D&tr=udp%3A%2F%2Ftracker.openbittorrent.com%3A80&tr=udp%3A%2F%2Ftracker.publicbt.com%3A80&tr=udp%3A%2F%2Ftracker.istole.it%3A6969&tr=udp%3A%2F%2Fopen.demonii.com%3A1337";
        File saveDir = new File("/Users/aldenml/Downloads/frostwire_installer.torrent");

        Session s = new Session();
        Downloader d = new Downloader(s);

        System.out.println("Fetching the magnet uri, please wait...");
        byte[] data = d.fetchMagnet(uri, 30000);

        if (data != null) {
            System.out.println(Entry.bdecode(data));

            Utils.writeByteArrayToFile(saveDir, data);
            System.out.println("Torrent data saved to: " + saveDir);
        } else {
            System.out.println("Failed to retrieve the magnet");
        }
    }
}
