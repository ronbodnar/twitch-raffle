/**
 * Copyright (c) 2014 Ron Bodnar.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 */
package org.aeternaly.raffle.ui.impl;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextPane;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.aeternaly.raffle.Main;
import org.aeternaly.raffle.util.Constants;
import org.aeternaly.raffle.util.IOHandler;
import org.aeternaly.raffle.util.ImageConstants;

public class AboutFrame extends JFrame {

	private JPanel headerPanel, contentPanel, footerPanel;
	
	private JTextPane textPane, headerPane;

	private JButton okButton;

	public AboutFrame() {
		setTitle("About");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Main.getImageConstants().getImage(ImageConstants.FAVICON));
		setResizable(false);
		setLayout(new BorderLayout());
		add(getHeader(), BorderLayout.NORTH);
		add(getContent(), BorderLayout.CENTER);
		add(getFooter(), BorderLayout.SOUTH);
		pack();
	}
	
	public JPanel getHeader() {
		StringBuilder content = new StringBuilder();
		content.append("<div align=\"center\" color=\"black\">");
		content.append("Current version: <font color=\"" + Constants.TWITCH_PURPLE + "\">" + Constants.VERSION + "</font>");
		content.append("</div>");
		
		headerPane = new JTextPane();
		headerPane.setOpaque(false);
		headerPane.setEditable(false);
		headerPane.setContentType("text/html");
		headerPane.setText(content.toString());
		
		headerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		headerPanel.add(headerPane);
		
		return headerPanel;
	}
	
	public JPanel getContent() {
		String msg = "<font color=\"black\">~<font color=\"" + Constants.TWITCH_PURPLE + "\">Twitch</font> Raffle is a tool that was designed to help streamers manage raffles or giveaways within their own <font color=\"#6441A5\">Twitch</font> chat without the hassle of dealing with the laggy chat environment that comes along with it.<br /><br />~This tool is a sophisticated and efficient way to conduct these promotions in a way that will be simple to both the streamer and their contestants.";
		
		StringBuilder content = new StringBuilder();
		for (int i = 0; i < msg.length(); i++) {
			if (msg.charAt(i) == '~') {
				content.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
			} else {
				content.append(msg.charAt(i));
			}
		}
		content.append("<br /><br /><div align=\"center\"><font color=\"black\">Developed by</font> <font color=\"" + Constants.TWITCH_PURPLE + "\"><a href=\"http://ieztech.net/~aeterna/\">Ron Bodnar</a></font></div>");
		
		textPane = new JTextPane();
		textPane.setOpaque(false);
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.setText(content.toString());
		textPane.setPreferredSize(new Dimension(400, 200));
		textPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					IOHandler.getInstance().openBrowser(event.getURL().toString());
				}
			}
			
		});
		
		contentPanel = new JPanel();
		contentPanel.add(textPane);
		
		return contentPanel;
	}

	public JPanel getFooter() {
		okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}

		});

		footerPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		footerPanel.add(okButton);

		return footerPanel;
	}
	
	@Override
	public void setVisible(boolean visible) {
		setLocationRelativeTo(Main.getRaffleBot().getRaffleBotUI().getFrame());
		
		super.setVisible(visible);
	}

}