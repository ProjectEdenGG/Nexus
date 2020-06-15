package me.pugabyte.bncore.models.curiosity;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.LocationConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.pretty;

@Data
@Builder
@Entity("curiosity")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class Curiosity extends PlayerOwnedObject {
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
			Utils.giveItem(getPlayer(), reward.getItem());
			getPlayer().sendMessage("");
			getPlayer().sendMessage(colorize("&3You earned &e" + pretty(reward.getItem()) + " &3for learning!"));
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
