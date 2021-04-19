package me.pugabyte.nexus.models.autotool;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("auto_tool")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class AutoTool extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = true;

	@Getter
	private static final List<String> disabledWorlds = Arrays.asList("gameworld", "deathswap");

	public boolean isEnabled() {
		if (!isOnline())
			return false;
		if (!enabled)
			return false;
		if (disabledWorlds.contains(getPlayer().getWorld().getName()))
			return false;
		if (!getPlayer().hasPermission("autotool.use"))
			return false;

		return true;
	}

	public boolean isEnabledRaw() {
		return enabled;
	}

}
