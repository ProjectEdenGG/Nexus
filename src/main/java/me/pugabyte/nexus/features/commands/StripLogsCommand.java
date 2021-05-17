package me.pugabyte.nexus.features.commands;

import lombok.NoArgsConstructor;
import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.striplogs.StripLogs;
import me.pugabyte.nexus.models.striplogs.StripLogs.Behavior;
import me.pugabyte.nexus.models.striplogs.StripLogsService;
import me.pugabyte.nexus.utils.ItemUtils;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.StringUtils;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

@NoArgsConstructor
public class StripLogsCommand extends CustomCommand implements Listener {
	private static final String TITLE = StringUtils.colorize("&6Stripped Logs Exchange");
	private final StripLogsService service = new StripLogsService();
	private StripLogs stripLogs;

	public StripLogsCommand(CommandEvent event) {
		super(event);
		stripLogs = service.get(player());
	}

	@Path
	void convert() {
		Inventory inv = Bukkit.createInventory(null, 54, TITLE);
		player().openInventory(inv);
	}

	@EventHandler
	public void onChestClose(InventoryCloseEvent event) {
		if (event.getInventory().getHolder() != null) return;
		if (!Utils.equalsInvViewTitle(event.getView(), TITLE)) return;

		for (ItemStack item : event.getInventory().getContents()) {
			if (ItemUtils.isNullOrAir(item))
				continue;

			if (MaterialTag.TREE_LOGS.isTagged(item.getType()) || MaterialTag.TREE_WOOD.isTagged(item.getType()))
				item.setType(Material.valueOf("STRIPPED_" + item.getType().name()));

			PlayerUtils.giveItem((Player) event.getPlayer(), item);
		}
	}

	@Path("behavior <behavior>")
	@Description("Change how you strip logs (default, require shift, or prevent)")
	void behavior(Behavior behavior) {
		stripLogs.setBehavior(behavior);
		service.save(stripLogs);

		send(PREFIX + "Behavior set to " + camelCase(behavior));
	}

	@EventHandler
	public void onStripLogs(BlockPlaceEvent event) {
		Player player = event.getPlayer();

		Material before = event.getBlockReplacedState().getType();
		Material after = event.getBlockPlaced().getType();

		boolean stripping = false;
		if (MaterialTag.TREE_LOGS.isTagged(before) && MaterialTag.STRIPPED_LOGS.isTagged(after))
			stripping = true;
		if (MaterialTag.TREE_WOOD.isTagged(before) && MaterialTag.STRIPPED_WOOD.isTagged(after))
			stripping = true;

		if (!stripping)
			return;

		StripLogs stripLogs = new StripLogsService().get(player);
		switch (stripLogs.getBehavior()) {
			case REQUIRE_SHIFT:
				if (!event.getPlayer().isSneaking())
					event.setCancelled(true);
				break;
			case PREVENT:
				event.setCancelled(true);
				break;
		}
	}

}
