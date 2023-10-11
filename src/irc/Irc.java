/*
 * Irc class : simple implementation of a chat using JAVANAISE
 */

package irc;

import jvn.JvnException;
import jvn.api.JvnLocalServer;
import jvn.api.JvnObject;
import jvn.JvnServerImpl;

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

public class Irc {
	public TextArea text;
	public TextField data;
	Frame frame;
	JvnObject sentence;

	/**
	 * Main method : Create a JVN object named IRC for representing the Chat application
	 **/
	public static void main(String[] argv) {
		try {
			// initialize JVN server
			JvnLocalServer js = JvnServerImpl.jvnGetServer();

			// look up the IRC object in the JVN server if not found, create it, and register it in the JVN server
			JvnObject jo = js.jvnLookupObject("IRC");
			if (jo == null) {
				jo = js.jvnCreateObject(new Sentence());
				// after creation, I have a write-lock on the object
				//jo.jvnUnLock();
				js.jvnRegisterObject("IRC", jo);
			}

			// create the graphical part of the Chat application
			new Irc(jo);
		} catch (Exception e) {
			System.out.println("IRC problem : " + e.getMessage());
			e.printStackTrace();
		}
	}

	/**
	 * IRC Constructor
	 *
	 * @param jo the JVN object representing the Chat
	 **/
	public Irc(JvnObject jo) {
		sentence = jo;
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
		read_button.addActionListener(new ReadListener(this));
		frame.add(read_button);
		Button write_button = new Button("write");
		write_button.addActionListener(new WriteListener(this));
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
			// lock the object in read mode
			irc.sentence.jvnLockRead();

			// invoke the method
			String s = ((Sentence) (irc.sentence.jvnGetSharedObject())).read();

			// unlock the object
			irc.sentence.jvnUnLock();

			// display the read value
			irc.data.setText(s);
			irc.text.append(s + "\n");
		} catch (JvnException je) {
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

			// lock the object in write mode
			irc.sentence.jvnLockWrite();

			// invoke the method
			((Sentence) (irc.sentence.jvnGetSharedObject())).write(s);

			// unlock the object
			irc.sentence.jvnUnLock();
		} catch (JvnException je) {
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
