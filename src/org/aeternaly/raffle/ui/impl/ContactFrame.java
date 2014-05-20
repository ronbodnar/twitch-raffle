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
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.SwingWorker;
import javax.swing.UIManager;

import org.aeternaly.raffle.Main;
import org.aeternaly.raffle.ui.util.ComboBoxRenderer;
import org.aeternaly.raffle.ui.util.SpringUtilities;
import org.aeternaly.raffle.util.Constants;
import org.aeternaly.raffle.util.IOHandler;
import org.aeternaly.raffle.util.ImageConstants;

public class ContactFrame extends JFrame {

	private JLabel priorityLabel;
	
	private JPanel panel, formPanel;
	
	private JButton submit, cancel;
	
	private JSlider prioritySlider;
	
	private JTextField subjectTextField;
	
	private JTextArea descriptionTextArea;
	
	private JComboBox<String> categoryCombo;
	
	private Hashtable<Integer, JLabel> labelTable;
	
	private JPanel categoryPane, subjectPane, descriptionPane, priorityPane;
	
	private boolean categoryError, descriptionError;

	public ContactFrame() {
		setTitle("Contact Form");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setIconImage(Main.getImageConstants().getImage(ImageConstants.FAVICON));
		setResizable(false);
		setLayout(new BorderLayout());
		add(getPanel(), BorderLayout.CENTER);
		pack();
	}
	
	public JPanel getPanel() {
		submit = new JButton("Submit");
		submit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				categoryError = categoryCombo.getSelectedIndex() == 0;
				
				descriptionError = descriptionTextArea.getText().isEmpty() || descriptionTextArea.getText().length() <= 20;
				
				if (categoryError || descriptionError) {
					repaint();
					return;
				}
				new SwingWorker<Boolean, Void>() {

					@Override
					protected Boolean doInBackground() throws Exception {
						String subject = subjectTextField.getText().isEmpty() ? "No subject" : subjectTextField.getText();
						
						setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
						IOHandler.getInstance().mailBugReport(subject, (String) categoryCombo.getSelectedItem(), descriptionTextArea.getText(), prioritySlider.getValue());
						return true;
					}

					@Override
					protected void done() {
						prioritySlider.setValue(2);
						subjectTextField.setText("");
						descriptionTextArea.setText("");
						categoryCombo.setSelectedIndex(0);
						JOptionPane.showOptionDialog(null, "Your message has been successfuly sent!", "Thanks!", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, new String[] { "Ok" }, 0);
						dispose();
					}
					
				}.execute();
			}

		});

		cancel = new JButton("Cancel");
		cancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
			}

		});

		JPanel temp2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
		temp2.add(submit);
		temp2.add(cancel);

		panel = new JPanel(new BorderLayout());
		panel.add(getForm(), BorderLayout.NORTH);
		panel.add(temp2, BorderLayout.SOUTH);

		return panel;
	}
	
	public JPanel getForm() {
		formPanel = new JPanel(new SpringLayout());
		formPanel.add(getCategorySector());
		formPanel.add(getSubjectSector());
		formPanel.add(getDescriptionSector());
		formPanel.add(getPrioritySector());

		SpringUtilities.makeCompactGrid(formPanel, 4, 1, 6, 6, 10, 10);
		
		return formPanel;
	}
	
	@SuppressWarnings("unchecked")
	public JPanel getCategorySector() {
		JLabel label = new JLabel("<html>Category <font color=\"" + Constants.TWITCH_PURPLE + "\">*</font>:</html>");
			
		categoryCombo = new JComboBox<String>(new String[] { " ", "-", "Bug report", "Suggestion", "Feedback", "-", "Other" }) {
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (categoryCombo.getSelectedIndex() == 0 || categoryCombo.getSelectedItem().equals(" ")) {
					Graphics2D graphics = (Graphics2D) g.create();
					graphics.setColor(Color.GRAY.brighter());
					graphics.setFont(new Font("Arial", Font.PLAIN, 12));
					graphics.drawString("Please select a category", 5, 17);
				}
			}
			
		};
		categoryCombo.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent event) {
				if (event.getStateChange() == ItemEvent.SELECTED) {
					priorityLabel.setEnabled(event.getItem().equals("Bug report"));
					prioritySlider.setEnabled(event.getItem().equals("Bug report"));
					descriptionTextArea.repaint();
				}
			}
			
		});
		categoryCombo.setPreferredSize(new Dimension(275, 25));
		categoryCombo.setRenderer(new ComboBoxRenderer(categoryCombo.getRenderer()));
		
		categoryPane = new JPanel(new SpringLayout()) {
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (categoryError) {
					Graphics2D graphics = (Graphics2D) g.create();
					graphics.setColor(new Color(255, 21, 21, 100));
					graphics.fillRect(0, 0, 346, 83);
				}
			}
			
		};
		categoryPane.setOpaque(true);
		categoryPane.add(label);
		label.setLabelFor(categoryCombo);
		categoryPane.add(categoryCombo);
		SpringUtilities.makeCompactGrid(categoryPane, 2, 1, 6, 6, 10, 10);
		
		return categoryPane;
	}
	
	public JPanel getSubjectSector() {
		JLabel label = new JLabel("<html>Subject <small>(optional)</small>:</html>");
		
		subjectTextField = new JTextField(14);
		
		subjectPane = new JPanel(new SpringLayout());
		subjectPane.setOpaque(true);

		subjectPane.add(label);
		label.setLabelFor(subjectTextField);
		subjectPane.add(subjectTextField);
		SpringUtilities.makeCompactGrid(subjectPane, 2, 1, 6, 6, 10, 10);
		
		return subjectPane;
	}
	
	public JPanel getDescriptionSector() {
		JLabel label = new JLabel("<html>Message body <small>(min 20, max 1,000 characters)</small> <font color=\"" + Constants.TWITCH_PURPLE + "\">*</font>:</html>");
		
		descriptionTextArea = new JTextArea(10, 30) {
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (getText().isEmpty() && ((String) categoryCombo.getSelectedItem()).equalsIgnoreCase("Bug Report")) {
					Graphics2D graphics = (Graphics2D) g.create();
					graphics.setColor(Color.GRAY);
					graphics.setFont(new Font("Trebuchet MS", Font.PLAIN, 12));
					graphics.drawString("An accurate description will help us deploy a fix faster!", 5, 15);
				}
			}
			
		};
		descriptionTextArea.setLineWrap(true);
		
		descriptionPane = new JPanel(new SpringLayout()) {
			
			@Override
			public void paintComponent(Graphics g) {
				super.paintComponent(g);
				
				if (descriptionError) {
					Graphics2D graphics = (Graphics2D) g.create();
					graphics.setColor(new Color(255, 21, 21, 100));
					graphics.fillRect(0, 0, 346, 224);
				}
			}
			
		};
		descriptionPane.setOpaque(true);
		descriptionPane.add(label);
		label.setLabelFor(descriptionTextArea);
		descriptionPane.add(new JScrollPane(descriptionTextArea));
		
		SpringUtilities.makeCompactGrid(descriptionPane, 2, 1, 6, 6, 10, 10);
		
		return descriptionPane;
	}
	
	public JPanel getPrioritySector() {
		priorityLabel = new JLabel("<html>Priority <font color=\"" + Constants.TWITCH_PURPLE + "\">*</font>:</html>");
		priorityLabel.setEnabled(false);
		
		UIManager.put("Slider.focus", panel.getBackground());
		
		labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(1), new JLabel("<html><font color=\"green\">Low</font></html>"));
		labelTable.put(new Integer(2), new JLabel("<html><font color=\"orange\">Moderate</font></html>"));
		labelTable.put(new Integer(3), new JLabel("<html><font color=\"red\">High</font></html>"));
		
		prioritySlider = new JSlider(1, 3);
		prioritySlider.setLabelTable(labelTable);
		prioritySlider.setPaintLabels(true);
		prioritySlider.setValue(2);
		prioritySlider.setEnabled(false);
		
		priorityPane = new JPanel(new SpringLayout());
		priorityPane.setOpaque(true);
		
		priorityPane.add(priorityLabel);
		priorityLabel.setLabelFor(prioritySlider);
		priorityPane.add(prioritySlider);
		SpringUtilities.makeCompactGrid(priorityPane, 2, 1, 6, 6, 10, 10);
		
		return priorityPane;
	}
	
	@Override
	public void setVisible(boolean visible) {
		prioritySlider.setValue(2);
		subjectTextField.setText("");
		descriptionTextArea.setText("");
		categoryCombo.setSelectedIndex(0);
		
		repaint();
		
		setLocationRelativeTo(Main.getRaffleBot().getRaffleBotUI().getFrame());
		
		super.setVisible(visible);
	}

}