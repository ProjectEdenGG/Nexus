package gg.projecteden.nexus.features.menus.sabotage;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.Minigames;
import gg.projecteden.nexus.features.minigames.models.arenas.SabotageArena;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Tasks;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.StringUtils.camelCase;

@RequiredArgsConstructor
public class ArenaTasksMenu extends MenuUtils implements InventoryProvider {
	private final SabotageArena arena;

	private final SmartInventory inventory = SmartInventory.builder()
			.size(getRows(Tasks.crewmateTasks().size(), 1), 9)
			.provider(this)
			.title("Enabled Tasks")
			.build();

	@Override
	public void open(Player player, int page) {
		inventory.open(player, page);
	}

	@Override
	public void init(Player player, InventoryContents contents) {
		addBackItem(contents, $ -> Minigames.getMenus().openCustomSettingsMenu(player, arena));
		int row = 1;
		int col = 0;
		for (Tasks task : Tasks.values()) {
			boolean enabled = arena.getTasks().contains(task);
			contents.set(row, col, ClickableItem.from(
					new ItemBuilder(enabled ? Material.WHITE_CONCRETE : Material.BLACK_CONCRETE)
							.name("&e" + task.name())
							.lore("&e" + camelCase(task.getTaskType()) + "&3 task")
							.lore("", "&3Parts:")
							.lore(Arrays.stream(task.getParts())
									.map(taskPart -> "&e" + taskPart.getName())
									.collect(Collectors.toList()))
							.build(),
					$ -> {
						if (enabled)
							arena.getTasks().remove(task);
						else
							arena.getTasks().add(task);
						arena.write();
						init(player, contents);
					}));

			col += 1;
			if (col == 9) {
				row += 1;
				col = 0;
			}
		}
	}
}
