package me.pugabyte.bncore.models.shop;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.ItemStackConverter;
import me.pugabyte.bncore.framework.persistence.serializer.mongodb.UUIDConverter;
import me.pugabyte.bncore.models.PlayerOwnedObject;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;

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
	@NonNull
	private UUID uuid;
	private String description;
	private List<ShopItem> items;

	@Data
	@NoArgsConstructor
	public class ShopItem {
		private int id;
		private ItemStack item;
		private int stock;
		private Exchange exchange;


		// Interface
		public class Exchange {
			private ExchangeAction action;

			private void process(OfflinePlayer customer) {


			}

		}
	}

	private enum ExchangeAction { SELL, BUY }

	public interface Exchange {

		void process(ShopItem item, OfflinePlayer customer);

	}

	public class ItemForItemExchange implements Exchange {

		@Override
		public void process(ShopItem item, OfflinePlayer customer) {

		}

	}

	public class ItemForMoneyExchange implements Exchange {

		@Override
		public void process(ShopItem item, OfflinePlayer customer) {

		}

	}

	public class MoneyForItemExchange implements Exchange {

		@Override
		public void process(ShopItem item, OfflinePlayer customer) {

		}

	}

}
