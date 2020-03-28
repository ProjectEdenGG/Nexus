package me.pugabyte.bncore.models.invisiblearmour;

import com.comphenix.protocol.wrappers.EnumWrappers.ItemSlot;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;

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

	public boolean showSelf(ItemSlot type) {
		switch (type) {
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

}
