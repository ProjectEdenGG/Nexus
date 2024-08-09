package gg.projecteden.nexus.utils;

import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.Product;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ExchangeType;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.models.voter.VoterService;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.math.BigDecimal;

import static gg.projecteden.api.common.utils.UUIDUtils.UUID0;
import static gg.projecteden.nexus.features.shops.ShopUtils.prettyMoney;

@Getter
public enum Currency {
	FREE() {
		@Override
		public String getPriceLore(Price price, boolean canAfford) {
			return "&3Price: &aFree";
		}

		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			return true;
		}
	},
	ITEM() {
		@Override
		public String pretty(Price price) {
			return StringUtils.pretty(price.asItem());
		}

		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			ItemStack priceItem = price.asItem();
			return PlayerUtils.playerHas(player, priceItem);
		}

		@Override
		public void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			ItemStack priceItem = price.asItem();
			if (Nullables.isNullOrAir(priceItem))
				return;

			player.getInventory().removeItem(priceItem);
		}
	},
	BALANCE() {
		@Override
		public String pretty(Price price) {
			return prettyMoney(price.asBalance());
		}

		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			if (price.asBalance() <= 0)
				return true;

			if (price.asBalance() > 0) {
				if (shopGroup != null)
					return new BankerService().get(player).has(price.asBalance(), shopGroup);
				else
					return false;
			}

			return false;
		}

		@Override
		public void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			double priceNum = price.asBalance();
			new BankerService().withdraw(TransactionCause.MARKET_PURCHASE.of(null, player, BigDecimal.valueOf(-priceNum), shopGroup, StringUtils.pretty(product.getDisplayItemStack())));
		}
	},

	EVENT_TOKENS() {
		@Override
		public String pretty(Price price) {
			return price.asInteger() + " Event Tokens";
		}

		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			return new EventUserService().get(player).hasTokens(price.asInteger());
		}

		@Override
		public void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			new EventUserService().get(player).charge(price.asInteger());
		}
	},
	VOTE_POINTS() {
		@Override
		public String pretty(Price price) {
			return price.asInteger() + " Vote Points";
		}

		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			return new VoterService().get(player).getPoints() >= price.asInteger();
		}

		@Override
		public void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			new VoterService().get(player).takePoints(price.asInteger());
		}
	},
	MINIGAME_TOKENS() {
		@Override
		public String pretty(Price price) {
			return price.asInteger() + " Minigame Tokens";
		}

		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			return new PerkOwnerService().get(player).getTokens() >= price.asInteger();
		}

		@Override
		public void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			new PerkOwnerService().get(player).takeTokens(price.asInteger());
		}
	},
	STORE_CREDIT() {
		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			return new ContributorService().get(player).getCredit() >= price.asBalance();
		}

		@Override
		public void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			new ContributorService().get(player).takeCredit(price.asBalance());
		}
	},
	;

	Price price;

	final public void withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
		if (price == null)
			throw new InvalidInputException("price cannot be null, set to 0 if wanted free");

		_withdraw(player, price, shopGroup, product);
	}

	protected void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
	}

	final public boolean canAfford(Player player, Price price, ShopGroup shopGroup) {
		if (price == null)
			throw new InvalidInputException("price cannot be null, set to 0 if wanted free");

		return _canAfford(player, price, shopGroup);
	}

	protected boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
		return false;
	}

	protected String pretty(Price price) {
		return price.asInteger() + "";
	}

	public String getPriceLore(Price price, boolean canAfford) {
		return "&3Price: " + (canAfford ? "&a" : "&c") + pretty(price);
	}

	public void log(Player viewer, Price price, Product product, ShopGroup shopGroup) {
		ItemStack item = product.getItemStack();

		if (this != BALANCE || shopGroup == null)
			return;

		double priceNum = price.asBalance();
		Shop.log(UUID0, viewer.getUniqueId(), shopGroup, StringUtils.pretty(item).split(" ", 2)[1], 1, ExchangeType.SELL, String.valueOf(priceNum), "");
	}

	@Getter
	public static class Price {
		ItemStack item;
		int numInt;
		double numDouble;
		boolean free;
		boolean empty = true;

		public static Price of(Object value, int amount) {
			if (value instanceof Material material)
				return of(new ItemBuilder(material).amount(amount).build());
			else if (value instanceof ItemStack itemStack)
				return of(new ItemBuilder(itemStack).amount(amount).build());

			return of(value);
		}

		public static Price of(Object value) {
			Price result = new Price();
			if (value == null) {
				result.empty = false;
				return result;
			}

			if (value instanceof Material material) {
				result.item = new ItemStack(material);
				result.empty = false;
			} else if (value instanceof ItemStack) {
				result.item = (ItemStack) value;
				result.empty = false;
			} else if (value instanceof Integer) {
				result.numInt = (int) value;
				result.empty = false;
				if (result.numInt == 0)
					result.free = true;
			} else if (value instanceof Double) {
				result.numDouble = (double) value;
				result.empty = false;
				if (result.numDouble == 0)
					result.free = true;
			}

			return result;
		}

		public double asBalance() {
			return numDouble;
		}

		public int asInteger() {
			return numInt;
		}

		public ItemStack asItem() {
			return item;
		}

	}


}
