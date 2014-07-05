package labrom.litlbro.util;

import android.net.Uri;

public class UrlUtil {
    


    public static String getHost(String url) {
        Uri u = Uri.parse(url);
        String host = u.getHost();
        return host;
    }

    public static String getDomain(String url) {
        Uri u = Uri.parse(url);
        String host = u.getHost();
        String[] segs = host.split("\\.");
        if(segs.length > 2) {
            StringBuilder sb = new StringBuilder();
            for(int i  = segs.length - 2; i < segs.length; i ++) {
                if(i > segs.length -2)
                    sb.append('.');
                sb.append(segs[i]);
            }
            return sb.toString();
        }
        return host;
    }
    
    public static String simplifyHost(String host) {
        if(host == null)
            return null;
        String[] segs = host.split("\\.");
        if(segs.length == 3 && "www".equals(segs[0])) {
            return segs[1] + "." + segs[2];
        }
        return host;
    }
    
    public static String shortenUrl(String url) {
        if(url == null)
            return null;
        String u = url;
        if(u.startsWith("http://"))
            u = u.substring("http://".length());
        else if(u.startsWith("https://"))
            u = u.substring("https://".length());
        if(u.startsWith("www."))
            u = u.substring("www.".length());
        return u;
    }

}
