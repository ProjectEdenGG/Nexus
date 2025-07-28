package gg.projecteden.nexus.models.twitch;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Data
@Entity(value = "twitch_oauth_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class TwitchOAuthConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private String accessToken;

	public String getClientId() {
		return Nexus.getInstance().getConfig().getString("tokens.twitch.id");
	}

	public String getClientSecret() {
		return Nexus.getInstance().getConfig().getString("tokens.twitch.secret");
	}

}
