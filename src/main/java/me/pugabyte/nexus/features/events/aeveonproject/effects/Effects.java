package me.pugabyte.nexus.features.events.aeveonproject.effects;

import eden.utils.TimeUtils.Time;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.aeveonproject.APUtils;
import me.pugabyte.nexus.features.events.aeveonproject.sets.lobby.Lobby;
import me.pugabyte.nexus.features.events.aeveonproject.sets.sialia.Sialia;
import me.pugabyte.nexus.features.events.aeveonproject.sets.sialiaCrashing.SialiaCrashing;
import me.pugabyte.nexus.models.cooldown.CooldownService;
import me.pugabyte.nexus.utils.EntityUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

import static me.pugabyte.nexus.features.events.aeveonproject.APUtils.isInWorld;

public class Effects implements Listener {
	public Effects() {
		Nexus.registerListener(this);

		new DockingPorts();
		new GravLift();
		new PlayerTime();
		new ClientsideBlocks();

		shipRobotTask();
	}

	private void shipRobotTask() {
		List<Location> shipRobotLocs = Arrays.asList(Lobby.shipRobot, Sialia.shipRobot, SialiaCrashing.shipRobot);
		Tasks.repeat(0, 2, () -> {
			for (Location shipRobot : shipRobotLocs) {
				Player nearestPlayer = (Player) EntityUtils.getNearestEntityType(shipRobot, EntityType.PLAYER, 7);
				ArmorStand armorStand = (ArmorStand) EntityUtils.getNearestEntityType(shipRobot, EntityType.ARMOR_STAND, 1);
				if (nearestPlayer != null && armorStand != null) {
					APUtils.makeArmorStandLookAtPlayer(armorStand, nearestPlayer);
				}
			}
		});
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
			PlayerUtils.runCommandAsOp(player, "sit");
		}
	}
}
