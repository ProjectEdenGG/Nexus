package me.pugabyte.nexus.models.ambience;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.UUID;

@Data
@Builder
@Entity("ambience_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class AmbienceUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean debug;
	private transient Variables variables;

	private boolean sounds;
	private boolean particles;

	@Override
	public void debug(String message) {
		if (debug)
			sendMessage(message);
	}

	public Variables getVariables() {
		if (variables == null) {
			variables = new Variables(getPlayer());
		}

		return variables;
	}
}
