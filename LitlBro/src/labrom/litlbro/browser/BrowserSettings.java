package labrom.litlbro.browser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;

import labrom.litlbro.L;
import android.util.Log;
import android.webkit.WebSettings;

public class BrowserSettings {
    
    static class Signature {
        
        final String methodName;
        Method method;
        final Object[] args;
        
        Signature(String methodName, Object... args) {
            this.methodName = methodName;
            this.args = args;
        }
        
        void bind() {
            try {
                method = WebSettings.class.getMethod(methodName, boolean.class);
            } catch(Exception e) {
                Log.w(L.TAG, "Method WebSettings." + methodName + " not available");
                
            }
            if(args != null)
            for(int i = 0; i < args.length; i ++) {
                Object arg = args[i];
                if(arg instanceof ReflectLateArg) {
                    ((ReflectLateArg)arg).bind();
                    args[i] = ((ReflectLateArg)arg).getValue();
                }
            }

        }
        
        void invoke(WebSettings target) {
            if(method == null)
                return;
            try {
                method.invoke(target, args);
            } catch(Exception e) {
                Log.w(L.TAG, "Unable to set " + method.getName() + " - " + e.getClass().getSimpleName() + " : " + e.getMessage());
            }
        }
    }
    
    private static boolean reflectDone;
    private static final Collection<Signature> availableReflectMethods = new ArrayList<Signature>();
    private static final Signature[] methods = {
        new Signature("setDomStorageEnabled", Boolean.TRUE),
        new Signature("setAppCacheEnabled", Boolean.TRUE),
        new Signature("setDatabaseEnabled", Boolean.TRUE),
        new Signature("setAllowFileAccess", Boolean.TRUE),
        new Signature("setLoadWithOverviewMode", Boolean.TRUE),
        new Signature("setDisplayZoomControls", Boolean.FALSE),
        new Signature("setPluginState", new PluginStateOnArg())
        };
    
    public static final void configure(WebSettings settings) {
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        settings.setSupportZoom(true);
        settings.setLightTouchEnabled(true);
        settings.setLoadsImagesAutomatically(true);
        settings.setPluginsEnabled(true); // Deprecated but using reflection to set setPluginState(PluginState) is a pain
        settings.setSupportMultipleWindows(false);
        settings.setJavaScriptCanOpenWindowsAutomatically(false);

        initReflect();
        for(Signature s : availableReflectMethods) {
            s.invoke(settings);
        }
    }


    private static final void initReflect() {
        if(reflectDone)
            return;
        
        availableReflectMethods.clear();
        for(Signature s : methods) {
            s.bind();
        }
        reflectDone = true;
    }

}
