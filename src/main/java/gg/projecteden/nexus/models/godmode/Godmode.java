package gg.projecteden.nexus.models.godmode;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.bearfair21.BearFair21ConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

import static gg.projecteden.nexus.models.bearfair21.BearFair21Config.BearFair21ConfigOption.WARP;

@Data
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
	private static final List<WorldGroup> disabledWorlds = List.of(WorldGroup.MINIGAMES, WorldGroup.SKYBLOCK);

	public boolean isActive() {
		if (!isOnline())
			return false;

		final Player player = getOnlinePlayer();
		if (!Nerd.of(this).hasMoved())
			return true;
		if (player.getWorld().getName().equals("bearfair21") && new BearFair21ConfigService().get0().isEnabled(WARP) && !PlayerUtils.isVanished(player))
			return false;
		if (!Rank.of(player).isStaff())
			return false;
		if (disabledWorlds.contains(WorldGroup.of(player)))
			return false;

		return enabled;
	}

}
