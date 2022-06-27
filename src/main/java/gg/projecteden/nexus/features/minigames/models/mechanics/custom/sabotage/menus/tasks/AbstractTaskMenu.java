package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks;

import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.mechanics.Sabotage;
import gg.projecteden.nexus.features.minigames.models.Match.MatchTasks;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import gg.projecteden.parchment.HasPlayer;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractTaskMenu extends InventoryProvider {
	@Getter
	protected final Task task;
	private final Map<UUID, Integer> closeInvTasks = new HashMap<>();

	// todo: use for more tasks
	public void scheduleInvClose(HasPlayer _player) {
		Player player = _player.getPlayer();
		Minigamer minigamer = Minigamer.of(player);
		if (!minigamer.isPlaying(Sabotage.class)) return; // don't schedule if match just ended

		MatchTasks tasks = minigamer.getMatch().getTasks();
		if (closeInvTasks.containsKey(player.getUniqueId()))
			tasks.cancel(closeInvTasks.remove(player.getUniqueId()));

		closeInvTasks.put(player.getUniqueId(), tasks.wait(20, () -> {
			player.sendMessage(Sabotage.COMPLETED_TASK_TEXT);
			player.closeInventory();
		}));
	}

	@Override
	public void onClose(InventoryCloseEvent event, List<ItemStack> contents) {
		if (closeInvTasks.containsKey(event.getPlayer().getUniqueId()))
			Minigamer.of(event.getPlayer()).getMatch().getTasks()
				.cancel(closeInvTasks.remove(event.getPlayer().getUniqueId()));
	}

}
