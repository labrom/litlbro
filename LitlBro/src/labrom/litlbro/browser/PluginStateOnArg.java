package labrom.litlbro.browser;

import labrom.litlbro.L;
import android.util.Log;
import android.webkit.WebSettings;

public class PluginStateOnArg implements ReflectLateArg {
    
    private Object val;

    @Override
    public Object getValue() {
        return val;
    }

    @Override
    public void bind() {
        for(Class<?> c : WebSettings.class.getDeclaredClasses()) {
            if(c.getSimpleName().equals("PluginState") && c.isEnum()) {
                for(Object o : c.getEnumConstants()) {
                    Enum<?> e = (Enum<?>)o;
                    if(e.name().equals("ON")) {
                        Log.d(L.TAG, "Plugin state is ON");
                        val = e;
                        break;
                    }
                }
            }
        }
    }

}
