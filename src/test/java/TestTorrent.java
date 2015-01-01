import com.frostwire.jlibtorrent.*;
import com.frostwire.jlibtorrent.alerts.BlockFinishedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentAddedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentFinishedAlert;
import org.junit.Test;
import org.slf4j.*;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Created by teg on 26/12/14.
 */
public class TestTorrent {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger("test");

    @Test
    public void test() throws Throwable {
        LOGGER.info("Using libtorrent version: " + LibTorrent.version());

        String uri = "magnet:?xt=urn:btih:86d0502ead28e495c9e67665340f72aa72fe304e";
        File torrentFile = new File("./src/test/resources/frostwire_installer.torrent");
        if (torrentFile.exists()) {
            torrentFile.delete();
        }

        Session s = new Session();
        Downloader d = new Downloader(s);
        DHT dht = new DHT(s);

        LOGGER.info("Waiting for nodes in DHT");
        dht.waitNodes(1);
        LOGGER.info("Nodes in DHT: " + dht.nodes());

        LOGGER.info("Fetching the magnet uri, please wait...");
        byte[] data = d.fetchMagnet(uri, 30000);

        if (data != null) {
            LOGGER.info(Entry.bdecode(data).toString());

            Utils.writeByteArrayToFile(torrentFile, data);
            LOGGER.info("Torrent data saved to: " + torrentFile);
        } else {
            LOGGER.info("Failed to retrieve the magnet");
        }


        File parentFile = torrentFile.getParentFile();
        s.asyncAddTorrent(torrentFile, parentFile);
        final TorrentHandle th = s.getTorrents().get(0);

        final CountDownLatch signal = new CountDownLatch(1);

        s.addListener(new TorrentAlertAdapter(th) {

            @Override
            public void blockFinished(BlockFinishedAlert alert) {
                int p = (int) (th.getStatus().getProgress() * 100);
                LOGGER.info("Progress: " + p);
            }

            @Override
            public void torrentFinished(TorrentFinishedAlert alert) {
                LOGGER.info("Torrent finished");
                signal.countDown();
            }

            @Override
            public void torrentAdded(TorrentAddedAlert alert) {
                LOGGER.info("Torrent added");
                signal.countDown();
            }
        });

        File targetFile = new File(parentFile, th.getTorrentInfo().getName());
        if (targetFile.exists()) {
            targetFile.delete();
        }
        LOGGER.info("Nome torrent: " + th.getTorrentInfo().getName());
        th.resume();

        signal.await();
    }
}
