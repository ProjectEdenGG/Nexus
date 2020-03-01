package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

/**
 * @author Camaros
 */
@Aliases("dw")
@NoArgsConstructor
@Permission("durabilitywarning.use")
public class DurabilityWarningCommand extends CustomCommand implements Listener {
	private static final ArrayList<Player> disabledPlayers = new ArrayList<>();

	public DurabilityWarningCommand(CommandEvent event) {
		super(event);
	}

	static {
		BNCore.registerListener(new DurabilityWarningCommand());
	}

	@Path
	void toggle() {
		if (!disabledPlayers.contains(player())) {
			disabledPlayers.add(player());
			send(PREFIX + "Disabled");
		} else {
			disabledPlayers.remove(player());
			send(PREFIX + "Enabled");
		}
	}

	//All checkpoints with colors. More points can be added to this list.
	private double[] checkPoints = {0.10, 0.05};
	private ChatColor[] colors = {ChatColor.RED, ChatColor.DARK_RED};

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {

		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		if (!player.hasPermission("durabilitywarning.use")) return;
		if (disabledPlayers.contains(player)) return;

		int maxDurability = item.getType().getMaxDurability();
		int oldDurability = maxDurability - (item.getDurability());
		int newDurability = maxDurability - (item.getDurability() + event.getDamage());
		double oldPercentage = (double) oldDurability / (double) maxDurability;
		double newPercentage = (double) newDurability / (double) maxDurability;

		String itemName = item.getItemMeta().hasDisplayName() ?
				item.getItemMeta().getDisplayName() :
				item.getType().name().replaceAll("_", " ").toLowerCase();

		for (int i = 0; i < checkPoints.length; i++) {
			if (hasDroppedBelowPercentage(checkPoints[i], oldPercentage, newPercentage)) {
				player.sendMessage(StringUtils.getPrefix("DurabilityWarning") + colors[i] + "Your " + itemName + colors[i] + "'s durability "
						+ "has dropped below " + (int) (checkPoints[i] * 100) + "% (" + newDurability + " uses left)");
			}
		}
	}

	private boolean hasDroppedBelowPercentage(double percent, double previous, double current) {
		return previous >= percent && current < percent;
	}
}
