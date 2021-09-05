package gg.projecteden.nexus.models.socialmedia;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.socialmedia.SocialMedia.SocialMediaSite;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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
