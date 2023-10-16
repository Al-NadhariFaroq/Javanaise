package irc;

import java.io.Serializable;

public interface Sentence extends Serializable {

	void write(String text);

	String read();
}
