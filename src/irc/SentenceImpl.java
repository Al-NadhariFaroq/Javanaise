/*
 * Sentence class : used for keeping the text exchanged between users during a chat application
 */

package irc;

import java.io.Serial;

public class SentenceImpl implements Sentence {
	@Serial
	private static final long serialVersionUID = 1L;

	String data;

	public SentenceImpl() {
		data = "";
	}

	public void write(String text) {
		data = text;
	}

	public String read() {
		return data;
	}
}