package ru.justagod.justacore.gui.overlay;

import javafx.util.Callback;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import ru.justagod.justacore.gui.listener.MouseClickListener;
import ru.justagod.justacore.gui.parent.PanelOverlay;

import java.io.File;
import java.io.FileFilter;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Yuri on 01.10.17.
 */
public class FilesScrollingListOverlay extends PanelOverlay {

    private final FileFilter filter;
    private List<File> entries;

    private List<List<File>> pages = new LinkedList<List<File>>();
    private List<List<FileEntry>> pagesEntries = new LinkedList<List<FileEntry>>();
    private FileEntry currentEntry;
    private int pagesCount = 0;
    private int page = 0;

    private ButtonOverlay prev;
    private ButtonOverlay accept;
    private ButtonOverlay next;

    public FilesScrollingListOverlay(List<File> entries, FileFilter filter, final Callback<File, Void> callback) {
        super(0, 0, 100, 100);
        this.entries = entries;

        this.filter = filter;


        parent.addOverlay(prev = new ButtonOverlay(29, 75, 13, 6, I18n.format("choose.previous"), new Runnable() {
            @Override
            public void run() {
                if (page > 0) setPage(page - 1);

            }
        }));
        parent.addOverlay(accept = new ButtonOverlay(57, 75, 13, 6, I18n.format("choose.next"), new Runnable() {
            @Override
            public void run() {
                if (page < pagesCount - 1) setPage(page + 1);
            }
        }));
        parent.addOverlay(next = new ButtonOverlay(43, 75, 13, 6, I18n.format("choose.accept"), new Runnable() {
            @Override
            public void run() {
                if (currentEntry != null) {
                    callback.call(currentEntry.getFile());
                }
            }
        }));
        createPages();
        createEntries();
        setPage(0);
    }


    private void createEntries() {

        int id = 666666;
        for (List<File> files : pages) {
            int y = 10;
            List<FileEntry> entries = new LinkedList<FileEntry>();
            for (File file : files) {
                FileEntry entry = new FileEntry(24, y, 52, 5, file);
                entries.add(entry);
                y += 6;
            }
            pagesEntries.add(entries);
        }
    }

    private void createPages() {
        int entriesForPage = 10;


        for (int i = 0; i < entries.size(); i++) {
            int page = i / entriesForPage;

            if (pages.size() <= page) pages.add(new LinkedList<File>());
            List<File> pageList = pages.get(page);

            pageList.add(entries.get(i));

        }

        if (pages.size() == 0) {
            pages.add(new LinkedList<File>());
        }
        pagesCount = pages.size();

    }

    private void setPage(int page) {
        removeButtonsForPage();
        this.page = page;
        addButtonsForPage();

        currentEntry = null;


    }

    private void removeButtonsForPage() {
        List<FileEntry> entries = pagesEntries.get(page);

        for (FileEntry entry : entries) {
            parent.removeOverlay(entry);
        }

        if (currentEntry != null) {
            currentEntry.setText((filter.accept(currentEntry.file)?"§6":"§3") + currentEntry.file.getName());
        }
    }

    private void addButtonsForPage() {
        List<FileEntry> entries = pagesEntries.get(page);

        for (FileEntry entry : entries) {
            parent.addOverlay(entry);
        }
    }

    public void destroy() {
        removeButtonsForPage();
        parent.removeOverlay(prev);
        parent.removeOverlay(accept);
        parent.removeOverlay(next);
    }

    private class FileEntry extends CustomButtonOverlay {
        private File file;

        public FileEntry(int x, int y, int width, int height, final File file) {
            super(x, y, width, height, new ResourceLocation("pich", "textures/gui/send_gui_bg.png"), (filter.accept(file)?"§6":"§3") + file.getName(), null);
            this.file = file;

            mouseClickListeners.add(new MouseClickListener() {
                @Override
                public void onClick(double x, double y, ScaledOverlay overlay) {
                    if (currentEntry != null) {
                        currentEntry.setText((filter.accept(currentEntry.file)?"§6":"§3") + currentEntry.file.getName());
                    }
                    if (filter.accept(file)) {
                        currentEntry = FileEntry.this;
                        setText("§4" + file.getName());
                    }
                }
            });
        }






        public File getFile() {
            return file;
        }
    }

}
