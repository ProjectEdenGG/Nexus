package me.pugabyte.nexus.models.godmode;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.afk.AFK;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("godmode")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Godmode implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = false;
	private Location loginLocation;

	@Getter
	private static final List<String> disabledWorlds = new ArrayList<String>(Arrays.asList("gameworld", "deathswap")) {{
		addAll(WorldGroup.SKYBLOCK.getWorlds());
		addAll(WorldGroup.ONEBLOCK.getWorlds());
	}};

	public boolean isEnabled() {
		if (isOnline() && loginLocation != null)
			if (AFK.isSameLocation(loginLocation, getPlayer().getLocation()))
				return true;
			else
				loginLocation = null;
		if (isOnline() && !PlayerUtils.isStaffGroup(getPlayer()))
			return false;
		if (isOnline() && disabledWorlds.contains(getPlayer().getWorld().getName()))
			return false;
		return enabled;
	}

	public boolean isEnabledRaw() {
		return enabled;
	}



}
