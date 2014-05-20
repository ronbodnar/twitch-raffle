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
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;

import org.aeternaly.raffle.Main;
import org.aeternaly.raffle.RaffleBot;
import org.aeternaly.raffle.util.Constants;
import org.aeternaly.raffle.util.IOHandler;
import org.aeternaly.raffle.util.ImageConstants;

public class IntroductoryFrame extends JFrame {
	
	private JLabel headerLabel;
	
	private JTextPane textPane;
	
	private JTextField channelField;
	
	private JButton okButton, cancelButton;
	
	private StringBuilder introductionStringBuilder;
	
	private JPanel panel, topPanel, middlePanel, bottomPanel;
	
	public IntroductoryFrame() {
		setTitle("Twitch Raffle");
		setIconImage(Main.getImageConstants().getImage(ImageConstants.FAVICON));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		add(getPanel());
		pack();
		centerWindow();
	}
	
	public JPanel getPanel() {
		panel = new JPanel(new BorderLayout());
		panel.add(getTopPanel(), BorderLayout.NORTH);
		panel.add(getMiddlePanel(), BorderLayout.CENTER);
		panel.add(getBottomPanel(), BorderLayout.SOUTH);
		
		return panel;
	}
	
	public JPanel getTopPanel() {
		headerLabel = new JLabel("Welcome to Twitch Raffle!", JLabel.CENTER);
		headerLabel.setForeground(Color.decode(Constants.TWITCH_PURPLE));
		headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
		headerLabel.setBorder(new EmptyBorder(10, 0, 10, 0));
		
		textPane = new JTextPane();
		textPane.setOpaque(false);
		textPane.setEditable(false);
		textPane.setContentType("text/html");
		textPane.setText(getInstructions());
		textPane.addHyperlinkListener(new HyperlinkListener() {

			@Override
			public void hyperlinkUpdate(HyperlinkEvent event) {
				if (event.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
					IOHandler.getInstance().openBrowser(event.getURL().toString());
				}
			}
			
		});
		
		topPanel = new JPanel(new BorderLayout());
		topPanel.add(headerLabel, BorderLayout.NORTH);
		topPanel.add(textPane, BorderLayout.CENTER);
		
		return topPanel;
	}
	
	public JPanel getMiddlePanel() {
		channelField = new JTextField(10);
		channelField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (channelField == null || channelField.getText().isEmpty() || channelField.getText().length() < 3) {
					Object[] options = {
						"OK"
					};
					JOptionPane.showOptionDialog(null, "You must enter a valid channel name in order to connect to the chat.\n\nMinimum of 3 characters.", "Fatal Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
					return; 
				}
				Main.setChannelName(channelField.getText());
				
				Main.setRaffleBot(new RaffleBot());
				Main.getRaffleBot().getRaffleBotUI().constructFrame();
				
				Main.getSettings().setChannelName(channelField.getText());
				
				IOHandler.getInstance().writeSettings();
				
				dispose();
			}

		});
		
		middlePanel = new JPanel(new GridLayout(0, 2, 10, 10));
		middlePanel.add(new JLabel("<html><div align='center'>Enter your <font color=\"#6441A5\">Twitch</font> channel name:<br /><small>eg: <strong>wingsofdeath</strong> or <strong>nightblue3</strong></small></div></html>"));
		middlePanel.add(channelField);
		middlePanel.setBorder(new EmptyBorder(20, 30, 20, 30));
		
		return middlePanel;
	}
	
	public JPanel getBottomPanel() {
		okButton = new JButton("Ok");
		okButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SwingUtilities.invokeLater(new Runnable() {
					
					@Override
					public void run() {
						if (channelField == null || channelField.getText().isEmpty()) {
							Object[] options = {
								"OK"
							};
							JOptionPane.showOptionDialog(null, "You must enter a valid channel name in order to connect to the chat.", "Fatal Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
							return; 
						}
						Main.setChannelName(channelField.getText());
						
						Main.setRaffleBot(new RaffleBot());
						Main.getRaffleBot().getRaffleBotUI().constructFrame();
						
						Main.getSettings().setChannelName(channelField.getText());
						
						IOHandler.getInstance().writeSettings();
						
						dispose();
					}
					
				});
			}
			
		});
		
		cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
			
		});
		
		bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		bottomPanel.add(okButton);
		bottomPanel.add(cancelButton);
		
		return bottomPanel;
	}
	
	public String getInstructions() {
		introductionStringBuilder = new StringBuilder();
		introductionStringBuilder.append("<div align=\"center\">");
		introductionStringBuilder.append("Before you continue, be sure to check out our <font color=\"#65449b\"><a href=\"http://ieztech.net/~aeterna/twitch-raffle-faq/\">FAQs</a></font> to ensure<br />");
		introductionStringBuilder.append("that you have a more pleasant experience.<br />");
		introductionStringBuilder.append("</div>");
		
		return introductionStringBuilder.toString();
	}
	
	public void centerWindow() {
		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = graphicsDevice.getDisplayMode().getWidth();
		int height = graphicsDevice.getDisplayMode().getHeight();

		int x = (int) ((width - getWidth()) / 2);
		int y = (int) ((height - getHeight()) / 2);
		
		setLocation(x, y);
	}

}