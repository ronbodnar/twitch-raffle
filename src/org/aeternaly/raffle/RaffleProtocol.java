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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.swing.SwingUtilities;

public class RaffleProtocol {

	private int retryCount;

	private boolean raffleActive;

	private Random randomGenerator;

	private List<String> contestants;
	
	private Deque<String> contestantsQueue;

	public RaffleProtocol() {
		this.randomGenerator = new Random();
		this.contestants = new LinkedList<String>();
		this.contestantsQueue = new ArrayDeque<String>();
	}

	public void startRaffle(boolean announce) {
		if (!raffleActive) {
			contestants.clear();
			this.raffleActive = true;
			Main.getRaffleBot().getRaffleBotUI().updateContestants();
			if (announce) {
				Main.getRaffleBot().sendMessage("#" + Main.getChannelName(), "A raffle has been activated! Type \"" + Main.getSettings().getRaffleKey() + "\" to participate.");
			}
		}
	}

	public void endRaffle() {
		raffleActive = false;
	}

	public void pickWinner(final boolean followersOnly) {
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				if (contestants.size() > 0) {
					int index = randomGenerator.nextInt(contestants.size());
					if (followersOnly) {
						while (!isFollowing(contestants.get(index)) && retryCount < 10) {
							index = randomGenerator.nextInt(contestants.size());
							retryCount++;
						}
						retryCount = 0;
					}
					Main.getRaffleBot().getRaffleBotUI().setWinner(false, contestants.get(index));
				} else {
					Main.getRaffleBot().getRaffleBotUI().setWinner(true, "<html><font color='red'>" + (raffleActive ? "There are no active participants" : "The raffle is not currently active") + "</font></html>");
				}
			}
			
		});
	}
	
	public boolean isFollowing(String user) {
		try {
			StringBuilder sb = new StringBuilder();
			URL url = new URL("192.185.9.14/~aeterna/development/twitch-raffle/api/followcheck.php?username=" + user.trim().replaceAll(" ", "_") + "&channel=" + Main.getChannelName());

			URLConnection connection = url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestProperty("User-Agent", "TwitchRaffleBot");
			connection.setReadTimeout(7500);
			connection.setConnectTimeout(7500);
			connection.connect();
			
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));

			String input;
			while ((input = bufferedReader.readLine()) != null) {
				sb.append(input);
			}
			bufferedReader.close();
			
			return Boolean.parseBoolean(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public boolean isRaffleActive() {
		return raffleActive;
	}

	public void setRaffleActive(boolean raffleActive) {
		this.raffleActive = raffleActive;
	}

	public List<String> getContestants() {
		return contestants;
	}

	public Deque<String> getContestantsQueue() {
		return contestantsQueue;
	}

	public void addContestant(String name) {
		if (contestantsQueue.contains(name) || contestants.contains(name)) {
			return;
		}
		contestantsQueue.add(name);
	}

}