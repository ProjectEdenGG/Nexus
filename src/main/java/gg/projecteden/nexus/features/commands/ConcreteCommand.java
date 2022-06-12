package gg.projecteden.nexus.features.commands;

import gg.projecteden.nexus.features.listeners.TemporaryMenuListener;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.tip.Tip;
import gg.projecteden.nexus.models.tip.Tip.TipType;
import gg.projecteden.nexus.models.tip.TipService;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.worldgroup.WorldGroup;
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

@NoArgsConstructor
@Description("Turn your concrete powder into hardened concrete with ease")
public class ConcreteCommand extends CustomCommand implements Listener {

	public ConcreteCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void concrete() {
		new ConcreteMenu(player());
	}

	@Data
	public static class ConcreteMenu implements TemporaryMenuListener {
		private static final String TITLE = "Concrete Exchange";
		private final Player player;

		public ConcreteMenu(Player player) {
			this.player = player;
			open(6);
		}

		@Override
		public String getTitle() {
			return TITLE;
		}

		@Override
		public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
			for (ItemStack item : contents) {
				if (!MaterialTag.CONCRETE_POWDERS.isTagged(item.getType())) {
					PlayerUtils.giveItem((Player) event.getPlayer(), item);
					continue;
				}

				item.setType(Material.valueOf(item.getType().name().replace("_POWDER", "")));
				PlayerUtils.giveItem((Player) event.getPlayer(), item);
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent event) {
		if (WorldGroup.of(event.getPlayer()) != WorldGroup.SURVIVAL)
			return;

		if (!MaterialTag.CONCRETE_POWDERS.isTagged(event.getBlock().getType()))
			return;

		TipService tipService = new TipService();
		Tip tip = tipService.get(event.getPlayer());
		if (tip.show(TipType.CONCRETE))
			send(event.getPlayer(), "&3Did you know? &e- &3You can use &c/concrete &3to easily convert concrete powder into hardened concrete.");
	}

}
