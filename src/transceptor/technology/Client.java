package transceptor.technology;

import java.nio.channels.CompletionHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author tristan
 */
public class Client {

    private final List<Connection> connectionList;
    private final List<Connection> connectionPriorityList;
    private final Random rand;
    private final String[][] hostlist;
    private final String username;
    private final String password;
    private final String dbname;

    public Client(String username, String password, String dbname, String[][] hostlist, boolean keepAlive) {
        connectionList = new ArrayList<>();
        connectionPriorityList = new ArrayList<>();
        rand = new Random();
        this.hostlist = hostlist;
        this.username = username;
        this.password = password;
        this.dbname = dbname;
    }

    Connection randomConnection() {
        List<Connection> l = (connectionPriorityList.stream()
                .filter(conn -> conn.isConnected())
                .collect(Collectors.toList())
                .isEmpty())
                        ? connectionList : connectionPriorityList;
        return l.stream()
                .filter(conn -> conn.isConnected())
                .collect(Collectors.toList())
                .get(rand.nextInt(l.size()));
    }

    public void connect(CompletionHandler handler) {
        for (String[] strings : hostlist) {
            Connection connection = new Connection(username, password, dbname, strings[0], Integer.parseInt(strings[1]));
            connection.connect(handler);
            int priority = Integer.parseInt(strings[2]);
            if (priority == -1) {
                connectionPriorityList.add(connection);
            } else if (!connectionList.contains(connection)) {
                for (int i = 0; i < priority; i++) {
                    connectionList.add(connection);
                }
            }
        }
    }

    public void query(String query, CompletionHandler handler) {
        randomConnection().query(query, handler);
    }

    public void query(String query, int timePrecision, CompletionHandler handler) {
        randomConnection().query(query, timePrecision, handler);
    }

    public void close() {
        connectionList.forEach((connection) -> {
            connection.close();
        });
    }

}
