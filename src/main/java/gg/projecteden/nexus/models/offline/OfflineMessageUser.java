package gg.projecteden.nexus.models.offline;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.SoundUtils;
import lombok.*;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "offline_message_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class OfflineMessageUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<String> messages = new ArrayList<>(); // Serialized to JSON

	public void send(JsonBuilder builder) {
		if (isOnline())
			builder.send(this);
		else {
			messages.add(builder.serialize());
			save();
		}
	}

	public void send(String message) {
		send(new JsonBuilder(message));
	}

	public void send(ComponentLike componentLike) {
		send(new JsonBuilder(componentLike));
	}

	public void save() {
		new OfflineMessageService().save(this);
	}

	public void sendNext() {
		if (!isOnline())
			return;

		if (messages.isEmpty())
			return;

		if (getPlayer() == null)
			return;

		Component component = JSONComponentSerializer.json().deserialize(messages.get(0));
		getPlayer().sendMessage(component);
		messages.remove(0);
		save();
		SoundUtils.Jingle.PING.play(getPlayer());
	}
}
