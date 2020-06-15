package me.pugabyte.bncore.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Aliases;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.models.durabilitywarning.DurabilityWarning;
import me.pugabyte.bncore.models.durabilitywarning.DurabilityWarningService;
import me.pugabyte.bncore.utils.StringUtils;
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

		double oldPercentage = (double) oldDurability / (double) maxDurability;
		double newPercentage = (double) newDurability / (double) maxDurability;

		for (int i = 0; i < checkPoints.length; i++)
			if (hasDroppedBelowPercentage(checkPoints[i], oldPercentage, newPercentage)) {
				String itemName = item.getItemMeta().hasDisplayName() ?
						item.getItemMeta().getDisplayName() :
						item.getType().name().replaceAll("_", " ").toLowerCase();

				player.sendMessage(StringUtils.getPrefix("DurabilityWarning") + colors[i] + "Your " + itemName + colors[i] + "'s durability "
						+ "has dropped below " + (int) (checkPoints[i] * 100) + "% (" + newDurability + " uses left)");
			}
	}

	private boolean hasDroppedBelowPercentage(double percent, double previous, double current) {
		return previous >= percent && current < percent;
	}
}
