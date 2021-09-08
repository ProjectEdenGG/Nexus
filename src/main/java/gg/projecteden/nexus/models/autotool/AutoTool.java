package gg.projecteden.nexus.models.autotool;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.GameMode;

import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "auto_tool", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class AutoTool implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = true;

	@Getter
	private static final List<String> disabledWorlds = WorldGroup.MINIGAMES.getWorldNames();

	public boolean isEnabled() {
		if (!isOnline())
			return false;
		if (!enabled)
			return false;
		if (disabledWorlds.contains(getOnlinePlayer().getWorld().getName()))
			return false;
		if (getOnlinePlayer().getGameMode() != GameMode.SURVIVAL)
			return false;
		if (!getOnlinePlayer().hasPermission("autotool.use"))
			return false;

		return true;
	}

	public boolean isEnabledRaw() {
		return enabled;
	}

}
