package me.pugabyte.nexus.features.menus.sabotage.tasks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import lombok.Getter;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import me.pugabyte.nexus.utils.AdventureUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
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
			.size(3, 9)
			.build();

	@Override
	public void init(Player player, InventoryContents contents) {
		contents.set(1, 4, ClickableItem.empty(new ItemBuilder(Material.RED_CONCRETE).name("&cWaiting...").lore("&4Waiting for another crew member").build()));
	}

	public static boolean isOpen(HasPlayer player) {
		return AdventureUtils.asPlainText(player.getPlayer().getOpenInventory().title()).equals(TITLE);
	}
}
