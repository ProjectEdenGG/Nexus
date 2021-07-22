package gg.projecteden.nexus.models.godmode;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.bearfair21.BearFair21ConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.models.bearfair21.BearFair21Config.BearFair21ConfigOption.WARP;

@Data
@Builder
@Entity(value = "godmode", noClassnameStored = true)
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
		if (!isOnline())
			return false;

		final Player player = getOnlinePlayer();
		if (!Nerd.of(this).hasMoved())
			return true;
		if (player.getWorld().getName().equals("bearfair21") && new BearFair21ConfigService().get0().isEnabled(WARP) && !PlayerUtils.isVanished(player))
			return false;
		if (!Rank.of(player).isStaff())
			return false;
		if (disabledWorlds.contains(player.getWorld().getName()))
			return false;
		return enabled;
	}

	public boolean isEnabledRaw() {
		return enabled;
	}

}
