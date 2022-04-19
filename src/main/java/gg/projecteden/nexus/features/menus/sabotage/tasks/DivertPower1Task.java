package gg.projecteden.nexus.features.menus.sabotage.tasks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata.DivertPower1TaskPartData;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DivertPower1Task extends AbstractTaskMenu {
	private static final ItemStack EMPTY_ITEM = new ItemBuilder(Material.ORANGE_STAINED_GLASS_PANE).name(" ").build();
	private static final ItemStack LEVER_ITEM = new ItemBuilder(Material.RED_STAINED_GLASS_PANE).name(" ").build();
	private static final ItemStack CLICKED_LEVER_ITEM = new ItemBuilder(Material.GREEN_STAINED_GLASS_PANE).name(" ").build();

	public DivertPower1Task(Task task) {
		super(task);
	}

	@Getter
	private final SmartInventory inventory = SmartInventory.builder()
		.maxSize()
		.title("Divert Power")
		.listener(handleInvClose)
		.provider(this)
		.build();

	@Override
	public void init(Player player, InventoryContents contents) {
		int leverCol = task.<DivertPower1TaskPartData>getData().getLever();
		for (int i = 0; i < 9; i++) {
			ClickableItem item = i == leverCol
				? ClickableItem.of(LEVER_ITEM, $ -> {
					contents.set(5, leverCol, ClickableItem.empty(CLICKED_LEVER_ITEM));
					task.partCompleted(player);
					scheduleInvClose(player);
			})
				: ClickableItem.empty(EMPTY_ITEM);
			contents.set(5, leverCol, item);
		}
		// TODO: rip panel texture from the game?
	}
}
