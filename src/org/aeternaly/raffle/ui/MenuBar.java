/**
 * Copyright (c) 2014 Ron Bodnar.
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.aeternaly.raffle.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Box;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.aeternaly.raffle.Main;
import org.aeternaly.raffle.ui.impl.AboutFrame;
import org.aeternaly.raffle.ui.impl.ContactFrame;
import org.aeternaly.raffle.ui.impl.PreferencesFrame;
import org.aeternaly.raffle.util.Constants;
import org.aeternaly.raffle.util.IOHandler;

public class MenuBar extends JMenuBar implements ActionListener {

	private JTextPane connectionTextPane;

	private AboutFrame aboutFrame;

	private ContactFrame contactFrame;

	private PreferencesFrame preferencesFrame;

	private Object[][] menuData = new Object[][] {
	{
	"File", new String[] {
		"Exit"
	}
	}, {
	"Help", new String[] {
	"About", "Donate", "-", "Contact Form"
	}
	}
	};

	public MenuBar() {
		setOpaque(false);

		aboutFrame = new AboutFrame();

		contactFrame = new ContactFrame();

		preferencesFrame = new PreferencesFrame();

		for (Object[] data : menuData) {
			JMenu menu = new JMenu((String) data[0]);
			add(menu);

			String[] menuContents = (String[]) data[1];
			for (String e : menuContents) {
				if (e.equals("-")) {
					menu.addSeparator();
					continue;
				}
				JMenuItem menuItem = new JMenuItem(e);
				menuItem.addActionListener(this);
				menu.add(menuItem);
			}
		}

		connectionTextPane = new JTextPane();
		connectionTextPane.setOpaque(false);
		connectionTextPane.setContentType("text/html");
		connectionTextPane.setText("<div align=\"right\"><font color=\"" + Constants.TWITCH_PURPLE + "\">Connected to <strong>" + Main.getSettings().getChannelName() + "</strong> (<a href=\"#\">change</a>)</font></div>");
		connectionTextPane.setEditable(false);
		connectionTextPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					if (Main.getRaffleBot().getRaffleProtocol().isRaffleActive()) {
						Object[] options = {
							"OK"
						};
						JOptionPane.showOptionDialog(null, "You cannot switch channels while a raffle is running!", "Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
						return;
					}
					preferencesFrame.setVisible(true);
				}
			}

		});

		add(Box.createGlue());
		add(connectionTextPane);
	}

	@Override
	public void actionPerformed(final ActionEvent arg0) {
		String command = arg0.getActionCommand();
		if (command.equals("Exit")) {
			System.exit(0);
		} else if (command.equals("Contact Form")) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					contactFrame.setVisible(true);
				}

			});
		} else if (command.equals("About")) {
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					aboutFrame.setVisible(true);
				}

			});
		} else if (command.equals("Donate")) {
			IOHandler.getInstance().openBrowser("https://www.paypal.com/cgi-bin/webscr?cmd=_s-xclick&hosted_button_id=UME5EXWKE9SSC");
		} else {
			System.out.println("Unrecognized action: " + command);
		}
	}

	public JTextPane getConnectionTextPane() {
		return connectionTextPane;
	}

}