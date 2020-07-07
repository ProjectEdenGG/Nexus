package me.pugabyte.bncore.models.socialmedia;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.MaterialTag;
import me.pugabyte.bncore.utils.WorldEditUtils;
import me.pugabyte.bncore.utils.WorldGuardUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.Skull;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.camelCase;

@Data
@Builder
@Entity("social_media")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class SocialMedia extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private List<Connection> connections = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters(UUIDConverter.class)
	public static class Connection {
		private UUID uuid;
		private SocialMediaSite site;
		private String username;

		public String getUrl() {
			return site.getProfileUrl().replace("{{USERNAME}}", username);
		}
	}

	// TODO Use 1.16 RGB
	public enum SocialMediaSite {
		TWITTER("&bTwitter", "https://twitter.com", "https://twitter.com/{{USERNAME}}"),
		INSTAGRAM("&dInstagram", "https://instgram.com", "https://instgram.com/{{USERNAME}}"),
		SNAPCHAT("&eSnapchat", "https://snapchat.com", "https://snapchat.com/add/{{USERNAME}}"),
		YOUTUBE("&fYou&4Tube", "https://youtube.com", "{{USERNAME}}"),
		TWITCH("&5Twitch", "https://twitch.tv", "https://twitch.tv/{{USERNAME}}"),
		DISCORD("&9Discord", "https://discord.com", "{{USERNAME}}"),
		STEAM("&9Steam", "https://store.steampowered.com", "https://steamcommunity.com/id/{{USERNAME}}"),
		REDDIT("&6Reddit", "https://reddit.com", "https://reddit.com/u/{{USERNAME}}"),
		GITHUB("&fGitHub", "https://github.com", "https://github.com/{{USERNAME}}");

		@Getter
		private final String label;
		@Getter
		private final String url;
		@Getter
		private final String profileUrl;
		@Getter
		@Setter
		private ItemStack head = new ItemStack(Material.PLAYER_HEAD);

		SocialMediaSite(String label, String url, String profileUrl) {
			this.label = label;
			this.url = url;
			this.profileUrl = profileUrl;
		}

		static {
			reload();
		}

		public static void reload() {
			try {
				World world = Bukkit.getWorld("survival");

				for (Block block : new WorldEditUtils(world).getBlocks(new WorldGuardUtils(world).getRegion("socialmedia"))) {
					try {
						if (!MaterialTag.SIGNS.isTagged(block.getType())) continue;
						Sign sign = (Sign) block.getState();
						String line = sign.getLine(0);
						try {
							SocialMediaSite site = SocialMediaSite.valueOf(line);
							Block head = block.getRelative(BlockFace.DOWN);
							if (head.getState() instanceof Skull)
								site.setHead(head.getDrops().iterator().next());
							else
								BNCore.warn("Head for " + camelCase(site.name()) + " not found");
						} catch (IllegalArgumentException ex) {
							BNCore.warn("Found unknown social media head: " + line);
						}
					} catch (Throwable ex) {
						ex.printStackTrace();
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}
	}

	public enum BNSocialMediaSite {
		WEBSITE("https://bnn.gg"),
		DISCORD("https://discord.gg/bearnation"),
		YOUTUBE("https://youtube.bnn.gg"),
		TWITTER("https://twitter.bnn.gg"),
		INSTAGRAM("https://instagram.bnn.gg"),
		REDDIT("https://reddit.bnn.gg"),
		STEAM("https://steam.bnn.gg");

		@Getter
		private String name = "&3" + camelCase(name());
		@Getter
		private final String url;

		BNSocialMediaSite(String url) {
			try {
				this.name = SocialMediaSite.valueOf(name()).getLabel();
			} catch (IllegalArgumentException ignore) {}

			this.url = url;
		}
	}

}
