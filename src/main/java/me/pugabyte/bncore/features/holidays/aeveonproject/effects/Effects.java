package me.pugabyte.bncore.features.holidays.aeveonproject.effects;

import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.models.cooldown.CooldownService;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import static me.pugabyte.bncore.features.holidays.aeveonproject.AeveonProject.isInWorld;

public class Effects implements Listener {
	public Effects() {
		BNCore.registerListener(this);

		new DockingPorts();
		new GravLift();
		new PlayerTime();
		new ClientsideBlocks();
	}

	// Netherbrick chairs
	@EventHandler
	public void onClickNetherBrickStair(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block clicked = player.getTargetBlockExact(2);
		if (!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
		if (clicked == null) return;
		if (!isInWorld(clicked)) return;
		if (!(new CooldownService().check(player, "AeveonProject_Sit", Time.SECOND.x(2)))) return;

		if (clicked.getType().equals(Material.NETHER_BRICK_STAIRS)) {
			Utils.runCommandAsOp(player, "sit");
		}
	}
}
