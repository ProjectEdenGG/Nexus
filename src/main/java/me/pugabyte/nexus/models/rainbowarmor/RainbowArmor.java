package me.pugabyte.nexus.models.rainbowarmor;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.features.minigames.managers.PlayerManager;
import me.pugabyte.nexus.features.resourcepack.CustomModel;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.MaterialTag;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;

@Data
@Builder
@Entity("rainbow_armor")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class RainbowArmor implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;
	private transient int taskId;
	private transient Color color = Color.fromRGB(255, 0, 0);

	public RainbowArmor(Player player, int taskId) {
		this.uuid = player.getUniqueId();
		this.taskId = taskId;
		this.enabled = taskId > 0;
	}

	public ItemStack color(ItemStack item) {
		LeatherArmorMeta meta = (LeatherArmorMeta) item.getItemMeta();
		meta.setColor(color);
		item.setItemMeta(meta);
		return item;
	}

	public void editArmor(Consumer<ItemStack> editor) {
		PlayerInventory inv = getOnlinePlayer().getInventory();
		inv.setArmorContents(Arrays.stream(inv.getArmorContents()).peek(item -> {
			if (isLeatherArmor(item))
				editor.accept(item);
		}).toArray(ItemStack[]::new));
	}

	public void removeColor() {
		editArmor(this::removeColor);
	}

	public void removeColor(ItemStack itemStack) {
		LeatherArmorMeta meta = (LeatherArmorMeta) itemStack.getItemMeta();
		meta.setColor(null);
		itemStack.setItemMeta(meta);
	}

	public void stopArmor() {
		Tasks.cancel(taskId);
		removeColor();
	}

	public void startArmor() {
		stopArmor();

		taskId = Tasks.repeat(4, 2, () -> {
			if (!canUse()) {
				stopArmor();
				return;
			}

			increment();
			editArmor(this::color);
		});
	}

	public boolean canUse() {
		if (!isOnline())
			return false;

		if (PlayerManager.get(getOnlinePlayer()).isPlaying())
			return false;

		return true;
	}

	private static final int rate = 12;

	public void increment() {
		int r = color.getRed();
		int g = color.getGreen();
		int b = color.getBlue();

		if (r > 0 && b == 0) {
			if (r == 255 && g < 255) {
				g += rate;
			} else {
				r -= rate;
				g += rate;
			}
		}
		if (g > 0 && r == 0) {
			if (g == 255 && b < 255) {
				b += rate;
			} else {
				g -= rate;
				b += rate;
			}
		}
		if (b > 0 && g == 0) {
			if (b == 255 && r < 255) {
				r += rate;
			} else {
				b -= rate;
				r += rate;
			}
		}

		if (r < 0) r = 0;
		if (r > 255) r = 255;
		if (g < 0) g = 0;
		if (g > 255) g = 255;
		if (b < 0) b = 0;
		if (b > 255) b = 255;

		color = Color.fromRGB(r, g, b);
	}

	@Contract("null -> false; !null -> _")
	public static boolean isLeatherArmor(ItemStack item) {
		if (isNullOrAir(item))
			return false;
		if (CustomModel.exists(item))
			return false;

		return MaterialTag.ARMOR_LEATHER.isTagged(item.getType());
	}

}
