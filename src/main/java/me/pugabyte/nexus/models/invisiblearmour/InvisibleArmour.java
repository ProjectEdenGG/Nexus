package me.pugabyte.nexus.models.invisiblearmour;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import javax.persistence.Table;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Accessors(fluent = true)
@Table(name = "invisible_armour")
public class InvisibleArmour {
	@NonNull
	@Accessors(fluent = false)
	private String uuid;
	@Accessors(fluent = false)
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
