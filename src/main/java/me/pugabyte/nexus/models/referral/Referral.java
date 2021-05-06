package me.pugabyte.nexus.models.referral;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Entity("referral")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Referral implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String ip;
	private Origin origin;
	private String extra;

	@Getter
	@AllArgsConstructor
	public enum Origin {
		PLANET_MINECRAFT("PlanetMC.com", "http://www.planetminecraft.com/server/projecteden/"),
		MCSERVERS_BIZ("MCServers.biz", "https://minecraftservers.biz/servers/891/"),
		TOP_MINECRAFT_SERVERS("TopMCServers.org", "https://topminecraftservers.org/server/3738"),
		MINECRAFT_SERVER_LIST("MC-Server-List.com", "http://minecraft-server-list.com/server/314528/"),
		MINECRAFT_MULTIPLAYER("MC-MP.com", "http://minecraft-mp.com/server/88565/"),
		MINECRAFT_SERVERS("MCServers.org", "http://minecraftservers.org/server/248930"),
//		MC_SERVER("MC-Server.net", "https://minecraft-server.net/vote/ProjectEden/"), Not enough space in book
		TOPG("TopG.org", "https://topg.org/Minecraft/server-505487"),
		YOUTUBE("YouTube", "https://www.youtube.com/channel/UClJ8Mnz2R-1cWuwnLQGi6HA"),
		TWITTER("Twitter", "https://twitter.com/ProjectEdenGG"),
		INSTAGRAM("Instagram", "https://www.instagram.com/ProjectEdenGG/"),
		REDDIT("Reddit", "https://www.reddit.com/user/ProjectEdenGG/"),
		DISBOARD("Disboard", "https://disboard.org/server/132680070480396288"),
		FRIEND("A friend", "A friend"),
		OTHER("Other", "Other");

		@Getter
		private final String display;
		private final String link;
	}

}
