package gg.projecteden.nexus.models.vote;

import lombok.Getter;

public enum VoteSite {
	PMC("PlanetMinecraft.com", "http://www.planetminecraft.com/server/projecteden/vote/&username={{USERNAME}}", 24),
	MCMP("Minecraft-MP.com", "http://minecraft-mp.com/server/88565/vote/", 24),
	MCBIZ("MinecraftServers.biz", "https://minecraftservers.biz/servers/891/#vote_now", 24), // contacted
	MCSL("MCSL", "http://minecraft-server-list.com/server/314528/vote/", 24), // contacted
	MCSO("MinecraftServers.org", "http://minecraftservers.org/vote/248930", 24), // contacted
	MCSN("Minecraft-Server.net", "https://minecraft-server.net/vote/ProjectEden/", 24),
	TMCS("TopMinecraftServers", "https://topminecraftservers.org/vote/3738", 24), // cookie autofill?
	TOPG("TopG.org", "https://topg.org/Minecraft/in-505487-{{USERNAME}}", 24);

	@Getter
	private String id;
	@Getter
	private String url;
	@Getter
	private int expirationHours;

	VoteSite(String id, String url, int expirationHours) {
		this.id = id;
		this.url = url;
		this.expirationHours = expirationHours;
	}

	public String getUrl(String username) {
		return url.replace("{{USERNAME}}", username);
	}

	public static VoteSite getFromId(String id) {
		for (VoteSite site : values())
			if (site.getId().equals(id))
				return site;
		return null;
	}

}
