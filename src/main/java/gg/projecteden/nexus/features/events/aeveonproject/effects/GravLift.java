package gg.projecteden.nexus.features.events.aeveonproject.effects;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.AeveonProject;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.PotionEffectBuilder;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GravLift implements Listener {

	private static final List<UUID> IN_GRAVLIFT = new ArrayList<>();

	public GravLift() {
		Nexus.registerListener(this);

		Tasks.repeat(0, TickTime.TICK.x(5), () -> {
			List<UUID> inGravLiftCopy = new ArrayList<>(IN_GRAVLIFT);
			for (UUID uuid : inGravLiftCopy) {
				var player = Bukkit.getPlayer(uuid);
				if (player == null || !player.isOnline())
					continue;

				Set<ProtectedRegion> regions = AeveonProject.worldguard().getRegionsAt(player.getLocation());
				// Make sure they are in the region still
				boolean verify = false;
				for (ProtectedRegion region : regions) {
					if (region.getId().contains("gravlift")) {
						verify = true;
						break;
					}
				}
				if (!verify) {
					IN_GRAVLIFT.remove(player.getUniqueId());
					continue;
				}
				//

				PotionEffectBuilder effectBuilder = new PotionEffectBuilder().duration(6);
				if (player.isSneaking())
					player.addPotionEffect(effectBuilder.type(PotionEffectType.SLOW_FALLING).amplifier(4).build());
				else
					player.addPotionEffect(effectBuilder.type(PotionEffectType.LEVITATION).amplifier(3).build());
			}
		});
	}

	@EventHandler
	public void onEnterRegion_GravLift(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("gravlift")) return;

		if (IN_GRAVLIFT.contains(player.getUniqueId())) return;
		IN_GRAVLIFT.add(player.getUniqueId());
	}

	@EventHandler
	public void onExitRegion_GravLift(PlayerLeftRegionEvent event) {
		Player player = event.getPlayer();
		if (!APUtils.isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("gravlift")) return;

		if (!IN_GRAVLIFT.contains(player.getUniqueId())) return;
		IN_GRAVLIFT.remove(player.getUniqueId());
	}
}
