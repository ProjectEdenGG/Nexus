package gg.projecteden.nexus.features.menus.sabotage.tasks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DivertPower2Task extends AbstractTaskMenu {

	public DivertPower2Task(Task task) {
		super(task);
	}

	@Getter
	private final SmartInventory inventory = SmartInventory.builder()
		.size(3, 9)
		.title("Divert Power")
		.provider(this)
		.listener(handleInvClose)
		.build();

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(1, 4, ClickableItem.from(new ItemBuilder(Material.ORANGE_CONCRETE).name(" ").build(), $ -> {
			contents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.YELLOW_CONCRETE).name(" ").build()));
			task.partCompleted(player);
			scheduleInvClose(player);
		}));
	}
}
