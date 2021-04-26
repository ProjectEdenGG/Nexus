package me.pugabyte.nexus.models.invisiblearmour;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
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
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.UUID;

@Data
@Builder
@Entity("invisible_armor")
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
		switch (slot) {
			case HEAD:
				return !helmet;
			case CHEST:
				return !chestplate;
			case LEGS:
				return !leggings;
			case FEET:
				return !boots;
		}
		return false;
	}

	public boolean showSelf(ItemSlot slot) {
		switch (slot) {
			case HEAD:
				return showSelfHelmet;
			case CHEST:
				return showSelfChestplate;
			case LEGS:
				return showSelfLeggings;
			case FEET:
				return showSelfBoots;
		}
		return false;
	}

	public void toggle(ItemSlot slot) {
		switch (slot) {
			case HEAD:
				helmet = !helmet;
				break;
			case CHEST:
				chestplate = !chestplate;
				break;
			case LEGS:
				leggings = !leggings;
				break;
			case FEET:
				boots = !boots;
				break;
		}
	}

	public void toggleShowSelf(ItemSlot slot) {
		switch (slot) {
			case HEAD:
				showSelfHelmet = !showSelfHelmet;
				break;
			case CHEST:
				showSelfChestplate = !showSelfChestplate;
				break;
			case LEGS:
				showSelfLeggings = !showSelfLeggings;
				break;
			case FEET:
				showSelfBoots = !showSelfBoots;
				break;
		}
	}

	public ItemStack getItem(ItemSlot slot) {
		PlayerInventory inventory = PlayerUtils.getPlayer(uuid).getPlayer().getInventory();
		switch (slot) {
			case HEAD:
				return inventory.getHelmet();
			case CHEST:
				return inventory.getChestplate();
			case LEGS:
				return inventory.getLeggings();
			case FEET:
				return inventory.getBoots();
		}
		return null;
	}

}
