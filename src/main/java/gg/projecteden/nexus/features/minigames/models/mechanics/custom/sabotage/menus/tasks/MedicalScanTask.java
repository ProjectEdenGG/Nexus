package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks;

import com.google.common.util.concurrent.AtomicDouble;
import gg.projecteden.api.common.utils.MathUtils;
import gg.projecteden.nexus.features.menus.api.ClickableItem;
import gg.projecteden.nexus.features.menus.api.annotations.Rows;
import gg.projecteden.nexus.features.menus.api.annotations.Title;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.matchdata.SabotageMatchData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.SabotageColor;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.SoundBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

@Rows(2)
@Title("Submit Scan")
public class MedicalScanTask extends AbstractTaskMenu {
	private int taskId = -1;
	private Match.MatchTasks tasks;

	public MedicalScanTask(Task task) {
		super(task);
	}

	@Override
	public void init() {
		Minigamer minigamer = Minigamer.of(viewer);
		Match match = minigamer.getMatch();
		SabotageMatchData data = match.getMatchData();
		tasks = match.getTasks();

		SabotageColor color = data.getColor(minigamer);
		String ID = "&3ID: &e" + color.name().replace("_", "").substring(0, 3) + "P";
		for (int i = 0; i < match.getAllMinigamers().size(); i++) // super unnecessary authenticity
			if (match.getAllMinigamers().get(i).equals(minigamer)) {
				ID += String.valueOf(i);
				break;
			}
		final String finalID = ID;
		String bloodType = "&3BT: &e" + RandomUtils.randomElement("O-", "O+", "B-", "B+", "A-", "A+", "AB-", "AB+");

		AtomicDouble progress = new AtomicDouble();
		taskId = tasks.repeatAsync(0, 2, () -> {
			double prog = progress.addAndGet(0.01);
			double rawSecondsLeft = 10 - (prog * 10);
			if (((prog * 10) % 1) < MathUtils.FLOAT_ROUNDING_ERROR)
				tasks.sync(() -> new SoundBuilder(Sound.BLOCK_NOTE_BLOCK_BIT).receiver(minigamer).play());

			if (rawSecondsLeft <= 0)
				tasks.sync(() -> {
					getTask().partCompleted(minigamer);
					viewer.closeInventory();
				});

			int secondsLeft = (int) Math.ceil(rawSecondsLeft);
			String duration = "&e" + secondsLeft + StringUtils.plural(" second", secondsLeft) + "&3 left";
			ClickableItem bar1 = ClickableItem.empty(new ItemBuilder(Material.GREEN_CONCRETE).name(duration).build());
			ClickableItem bar2 = ClickableItem.empty(new ItemBuilder(Material.WHITE_CONCRETE).name(duration).build());
			int greenBars = 8 - ((int) (rawSecondsLeft * .9) - 1);
			for (int col = 0; col < 9; col++)
				contents.set(1, col, col >= greenBars ? bar2 : bar1);

			ItemBuilder stats = new ItemBuilder(Material.PLAYER_HEAD).skullOwner(viewer).name(finalID);
			if (rawSecondsLeft <= 8)
				stats.lore("&3HT: &e3'6\"");
			if (rawSecondsLeft <= 6)
				stats.lore("&3WT: &e92lb");
			if (rawSecondsLeft <= 4)
				stats.lore("&3C: &e" + color.name());
			if (rawSecondsLeft <= 2)
				stats.lore(bloodType);
			contents.set(0, 4, ClickableItem.empty(stats.build()));
		});
	}

	@Override
	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		if (taskId != -1)
			tasks.cancel(taskId);
	}

}
