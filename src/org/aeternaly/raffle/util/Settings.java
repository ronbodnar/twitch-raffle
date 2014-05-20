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

public class Settings {
	
	private String raffleKey = "N/A";
	
	private boolean alwaysOnTop = false;
	private boolean announceRaffle = false;
	private boolean followersOnly = false;
	
	private String channelName;

	public String getChannelName() {
		return channelName;
	}
	
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}

	public String getRaffleKey() {
		return raffleKey;
	}

	public void setRaffleKey(String raffleKey) {
		this.raffleKey = raffleKey;
	}

	public boolean isFollowersOnly() {
		return followersOnly;
	}

	public void setFollowersOnly(boolean followersOnly) {
		this.followersOnly = followersOnly;
	}

	public boolean isAnnounceRaffle() {
		return announceRaffle;
	}

	public void setAnnounceRaffle(boolean announceRaffle) {
		this.announceRaffle = announceRaffle;
	}

	public boolean isAlwaysOnTop() {
		return alwaysOnTop;
	}

	public void setAlwaysOnTop(boolean alwaysOnTop) {
		this.alwaysOnTop = alwaysOnTop;
	}

}