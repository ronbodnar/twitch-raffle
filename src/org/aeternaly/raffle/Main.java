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
package org.aeternaly.raffle;

import java.awt.EventQueue;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.aeternaly.raffle.ui.impl.IntroductoryFrame;
import org.aeternaly.raffle.util.IOHandler;
import org.aeternaly.raffle.util.ImageConstants;
import org.aeternaly.raffle.util.Settings;

public class Main {

	private static String channelName;
	
	private static Settings settings;

	private static RaffleBot raffleBot;
	
	private static ImageConstants imageConstants;
	
	private static IntroductoryFrame introductoryFrame;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				try {
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
				} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
					e.printStackTrace();
				}
		
				settings = new Settings();
				
				imageConstants = new ImageConstants();
				
				if (!IOHandler.settingsExist()) {
					introductoryFrame = new IntroductoryFrame();
					introductoryFrame.setVisible(true);
				} else {
					IOHandler.getInstance().loadSettings();
					
					channelName = settings.getChannelName();
					
					raffleBot = new RaffleBot();
					raffleBot.getRaffleBotUI().constructFrame();
				}
			}
			
		});
	}

	public static Settings getSettings() {
		return settings;
	}
	
	public static String getChannelName() {
		return channelName;
	}
	
	public static void setChannelName(String name) {
		channelName = name;
	}

	public static RaffleBot getRaffleBot() {
		return raffleBot;
	}
	
	public static void setRaffleBot(RaffleBot bot) {
		raffleBot = bot;
	}

	public static ImageConstants getImageConstants() {
		return imageConstants;
	}
	
}