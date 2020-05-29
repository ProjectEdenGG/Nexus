package me.pugabyte.bncore.features.holidays.bearfair20.quests;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Collections;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class Beehive implements Listener {
	private String allowedMsg = "TODO: Allowed message here";
	private String deniedMsg = "TODO: Denied message here";
	private Location enterLoc = new Location(BearFair20.world, -1084, 135, -1548, 228, 20);
	private Location exitLoc = new Location(BearFair20.world, -1088, 136, -1548, 40, 0);
	private Material key = Material.HONEY_BOTTLE;

	public Beehive() {
		BNCore.registerListener(this);
	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String id = event.getRegion().getId();
		Player player = event.getPlayer();

		String enterRg = "bearfair2020_beehive_enter";
		String exitRg = "bearfair2020_beehive_exit";
		if (id.equalsIgnoreCase(enterRg)) {
			if (player.getInventory().contains(key)) {
				allowed(player);
			} else {
				denied(player);
			}
		} else if (id.equalsIgnoreCase(exitRg)) {
			player.teleport(exitLoc);
		}
	}

	private void allowed(Player player) {
		player.teleport(enterLoc);
		player.sendMessage(allowedMsg);
	}

	private void denied(Player player) {
		player.addPotionEffects(Collections.singletonList
				(new PotionEffect(PotionEffectType.BLINDNESS, 40, 250, false, false, false)));
		player.teleport(exitLoc);
		player.playSound(enterLoc, Sound.ENTITY_BEE_LOOP_AGGRESSIVE, 0.5F, 1F);
		Tasks.wait(5, () -> player.sendMessage(colorize(deniedMsg)));

	}
}
