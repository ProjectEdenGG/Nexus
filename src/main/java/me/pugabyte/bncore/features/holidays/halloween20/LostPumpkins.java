package me.pugabyte.bncore.features.holidays.halloween20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.features.holidays.halloween20.models.Pumpkin;
import me.pugabyte.bncore.features.holidays.halloween20.models.QuestStage;
import me.pugabyte.bncore.models.halloween20.Halloween20Service;
import me.pugabyte.bncore.models.halloween20.Halloween20User;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

import static me.pugabyte.bncore.utils.StringUtils.colorize;

public class LostPumpkins implements Listener {

	public LostPumpkins() {
		startParticleTask();
	}

	private void startParticleTask() {
		Tasks.repeatAsync(0, 2 * 20, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				Halloween20Service service = new Halloween20Service();
				Halloween20User user = service.get(player);
				for (Pumpkin pumpkin : Pumpkin.values()) {
					if (user.getFoundPumpkins().contains(pumpkin.getOriginal())) continue;
					player.spawnParticle(Particle.REDSTONE, pumpkin.getOriginal(), 5);
				}
			}
		});
	}

	// Finding Pumpkins
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!Arrays.asList(Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK).contains(event.getAction())) return;
		Pumpkin pumpkin = Pumpkin.getByLocation(event.getClickedBlock().getLocation());
		if (pumpkin == null) return;
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(event.getPlayer());
		if (user.getLostPumpkinsStage() == QuestStage.LostPumpkins.NOT_STARTED) {
			event.getPlayer().sendMessage("&3This looks like it should be in the pumpkin carving contest. Maybe I should talk talk to &e(NPC).");
			return;
		}
		if (user.getFoundPumpkins().contains(event.getClickedBlock())) {
			event.getPlayer().sendMessage(colorize("&3You have already found that pumpkin."));
			return;
		}
		user.getFoundPumpkins().add(event.getClickedBlock().getLocation());
		event.getPlayer().sendBlockChange(event.getClickedBlock().getLocation(), Material.AIR.createBlockData());
		event.getPlayer().sendBlockChange(pumpkin.getEnd(), event.getClickedBlock().getBlockData());
		if (user.getFoundPumpkins().size() == 8) {
			event.getPlayer().sendMessage(colorize("&3You have found the last pumpkin! Talk to &e(NPC) &3at the pumpkin carving contest"));
		}
	}

	// Update Pumpkins Per User
	@EventHandler
	public void onEnterRegion(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!event.getRegion().getId().equalsIgnoreCase(Halloween20.region)) return;
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(player);
		for (Location loc : user.getFoundPumpkins()) {
			Pumpkin pumpkin = Pumpkin.getByLocation(loc);
			if (pumpkin == null) continue;
			Block block = pumpkin.getOriginal().getBlock();
			player.sendBlockChange(loc, Material.AIR.createBlockData());
			player.sendBlockChange(pumpkin.getEnd(), block.getBlockData());
		}
	}


}
