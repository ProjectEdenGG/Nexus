package gg.projecteden.nexus.models.halloween21;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.events.y2021.halloween21.Pumpkin;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;

@Data
@Entity(value = "halloween21_pumpkin_head_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Halloween21PumpkinHeadConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<UUID, Integer> entities = new ConcurrentHashMap<>();

	public void pumpkin(LivingEntity entity) {
		entities.put(entity.getUniqueId(), Pumpkin.randomCustomModelData());
	}

	public void unpumpkin(LivingEntity victim) {
		entities.remove(victim.getUniqueId());
	}

	public boolean isPumpkining(LivingEntity entity) {
		if (entity.getEquipment() != null && !isNullOrAir(entity.getEquipment().getItem(EquipmentSlot.HEAD)))
			return false;
		if (!entities.containsKey(entity.getUniqueId()))
			return false;

		return true;
	}

	public ItemStack getPumpkin(LivingEntity entity) {
		return Pumpkin.itemOf(entities.getOrDefault(entity.getUniqueId(), 0)).build();
	}

}
