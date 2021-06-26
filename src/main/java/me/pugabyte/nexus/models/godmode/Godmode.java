package me.pugabyte.nexus.models.godmode;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.models.nerd.Nerd;
import me.pugabyte.nexus.models.nerd.Rank;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.WorldGroup;
import org.bukkit.entity.Player;

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
public class Godmode implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled = false;

	@Getter
	private static final List<String> disabledWorlds = new ArrayList<>(Arrays.asList("gameworld", "deathswap")) {{
		addAll(WorldGroup.SKYBLOCK.getWorldNames());
		addAll(WorldGroup.ONEBLOCK.getWorldNames());
	}};

	public boolean isEnabled() {
		final Player player = getOnlinePlayer();
		if (!Nerd.of(this).hasMoved())
			return true;
		if (isOnline() && !PlayerUtils.isVanished(player) && player.getWorld().getName().equals("bearfair21"))
			return false;
		if (isOnline() && !Rank.of(player).isStaff())
			return false;
		if (isOnline() && disabledWorlds.contains(player.getWorld().getName()))
			return false;
		return enabled;
	}

	public boolean isEnabledRaw() {
		return enabled;
	}

}
