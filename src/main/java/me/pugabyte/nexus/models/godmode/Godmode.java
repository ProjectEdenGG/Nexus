package me.pugabyte.nexus.models.godmode;

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
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("godmode")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class Godmode extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = false;

	@Getter
	private static final List<String> disabledWorlds = Arrays.asList("gameworld", "deathswap");

	public boolean isEnabled() {
		if (isOnline() && !PlayerUtils.isStaff(getPlayer()))
			return false;
		if (isOnline() && disabledWorlds.contains(getPlayer().getWorld().getName()))
			return false;
		return enabled;
	}

	public boolean isEnabledRaw() {
		return enabled;
	}



}
