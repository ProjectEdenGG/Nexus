package gg.projecteden.nexus.features.menus.sabotage.tasks;

import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata.LightsTaskPartData;
import gg.projecteden.nexus.utils.ItemBuilder;
import org.bukkit.Material;

import java.util.concurrent.atomic.AtomicInteger;

@Rows(3)
public class LightsTask extends AbstractTaskMenu {
	private final LightsTaskPartData data;

	public LightsTask(Task task) {
		super(task);
		task.nextPart();
		data = task.getData();
	}

	@Override
	public void init() {
		AtomicInteger taskId = new AtomicInteger();
		Match match = PlayerManager.get(player).getMatch();
		SabotageMatchData matchData = match.getMatchData();
		taskId.set(match.getTasks().repeat(1, 1, () -> {
			if (matchData.getSabotage() == null)
				match.getTasks().cancel(taskId.get());
			contents.fill(ClickableItem.empty(new ItemBuilder(Material.BLACK_STAINED_GLASS).name(" ").build()));
			int index = 0;
			for (boolean swtch : data.getSwitches()) {
				final int thisIndex = index;
				contents.set(1, (index * 2) - 1, ClickableItem.of(new ItemBuilder(swtch ? Material.LIME_CONCRETE : Material.GREEN_TERRACOTTA).name(swtch ? "&aON" : "&2OFF").build(), $ -> {
					if (data.toggle(thisIndex))
						matchData.endSabotage();
				}));
				index += 1;
			}
		}));
	}

}
