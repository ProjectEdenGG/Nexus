package gg.projecteden.nexus.features.events.aeveonproject.effects;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.TickTime;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static gg.projecteden.nexus.features.events.aeveonproject.APUtils.isInWorld;
import static gg.projecteden.nexus.features.events.aeveonproject.AeveonProject.worldguard;

public class GravLift implements Listener {

	static List<Player> inGravlift = new ArrayList<>();

	public GravLift() {
		Nexus.registerListener(this);

		Tasks.repeat(0, TickTime.TICK.x(5), () -> {
			List<Player> inGravLiftCopy = new ArrayList<>(inGravlift);
			for (Player player : inGravLiftCopy) {
				Set<ProtectedRegion> regions = worldguard().getRegionsAt(player.getLocation());
				// Make sure they are in the region still
				boolean verify = false;
				for (ProtectedRegion region : regions) {
					if (region.getId().contains("gravlift")) {
						verify = true;
						break;
					}
				}
				if (!verify) {
					inGravlift.remove(player);
					continue;
				}
				//

				if (player.isSneaking())
					player.addPotionEffect(new PotionEffect(PotionEffectType.SLOW_FALLING, 6, 4, false, false, false));
				else
					player.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 6, 3, false, false, false));
			}
		});
	}

	@EventHandler
	public void onEnterRegion_GravLift(PlayerEnteredRegionEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("gravlift")) return;

		if (inGravlift.contains(player)) return;
		inGravlift.add(player);
	}

	@EventHandler
	public void onExitRegion_GravLift(PlayerLeftRegionEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("gravlift")) return;

		if (!inGravlift.contains(player)) return;
		inGravlift.remove(player);
	}
}
