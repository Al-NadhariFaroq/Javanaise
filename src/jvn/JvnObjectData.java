package jvn;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class JvnObjectData {
    private final int joi;
    private JvnObject jo;
    private final List<String> names;
    private final Hashtable<JvnRemoteServer, JvnLockState> servers;

    JvnObjectData(int joi, JvnObject jo, String jon, JvnRemoteServer js) {
        this.joi = joi;
        this.jo = jo;
        this.names = new ArrayList<>();
        this.names.add(jon);
        this.servers = new Hashtable<>();
        this.servers.put(js, JvnLockState.W);
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

    public JvnLockState getServerLock(JvnRemoteServer js) {
        return servers.get(js);
    }

    public Boolean containsServer(JvnRemoteServer js) {
        return servers.containsKey(js);
    }

    public void addServer(JvnRemoteServer js) {
        if (!servers.containsKey(js)) {
            servers.put(js, JvnLockState.NL);
        }
    }

    public void removeServer(JvnRemoteServer js) {
        servers.remove(js);
    }

    public void updateLock(JvnRemoteServer js, JvnLockState lock) {
        if (servers.containsKey(js)) {
            servers.put(js, lock);
        }
    }

    public JvnRemoteServer findWriteLockServer() {
        for (JvnRemoteServer server : servers.keySet()) {
            if (JvnLockState.W.equals(servers.get(server))) {
                return server;
            }
        }
        return null;
    }

    public List<JvnRemoteServer> findReadLockServers() {
        List<JvnRemoteServer> list = new ArrayList<>();
        for (JvnRemoteServer server : servers.keySet()) {
            if (servers.get(server).equals(JvnLockState.R)) {
                list.add(server);
            }
        }
        return list;
    }

    @Override
    public String toString() {
        StringBuilder txt = new StringBuilder("joi: " + joi + " (");
        for (String name : names) {
            txt.append(name).append(", ");
        }
        txt.delete(txt.length()-2, txt.length());
        txt.append("): shared with ").append(servers.size()).append(" servers [");
        for (JvnLockState jvnLockState : servers.values()) {
            txt.append(jvnLockState).append(", ");
        }
        txt.delete(txt.length()-2, txt.length());
        txt.append("]");
        return txt.toString();
    }
}
