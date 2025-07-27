package gg.projecteden.nexus.models.godmode;

import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.nexus.features.vanish.Vanish;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.models.bearfair21.BearFair21Config;
import gg.projecteden.nexus.models.bearfair21.BearFair21ConfigService;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nerd.Rank;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;

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

	public static Godmode of(Player player) {
		return new GodmodeService().get(player);
	}

	public void setEnabled(boolean enabled) {
		IOUtils.fileAppend("cheats", getNickname() + " " + (enabled ? "enabled" : "disabled") + " god at " + StringUtils.xyzw(getLocation()));
		this.enabled = enabled;
	}

	public boolean isActive() {
		if (!isOnline())
			return false;

		final Player player = getOnlinePlayer();

		Nerd nerd = Nerd.of(this);
		if (!nerd.hasMoved())
			if (player.getLocation().getY() >= -100) // If they logged out & back in while falling into the void, don't save them
				return true;

		LocalDateTime lastSurvivalDeath = nerd.getLastDeath(WorldGroup.SURVIVAL);
		if (lastSurvivalDeath != null) {
			var secondsSinceLastDeath = lastSurvivalDeath.until(LocalDateTime.now(), ChronoUnit.SECONDS);
			if (secondsSinceLastDeath < 15)
				if (WorldGroup.SURVIVAL == WorldGroup.of(player))
					return true;
		}

		if (Vanish.isVanished(player))
			return true;

		if ("bearfair21".equals(player.getWorld().getName()) && new BearFair21ConfigService().get0().isEnabled(BearFair21Config.BearFair21ConfigOption.WARP) && !Vanish.isVanished(player))
			return false;

		if (Rank.of(player).lt(Rank.ARCHITECT))
			return false;

		if (disabledWorlds.contains(WorldGroup.of(player)))
			return false;

		return enabled;
	}

}
