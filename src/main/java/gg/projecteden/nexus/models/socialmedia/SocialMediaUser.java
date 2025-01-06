package gg.projecteden.nexus.models.socialmedia;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.discord.Discord;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Entity(value = "social_media_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class SocialMediaUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private Map<SocialMediaSite, Connection> connections = new HashMap<>();
	private boolean mature;

	private boolean streaming;

	public Connection getConnection(SocialMediaSite site) {
		return connections.get(site);
	}

	public void addConnection(SocialMediaSite site, String username) {
		final Connection connection = new Connection(uuid, site, username);
		connections.put(site, connection);
		Discord.staffLog(StringUtils.getDiscordPrefix("SocialMedia") + getNickname() + " linked their " +
			StringUtils.camelCase(site) + " account to " + connection.getDiscordUrl());
	}

	public void removeConnection(SocialMediaSite site) {
		connections.remove(site);
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@Converters(UUIDConverter.class)
	public static class Connection {
		private UUID uuid;
		private SocialMediaSite site;
		private String username;

		public String getUrl() {
			return String.format(site.getProfileUrl(), username);
		}
		public String getDiscordUrl() {
			final String format = String.format(site.getProfileUrl(), username);

			if ("%s".equals(site.getProfileUrl()))
				return format;

			return "<" + format + ">";
		}
	}

}
