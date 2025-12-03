package gg.projecteden.nexus.utils;

import de.tr7zw.nbtapi.NBT;
import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.nexus.features.menus.MenuUtils.NPCShopMenu.Product;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.shops.ShopUtils;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.models.banker.BankerService;
import gg.projecteden.nexus.models.banker.Transaction.TransactionCause;
import gg.projecteden.nexus.models.eventuser.EventUser;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.models.shop.Shop;
import gg.projecteden.nexus.models.shop.Shop.ExchangeType;
import gg.projecteden.nexus.models.shop.Shop.ShopGroup;
import gg.projecteden.nexus.models.store.Contributor;
import gg.projecteden.nexus.models.store.ContributorService;
import gg.projecteden.nexus.models.voter.Voter;
import gg.projecteden.nexus.models.voter.VoterService;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public enum Currency {
	FREE() {
		@Override
		public List<String> getPriceLore(Player player, Price price, boolean canAfford) {
			return new ArrayList<>(List.of("", "&3Price: &aFree"));
		}

		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			return true;
		}
	},
	ITEMS() {
		@Override
		public List<String> getPriceLore(Player player, Price price, boolean canAfford) {
			List<ItemStack> priceItems = price.asItems();

			List<String> lore = new ArrayList<>(List.of("", "&3Price:"));
			for (ItemStack priceItem : priceItems) {
				lore.add(" &3- " + getCanAffordColor(player, priceItem) + StringUtils.stripColor(StringUtils.pretty(priceItem)));
			}

			return lore;
		}

		private String getCanAffordColor(Player player, ItemStack item) {
			return PlayerUtils.playerHas(player, item) ? "&a" : "&c";
		}

		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			List<ItemStack> priceItems = price.asItems();
			for (ItemStack priceItem : priceItems) {
				if (!PlayerUtils.playerHas(player, priceItem)) {
					return false;
				}
			}
			return true;
		}

		@Override
		public void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			List<ItemStack> priceItems = price.asItems();
			for (ItemStack priceItem : priceItems) {
				if (Nullables.isNullOrAir(priceItem))
					continue;

				player.getInventory().removeItem(priceItem);
			}
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
			ItemStack pouch = PlayerUtils.searchInventory(player, CommonQuestItem.COIN_POUCH.getItemModel());
			if (Nullables.isNullOrAir(pouch))
				throw new InvalidInputException("Cannot deposit to " + player.getName() + ", couldn't find a pouch");

			AtomicInteger finalPouchCoins = new AtomicInteger();
			NBT.modify(pouch, nbt -> {
				if (!nbt.hasTag(CommonQuestItem.COIN_POUCH_NBT_KEY))
					throw new InvalidInputException("Cannot deposit to " + player.getName() + ", pouch is missing NBT KEY: " + CommonQuestItem.COIN_POUCH_NBT_KEY);

				Integer pouchCoins = nbt.getInteger(CommonQuestItem.COIN_POUCH_NBT_KEY);
				if (pouchCoins == null)
					throw new InvalidInputException("Cannot deposit to " + player.getName() + ", pouch NBT KEY returned null");

				pouchCoins += price.asInteger();
				nbt.setInteger(CommonQuestItem.COIN_POUCH_NBT_KEY, pouchCoins);

				finalPouchCoins.set(pouchCoins);
			});

			updateLore(pouch, finalPouchCoins.get());
		}

		@Override
		protected boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			ItemStack pouch = PlayerUtils.searchInventory(player, CommonQuestItem.COIN_POUCH.getItemModel());
			if (Nullables.isNullOrAir(pouch))
				return false;

			AtomicBoolean booleanResult = new AtomicBoolean(false);
			NBT.modify(pouch, nbt -> {
				if (nbt.hasTag(CommonQuestItem.COIN_POUCH_NBT_KEY)) {
					Integer pouchCoins = nbt.getInteger(CommonQuestItem.COIN_POUCH_NBT_KEY);
					if (pouchCoins != null)
						booleanResult.set(pouchCoins >= price.asInteger());
				}
			});

			return booleanResult.get();
		}

		@Override
		protected void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			ItemStack pouch = PlayerUtils.searchInventory(player, CommonQuestItem.COIN_POUCH.getItemModel());
			if (Nullables.isNullOrAir(pouch))
				throw new InvalidInputException("Cannot withdraw from " + player.getName() + ", couldn't find a pouch");

			AtomicInteger finalPouchCoins = new AtomicInteger();
			NBT.modify(pouch, nbt -> {
				if (!nbt.hasTag(CommonQuestItem.COIN_POUCH_NBT_KEY))
					throw new InvalidInputException("Cannot withdraw from " + player.getName() + ", pouch is missing NBT KEY: " + CommonQuestItem.COIN_POUCH_NBT_KEY);

				Integer pouchCoins = nbt.getInteger(CommonQuestItem.COIN_POUCH_NBT_KEY);
				if (pouchCoins == null)
					throw new InvalidInputException("Cannot withdraw from " + player.getName() + ", pouch NBT KEY returned null");

				pouchCoins -= price.asInteger();
				nbt.setInteger(CommonQuestItem.COIN_POUCH_NBT_KEY, pouchCoins);

				finalPouchCoins.set(pouchCoins);
			});

			updateLore(pouch, finalPouchCoins.get());
		}

		private void updateLore(ItemStack pouch, int amount) {
			List<String> lore = new ArrayList<>(CommonQuestItem.COIN_POUCH.getItemBuilder().getLore());
			lore.addAll(List.of("", "&fCoins: &e" + StringUtils.getCnf().format(amount)));
			List<String> colorizedLore = new ArrayList<>();
			for (String line : lore) {
				colorizedLore.add(StringUtils.colorize(line));
			}
			ItemMeta itemMeta = pouch.getItemMeta();
			itemMeta.setLore(colorizedLore);
			pouch.setItemMeta(itemMeta);
		}
	},
	BALANCE() {
		@Override
		public String pretty(Price price) {
			return ShopUtils.prettyMoney(price.asBalance(), price.isFree());
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
			EventUserService service = new EventUserService();
			EventUser user = service.get(player);
			user.charge(price.asInteger());
			service.save(user);
		}

		@Override
		protected void _deposit(Player player, Price price) {
			EventUserService service = new EventUserService();
			EventUser user = service.get(player);
			user.giveTokens(price.asInteger());
			service.save(user);
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
			VoterService service = new VoterService();
			Voter user = service.get(player);
			user.takePoints(price.asInteger());
			service.save(user);
		}

		@Override
		protected void _deposit(Player player, Price price) {
			VoterService service = new VoterService();
			Voter user = service.get(player);
			user.givePoints(price.asInteger());
			service.save(user);
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
			PerkOwnerService service = new PerkOwnerService();
			PerkOwner user = service.get(player);
			user.takeTokens(price.asInteger());
			service.save(user);
		}
	},
	STORE_CREDIT() {
		@Override
		public boolean _canAfford(Player player, Price price, ShopGroup shopGroup) {
			return new ContributorService().get(player).getCredit() >= price.asBalance();
		}

		@Override
		public void _withdraw(Player player, Price price, ShopGroup shopGroup, Product product) {
			ContributorService service = new ContributorService();
			Contributor user = service.get(player);
			user.takeCredit(price.asBalance());
			service.save(user);
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

	public List<String> getPriceLore(Player player, Price price, boolean canAfford) {
		return new ArrayList<>(List.of("", "&3Price: " + (canAfford ? "&a" : "&c") + this.pretty(price)));
	}

	public void log(Player viewer, Price price, Product product, ShopGroup shopGroup) {
		ItemStack item = product.getItemStack();

		if (this != BALANCE || shopGroup == null)
			return;

		double priceNum = price.asBalance();
		Shop.log(UUIDUtils.UUID0, viewer.getUniqueId(), shopGroup, StringUtils.pretty(item).split(" ", 2)[1], 1, ExchangeType.SELL, String.valueOf(priceNum), "");
	}

	@Getter
	public static class Price implements Cloneable {
		ItemStack item;
		List<ItemStack> items;
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

		@SuppressWarnings("unchecked")
		public static Price of(Object value) {
			Price result = new Price();
			if (value == null) {
				result.empty = false;
				return result;
			}

			if (value instanceof List<?> list && list.stream().allMatch(ItemStack.class::isInstance)) {
				result.items = (List<ItemStack>) list;
				result.empty = false;
			} else if (value instanceof Material material) {
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

		public List<ItemStack> asItems() {
			return items;
		}

		public void applyDiscount(double percentage) {
			if (free || empty)
				return;

			if (Nullables.isNotNullOrEmpty(items)) {
				for (ItemStack _item : new ArrayList<>(items)) {
					int amount = (int) (_item.getAmount() - Math.ceil(_item.getAmount() * percentage));
					if (amount <= 0) {
						amount = 0;
						items.remove(_item);
					}
					item.setAmount(amount);
				}

				if (items.isEmpty())
					free = true;
			}

			if (Nullables.isNotNullOrAir(item)) {
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
			if (Nullables.isNotNullOrEmpty(items))
				price.items = new ArrayList<>(items);
			if (Nullables.isNotNullOrAir(item))
				price.item = item.clone();
			price.numInt = numInt;
			price.numDouble = numDouble;
			price.free = free;
			price.empty = empty;

			return price;
		}
	}


}
