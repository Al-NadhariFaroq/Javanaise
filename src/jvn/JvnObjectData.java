package jvn;

import jvn.api.JvnObject;
import jvn.api.JvnRemoteServer;

import java.util.HashSet;
import java.util.Set;

class JvnObjectData {
	private JvnObject jvnObject;
	private final Set<JvnRemoteServer> servers;
	private final Set<JvnRemoteServer> readServers;
	private JvnRemoteServer writeServer;

	JvnObjectData(JvnObject jo, JvnRemoteServer js) {
		jvnObject = jo;
		servers = new HashSet<>();
		servers.add(js);
		readServers = new HashSet<>();
		writeServer = null;
	}

	JvnObject getJvnObject() {
		return jvnObject;
	}

	void setJvnObject(JvnObject jo) {
		jvnObject = jo;
	}

	Set<JvnRemoteServer> getServers() {
		return servers;
	}

	Set<JvnRemoteServer> getReadServers() {
		return readServers;
	}

	JvnRemoteServer getWriteServer() {
		return writeServer;
	}

	void setWriteServer(JvnRemoteServer js) {
		writeServer = js;
	}
}
