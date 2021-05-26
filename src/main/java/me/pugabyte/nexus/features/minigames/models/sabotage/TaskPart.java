package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.RequiredArgsConstructor;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.menus.sabotage.tasks.AbstractTaskMenu;
import me.pugabyte.nexus.features.minigames.models.sabotage.taskpartdata.TaskPartData;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.function.Consumer;

import static eden.utils.StringUtils.camelCase;

@RequiredArgsConstructor
public enum TaskPart {
    SWIPE_CARD(new ItemStack(Material.DIRT), )
    ;

    private final String name;
    /**
     * Item on an armor stand head which a player must right click to use the task
     */
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

    // automatic name

    TaskPart(ItemStack interactionItem, Consumer<HasPlayer> action) {
        this(interactionItem, action, TaskPartData.class);
    }

    TaskPart(ItemStack interactionItem, AbstractTaskMenu menu) {
        this(interactionItem, menu::open);
    }

    TaskPart(ItemStack interactionItem, Consumer<HasPlayer> action, Class<? extends TaskPartData> data) {
        name = camelCase(this);
        this.interactionItem = interactionItem;
        this.action = action;
        this.data = data;
    }

    TaskPart(ItemStack interactionItem, AbstractTaskMenu menu, Class<? extends TaskPartData> data) {
        this(interactionItem, menu::open, data);
    }

    // item builders

    TaskPart(String name, ItemBuilder interactionItem, Consumer<HasPlayer> action, Class<? extends TaskPartData> data) {
        this(name, interactionItem.build(), action, data);
    }

    TaskPart(String name, ItemBuilder interactionItem, Consumer<HasPlayer> action) {
        this(name, interactionItem, action, TaskPartData.class);
    }

    TaskPart(String name, ItemBuilder interactionItem, AbstractTaskMenu menu) {
        this(name, interactionItem, menu, TaskPartData.class);
    }

    TaskPart(String name, ItemBuilder interactionItem, AbstractTaskMenu menu, Class<? extends TaskPartData> data) {
        this(name, interactionItem, menu::open, data);
    }

    TaskPart(ItemBuilder interactionItem, Consumer<HasPlayer> action) {
        this(interactionItem, action, TaskPartData.class);
    }

    TaskPart(ItemBuilder interactionItem, AbstractTaskMenu menu) {
        this(interactionItem, menu::open);
    }

    TaskPart(ItemBuilder interactionItem, Consumer<HasPlayer> action, Class<? extends TaskPartData> data) {
        this(interactionItem.build(), action, data);
    }

    TaskPart(ItemBuilder interactionItem, AbstractTaskMenu menu, Class<? extends TaskPartData> data) {
        this(interactionItem, menu::open, data);
    }
}
