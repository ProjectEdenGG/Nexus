package me.pugabyte.bncore.models.vote;

import lombok.Getter;

public enum VoteSite {
	MCMP("http://minecraft-mp.com/server/88565/vote/", 24),
	MCSL("http://minecraft-server-list.com/server/314528/vote/", 24),
	MCSO("http://minecraftservers.org/vote/248930", 24),
	PMC("http://www.planetminecraft.com/server/bear-nation/vote/", 24),
	TMCS("https://topminecraftservers.org/vote/3738", 24),
	TOPG("https://topg.org/Minecraft/in-505487", 24);

	@Getter
	private String link;
	@Getter
	private int expirationTime;

	VoteSite(String link, int expirationTime) {
		this.link = link;
		this.expirationTime = expirationTime;
	}

}
