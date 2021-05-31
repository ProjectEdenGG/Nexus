package me.pugabyte.nexus.features.minigames.models.sabotage;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
import me.lexikiq.HasPlayer;
import me.pugabyte.nexus.features.menus.sabotage.tasks.AbstractTaskMenu;
import me.pugabyte.nexus.features.menus.sabotage.tasks.SwipeCardTask;
import me.pugabyte.nexus.features.minigames.models.sabotage.taskpartdata.TaskPartData;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

@Data
@Builder
public class TaskPart {
    private final String name;
    private final ItemStack interactionItem;
    private final Class<? extends AbstractTaskMenu> menu;
    /**
     * Class used to store data about this task part
     */
    @Builder.Default
    private final Class<? extends TaskPartData> data = TaskPartData.class;

    /**
     * Item on an armor stand head which a player must right click to use the task
     */
    public ItemStack getInteractionItem() {
        return interactionItem.clone();
    }

    public AbstractTaskMenu instantiateMenu(Task task) {
        try {
            return menu.getConstructor(Task.class).newInstance(task);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not open menu for TaskPart " + name);
        }
    }

    public <T extends TaskPartData> T createTaskPartData() {
        try {
            return (T) data.getConstructor(TaskPart.class).newInstance(this);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Could not create task data for TaskPart " + name);
        }
    }

    public void openMenu(Task task, HasPlayer player) {
        instantiateMenu(task).open(player);
    }

    public static class TaskPartBuilder {
        @Contract("_ -> this")
        public TaskPartBuilder interactionItem(Supplier<ItemStack> item) {
            return interactionItem(item.get());
        }

        @Contract("_ -> this") // manual overload
        public TaskPartBuilder interactionItem(ItemStack item) {
            interactionItem = item;
            return this;
        }
    }

    @Getter @Accessors(fluent = true)
    private static final Set<TaskPart> values = new HashSet<>();
    private static final Map<ItemStack, TaskPart> itemMap = new HashMap<>();

    private static TaskPart add(TaskPart part) {
        values.add(part);
        itemMap.put(part.getInteractionItem(), part);
        return part;
    }

    public static TaskPart get(ItemStack interactionItem) {
        return itemMap.get(interactionItem.asOne());
    }

    private static final ItemStack EMPTY_ITEM = new ItemBuilder(Material.BARRIER).customModelData(1).build();
    private static ItemStack EMPTY_ITEM(String name) {return new ItemBuilder(EMPTY_ITEM).name(name).build();}

    public static final TaskPart SWIPE_CARD = add(new TaskPartBuilder().name("Swipe Card").interactionItem(EMPTY_ITEM("Swipe Card")).menu(SwipeCardTask.class).build());
}
