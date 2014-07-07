package labrom.litlbro.browser;

public interface PageLoadController {
    
    
    /**
     * Can be invoked many times consecutively.
     */
    void restart();
    
    boolean isRestarting();

}
