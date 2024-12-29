package gg.projecteden.nexus.models.coupon;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Embedded;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.interfaces.PlayerOwnedObject;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.ItemStackConverter;
import gg.projecteden.nexus.utils.ItemUtils;
import lombok.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Entity(value = "coupon", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class Coupons implements PlayerOwnedObject {
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
		for (Coupon coupon : coupons) {
			ItemStack couponItem = coupon.getItem();
			if (ItemUtils.isFuzzyMatch(item, couponItem)) return coupon;
			if (item.getType() != couponItem.getType())
				continue;

			ItemMeta itemMeta1 = item.getItemMeta();
			ItemMeta couponMeta = couponItem.getItemMeta();

			if (!itemMeta1.getDisplayName().equals(couponMeta.getDisplayName()))
				continue;

			int similarLore = 0;
			List<String> itemLore = item.getLore();
			List<String> couponLore = couponItem.getLore();

			if (couponLore == null || itemLore == null) continue;
			if (couponLore.size() != itemLore.size()) continue;

			for (int i = 0; i < couponLore.size(); i++) {
				if (couponLore.get(i).equals(itemLore.get(i))) {
					similarLore += 1;
				}
			}

			if (couponLore.size() == (similarLore + 1)) {
				return coupon;
			}
		}
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
