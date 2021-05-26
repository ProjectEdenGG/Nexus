package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.RequiredArgsConstructor;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.menus.sabotage.tasks.AbstractTaskMenu;
import me.pugabyte.nexus.features.minigames.models.Minigamer;
import me.pugabyte.nexus.features.minigames.models.mechanics.Mechanic;
import me.pugabyte.nexus.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.function.Consumer;

@RequiredArgsConstructor
public enum TaskPart {
    REACTOR("Fix Reactor Idk", new ItemStack(Material.DIRT), )
    ;

    private final String name;
    private final ItemStack interactionItem;
    private final Consumer<HasPlayer> action;
    private final Class<? extends TaskPartData> data;

    TaskPart(String name, ItemStack interactionItem, Consumer<HasPlayer> action) {
        this(name, interactionItem, action, TaskPartData.class);
    }

    TaskPart(String name, ItemStack interactionItem, AbstractTaskMenu menu) {
        this(name, interactionItem, menu, TaskPartData.class);
    }

    TaskPart(String name, ItemStack interactionItem, AbstractTaskMenu menu, Class<? extends TaskPartData> data) {
        this(name, interactionItem, menu::open, data);
    }
}
