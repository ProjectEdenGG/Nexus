package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

@Rows(3)
@Title("Reactor Meltdown")
public class ReactorTask extends AbstractTaskMenu {

	public ReactorTask(Task task) {
		super(task);
	}

	@Override
	public void init() {
		contents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.RED_CONCRETE).name("&cWaiting...").lore("&4Waiting for another crew member").build()));
	}

}
