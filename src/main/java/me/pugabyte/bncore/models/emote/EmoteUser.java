package me.pugabyte.bncore.models.emote;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.features.chat.Emotes;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.ChatColor;

@Data
@Builder
@Entity("emote_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class EmoteUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = true;

	private Set<String> disabled = new HashSet<>();

	public String getKey(Emotes emote, ChatColor color) {
		String key = emote.name();
		if (color != null)
			key += "-" + color.name();
		return key;
	}

	public boolean isEnabled(Emotes emote) {
		return !disabled.contains(getKey(emote, null));
	}

	public boolean isEnabled(Emotes emote, ChatColor color) {
		return !disabled.contains(getKey(emote, color));
	}

	public boolean enable(Emotes emote, ChatColor color) {
		String key = getKey(emote, color);
		if (disabled.contains(key)) {
			disabled.remove(key);
			return true;
		}

		return false;
	}

	public boolean disable(Emotes emote, ChatColor color) {
		String key = getKey(emote, color);
		if (!disabled.contains(key)) {
			disabled.add(key);
			return true;
		}

		return false;
	}

}
