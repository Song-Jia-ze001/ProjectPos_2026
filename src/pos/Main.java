package pos;

import java.io.File;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import pos.domain.Store;
import pos.ui.POSFrame;

/** 程序入口：加载商品目录并打开收银界面。 */
public class Main {
    public static void main(String[] args) {
        final boolean screenshots = args.length > 0 && "--screenshots".equals(args[0]);
        File catalog = locateCatalog();
        final Store store = new Store(catalog);
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
                } catch (Exception ignored) {
                }
                POSFrame frame = new POSFrame(store.getRegister());
                frame.setVisible(true);
                if (screenshots) {
                    frame.captureDemoScreenshots();
                    System.exit(0);
                }
            }
        });
    }

    private static File locateCatalog() {
        File[] candidates = new File[] {
                new File("data/catalog.csv"),
                new File("../data/catalog.csv"),
                new File("ProjectPos_2026/data/catalog.csv")
        };
        for (File f : candidates) {
            if (f.isFile()) {
                return f;
            }
        }
        throw new IllegalStateException("catalog.csv not found. Run from ProjectPos_2026.");
    }
}
