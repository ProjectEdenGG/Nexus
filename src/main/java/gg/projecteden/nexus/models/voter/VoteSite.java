package gg.projecteden.nexus.models.voter;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
@AllArgsConstructor
public enum VoteSite {
	PMC(true, "PlanetMinecraft.com", "http://www.planetminecraft.com/server/projecteden/vote/&username={{USERNAME}}", 24),
	MCMP(true, "Minecraft-MP.com", "http://minecraft-mp.com/server/88565/vote/", 24),
	MCBIZ(true, "MinecraftServers.biz", "https://minecraftservers.biz/servers/891/#vote_now", 24),
	MCSL(true, "MCSL", "http://minecraft-server-list.com/server/314528/vote/", 24),
	MCSO(true, "MinecraftServers.org", "http://minecraftservers.org/vote/248930", 24),
	MCSN(true, "Minecraft-Server.net", "https://minecraft-server.net/vote/ProjectEden/", 24),
	TMCS(true, "TopMinecraftServers", "https://topminecraftservers.org/vote/3738", 24),
	TOPG(true, "TopG.org", "https://topg.org/Minecraft/in-505487-{{USERNAME}}", 24),
	MCF(false, null, null, -1),
	MCSB(false, null, null, -1),
	MCSLN(false, null, null, -1),
	MSC(false, null, null, -1),
	MST(false, null, null, -1),
	;

	private final boolean active;
	private final String id;
	private final String url;
	private final int expirationHours;

	public static List<VoteSite> getValues() {
		return Arrays.stream(values()).filter(VoteSite::isActive).toList();
	}

	public String getUrl(String username) {
		return url.replace("{{USERNAME}}", username);
	}

	public static VoteSite getFromId(String id) {
		for (VoteSite site : getValues())
			if (site.getId().equals(id))
				return site;
		return null;
	}

}
