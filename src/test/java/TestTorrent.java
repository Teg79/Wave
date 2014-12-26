import com.frostwire.jlibtorrent.LibTorrent;
import com.frostwire.jlibtorrent.Session;
import com.frostwire.jlibtorrent.TorrentAlertAdapter;
import com.frostwire.jlibtorrent.TorrentHandle;
import com.frostwire.jlibtorrent.alerts.BlockDownloadingAlert;
import com.frostwire.jlibtorrent.alerts.BlockFinishedAlert;
import com.frostwire.jlibtorrent.alerts.TorrentFinishedAlert;
import org.junit.Test;

import java.io.File;
import java.util.concurrent.CountDownLatch;

/**
 * Created by teg on 26/12/14.
 */
public class TestTorrent {

    @Test
    public void test() throws Throwable {

        // comment this line for a real application
        String[] args = new String[]{"/Users/teg/Downloads/The_Hunger_Games_2012_1080p_1080p.torrent"};

        File torrentFile = new File(args[0]);

        System.out.println("Using libtorrent version: " + LibTorrent.version());

        final Session s = new Session();

        final TorrentHandle th = s.addTorrent(torrentFile, torrentFile.getParentFile());

        final CountDownLatch signal = new CountDownLatch(1);

        s.addListener(new TorrentAlertAdapter(th) {
            @Override
            public void blockDownloading(BlockDownloadingAlert alert) {
                int p = (int) (th.getStatus().getProgress() * 100);
                System.out.println("Ding Progress: " + p);
            }

            @Override
            public void blockFinished(BlockFinishedAlert alert) {
                int p = (int) (th.getStatus().getProgress() * 100);
                System.out.println("Progress: " + p);
            }

            @Override
            public void torrentFinished(TorrentFinishedAlert alert) {
                System.out.print("Torrent finished");
                signal.countDown();
            }
        });

        signal.await();
    }
}
