package gg.projecteden.nexus.models.rainbowarmor;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.managers.PlayerManager;
import gg.projecteden.nexus.features.resourcepack.CustomModel;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.Tasks;
import gg.projecteden.utils.MathUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.Contract;

import java.util.Arrays;
import java.util.UUID;
import java.util.function.Consumer;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

@Data
@Entity(value = "rainbow_armor", noClassnameStored = true)
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
		return isOnline() && !PlayerManager.get(getOnlinePlayer()).isPlaying();
	}

	private static final int rate = 12;

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

	@Contract("null -> false; !null -> _")
	public static boolean isLeatherArmor(ItemStack item) {
		if (isNullOrAir(item))
			return false;
		if (CustomModel.exists(item))
			return false;

		return MaterialTag.ARMOR_LEATHER.isTagged(item.getType());
	}

}
