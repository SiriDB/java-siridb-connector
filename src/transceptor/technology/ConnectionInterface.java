package transceptor.technology;

import java.nio.channels.CompletionHandler;
import java.util.Map;

/**
 *
 * @author tristan
 */
public interface ConnectionInterface {
    public void close();
    public void connect(CompletionHandler handler);
    public void insert(Map map, CompletionHandler handler);
    public void query(String query, CompletionHandler handler);
    public void query(String query, int timePrecision, CompletionHandler handler);
}
