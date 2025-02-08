package gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage;

import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks.AbstractTaskMenu;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks.DivertPower1Task;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks.DivertPower2Task;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks.MedicalScanTask;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks.ReactorTask;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.menus.tasks.SwipeCardTask;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata.DivertPower1TaskPartData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata.ReactorTaskPartData;
import gg.projecteden.nexus.features.minigames.models.mechanics.custom.sabotage.taskpartdata.TaskPartData;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.parchment.HasPlayer;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.experimental.Accessors;
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

	@Getter
	@Accessors(fluent = true)
	private static final Set<TaskPart> values = new HashSet<>();
	private static final Map<ItemStack, TaskPart> itemMap = new HashMap<>();

	private static TaskPart add(TaskPart part) {
		values.add(part);
		itemMap.put(part.getInteractionItem(), part);
		return part;
	}

	private static TaskPart add(TaskPartBuilder part) {
		return add(part.build());
	}

	public static TaskPart get(ItemStack interactionItem) {
		return itemMap.get(interactionItem.asOne());
	}

	private static final ItemStack EMPTY_ITEM = new ItemBuilder(CustomMaterial.INVISIBLE).build();

	private static ItemStack EMPTY_ITEM(String name) {
		return new ItemBuilder(EMPTY_ITEM).name(name).build();
	}

	public static final TaskPart SWIPE_CARD = add(builder().name("Swipe Card").interactionItem(EMPTY_ITEM("Swipe Card")).menu(SwipeCardTask.class));
//	public static final TaskPart LIGHTS = add(builder().name("Fix Lights").interactionItem(new ItemBuilder(CustomMaterial.LIGHT)).data(LightsTaskPartData.class).menu(LightsTask.class));
	public static final TaskPart REACTOR = add(builder().name("Reactor Meltdown").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_REACTOR_MELTDOWN)).data(ReactorTaskPartData.class).menu(ReactorTask.class));
	public static final TaskPart SUBMIT_SCAN = add(builder().name("Submit Scan").interactionItem(EMPTY_ITEM("Medical Scan")).menu(MedicalScanTask.class));
	public static final TaskPart DIVERT_POWER_1 = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("1")).data(DivertPower1TaskPartData.class).menu(DivertPower1Task.class));
	public static final TaskPart DIVERT_POWER_2A = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2a")).menu(DivertPower2Task.class));
	public static final TaskPart DIVERT_POWER_2B = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2b")).menu(DivertPower2Task.class));
	public static final TaskPart DIVERT_POWER_2C = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2c")).menu(DivertPower2Task.class));
	public static final TaskPart DIVERT_POWER_2D = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2d")).menu(DivertPower2Task.class));
	public static final TaskPart DIVERT_POWER_2E = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2e")).menu(DivertPower2Task.class));
	public static final TaskPart DIVERT_POWER_2F = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2f")).menu(DivertPower2Task.class));
	public static final TaskPart DIVERT_POWER_2G = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2g")).menu(DivertPower2Task.class));
	public static final TaskPart DIVERT_POWER_2H = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2h")).menu(DivertPower2Task.class));
	public static final TaskPart DIVERT_POWER_2I = add(builder().name("Divert Power").interactionItem(new ItemBuilder(CustomMaterial.SABOTAGE_DIVERT_POWER).name("2i")).menu(DivertPower2Task.class));

}
