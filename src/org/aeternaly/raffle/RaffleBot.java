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

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JOptionPane;

import org.aeternaly.raffle.ui.RaffleBotUI;
import org.jibble.pircbot.PircBot;

public class RaffleBot extends PircBot {
	
	private Timer refreshTimer;

	private RaffleBotUI raffleBotUI;

	private RaffleProtocol raffleProtocol;

	public RaffleBot() {
		this.setName("Twitch_Raffle");
		this.setVerbose(false);
		this.setMessageDelay(600);
		
		try {
			connect();
		} catch (IOException e) {
			e.printStackTrace();
		}
		joinChannel("#" + Main.getChannelName());
	}

	@Override
	public void onMessage(String channel, String sender, String login, String hostname, String message) {
		if (channel.contains(Main.getChannelName()) && raffleProtocol.isRaffleActive()) {
			if (!message.equalsIgnoreCase(Main.getSettings().getRaffleKey())) {
				raffleProtocol.addContestant(sender);
			}
		}
	}

	@Override
	public void onConnect() {
		raffleBotUI = new RaffleBotUI(); 
		raffleProtocol = new RaffleProtocol();
		refreshTimer = new Timer();
		refreshTimer.scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {
				if (raffleProtocol.isRaffleActive()) {
					raffleBotUI.updateContestants();
				}
			}
			
		}, 0, 2000);
	}

	@Override
	public void onDisconnect() {
		Object[] options = {
			"OK"
		};
		JOptionPane.showOptionDialog(null, "The connection to the chat server was interrupted - please try again in a few minutes.\n\nThe program will now exit.", "Fatal Error", JOptionPane.PLAIN_MESSAGE, JOptionPane.ERROR_MESSAGE, null, options, options[0]);
		System.exit(0);
	}

	public RaffleProtocol getRaffleProtocol() {
		return raffleProtocol;
	}

	public RaffleBotUI getRaffleBotUI() {
		return raffleBotUI;
	}

}