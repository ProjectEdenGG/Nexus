package gg.projecteden.nexus.models.invisiblearmour;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

@Data
@Entity(value = "invisible_armor", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class InvisibleArmor implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private boolean enabled;

	private boolean helmet = true;
	private boolean chestplate = true;
	private boolean leggings = true;
	private boolean boots = true;

	private boolean showSelfHelmet;
	private boolean showSelfChestplate;
	private boolean showSelfLeggings;
	private boolean showSelfBoots;

	public boolean show(ItemSlot slot) {
		return switch (slot) {
			case HEAD -> !helmet;
			case CHEST -> !chestplate;
			case LEGS -> !leggings;
			case FEET -> !boots;
			default -> false;
		};
	}

	public boolean showSelf(ItemSlot slot) {
		return switch (slot) {
			case HEAD -> showSelfHelmet;
			case CHEST -> showSelfChestplate;
			case LEGS -> showSelfLeggings;
			case FEET -> showSelfBoots;
			default -> false;
		};
	}

	public void toggle(ItemSlot slot) {
		switch (slot) {
			case HEAD -> helmet = !helmet;
			case CHEST -> chestplate = !chestplate;
			case LEGS -> leggings = !leggings;
			case FEET -> boots = !boots;
		}
	}

	public void toggleShowSelf(ItemSlot slot) {
		switch (slot) {
			case HEAD -> showSelfHelmet = !showSelfHelmet;
			case CHEST -> showSelfChestplate = !showSelfChestplate;
			case LEGS -> showSelfLeggings = !showSelfLeggings;
			case FEET -> showSelfBoots = !showSelfBoots;
		}
	}

	public ItemStack getItem(ItemSlot slot) {
		PlayerInventory inventory = PlayerUtils.getPlayer(uuid).getPlayer().getInventory();
		return switch (slot) {
			case HEAD -> inventory.getHelmet();
			case CHEST -> inventory.getChestplate();
			case LEGS -> inventory.getLeggings();
			case FEET -> inventory.getBoots();
			default -> null;
		};
	}

}
