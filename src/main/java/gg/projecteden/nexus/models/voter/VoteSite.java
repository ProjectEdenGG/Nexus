package gg.projecteden.nexus.models.voter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum VoteSite {
	PMC(false, "PlanetMinecraft.com", "PlanetMinecraft.com", "http://www.planetminecraft.com/server/projecteden/vote/&username={{USERNAME}}", 24),
	TMCS(false, "TopMinecraftServers", "TopMinecraftServers", "https://topminecraftservers.org/vote/3738", 24),
	MCSL(true, "Minecraft Server List", "MCSL", "http://minecraft-server-list.com/server/314528/vote/", 24),
	MCMP(true, "Minecraft Multiplayer", "Minecraft-MP.com", "http://minecraft-mp.com/server/88565/vote/", 24),
	MCSN(false, "Minecraft-Server.net", "Minecraft-Server.net", "https://minecraft-server.net/vote/ProjectEden/", 24),
	MCBIZ(true, "MinecraftServers.biz", "MinecraftServers.biz", "https://minecraftservers.biz/servers/891/#vote_now", 24),
	MCSO(true, "MinecraftServers.org", "MinecraftServers.org", "http://minecraftservers.org/vote/248930", 24),
	TOPG(false, "TopG", "TopG.org", "https://topg.org/Minecraft/in-505487-{{USERNAME}}", 24),
	FMCS(true, "FindMCServer", "FindMCServer", "https://findmcserver.com/server/projecteden?vote=true", 24),
	MCF(false, null, null, null, -1),
	MCSB(false, null, null, null, -1),
	MCSLN(false, null, null, null, -1),
	MSC(false, null, null, null, -1),
	MST(false, null, null, null, -1),
	;

	private final boolean active;
	private final String name;
	private final String id;
	private final String url;
	private final int expirationHours;

	public static List<VoteSite> getActiveSites() {
		return Arrays.stream(values()).filter(VoteSite::isActive).toList();
	}

	public String getUrl(String username) {
		return url.replace("{{USERNAME}}", username);
	}

	public String getPhpUrl() {
		return url.replace("{{USERNAME}}", "<?php if ($isLoggedIn) echo $username ?>");
	}

	public static VoteSite getFromId(String id) {
		for (VoteSite site : getActiveSites())
			if (site.getId().equals(id))
				return site;
		return null;
	}

}
