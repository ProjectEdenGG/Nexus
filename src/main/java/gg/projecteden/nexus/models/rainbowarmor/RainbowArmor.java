package gg.projecteden.nexus.models.rainbowarmor;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.minigames.models.Minigamer;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils.ArmorSlot;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

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
	private double speed = 1.0;
	private Set<ArmorSlot> disabledSlots = new HashSet<>();
	private transient RainbowArmorTask task;

	public void stop() {
		if (task != null) {
			task.stop();
			task = null;
		}
	}

	public void start() {
		stop();

		if (!isOnline())
			return;

		task = RainbowArmorTask.builder()
			.entity(getOnlinePlayer())
			.rate((int) Math.floor(12 * speed))
			.disabledSlots(disabledSlots)
			.cancelIf(this::isNotAllowed)
			.build()
			.start();
	}

	public boolean isNotAllowed() {
		return !isOnline() || Minigamer.of(getOnlinePlayer()).isPlaying();
	}

	public boolean isSlotEnabled(ArmorSlot slot) {
		return !disabledSlots.contains(slot);
	}

	public void toggleSlot(ArmorSlot slot) {
		if (isSlotEnabled(slot))
			disableSlot(slot);
		else
			enableSlot(slot);
	}

	public void enableSlot(ArmorSlot slot) {
		disabledSlots.remove(slot);
	}

	public void disableSlot(ArmorSlot slot) {
		disabledSlots.add(slot);
	}

	public ItemStack getHiddenIcon(ArmorSlot slot) {
		CustomMaterial material = switch (slot) {
			case HELMET -> CustomMaterial.ARMOR_OUTLINE_HELMET;
			case CHESTPLATE -> CustomMaterial.ARMOR_OUTLINE_CHESTPLATE;
			case LEGGINGS -> CustomMaterial.ARMOR_OUTLINE_LEGGINGS;
			case BOOTS -> CustomMaterial.ARMOR_OUTLINE_BOOTS;
		};

		return new ItemBuilder(material)
			.name(StringUtils.camelCase(slot))
			.build();
	}

	public ItemStack getShownIcon(ArmorSlot slot) {
		return new ItemBuilder(Material.matchMaterial("LEATHER_" + slot.name()))
			.name(StringUtils.camelCase(slot))
			.build();
	}

}
