package me.pugabyte.nexus.models.curiosity;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import eden.mongodb.serializers.UUIDConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import me.pugabyte.nexus.utils.PlayerUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.nexus.utils.StringUtils.pretty;

@Data
@Builder
@Entity("curiosity")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Curiosity implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<CuriosityReward> earned = new HashSet<>();

	public boolean has(CuriosityReward reward) {
		return earned.contains(reward);
	}

	public void give(CuriosityReward reward) {
		earned.add(reward);
		if (isOnline()) {
			PlayerUtils.giveItem(getPlayer(), reward.getItem());
			PlayerUtils.send(getPlayer(), "");
			PlayerUtils.send(getPlayer(), "&3You earned &e" + pretty(reward.getItem()) + " &3for learning!");
		}
	}

	public enum CuriosityReward {
		COOKIES(new ItemStack(Material.COOKIE, 16)),
		CAKE(new ItemStack(Material.CAKE));

		@Getter
		private final ItemStack item;

		CuriosityReward(ItemStack item) {
			this.item = item;
		}
	}

}
