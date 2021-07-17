package me.pugabyte.nexus.features.menus.sabotage.tasks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import lombok.Getter;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import me.pugabyte.nexus.utils.ItemBuilder;
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
