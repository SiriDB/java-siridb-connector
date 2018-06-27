package transceptor.technology;

import java.nio.channels.CompletionHandler;
import java.util.Map;

/**
 *
 * @author Tristan Nottelman
 */
public interface ConnectionInterface {
    public void close();
    public void connect(CompletionHandler handler, Object attachment);
    public void authenticate(CompletionHandler handler, Object attachment);
    public void insert(Map map, CompletionHandler handler, Object attachment);
    public void query(String query, CompletionHandler handler, Object attachment);
    public void query(String query, int timePrecision, CompletionHandler handler, Object attachment);
}
