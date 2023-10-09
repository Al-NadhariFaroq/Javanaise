package jvn;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class JvnObjectData {
    private final int joi;
    private JvnObject jo;
    private final List<String> names;
    private final Hashtable<JvnRemoteServer, State> servers;

    JvnObjectData(int joi, JvnObject jo, String jon, JvnRemoteServer js) {
        this.joi = joi;
        this.jo = jo;
        names = new ArrayList<>();
        names.add(jon);
        servers = new Hashtable<>();
        servers.put(js, State.NL);
    }

    public int getJvnObjectId() {
        return joi;
    }

    public JvnObject getJvnObject() {
        return jo;
    }

    public void setJvnObject(JvnObject jo) {
        this.jo = jo;
    }

    public Boolean containsName(String name) {
        return names.contains(name);
    }

    public void addName(String name) {
        if (!names.contains(name)) {
            names.add(name);
        }
    }

    public State getServerLock(JvnRemoteServer js) {
        return servers.get(js);
    }

    public Boolean containsServer(JvnRemoteServer js) {
        return servers.containsKey(js);
    }

    public void addServer(JvnRemoteServer js) {
        if (!servers.containsKey(js)) {
            servers.put(js, State.NL);
        }
    }

    public void removeServer(JvnRemoteServer js) {
        servers.remove(js);
    }

    public void updateLock(JvnRemoteServer js, State lock) {
        if (servers.containsKey(js)) {
            servers.put(js, lock);
        }
    }

    public JvnRemoteServer findWriteLockServer() {
        for(JvnRemoteServer server : servers.keySet()) {
            if (State.W.equals(servers.get(server))) {
                return server;
            }
        }
        return null;
    }

    public List<JvnRemoteServer> findReadLockServers() {
        List<JvnRemoteServer> list = new ArrayList<>();
        for (JvnRemoteServer server : servers.keySet()) {
            if (servers.get(server).equals(State.R)) {
                list.add(server);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder(joi + " (");
        for (String name : names) {
            txt.append(name).append(", ");
        }
        txt.append("): ").append(servers.size()).append(" servers [");
        for (State state : servers.values()) {
            txt.append(state).append(", ");
        }
        txt.append("]");
        return txt.toString();
    }
}
