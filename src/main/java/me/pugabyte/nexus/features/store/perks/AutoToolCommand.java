package me.pugabyte.nexus.features.store.perks;

import eden.utils.Utils.MinMaxResult;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.annotations.Permission;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.autotool.AutoTool;
import me.pugabyte.nexus.models.autotool.AutoToolService;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.PlayerUtils.getAllInventoryContents;

@NoArgsConstructor
@Permission("autotool.use")
public class AutoToolCommand extends CustomCommand implements Listener {
	private final AutoToolService service = new AutoToolService();
	private AutoTool autoTool;

	public AutoToolCommand(@NonNull CommandEvent event) {
		super(event);
		if (isPlayerCommandEvent())
			autoTool = service.get(player());
	}

	@Path("[enabled]")
	void toggle(Boolean enabled) {
		if (AutoTool.getDisabledWorlds().contains(world().getName()))
			error("You cannot use AutoTool in this world");

		if (enabled == null)
			enabled = !autoTool.isEnabledRaw();

		autoTool.setEnabled(enabled);
		service.save(autoTool);

		send(PREFIX + (enabled ? "&aEnabled" : "&cDisabled"));
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
		Block block = event.getClickedBlock();

		if (event.getAction() != Action.LEFT_CLICK_BLOCK)
			return;
		if (block == null)
			return;
		if (!player.hasPermission("autotool.use"))
			return;
		if (event.getHand() != EquipmentSlot.HAND)
			return;

		MinMaxResult<ItemStack> result = Utils.getMax(getAllInventoryContents(player), item -> {
			if (isNullOrAir(item))
				return 0;
			if (!block.isValidTool(item))
				return 0;
			if (item.getType().name().contains("GOLDEN"))
				return 0;

			float speed = block.getDestroySpeed(item);
			if (speed > 1)
				return speed;

			return 0;
		});

		ItemStack bestTool = result.getObject();

		if (isNullOrAir(bestTool))
			return;
		if (bestTool.equals(event.getItem()))
			return;

		ItemStack[] contents = player.getInventory().getContents();

		for (int i = 0; i <= 8; i++) {
			ItemStack slot = contents[i];
			if (isNullOrAir(slot))
				continue;

			if (slot.equals(bestTool)) {
				player.getInventory().setHeldItemSlot(i);
				return;
			}
		}

	}

}
