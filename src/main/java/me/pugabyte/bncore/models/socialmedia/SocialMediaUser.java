package me.pugabyte.bncore.models.socialmedia;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.socialmedia.SocialMedia.SocialMediaSite;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("social_media_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class SocialMediaUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private List<Connection> connections = new ArrayList<>();

	public Connection getConnection(SocialMediaSite site) {
		return connections.stream().filter(connection -> connection.getSite() == site).findFirst().orElse(null);
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
			return site.getProfileUrl().replace("{{USERNAME}}", username);
		}
	}

}
