package me.pugabyte.bncore.features.holidays.halloween20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.halloween20.models.Pumpkin;
import me.pugabyte.bncore.features.holidays.halloween20.models.QuestStage;
import me.pugabyte.bncore.models.halloween20.Halloween20Service;
import me.pugabyte.bncore.models.halloween20.Halloween20User;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;

public class LostPumpkins implements Listener {
	private static final String PREFIX = StringUtils.getPrefix("LostPumpkins");

	public LostPumpkins() {
		BNCore.registerListener(this);
		startParticleTask();
	}

	private void startParticleTask() {
		Tasks.repeatAsync(0, 2 * 20, () -> {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getWorld() != Halloween20.getWorld())
					continue;

				Halloween20User user = new Halloween20Service().get(player);
				for (Pumpkin pumpkin : Pumpkin.values()) {
					if (user.getFoundPumpkins().contains(pumpkin.getOriginal())) continue;
					player.spawnParticle(Particle.REDSTONE, pumpkin.getOriginal(), 5, new DustOptions(Color.ORANGE, 1));
				}
			}
		});
	}

	// Finding Pumpkins
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!Arrays.asList(Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK).contains(event.getAction()) || event.getClickedBlock() == null) return;
		Pumpkin pumpkin = Pumpkin.getByLocation(event.getClickedBlock().getLocation());
		if (pumpkin == null) return;
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(event.getPlayer());
		if (user.getLostPumpkinsStage() == QuestStage.LostPumpkins.NOT_STARTED) {
			user.send(PREFIX + "This looks like it should be in the pumpkin carving contest. Maybe I should talk talk to &e(NPC).");
			return;
		}
		if (user.getFoundPumpkins().contains(event.getClickedBlock())) {
			user.send(PREFIX + "&cYou have already found that pumpkin");
			return;
		}
		user.getFoundPumpkins().add(event.getClickedBlock().getLocation());
		event.getPlayer().sendBlockChange(event.getClickedBlock().getLocation(), Material.AIR.createBlockData());
		event.getPlayer().sendBlockChange(pumpkin.getEnd(), event.getClickedBlock().getBlockData());
		if (user.getFoundPumpkins().size() == 8) {
			user.send(PREFIX + "You have found the last pumpkin! Talk to &e(NPC) &3at the pumpkin carving contest");
		}
	}

	// Update Pumpkins Per User
	@EventHandler
	public void onEnterRegion(RegionEnteredEvent event) {
		Player player = event.getPlayer();
		if (!event.getRegion().getId().equalsIgnoreCase(Halloween20.getRegion())) return;
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
