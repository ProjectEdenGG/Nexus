package gg.projecteden.nexus.features.menus.sabotage.tasks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.SmartInventory;
import gg.projecteden.nexus.features.menus.api.content.InventoryContents;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.utils.AdventureUtils;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.parchment.HasPlayer;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ReactorTask extends AbstractTaskMenu {
	public ReactorTask(Task task) {
		super(task);
	}

	public static final String TITLE = "Reactor Meltdown";
	@Getter
	private final SmartInventory inventory = SmartInventory.builder()
			.title(TITLE)
			.provider(this)
			.rows(3)
			.build();

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.RED_CONCRETE).name("&cWaiting...").lore("&4Waiting for another crew member").build()));
	}

	public static boolean isOpen(HasPlayer player) {
		return AdventureUtils.asPlainText(player.getPlayer().getOpenInventory().title()).equals(TITLE);
	}
}
