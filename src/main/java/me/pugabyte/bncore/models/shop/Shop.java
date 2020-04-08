package me.pugabyte.bncore.models.shop;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@Entity("shop")
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, ItemStackConverter.class})
public class Shop extends PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private String description;
	private List<ShopItem> items = new ArrayList<>();

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ShopItem {
		private UUID uuid;
		private ItemStack item;
		private int stock;
		private Exchange exchange;

		public Shop getShop() {
			return new ShopService().get(uuid);
		}
	}

	public interface Exchange {

		<T> T getPrice();

		void process(ShopItem item, OfflinePlayer customer);

	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemForItemExchange implements Exchange {
		@NonNull
		private ItemStack price;

		@Override
		public void process(ShopItem item, OfflinePlayer customer) {
			BNCore.log("Processing ItemForItemExchange");
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ItemForMoneyExchange implements Exchange {
		@NonNull
		private Integer price;

		@Override
		public void process(ShopItem item, OfflinePlayer customer) {
			BNCore.log("Processing ItemForMoneyExchange");
		}
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class MoneyForItemExchange implements Exchange {
		@NonNull
		private ItemStack price;

		@Override
		public void process(ShopItem item, OfflinePlayer customer) {
			BNCore.log("Processing MoneyForItemExchange");
		}
	}

}
