package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.menus.MechanicsMenu;
import gg.projecteden.nexus.features.minigames.models.arenas.SabotageArena;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Tasks;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.stream.Collectors;

@Title("Enabled Tasks")
@RequiredArgsConstructor
public class ArenaTasksMenu extends InventoryProvider {
	private final SabotageArena arena;

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(Tasks.crewmateTasks().size(), 1);
	}

	@Override
	public void init() {
		addBackItem($ -> MechanicsMenu.openCustomSettingsMenu(viewer, arena));
		int row = 1;
		int col = 0;
		for (Tasks task : Tasks.values()) {
			boolean enabled = arena.getTasks().contains(task);
			contents.set(row, col, ClickableItem.of(
				new ItemBuilder(enabled ? Material.WHITE_CONCRETE : Material.BLACK_CONCRETE)
					.name("&e" + task.name())
					.lore("&e" + StringUtils.camelCase(task.getTaskType()) + "&3 task")
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
					init();
				}));

			col += 1;
			if (col == 9) {
				row += 1;
				col = 0;
			}
		}
	}

}
