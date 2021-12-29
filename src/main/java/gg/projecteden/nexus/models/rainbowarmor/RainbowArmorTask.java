package gg.projecteden.nexus.models.rainbowarmor;

import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.MathUtils;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Data
public class RainbowArmorTask {
	private final PlayerInventory inventory;
	@Builder.Default
	private final int rate = 12;
	@Builder.Default
	private Color color = Color.fromRGB(255, 0, 0);
	private Supplier<Boolean> cancelIf;
	private Runnable onCancel;

	private int taskId;

	@Builder(buildMethodName = "start")
	public RainbowArmorTask(PlayerInventory inventory, Supplier<Boolean> cancelIf, Runnable onCancel) {
		this.inventory = inventory;
		this.cancelIf = cancelIf;
		this.onCancel = onCancel;
		start();
	}

	public void start() {
		taskId = Tasks.repeat(4, 2, () -> {
			if (cancelIf != null && cancelIf.get()) {
				stop();
				return;
			}

			increment();
			editArmor(this::color);
		});
	}

	public void stop() {
		Tasks.cancel(taskId);
		if (onCancel != null)
			onCancel.run();
	}

	public ItemStack color(ItemStack item) {
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}

	public void editArmor(Consumer<ItemStack> editor) {
		inventory.setArmorContents(Arrays.stream(inventory.getArmorContents()).peek(item -> {
			if (isLeatherArmor(item))
				editor.accept(item);
		}).toArray(ItemStack[]::new));
	}

	public void removeColor() {
		editArmor(RainbowArmorTask::removeColor);
	}

	public static void removeColor(ItemStack itemStack) {
		LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
		meta.setColor(null);
		itemStack.setItemMeta(meta);
	}

	@Contract("null -> false; !null -> _")
	public static boolean isLeatherArmor(ItemStack item) {
		if (ItemUtils.isNullOrAir(item))
			return false;

		if (CustomModel.exists(item)) {
			if (item.getType().equals(Material.LEATHER_HORSE_ARMOR))
				return true;
			return false;
		}

		return MaterialTag.ARMOR_LEATHER.isTagged(item.getType());
	}

	public void increment() {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		if (r > 0 && b == 0) {
			if (r != 255 || g >= 255)
				r -= rate;
			g += rate;
		}
		if (g > 0 && r == 0) {
			if (g != 255 || b >= 255)
				g -= rate;
			b += rate;
		}
		if (b > 0 && g == 0) {
			if (b != 255 || r >= 255)
				b -= rate;
			r += rate;
		}

		r = MathUtils.clamp(r, 0, 255);
		g = MathUtils.clamp(g, 0, 255);
		b = MathUtils.clamp(b, 0, 255);

		color = Color.fromRGB(r, g, b);
	}

}
