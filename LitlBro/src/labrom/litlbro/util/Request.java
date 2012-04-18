package labrom.litlbro.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;
import java.nio.CharBuffer;

public abstract class Request {
    
    protected String url;
    protected String result;
    
    
    public void send() throws IOException {
        buildUrl();
        InputStream in = new URL(this.url).openStream();
        readAll(in);
        in.close();
        extractResult();
    }




    /**
     * Build the {@link #url} member variable.
     * @return
     */
    protected abstract void buildUrl();
    
    /**
     * Extract the result. Raw result string is available
     * in member variable {@link #result}.
     */
    protected abstract void extractResult();



    /**
     * Reads the raw request result into the {@link #result} member variable.
     * @param in
     * @throws IOException
     */
    private void readAll(InputStream in) throws IOException {
        Reader r = new InputStreamReader(in, "UTF-8");
        StringWriter w = new StringWriter();
        CharBuffer buff = CharBuffer.allocate(1000);
        while(r.read(buff) >= 0) {
            w.append((CharSequence)buff.flip());
            buff.clear();
        }
        this.result = w.toString();
    }
    
    public String getUrl() {
        return this.url;
    }
    
    public String getResult() {
        return this.result;
    }
    

}
