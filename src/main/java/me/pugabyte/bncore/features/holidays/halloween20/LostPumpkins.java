package me.pugabyte.bncore.features.holidays.halloween20;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.halloween20.models.Pumpkin;
import me.pugabyte.bncore.features.holidays.halloween20.models.QuestStage;
import me.pugabyte.bncore.models.halloween20.Halloween20Service;
import me.pugabyte.bncore.models.halloween20.Halloween20User;
import me.pugabyte.bncore.utils.NMSUtils;
import me.pugabyte.bncore.utils.StringUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import me.pugabyte.bncore.utils.Utils.ActionGroup;
import org.bukkit.*;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

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
					player.spawnParticle(Particle.REDSTONE, Utils.getCenteredLocation(pumpkin.getOriginal()), 5, .5, .5, .5, new DustOptions(Color.ORANGE, 1));
				}
			}
		});
	}

	// Finding Pumpkins
	@EventHandler
	public void onClick(PlayerInteractEvent event) {
		if (!ActionGroup.CLICK_BLOCK.applies(event)) return;
		if (event.getHand() != EquipmentSlot.HAND) return;
		Pumpkin pumpkin = Pumpkin.getByLocation(event.getClickedBlock().getLocation());
		if (pumpkin == null) return;
		Halloween20Service service = new Halloween20Service();
		Halloween20User user = service.get(event.getPlayer());
		if (user.getLostPumpkinsStage() == QuestStage.LostPumpkins.NOT_STARTED) {
			user.send(PREFIX + "This looks like it should be in the pumpkin carving contest. Maybe I should talk talk to &eJeffery &3at the pumpkin carving contest.");
			return;
		}
		if (user.getFoundPumpkins().contains(event.getClickedBlock().getLocation())) {
			user.send(PREFIX + "&cYou have already found that pumpkin");
			return;
		}
		user.getFoundPumpkins().add(event.getClickedBlock().getLocation());
		event.getPlayer().sendBlockChange(event.getClickedBlock().getLocation(), Material.AIR.createBlockData());
		NMSUtils.copyTileEntityClient(event.getPlayer(), pumpkin.getEnd(), pumpkin.getOriginal().getBlock());
		user.send(PREFIX + "You have just found a pumpkin! It has been returned to Jeffery. &e(" + user.getFoundPumpkins().size() + "/8)");
		if (user.getFoundPumpkins().size() == 8) {
			user.send(PREFIX + "You have found the last pumpkin! Talk to &eJeffery &3at the pumpkin carving contest.");
			user.setLostPumpkinsStage(QuestStage.LostPumpkins.FOUND_ALL);
			service.save(user);
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
			event.getPlayer().sendBlockChange(pumpkin.getOriginal(), Material.AIR.createBlockData());
			NMSUtils.copyTileEntityClient(event.getPlayer(), pumpkin.getEnd(), pumpkin.getOriginal().getBlock());
		}
	}


}
