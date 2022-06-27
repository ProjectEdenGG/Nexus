package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.menus.api.TemporaryMenuListener;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.striplogs.StripLogs;
import gg.projecteden.nexus.models.striplogs.StripLogs.Behavior;
import gg.projecteden.nexus.models.striplogs.StripLogsService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
public class StripLogsCommand extends CustomCommand implements Listener {
	private final StripLogsService service = new StripLogsService();
	private StripLogs stripLogs;

	public StripLogsCommand(CommandEvent event) {
		super(event);
		stripLogs = service.get(player());
	}

	@Path
	void convert() {
		new StripLogsMenu(player());
	}

	@Data
	@Title("&6Stripped Logs Exchange")
	public static class StripLogsMenu implements TemporaryMenuListener {
		private final Player player;

		public StripLogsMenu(Player player) {
			this.player = player;
			open(6);
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			for (ItemStack item : contents) {
				if (isNullOrAir(item))
					continue;

				if (MaterialTag.TREE_LOGS.isTagged(item.getType()) || MaterialTag.TREE_WOOD.isTagged(item.getType()))
					item.setType(Material.valueOf("STRIPPED_" + item.getType().name()));

				PlayerUtils.giveItem((Player) event.getPlayer(), item);
			}
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
