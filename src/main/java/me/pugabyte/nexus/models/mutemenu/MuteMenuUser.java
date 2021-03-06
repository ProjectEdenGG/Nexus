package me.pugabyte.nexus.models.mutemenu;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.chat.Chat.StaticChannel;
import me.pugabyte.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.chat.ChatService;
import org.bukkit.OfflinePlayer;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("mute_menu_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class MuteMenuUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<MuteMenuItem> muted = new HashSet<>();

	public boolean hasMuted(MuteMenuItem item) {
		if (item.name().startsWith("CHANNEL_"))
			return !new ChatService().get(uuid).hasJoined(StaticChannel.valueOf(item.name().replace("CHANNEL_", "")).getChannel());
		else
			return muted.contains(item);
	}

	public static boolean hasMuted(OfflinePlayer player, MuteMenuItem item) {
		if (item == null) return false;
		MuteMenuService service = new MuteMenuService();
		MuteMenuUser user = service.get(player);
		return user.hasMuted(item);
	}
}
