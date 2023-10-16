/*
 * Irc class : simple implementation of a chat using JAVANAISE
 */

package jvn;

import irc.Sentence;
import irc.SentenceImpl;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Random;

public class Irc {
	public TextArea text;
	public TextField data;
	Frame frame;
	Sentence sentence;
	ReadListener readListener;
	WriteListener writeListener;

	/**
	 * Main method : Create a JVN object named IRC for representing the Chat application
	 **/
	public static void main(String[] argv) {
		try {
			Random rand = new Random();

			// create a proxy to access at a sentence
			Sentence sentence = (Sentence) JvnProxy.newInstance(new SentenceImpl());

			// create the graphical part of the Chat application
			Irc irc = new Irc(sentence);

			for (int i = 0; i < rand.nextInt(10000, 20000); i++) {
				if (rand.nextBoolean()) {
					irc.data.setText(String.valueOf(rand.nextInt()));
					irc.writeListener.actionPerformed(null);
				} else {
					irc.readListener.actionPerformed(null);
				}
				Thread.sleep(rand.nextInt(0, 1000));
			}
		} catch (Exception e) {
			System.out.println("IRC problem : " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * IRC Constructor
	 *
	 * @param sentence the JVN object representing the Chat
	 **/
	public Irc(Sentence sentence) {
		this.sentence = sentence;
		frame = new Frame();
		frame.addWindowListener(new WindowListener());
		frame.setLayout(new GridLayout(1, 1));
		text = new TextArea(10, 60);
		text.setEditable(false);
		text.setForeground(Color.red);
		frame.add(text);
		data = new TextField(40);
		frame.add(data);
		Button read_button = new Button("read");
		readListener = new ReadListener(this);
		read_button.addActionListener(readListener);
		frame.add(read_button);
		Button write_button = new Button("write");
		writeListener = new WriteListener(this);
		write_button.addActionListener(writeListener);
		frame.add(write_button);
		frame.setSize(545, 201);
		text.setBackground(Color.black);
		frame.setVisible(true);
	}
}

/**
 * Internal class to manage user events (read) on the Chat application
 **/
class ReadListener implements ActionListener {
	Irc irc;

	public ReadListener(Irc i) {
		irc = i;
	}

	/**
	 * Management of user events
	 **/
	public void actionPerformed(ActionEvent e) {
		try {
			// invoke the method
			String s = irc.sentence.read();

			// display the read value
			irc.data.setText(s);
			irc.text.append(s + "\n");
		} catch (Exception je) {
			System.out.println("IRC problem : " + je.getMessage());
			je.printStackTrace();
		}
	}
}

/**
 * Internal class to manage user events (write) on the Chat application
 **/
class WriteListener implements ActionListener {
	Irc irc;

	public WriteListener(Irc i) {
		irc = i;
	}

	/**
	 * Management of user events
	 **/
	public void actionPerformed(ActionEvent e) {
		try {
			// get the value to be written from the buffer
			String s = irc.data.getText();

			// invoke the method
			irc.sentence.write(s);
		} catch (Exception je) {
			System.out.println("IRC problem  : " + je.getMessage());
			je.printStackTrace();
		}
	}
}

class WindowListener extends WindowAdapter {

	@Override
	public void windowClosing(WindowEvent we) {
		try {
			JvnServerImpl.jvnGetServer().jvnTerminate();
			System.exit(0);
		} catch (JvnException e) {
			throw new RuntimeException(e);
		}
	}
}
