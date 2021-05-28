package me.pugabyte.nexus.features.menus.sabotage.tasks;

import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryProvider;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.features.minigames.models.sabotage.Task;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public abstract class AbstractTaskMenu extends MenuUtils implements InventoryProvider {
    @Getter
    private final Task task;

    public abstract SmartInventory getInventory();

    @Override
    public void open(Player viewer, int page) {
        getInventory().open(viewer, page);
    }
}
