package gg.projecteden.nexus.models.invisiblearmour;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.equipment.skins.ArmorSkin;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.PlayerNotOnlineException;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.ArmorSlot;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

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

	private Set<ArmorSlot> hide = new HashSet<>(Set.of(ArmorSlot.values()));

	public boolean isHidden(ArmorSlot slot) {
		return hide.contains(slot);
	}

	public boolean isShown(ArmorSlot slot) {
		return !isHidden(slot);
	}

	public void toggleHide(ArmorSlot slot) {
		if (isHidden(slot))
			hide.remove(slot);
		else
			hide.add(slot);
	}

	public ItemStack getDisplayItem(ArmorSlot slot) {
		final PlayerInventory inventory = getOnlinePlayer().getInventory();
		final ItemStack item = inventory.getItem(slot.getSlot());
		if (isNullOrAir(item))
			return getHiddenIcon(slot);
		else
			return item;
	}

	public ItemStack getHiddenIcon(ArmorSlot slot) {
		ItemModelType itemModelType = switch (slot) {
			case HELMET -> ItemModelType.ARMOR_OUTLINE_HELMET;
			case CHESTPLATE -> ItemModelType.ARMOR_OUTLINE_CHESTPLATE;
			case LEGGINGS -> ItemModelType.ARMOR_OUTLINE_LEGGINGS;
			case BOOTS -> ItemModelType.ARMOR_OUTLINE_BOOTS;
		};

		return new ItemBuilder(itemModelType)
			.name(StringUtils.camelCase(slot))
			.build();
	}

	public ItemStack getShownIcon(ArmorSlot slot) {
		ItemModelType itemModelType = switch (slot) {
			case HELMET -> ItemModelType.ARMOR_FILLED_HELMET;
			case CHESTPLATE -> ItemModelType.ARMOR_FILLED_CHESTPLATE;
			case LEGGINGS -> ItemModelType.ARMOR_FILLED_LEGGINGS;
			case BOOTS -> ItemModelType.ARMOR_FILLED_BOOTS;
		};

		return new ItemBuilder(itemModelType)
			.name(StringUtils.camelCase(slot))
			.build();
	}

	public void updateTextures() {
		try {
			Player player = getOnlinePlayer();
			for (ItemStack item : player.getInventory().getStorageContents()) {
				if (isNullOrAir(item))
					continue;
				updateTexture(item, null);
			}
			updateTexture(player.getInventory().getHelmet(), ArmorSlot.HELMET);
			updateTexture(player.getInventory().getChestplate(), ArmorSlot.CHESTPLATE);
			updateTexture(player.getInventory().getLeggings(), ArmorSlot.LEGGINGS);
			updateTexture(player.getInventory().getBoots(), ArmorSlot.BOOTS);
		} catch (PlayerNotOnlineException ignored) {}
	}

	public ItemStack updateTexture(ItemStack item, ArmorSlot slot) {
		if (isNullOrAir(item))
			return item;

		ItemBuilder builder = new ItemBuilder(item, true);

		if (!isEnabled()) {
			ArmorSkin.applyEquippableComponent(builder, ArmorSkin.of(item));
			return builder.build();
		}

		boolean invisible = isHidden(slot);
		ArmorSkin.applyEquippableComponent(builder, invisible ? ArmorSkin.INVISIBLE : ArmorSkin.of(item));
		return builder.build();
	}

}
