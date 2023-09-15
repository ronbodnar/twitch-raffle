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
package org.aeternaly.raffle.util;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.aeternaly.raffle.Main;

public class IOHandler {

	private File settingsFile;

	private static IOHandler instance;

	public IOHandler() {
		createStructure();
	}

	public boolean loadSettings() {
		try {
			FileInputStream fis = new FileInputStream(settingsFile);
			DataInputStream ois = new DataInputStream(fis);

			Main.getSettings().setChannelName(ois.readUTF());
			Main.getSettings().setRaffleKey(ois.readUTF());
			Main.getSettings().setAlwaysOnTop(ois.readBoolean());
			Main.getSettings().setAnnounceRaffle(ois.readBoolean());
			Main.getSettings().setFollowersOnly(ois.readBoolean());

			ois.close();
			fis.close();

			return true;
		} catch (IOException ex) {
			return false;
		}
	}

	public boolean writeSettings() {
		try {
			FileOutputStream fis = new FileOutputStream(settingsFile);
			DataOutputStream oos = new DataOutputStream(fis);

			oos.writeUTF(Main.getSettings().getChannelName());
			oos.writeUTF(Main.getSettings().getRaffleKey());
			oos.writeBoolean(Main.getSettings().isAlwaysOnTop());
			oos.writeBoolean(Main.getSettings().isAnnounceRaffle());
			oos.writeBoolean(Main.getSettings().isFollowersOnly());

			oos.close();
			fis.close();

			return true;
		} catch (IOException e) {
			return false;
		}
	}

	public void createStructure() {
		try {
			File file = new File(Constants.SETTINGS_DIRECTORY);
			if (!file.exists()) {
				file.mkdir();
			}

			settingsFile = new File(Constants.SETTINGS_DIRECTORY + "settings.bin");
			if (!settingsFile.exists()) {
				settingsFile.createNewFile();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void mailBugReport(String subject, String category, String description, int priority) {
		final String username = "";
		final String password = "";

		Properties props = new Properties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		Session session = Session.getInstance(props, new Authenticator() {
			
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(username, password);
			}
			
		});

		try {
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("Twitch Raffle <noreply@aeternaly.net>"));
			message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(""));
			message.setSubject("Twitch Raffle Bug Report");
			message.setContent("<strong>Subject:</strong><br />" + subject + "<br /><br /><strong>Priority:<br /></strong> " + getPriorityString(priority) + "<br /><br /><strong>Category:<br /></strong> " + category + "<br /><br /><strong>Description:</strong><br />" + description, "text/html");

			Transport.send(message);
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
	
	public String getPriorityString(int priority) {
		switch (priority) {
			case 1: return "<font color=\"green\">Low</font>";
			case 2: return "<font color=\"orange\">Moderate</font>";
			case 3: return "<font color=\"red\">High</font>";
		}
		return "N/A";
	}

	public static boolean settingsExist() {
		File file = new File(Constants.SETTINGS_DIRECTORY + "settings.bin");
		return file.isFile() && file.length() > 0;
	}
	
	public void openBrowser(String url) {
		Desktop desktop = Desktop.getDesktop();
		if (Desktop.isDesktopSupported()) {
			if (desktop.isSupported(Action.BROWSE)) {
				try {
					desktop.browse(new URL(url).toURI());
				} catch (IOException | URISyntaxException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static IOHandler getInstance() {
		if (instance == null) {
			instance = new IOHandler();
		}
		return instance;
	}

}
