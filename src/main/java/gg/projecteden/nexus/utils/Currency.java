package gg.projecteden.nexus.utils;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.Product;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
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
import java.util.ArrayList;
import java.util.List;

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
	COIN_POUCH() {
		@Override
		protected String pretty(Price price) {
			return price.asInteger() + " Coins";
		}

		@Override
		protected void _deposit(Player player, Price price) {
			ItemStack pouch = PlayerUtils.searchInventory(player, CommonQuestItem.COIN_POUCH.getCustomMaterial());
			if (Nullables.isNullOrAir(pouch))
				throw new InvalidInputException("Cannot deposit to " + player.getName() + ", couldn't find a pouch");

			final NBTItem nbtItem = new NBTItem(pouch);
			if (!nbtItem.hasKey(CommonQuestItem.COIN_POUCH_NBT_KEY))
				throw new InvalidInputException("Cannot deposit to " + player.getName() + ", pouch is missing NBT KEY: " + CommonQuestItem.COIN_POUCH_NBT_KEY);

			Integer pouchCoins = nbtItem.getInteger(CommonQuestItem.COIN_POUCH_NBT_KEY);
			if (pouchCoins == null)
				throw new InvalidInputException("Cannot deposit to " + player.getName() + ", pouch NBT KEY returned null");

			pouchCoins += price.asInteger();
			nbtItem.setInteger(CommonQuestItem.COIN_POUCH_NBT_KEY, pouchCoins);
			nbtItem.applyNBT(pouch);

			updateLore(pouch, pouchCoins);
		}

		@Override
		protected boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			ItemStack pouch = PlayerUtils.searchInventory(player, CommonQuestItem.COIN_POUCH.getCustomMaterial());
			if (Nullables.isNullOrAir(pouch))
				return false;

			final NBTItem nbtItem = new NBTItem(pouch);
			if (!nbtItem.hasKey(CommonQuestItem.COIN_POUCH_NBT_KEY))
				return false;

			Integer coins = nbtItem.getInteger(CommonQuestItem.COIN_POUCH_NBT_KEY);
			if (coins == null)
				return false;

			return coins >= price.asInteger();
		}

		@Override
		protected void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			ItemStack pouch = PlayerUtils.searchInventory(player, CommonQuestItem.COIN_POUCH.getCustomMaterial());
			if (Nullables.isNullOrAir(pouch))
				throw new InvalidInputException("Cannot withdraw from " + player.getName() + ", couldn't find a pouch");

			final NBTItem nbtItem = new NBTItem(pouch);
			if (!nbtItem.hasKey(CommonQuestItem.COIN_POUCH_NBT_KEY))
				throw new InvalidInputException("Cannot withdraw from " + player.getName() + ", pouch is missing NBT KEY: " + CommonQuestItem.COIN_POUCH_NBT_KEY);

			Integer pouchCoins = nbtItem.getInteger(CommonQuestItem.COIN_POUCH_NBT_KEY);
			if (pouchCoins == null)
				throw new InvalidInputException("Cannot withdraw from " + player.getName() + ", pouch NBT KEY returned null");

			pouchCoins -= price.asInteger();
			nbtItem.setInteger(CommonQuestItem.COIN_POUCH_NBT_KEY, pouchCoins);
			nbtItem.applyNBT(pouch);

			updateLore(pouch, pouchCoins);
		}

		private void updateLore(ItemStack pouch, int amount) {
			List<String> lore = CommonQuestItem.COIN_POUCH.getItemBuilder().getLore();
			lore.addAll(List.of("", "&fCoins: &e" + StringUtils.getCnf().format(amount)));
			List<String> coloredLore = new ArrayList<>();
			for (String line : lore) {
				coloredLore.add(StringUtils.colorize(line));
			}
			pouch.setLore(coloredLore);
		}
	},
	BALANCE() {
		@Override
		public String pretty(Price price) {
			return prettyMoney(price.asBalance(), price.isFree());
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

	final public void deposit(Player player, Price price) {
		if (price == null)
			throw new InvalidInputException("price cannot be null");

		_deposit(player, price);
	}

	protected void _deposit(Player player, Price price) {
		throw new InvalidInputException("Deposit has not been defined for currency: " + this.name());
	}

	final public void withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
		if (price == null)
			throw new InvalidInputException("price cannot be null, set to 0 if wanted free");

		_withdraw(player, price, shopGroup, product);
	}

	protected void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
		throw new InvalidInputException("Withdraw has not been defined for currency: " + this.name());
	}

	final public boolean canAfford(Player player, Price price, ShopGroup shopGroup) {
		if (price == null)
			throw new InvalidInputException("price cannot be null, set to 0 if wanted free");

		return _canAfford(player, price, shopGroup);
	}

	protected boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
		throw new InvalidInputException("canAfford has not been defined for currency: " + this.name());
	}

	protected String pretty(Price price) {
		return price.asInteger() + "";
	}

	public String getPriceLore(Price price, boolean canAfford) {
		return "&3Price: " + (canAfford ? "&a" : "&c") + this.pretty(price);
	}

	public void log(Player viewer, Price price, Product product, ShopGroup shopGroup) {
		ItemStack item = product.getItemStack();

		if (this != BALANCE || shopGroup == null)
			return;

		double priceNum = price.asBalance();
		Shop.log(UUID0, viewer.getUniqueId(), shopGroup, StringUtils.pretty(item).split(" ", 2)[1], 1, ExchangeType.SELL, String.valueOf(priceNum), "");
	}

	@Getter
	public static class Price implements Cloneable {
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

		public void applyDiscount(double percentage) {
			if (free || empty)
				return;

			if (!Nullables.isNullOrAir(item)) {
				int amount = (int) (item.getAmount() - Math.ceil(item.getAmount() * percentage));
				if (amount <= 0) {
					amount = 0;
					free = true;
				}
				item.setAmount(amount);
			}

			if (numInt != 0) {
				int amount = (int) (numInt - Math.ceil(numInt * percentage));
				if (amount <= 0) {
					amount = 0;
					free = true;
				}

				numInt = amount;
			}

			if (numDouble != 0) {
				double amount = numDouble - (numDouble * percentage);
				if (amount <= 0) {
					amount = 0;
					free = true;
				}
				numDouble = amount;
			}
		}

		@SuppressWarnings("MethodDoesntCallSuperMethod")
		public Price clone() {
			Price price = new Price();
			if (!Nullables.isNullOrAir(item))
				price.item = item.clone();
			price.numInt = numInt;
			price.numDouble = numDouble;
			price.free = free;
			price.empty = empty;

			return price;
		}
	}


}
