package me.pugabyte.bncore.features.commands.staff;

import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.framework.exceptions.postconfigured.InvalidInputException;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@NoArgsConstructor
@Permission("rank.owner")
public class SpamShitCommand extends CustomCommand implements Listener {
	private static boolean spamming = false;
	private static Map<Material, Integer> taskIds = new HashMap<>();
	private static Map<Material, Class<? extends Projectile>> projectiles = new HashMap<Material, Class<? extends Projectile>>() {{
		put(Material.EGG, Egg.class);
		put(Material.STICK, Arrow.class);
		put(Material.SNOWBALL, Snowball.class);
		put(Material.FIRE_CHARGE, Fireball.class);
	}};

	public SpamShitCommand(@NonNull CommandEvent event) {
		super(event);
	}

	boolean isPug(Player player) {
		return player.getUniqueId().equals(Utils.getPlayer("Pugabyte").getUniqueId());
	}

	@Path
	void run() {
		if (!isPug(player()))
			throw new InvalidInputException("You cannot run this command");

		spamming = !spamming;
		send("Spamming shit turned " + (spamming ? "on" : "off"));

		if (!spamming) {
			taskIds.forEach((material, taskId) -> Tasks.cancel(taskId));
			taskIds.clear();
		}
	}

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (!spamming)
			return;

		Player player = event.getPlayer();
		if (!isPug(player))
			return;

		if (!Arrays.asList(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR).contains(event.getAction()))
			return;

		Material material = player.getInventory().getItemInMainHand().getType();
		if (!projectiles.containsKey(material))
			return;

		event.setCancelled(true);

		if (taskIds.containsKey(material)) {
			Tasks.cancel(taskIds.get(material));
			taskIds.remove(material);
		} else {
			int taskId = Tasks.repeat(0, 1, () -> player.launchProjectile(projectiles.get(material), player.getLocation().getDirection().multiply(100)));
			taskIds.put(material, taskId);
		}
	}

}
