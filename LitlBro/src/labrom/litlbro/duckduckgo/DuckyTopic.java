package labrom.litlbro.duckduckgo;

import java.util.ArrayList;
import java.util.List;

/**
 * See format description at https://duckduckgo.com/api.html
 * 
 * @author Romain Laboisse labrom@gmail.com
 *
 */
public class DuckyTopic {
    
    public enum ResultType {
        ARTICLE,
        CATEGORY,
        DISAMBIGUATION,
        EXCLUSIVE,
        NAME,
        _NOTHING;
        
        public static ResultType fromCode(String code) {
            if(code == null || code.length() == 0)
                return _NOTHING;
            
            for(ResultType t : values()) {
                if(t.name().startsWith(code))
                    return t;
            }
            return _NOTHING;
        }
    }
    
    public ResultType type;
    public final List<DuckyResult> relatedTopics = new ArrayList<DuckyResult>();
    public final List<DuckyResult> results = new ArrayList<DuckyResult>();
    public String heading;
    public String abstractUrl;
    public String abstractSource;

}
