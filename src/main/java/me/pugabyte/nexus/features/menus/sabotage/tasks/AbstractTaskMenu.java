package me.pugabyte.nexus.features.menus.sabotage.tasks;

import fr.minuskube.inv.InventoryListener;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.minigames.mechanics.Sabotage;
import me.pugabyte.nexus.features.minigames.models.Match;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.mechanics.custom.sabotage.Task;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public abstract class AbstractTaskMenu extends MenuUtils implements InventoryProvider {
    @Getter
    protected final Task task;

    @NotNull
    public abstract SmartInventory getInventory();

    @Override
    public void open(Player viewer, int page) {
        getInventory().open(viewer, page);
    }

	@Override
	public abstract void init(Player player, InventoryContents contents); // changes the default variable name for InventoryContents when overriding :P

	private final Map<UUID, Integer> closeInvTasks = new HashMap<>();
	protected final InventoryListener<InventoryCloseEvent> handleInvClose = new InventoryListener<>(InventoryCloseEvent.class, event -> {
		if (closeInvTasks.containsKey(event.getPlayer().getUniqueId())) {
			Minigamer minigamer = PlayerManager.get(event.getPlayer());

			// we can assume that if they aren't playing Sabotage anymore that the task has already been cancelled
			if (!minigamer.isPlaying(Sabotage.class)) return;

			minigamer.getMatch().getTasks().cancel(closeInvTasks.remove(event.getPlayer().getUniqueId()));
		}
	});

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
