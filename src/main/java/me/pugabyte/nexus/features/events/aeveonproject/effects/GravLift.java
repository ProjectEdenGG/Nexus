package me.pugabyte.nexus.features.events.aeveonproject.effects;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static me.pugabyte.nexus.features.events.aeveonproject.APUtils.isInWorld;
import static me.pugabyte.nexus.features.events.aeveonproject.AeveonProject.getWGUtils;

public class GravLift implements Listener {

	static List<Player> inGravlift = new ArrayList<>();

	public GravLift() {
		Nexus.registerListener(this);

		Tasks.repeat(0, Time.TICK.x(5), () -> {
			List<Player> inGravLiftCopy = new ArrayList<>(inGravlift);
			for (Player player : inGravLiftCopy) {
				Set<ProtectedRegion> regions = getWGUtils().getRegionsAt(player.getLocation());
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
	public void onEnterRegion_GravLift(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("gravlift")) return;

		if (inGravlift.contains(player)) return;
		inGravlift.add(player);
	}

	@EventHandler
	public void onExitRegion_GravLift(RegionLeftEvent event) {
		Player player = event.getPlayer();
		if (!isInWorld(player)) return;

		String id = event.getRegion().getId();
		if (!id.contains("gravlift")) return;

		if (!inGravlift.contains(player)) return;
		inGravlift.remove(player);
	}
}
