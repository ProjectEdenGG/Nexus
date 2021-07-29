package gg.projecteden.nexus.features.events.aeveonproject.effects;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.aeveonproject.APUtils;
import gg.projecteden.nexus.features.events.aeveonproject.sets.lobby.Lobby;
import gg.projecteden.nexus.features.events.aeveonproject.sets.sialia.Sialia;
import gg.projecteden.nexus.features.events.aeveonproject.sets.sialiaCrashing.SialiaCrashing;
import gg.projecteden.nexus.models.cooldown.CooldownService;
import gg.projecteden.nexus.utils.EntityUtils;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.TimeUtils.Time;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.List;

import static gg.projecteden.nexus.features.events.aeveonproject.APUtils.isInWorld;

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
				if (!shipRobot.isChunkLoaded())
					return;

				Player nearestPlayer = EntityUtils.getNearestEntityType(shipRobot, Player.class, 7);
				ArmorStand armorStand = EntityUtils.getNearestEntityType(shipRobot, ArmorStand.class, 1);
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
