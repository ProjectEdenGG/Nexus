package gg.projecteden.nexus.features.menus.sabotage.tasks;

import gg.projecteden.nexus.features.menus.api.InventoryListener;
import gg.projecteden.nexus.features.menus.api.content.InventoryProvider;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.minigames.mechanics.Sabotage;
import gg.projecteden.nexus.features.minigames.models.Match;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractTaskMenu extends InventoryProvider {
	@Getter
	protected final Task task;
	private final Map<UUID, Integer> closeInvTasks = new HashMap<>();
	protected final InventoryListener<InventoryCloseEvent> handleInvClose = new InventoryListener<>(InventoryCloseEvent.class, event -> {
		if (closeInvTasks.containsKey(event.getPlayer().getUniqueId())) {
			Minigamer minigamer = PlayerManager.get(event.getPlayer());

			// we can assume that if they aren't playing Sabotage anymore that the task has already been cancelled
			if (!minigamer.isPlaying(Sabotage.class)) return;

			minigamer.getMatch().getTasks().cancel(closeInvTasks.remove(event.getPlayer().getUniqueId()));
		}
	});

	@Override
	public abstract void init(); // changes the default variable name for InventoryContents when overriding :P

	// todo: use for more tasks
	public void scheduleInvClose(HasPlayer _player) {
		Player player = _player.getPlayer();
		Minigamer minigamer = PlayerManager.get(player);
		if (!minigamer.isPlaying(Sabotage.class)) return; // don't schedule if match just ended

		Match.MatchTasks tasks = minigamer.getMatch().getTasks();
		if (closeInvTasks.containsKey(player.getUniqueId()))
			tasks.cancel(closeInvTasks.remove(player.getUniqueId()));
		closeInvTasks.put(player.getUniqueId(), tasks.wait(20, () -> {
			player.sendMessage(Sabotage.COMPLETED_TASK_TEXT);
			player.closeInventory();
		}));
	}

}
