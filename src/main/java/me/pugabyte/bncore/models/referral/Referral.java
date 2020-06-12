package me.pugabyte.bncore.models.referral;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Entity("referral")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Referral extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Origin origin;
	private String extra;

	public enum Origin {
		PLANET_MINECRAFT("PlanetMC.com", "http://www.planetminecraft.com/server/bear-nation/"),
		TOP_MINECRAFT_SERVERS("TopMCServers.org", "https://topminecraftservers.org/server/3738"),
		MINECRAFT_SERVER_LIST("MC-Server-List.com", "http://minecraft-server-list.com/server/314528/"),
		MINECRAFT_MULTIPLAYER("MC-MP.com", "http://minecraft-mp.com/server/88565/"),
		MINECRAFT_SERVERS("MCServers.org", "http://minecraftservers.org/server/248930"),
		TOPG("TopG.org", "https://topg.org/Minecraft/server-505487"),
		YOUTUBE("YouTube", "https://www.youtube.com/channel/UClJ8Mnz2R-1cWuwnLQGi6HA"),
		TWITTER("Twitter", "https://twitter.com/BearNationSMP"),
		INSTAGRAM("Instagram", "https://www.instagram.com/bearnationsmp/"),
		REDDIT("Reddit", "https://www.reddit.com/user/BearNationNetwork/"),
		FRIEND("A friend", "A friend"),
		OTHER("Other", "Other");

		@Getter
		private final String display;
		@Getter
		private final String link;

		Origin(String display, String link) {
			this.display = display;
			this.link = link;
		}
	}

}
