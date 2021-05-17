package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Aliases;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.durabilitywarning.DurabilityWarning;
import me.pugabyte.nexus.models.durabilitywarning.DurabilityWarningService;
import me.pugabyte.nexus.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

/**
 * @author Camaros
 */
@Aliases("dw")
@NoArgsConstructor
@Description("Toggle whether you want to notified of a tool or armor piece that is low on durability")
public class DurabilityWarningCommand extends CustomCommand implements Listener {
	private final DurabilityWarningService service = new DurabilityWarningService();
	private DurabilityWarning durabilityWarning;

	public DurabilityWarningCommand(CommandEvent event) {
		super(event);
		if (isPlayer())
			durabilityWarning = service.get(player());
	}

	@Path("[enable]")
	void toggle(Boolean enable) {
		if (enable == null)
			enable = !durabilityWarning.isEnabled();

		durabilityWarning.setEnabled(enable);
		service.save(durabilityWarning);

		send(PREFIX + (enable ? "&aEnabled" : "&cDisabled"));
	}

	//All checkpoints with colors. More points can be added to this list.
	private double[] checkPoints = {0.10, 0.05};
	private ChatColor[] colors = {ChatColor.RED, ChatColor.DARK_RED};

	@EventHandler
	public void onItemDamage(PlayerItemDamageEvent event) {
		Player player = event.getPlayer();
		ItemStack item = event.getItem();

		DurabilityWarning durabilityWarning = new DurabilityWarningService().get(player);
		if (!durabilityWarning.isEnabled()) return;

		int maxDurability = item.getType().getMaxDurability();
		int damage = ((Damageable) item.getItemMeta()).getDamage();
		int oldDurability = maxDurability - damage;
		int newDurability = oldDurability - event.getDamage();

		if (newDurability <= 0)
			return;

		double oldPercentage = (double) oldDurability / (double) maxDurability;
		double newPercentage = (double) newDurability / (double) maxDurability;

		for (int i = 0; i < checkPoints.length; i++)
			if (hasDroppedBelowPercentage(checkPoints[i], oldPercentage, newPercentage)) {
				String itemName = item.getItemMeta().hasDisplayName() ?
						item.getItemMeta().getDisplayName() :
						item.getType().name().replaceAll("_", " ").toLowerCase();

				if (!itemName.endsWith("s"))
					itemName = itemName + colors[i] + "'s";
				else
					itemName = itemName + colors[i] + "'";

				send(player, StringUtils.getPrefix("DurabilityWarning") + colors[i] + "Your " + itemName + " durability "
						+ "has dropped below " + (int) (checkPoints[i] * 100) + "% (" + newDurability + " uses left)");
			}
	}

	private boolean hasDroppedBelowPercentage(double percent, double previous, double current) {
		return previous >= percent && current < percent;
	}
}
