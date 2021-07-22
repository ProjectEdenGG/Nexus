package gg.projecteden.nexus.features.menus.sabotage.tasks;

import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata.LightsTaskPartData;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.concurrent.atomic.AtomicInteger;

public class LightsTask extends AbstractTaskMenu {
	@Getter
	private final SmartInventory inventory = SmartInventory.builder()
			.title("")
			.size(3, 9)
			.provider(this)
			.build();

	private final LightsTaskPartData data;
	public LightsTask(Task task) {
		super(task);
		task.nextPart();
		data = task.getData();
	}

	@Override
	public void init(Player player, InventoryContents contents) {
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
				contents.set(1, (index * 2) - 1, ClickableItem.from(new ItemBuilder(swtch ? Material.LIME_CONCRETE : Material.GREEN_TERRACOTTA).name(swtch ? "&aON" : "&2OFF").build(), $ -> {
					if (data.toggle(thisIndex))
						matchData.endSabotage();
				}));
				index += 1;
			}
		}));
	}
}
