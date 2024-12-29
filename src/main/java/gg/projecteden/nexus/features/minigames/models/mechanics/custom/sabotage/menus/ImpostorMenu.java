package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus;

import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.arenas.SabotageArena;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Tasks;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

// this should be called SabotageMenu but there's already a SabotageMenu so oh well
@Getter
@Title("&4Sabotage")
public class ImpostorMenu extends InventoryProvider {
	private final SabotageArena arena;
	private final Set<Tasks> sabotages;

	public ImpostorMenu(SabotageArena arena) {
		this.arena = arena;
		sabotages = arena.getTasks().stream().filter(task -> task.getTaskType() == Tasks.TaskType.SABOTAGE).collect(Collectors.toCollection(LinkedHashSet::new));
	}

	@Override
	protected int getRows(Integer page) {
		return MenuUtils.calculateRows(sabotages.size(), 0);
	}

	@Override
	public void init() {
		Minigamer minigamer = Minigamer.of(viewer);
		Match match = minigamer.getMatch();
		SabotageMatchData matchData = match.getMatchData();
		match.getTasks().repeat(1, 2, () -> {
			int row = 0;
			int col = 0;
			// TODO: block sabotages/doors if one of the other was just called
			boolean canSabotage = matchData.getSabotage() == null;
			ItemBuilder builder = new ItemBuilder(canSabotage ? Material.WHITE_CONCRETE : Material.BLACK_CONCRETE);
			for (Tasks tasks : sabotages) {
				contents.set(row, col, ClickableItem.of(builder.clone().name(StringUtils.camelCase(tasks.name())).build(), $ -> sabotage(minigamer, tasks)));
				row += 1;
				if (row == 9) {
					row = 0;
					col += 1;
				}
			}
		});
	}

	private void sabotage(Minigamer player, Tasks task) {
		SabotageMatchData matchData = player.getMatch().getMatchData();
		if (matchData.getSabotage() == null)
			matchData.sabotage(task);
		else
			new SoundBuilder(Sound.ENTITY_VILLAGER_NO).receiver(player).category(SoundCategory.VOICE).volume(.8f).play();
	}

}
