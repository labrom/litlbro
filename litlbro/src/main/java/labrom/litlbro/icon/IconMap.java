package labrom.litlbro.icon;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import labrom.litlbro.R;

public class IconMap {
    
    
    
    private static final Map<String, Integer> icons;
    
    static {
        Map<String, Integer> tmp = new HashMap<String, Integer>();
        tmp.put("7k7k.com", R.drawable.ic_7k7k_com);
        tmp.put("56.com", R.drawable.ic_56_com);
        tmp.put("163.com", R.drawable.ic_163_com);
        tmp.put("4399.com", R.drawable.ic_4399_com);
        tmp.put("about.com", R.drawable.ic_about_com);
        tmp.put("adobe", R.drawable.ic_adobe);
        tmp.put("amazon", R.drawable.ic_amazon);
        tmp.put("aol", R.drawable.ic_aol);
        tmp.put("apple", R.drawable.ic_apple);
        tmp.put("ask", R.drawable.ic_ask);
        tmp.put("baidu", R.drawable.ic_baidu);
        tmp.put("bbc.co.uk", R.drawable.ic_bbc);
        tmp.put("bing", R.drawable.ic_bing);
        tmp.put("blogspot", R.drawable.ic_blogspot);
        tmp.put("calendar.google", R.drawable.ic_calendar_google);
        tmp.put("docs.google", R.drawable.ic_docs_google);
        tmp.put("duckduckgo", R.drawable.ic_duckduckgo);
        tmp.put("ebay", R.drawable.ic_ebay);
        tmp.put("facebook", R.drawable.ic_facebook);
        tmp.put("flickr", R.drawable.ic_flickr);
        tmp.put("hao123", R.drawable.ic_hao123);
        tmp.put("ifeng", R.drawable.ic_ifeng_com);
        tmp.put("linkedin", R.drawable.ic_linkedin);
        tmp.put("live", R.drawable.ic_live);
        tmp.put("mail.google", R.drawable.ic_mail_google);
        tmp.put("gmail", R.drawable.ic_mail_google);
        tmp.put("maps.google", R.drawable.ic_maps_google);
        tmp.put("microsoft", R.drawable.ic_microsoft);
        tmp.put("mozilla", R.drawable.ic_mozilla);
        tmp.put("msn", R.drawable.ic_msn);
        tmp.put("netflix", R.drawable.ic_netflix);
        tmp.put("orkut", R.drawable.ic_orkut);
        tmp.put("orkut.com.br", R.drawable.ic_orkut);
        tmp.put("paypal", R.drawable.ic_paypal);
        tmp.put("picasaweb", R.drawable.ic_picasa);
        tmp.put("pps.tv", R.drawable.ic_pps_tv);
        tmp.put("qiyi", R.drawable.ic_qiyi);
        tmp.put("qq.com", R.drawable.ic_qq_com);
        tmp.put("reader.google", R.drawable.ic_reader_google);
        tmp.put("sina.com.cn", R.drawable.ic_sina_com_cn);
        tmp.put("skype", R.drawable.ic_skype);
        tmp.put("sogou", R.drawable.ic_sogou);
        tmp.put("sohu", R.drawable.ic_sohu_com);
        tmp.put("soso", R.drawable.ic_soso);
        tmp.put("taobao", R.drawable.ic_taobao);
        tmp.put("tmall", R.drawable.ic_tmall_com);
        tmp.put("tudou", R.drawable.ic_tudou_com);
        tmp.put("tumblr", R.drawable.ic_tumblr);
        tmp.put("twitter", R.drawable.ic_twitter);
        tmp.put("weibo", R.drawable.ic_weibo);
        tmp.put("wikipedia.org", R.drawable.ic_wikipedia);
        tmp.put("windows", R.drawable.ic_windows);
        tmp.put("wordpress", R.drawable.ic_wordpress);
        tmp.put("xunlei", R.drawable.ic_xunlei);
        tmp.put("yahoo.co.jp", R.drawable.ic_yahoo_jp);
        tmp.put("yahoo", R.drawable.ic_yahoo);
        tmp.put("yandex", R.drawable.ic_yandex);
        tmp.put("youku", R.drawable.ic_youku);
        tmp.put("youtube", R.drawable.ic_youtube);
        
        icons = Collections.unmodifiableMap(tmp);
    }
    
    public static int getIconResourceId(String host) {
        if(host == null)
            return 0;
        
        String[] segs = host.split("\\.");
        if(segs.length < 2)
            return 0;
        

        // Try without .com
        boolean com = segs[segs.length - 1].equals("com");
        int lastMeaningfulIndex = segs.length - (com ? 2 : 1);
        for(int i = Math.max(0, lastMeaningfulIndex - 3); i <= lastMeaningfulIndex; i ++) {
            String concat = concatSegments(segs, i, lastMeaningfulIndex);
            Integer resId = icons.get(concat);
            if(resId != null)
                return resId;
        }
        // Try with .com
        if(com) {
            lastMeaningfulIndex ++;
            for(int i = Math.max(0, lastMeaningfulIndex - 3); i <= lastMeaningfulIndex; i ++) {
                String concat = concatSegments(segs, i, lastMeaningfulIndex);
                Integer resId = icons.get(concat);
                if(resId != null)
                    return resId;
            }
        }
        return 0;
    }
    
    private static String concatSegments(String[] segments, int start, int stop) {
        StringBuilder sb = new StringBuilder();
        for(int i = start; i <= stop; i ++)
            sb.append('.').append(segments[i]);
        if(sb.length() > 0)
            sb.deleteCharAt(0);
        return sb.toString();
    }

}
