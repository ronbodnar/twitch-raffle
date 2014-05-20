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
package org.aeternaly.raffle.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.font.TextAttribute;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import org.aeternaly.raffle.Main;
import org.aeternaly.raffle.RaffleBot;
import org.aeternaly.raffle.RaffleProtocol;
import org.aeternaly.raffle.ui.impl.RaffleKeyFrame;
import org.aeternaly.raffle.util.Constants;
import org.aeternaly.raffle.util.IOHandler;
import org.aeternaly.raffle.util.ImageConstants;

public class RaffleBotUI {

	private JFrame frame;

	private JPanel panel, userPanel, optionPanel, contestantPanel;

	private JLabel raffleKeyLabel, headerLabel, winnerLabel, announceLabel, followerLabel, contestantCountLabel, messageWinnerLabel;

	private JButton keyButton, startButton, selectButton, clearButton, refreshButton;

	private JCheckBox announceCheckBox, followerCheckBox;

	private JScrollPane contestantListPane;

	private DefaultListModel<String> listModel;

	private JList<String> contestantList;

	private String winner;

	private RaffleKeyFrame raffleKeyFrame;
	
	private RaffleBot raffleBot;
	
	private RaffleProtocol raffleProtocol;
	
	private MenuBar menuBar;

	public void constructFrame() {
		this.menuBar = new MenuBar();
		this.raffleBot = Main.getRaffleBot();
		this.raffleProtocol = raffleBot.getRaffleProtocol();

		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				frame = new JFrame("Twitch Raffle");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setIconImage(Main.getImageConstants().getImage(ImageConstants.FAVICON));
				frame.setResizable(false);
				frame.setJMenuBar(menuBar);
				frame.add(getPanel());
				frame.pack();
		
				centerWindow(frame);
			}
			
		});
	}

	public JPanel getPanel() {
		panel = new JPanel(new BorderLayout());
		panel.add(getContestantPanel(), BorderLayout.WEST);
		panel.add(getUserPanel(), BorderLayout.EAST);
		
		return panel;
	}

	@SuppressWarnings("unchecked")
	public JPanel getUserPanel() {
		winnerLabel = new JLabel("", JLabel.CENTER);
		winnerLabel.setForeground(Color.decode(Constants.TWITCH_PURPLE));
		winnerLabel.setFont(new Font("Arial", Font.BOLD, 20));

		messageWinnerLabel = new JLabel("", JLabel.CENTER);
		messageWinnerLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		messageWinnerLabel.setForeground(Color.decode("#1d5bad"));

		Font font = new Font("Arial", Font.PLAIN, 12);
		Map<TextAttribute, Integer> attributes = (Map<TextAttribute, Integer>) font.getAttributes();
		attributes.put(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_ON);

		messageWinnerLabel.setFont(font.deriveFont(attributes));
		messageWinnerLabel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent event) {
				if (winner != null && !winner.isEmpty()) {
					IOHandler.getInstance().openBrowser("http://www.twitch.tv/message/compose?to=" + winner);
				}
			}

		});

		startButton = new JButton("Start Raffle");
		startButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (startButton.getText().equals("End Raffle")) {
					raffleProtocol.endRaffle();
					keyButton.setEnabled(true);
					clearButton.setEnabled(true);
					refreshButton.setEnabled(false);
				} else if (startButton.getText().equals("Start Raffle")) {
					if (Main.getSettings().getRaffleKey().equals("N/A") || Main.getSettings().getRaffleKey().length() < 3) {
						Object[] options = {
							"OK"
						};
						JOptionPane.showOptionDialog(null, "Your raffle key must exceed 3 characters before continuing.", "Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
						return;
					}
					if (!Main.getRaffleBot().isConnected() || Main.getRaffleBot().getChannels().length <= 0) {
						Object[] options = {
							"OK"
						};
						JOptionPane.showOptionDialog(null, "You are not currently connected to a server or channel.\n\nTry settings your channel in preferences or reload this program.", "Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
						return;
					}
					keyButton.setEnabled(false);
					refreshButton.setEnabled(true);
					selectButton.setEnabled(true);
					clearButton.setEnabled(false);
					raffleProtocol.startRaffle(announceCheckBox.isSelected());
				}
				startButton.setText(startButton.getText().equals("End Raffle") ? "Start Raffle" : "End Raffle");
			}

		});

		selectButton = new JButton("Select Winner");
		selectButton.setEnabled(false);
		selectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				raffleProtocol.pickWinner(followerCheckBox.isSelected());
				raffleProtocol.setRaffleActive(false);
				startButton.setText("Start Raffle");
				keyButton.setEnabled(true);
				clearButton.setEnabled(true);
			}

		});

		clearButton = new JButton("Reset Raffle");
		clearButton.setEnabled(false);
		clearButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				clearButton.setEnabled(false);
				selectButton.setEnabled(false);
				startButton.setText("Start Raffle");
				raffleProtocol.endRaffle();
				raffleProtocol.getContestants().clear();
				updateContestants();
			}

		});

		headerLabel = new JLabel("Raffle Options", SwingConstants.CENTER);
		headerLabel.setForeground(Color.decode("#545454"));
		headerLabel.setFont(new Font("Arial", Font.BOLD, 18));
		headerLabel.setBorder(new EmptyBorder(5, 5, 5, 5));
		
		JPanel temp = new JPanel(new BorderLayout());
		temp.add(headerLabel, BorderLayout.NORTH);
		temp.add(getOptionPanel());

		JPanel temp2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		temp2.add(startButton);
		temp2.add(selectButton);
		temp2.add(clearButton);

		JPanel temp3 = new JPanel(new BorderLayout());
		temp3.add(winnerLabel, BorderLayout.NORTH);
		temp3.add(messageWinnerLabel, BorderLayout.CENTER);
		temp3.setPreferredSize(new Dimension(350, 100));

		userPanel = new JPanel(new BorderLayout());
		userPanel.add(temp, BorderLayout.NORTH);
		userPanel.add(temp2);
		userPanel.add(temp3, BorderLayout.SOUTH);

		return userPanel;
	}

	public JPanel getOptionPanel() {
		announceCheckBox = new JCheckBox();
		announceCheckBox.setSelected(Main.getSettings().isAnnounceRaffle());
		announceCheckBox.setToolTipText("Selecting this will send a chat message when you begin a raffle.");
		announceCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Main.getSettings().setAnnounceRaffle(e.getStateChange() == ItemEvent.SELECTED);
                IOHandler.getInstance().writeSettings();
            }
            
        });
		
		announceLabel = new JLabel("Announce raffle in chat?");
		announceLabel.setForeground(Color.decode("#545454"));
		announceLabel.setToolTipText("Selecting this will send a chat message when you begin a raffle.");
		
		followerCheckBox = new JCheckBox();
		followerCheckBox.setSelected(Main.getSettings().isFollowersOnly());
		followerCheckBox.setToolTipText("Selecting this will only allow users that are following you to be chosen.");
		followerCheckBox.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent e) {
                Main.getSettings().setFollowersOnly(e.getStateChange() == ItemEvent.SELECTED);
                IOHandler.getInstance().writeSettings();
            }
            
        });

		followerLabel = new JLabel("Must be following?");
		followerLabel.setToolTipText("Selecting this will only allow users that are following you to be chosen.");
		followerLabel.setForeground(Color.decode("#545454"));

		boolean temp = Main.getSettings().getRaffleKey().equals("N/A") || Main.getSettings().getRaffleKey().isEmpty();
		
		raffleKeyLabel = new JLabel("<html>Current raffle key:<br /><font color='" + (temp ? "red" : Constants.TWITCH_PURPLE) + "'><strong>" + (temp ? "key not set" : Main.getSettings().getRaffleKey()) + "</strong></font></html>");
		raffleKeyLabel.setToolTipText("<html>This key will be the trigger for users in your chat.<br /><br />When they type this key, they will be entered in the raffle.</html>");
		raffleKeyLabel.setForeground(Color.decode("#545454"));

		keyButton = new JButton("Set key");
		keyButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (raffleKeyFrame == null) {
					raffleKeyFrame = new RaffleKeyFrame();
				}
				raffleKeyFrame.setVisible(true);
			}

		});

		optionPanel = new JPanel(new GridLayout(3, 2, 20, 10));
		optionPanel.add(announceLabel);
		optionPanel.add(announceCheckBox);
		optionPanel.add(followerLabel);
		optionPanel.add(followerCheckBox);
		optionPanel.add(raffleKeyLabel);
		optionPanel.add(keyButton);
		optionPanel.setBorder(new EmptyBorder(0, 20, 20, 20));

		return optionPanel;
	}

	public JPanel getContestantPanel() {
		contestantCountLabel = new JLabel("<html>Active contestants: <font color=\"#65449b\">0</font></html>", SwingConstants.CENTER);
		contestantCountLabel.setForeground(Color.decode("#545454"));
		contestantCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		contestantCountLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
		
		refreshButton = new JButton("Update list");
		refreshButton.setForeground(Color.decode("#65449b"));
		refreshButton.setOpaque(false);
		refreshButton.setEnabled(false);
		refreshButton.setFocusPainted(false);
		refreshButton.setBorderPainted(false);
		refreshButton.setContentAreaFilled(false);
		refreshButton.setToolTipText("Update the active contestant list");
		refreshButton.setIcon(new ImageIcon(Main.getImageConstants().getImage(ImageConstants.REFRESH_ICON)));
		refreshButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				updateContestants();
			}
			
		});

		contestantPanel = new JPanel(new BorderLayout());
		contestantPanel.add(contestantCountLabel, BorderLayout.NORTH);
		contestantPanel.add(getContestantList());
		contestantPanel.add(refreshButton, BorderLayout.SOUTH);

		return contestantPanel;
	}

	public JScrollPane getContestantList() {
		listModel = new DefaultListModel<String>();

		contestantList = new JList<String>(listModel);

		contestantListPane = new JScrollPane(contestantList);
		contestantListPane.setPreferredSize(new Dimension(150, 280));
		contestantListPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		contestantListPane.setBorder(BorderFactory.createCompoundBorder(new EmptyBorder(5, 5, 0, 0), new MatteBorder(1, 1, 1, 1, Color.decode("#545454"))));

		return contestantListPane;
	}

	public void updateContestants() {
		try {
			if (!raffleProtocol.isRaffleActive() && raffleProtocol.getContestants().size() <= 0 && raffleProtocol.getContestantsQueue().size() <= 0) {
				setWinner(true, null);
				listModel.clear();
				contestantCountLabel.setText("<html>Active contestants: <font color=\"#65449b\">" + NumberFormat.getInstance().format(listModel.size()) + "</font></html>");
				return;
			}
			for (Iterator<String> i = raffleProtocol.getContestantsQueue().iterator(); i.hasNext();) {
				String contestant = i.next();
				listModel.addElement(contestant);
				raffleProtocol.getContestants().add(contestant);
				i.remove();
			}
			contestantCountLabel.setText("<html>Active contestants: <font color=\"#65449b\">" + NumberFormat.getInstance().format(listModel.size()) + "</font></html>");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void updateRaffleKeyLabel(final String text) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				Main.getSettings().setRaffleKey(text);
				raffleKeyLabel.setText("<html>Current raffle key:<br /><font color=\"" + Constants.TWITCH_PURPLE + "\"><strong>" + text + "</strong></font></html>");
				optionPanel.repaint();
			}

		});
	}

	public void setWinner(boolean error, String text) {
		if (error && text != null && !text.isEmpty()) {
			winnerLabel.setText(text);
		} else if (error && (text == null || text.isEmpty())) {
			this.winner = "";
			winnerLabel.setText("");
			messageWinnerLabel.setText("");
		} else if (!error) {
			this.winner = text;
			winnerLabel.setText(format(text) + " is a winner!");
			messageWinnerLabel.setText("Click here to message " + format(winner) + " on Twitch");
		}
	}

	public String format(String str) {
		boolean prevWasWhiteSp = true;
		char[] chars = str.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			if (Character.isLetter(chars[i])) {
				if (prevWasWhiteSp) {
					chars[i] = Character.toUpperCase(chars[i]);
				}
				prevWasWhiteSp = false;
			} else {
				prevWasWhiteSp = Character.isWhitespace(chars[i]) || chars[i] == '_';
			}
		}
		return new String(chars);
	}

	public void centerWindow(Window frame) {
		GraphicsDevice graphicsDevice = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		int width = graphicsDevice.getDisplayMode().getWidth();
		int height = graphicsDevice.getDisplayMode().getHeight();

		int x = (int) ((width - frame.getWidth()) / 2);
		int y = (int) ((height - frame.getHeight()) / 2);
		frame.setLocation(x, y);
		frame.setVisible(true);
	}

	public JFrame getFrame() {
		return frame;
	}
	
	public MenuBar getMenuBar() {
		return menuBar;
	}

}