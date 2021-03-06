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
import me.pugabyte.nexus.features.menus.mutemenu.MuteMenuProvider.MuteMenuItem;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.OfflinePlayer;

import java.util.Set;
import java.util.UUID;

@Data
@Builder
@Entity("mute_menu_user")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class})
public class MuteMenuUser extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<MuteMenuItem> muted;

	public boolean hasMuted(MuteMenuItem item) {
		return muted.contains(item);
	}

	public static boolean hasMuted(OfflinePlayer player, MuteMenuItem item) {
		MuteMenuService service = new MuteMenuService();
		MuteMenuUser user = service.get(player);
		return user.hasMuted(item);
	}
}
