package me.pugabyte.nexus.models.coupon;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.nexus.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.nexus.models.PlayerOwnedObject;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.pugabyte.nexus.utils.ItemUtils.isFuzzyMatch;

@Data
@Builder
@Entity("coupon")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class Coupons extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	@Embedded
	private List<Coupon> coupons = new ArrayList<>();

	public Coupon of(String id) {
		for (Coupon coupon : coupons)
			if (coupon.getId().equalsIgnoreCase(id))
				return coupon;

		return null;
	}

	public Coupon of(ItemStack item) {
		for (Coupon coupon : coupons)
			if (isFuzzyMatch(item, coupon.getItem()))
				return coupon;

		return null;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	@RequiredArgsConstructor
	public static class Coupon {
		@NonNull
		private String id;
		@NonNull
		private ItemStack item;
		private int uses;

		public void use() {
			++uses;
		}
	}

}
