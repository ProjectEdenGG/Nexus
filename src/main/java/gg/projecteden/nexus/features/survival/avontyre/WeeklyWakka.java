package gg.projecteden.nexus.features.survival.avontyre;

import gg.projecteden.api.common.utils.TimeUtils;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.survival.Survival;
import gg.projecteden.nexus.framework.features.Feature;
import gg.projecteden.nexus.utils.ActionBarUtils;
import gg.projecteden.nexus.utils.CitizensUtils;
import gg.projecteden.nexus.utils.Distance;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.Tasks;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class WeeklyWakka extends Feature implements Listener {

	private static final int npcId = 5079;
	private static final Map<Player, Integer> playerMap = new HashMap<>();
	public static final ItemStack trackingDevice = new ItemBuilder(CustomMaterial.DETECTOR).name("Wakka Detector").lore("&eWeekly Wakka Item").build();

	public static NPC getNPC() {
		return CitizensUtils.getNPC(npcId);
	}

	private static boolean isHoldingTrackingDevice(Player player) {
		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNullOrAir(tool))
			return false;

		return ItemUtils.isFuzzyMatch(tool, trackingDevice);
	}

	@Override
	public void onStart() {
		final int tickIncrement = 2;
		Tasks.repeat(0, TimeUtils.TickTime.TICK.x(tickIncrement), () -> {
			for (Player player : Survival.getPlayersAtSpawn()) {
				if (!isHoldingTrackingDevice(player))
					continue;

				int ticks = 0;
				if (playerMap.containsKey(player))
					ticks = playerMap.remove(player);

				Location wakkaLoc = getNPC().getStoredLocation();
				Location playerLoc = player.getLocation();
				var distance = Distance.distance(wakkaLoc, playerLoc);
				boolean sameFloor = Math.abs(wakkaLoc.getY() - playerLoc.getY()) < 5;

				double pitch = 0;
				String message = "";
				boolean playSound = false;

				if (distance.gte(175)) {
					if (ticks >= TimeUtils.TickTime.SECOND.x(3)) {
						message = "Wakka is too far away...";
						playSound = true;
						pitch = 0.5;
					}
				} else if (distance.gte(100)) {
					if (ticks >= TimeUtils.TickTime.SECOND.x(2)) {
						message = "Searching for Wakka...";
						playSound = true;
						pitch = 0.7;
					}
				} else if (distance.gte(60)) {
					if (ticks >= TimeUtils.TickTime.SECOND.x(1)) {
						message = "Wakka is around...";
						playSound = true;
						pitch = 0.9;
					}
				} else if (distance.gte(20)) {
					if (ticks >= TimeUtils.TickTime.TICK.x(10)) {
						message = "Wakka is nearby...";
						playSound = true;
						pitch = 1.2;
					}
				} else {
					if (sameFloor) {
						if (ticks >= TimeUtils.TickTime.TICK.x(5)) {
							message = "Wakka is close!";
							playSound = true;
							pitch = 1.6;
						}
					} else {
						if (ticks >= TimeUtils.TickTime.TICK.x(10)) {
							message = "Wakka is nearby...";
							playSound = true;
							pitch = 1.2;
						}
					}
				}

				if (playSound) {
					ticks = 0;
					new SoundBuilder(Sound.ENTITY_ITEM_PICKUP).location(player).pitch(pitch).volume(0.5).play();
					ActionBarUtils.sendActionBar(player, message);
				}

				ticks += tickIncrement;
				playerMap.put(player, ticks);
			}
		});
	}
}
