package gg.projecteden.nexus.features.menus.sabotage.tasks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.jetbrains.annotations.NotNull;

@Rows(3)
@Title("Divert Power")
public class DivertPower2Task extends AbstractTaskMenu {

	public DivertPower2Task(Task task) {
		super(task);
	}

	@Override
	public @NotNull SmartInventory.Builder getInventory() {
		return super.getInventory().listener(handleInvClose);
	}

	@Override
	public void init() {
		contents.set(1, 4, ClickableItem.of(new ItemBuilder(Material.ORANGE_CONCRETE).name(" ").build(), $ -> {
			contents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.YELLOW_CONCRETE).name(" ").build()));
			task.partCompleted(player);
			scheduleInvClose(player);
		}));
	}

}
