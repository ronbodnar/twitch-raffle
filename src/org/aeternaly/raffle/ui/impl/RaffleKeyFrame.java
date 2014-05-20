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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.aeternaly.raffle.Main;
import org.aeternaly.raffle.util.IOHandler;
import org.aeternaly.raffle.util.ImageConstants;

public class RaffleKeyFrame extends JFrame {

	private JPanel panel;

	private JLabel label;

	private JButton ok, cancel;

	private JTextField textField;

	public RaffleKeyFrame() {
		setTitle("Raffle Key");
		setIconImage(Main.getImageConstants().getImage(ImageConstants.FAVICON));
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setLocationRelativeTo(Main.getRaffleBot().getRaffleBotUI().getFrame());
		setResizable(false);
		setLayout(new BorderLayout());
		add(getPanel(), BorderLayout.CENTER);
		pack();
		setVisible(true);
	}

	public JPanel getPanel() {
		ok = new JButton("OK");
		ok.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (textField.getText().length() < 3) {
					Object[] options = {
						"OK"
					};
					JOptionPane.showOptionDialog(null, "You must enter a raffle key of at least 3 characters.", "Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
					return;
				}
				dispose();
				Main.getSettings().setRaffleKey(textField.getText());
				Main.getRaffleBot().getRaffleBotUI().updateRaffleKeyLabel(textField.getText());
                IOHandler.getInstance().writeSettings();
			}

		});

		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				textField.setText("");
			}

		});

		label = new JLabel("Enter a new raffle key:");

		textField = new JTextField(10);
		textField.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (textField.getText().length() < 3) {
					Object[] options = {
						"OK"
					};
					JOptionPane.showOptionDialog(null, "You must enter a raffle key of at least 3 characters.", "Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
					return;
				}
				dispose();
				Main.getSettings().setRaffleKey(textField.getText());
				Main.getRaffleBot().getRaffleBotUI().updateRaffleKeyLabel(textField.getText());
                IOHandler.getInstance().writeSettings();
			}

		});

		JPanel temp = new JPanel(new FlowLayout(FlowLayout.CENTER));
		temp.add(label);
		temp.add(textField);

		JPanel temp2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		temp2.add(ok);
		temp2.add(cancel);

		panel = new JPanel(new BorderLayout());
		panel.add(temp, BorderLayout.NORTH);
		panel.add(temp2, BorderLayout.SOUTH);
		panel.setPreferredSize(new Dimension(290, 70));

		return panel;
	}

	public JTextField getTextField() {
		return textField;
	}

}