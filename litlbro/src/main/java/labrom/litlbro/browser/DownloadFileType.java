package labrom.litlbro.browser;

import java.util.Locale;

import labrom.litlbro.R;

/**
 * Created by labrom on 8/23/14.
 */
public enum DownloadFileType {

    PDF(".pdf", R.string.extensionPdf)

    ;

    public String extension;
    public int titleResId;

    DownloadFileType(String extension, int titleResId) {
        this.extension = extension;
        this.titleResId = titleResId;
    }

    public static DownloadFileType fromPath(String path) {
        if (path == null) return null;
        path = path.toLowerCase(Locale.US);
        for (DownloadFileType val : values()) {
            if (path.endsWith(val.extension)) {
                return val;
            }
        }
        return null;
    }
}
