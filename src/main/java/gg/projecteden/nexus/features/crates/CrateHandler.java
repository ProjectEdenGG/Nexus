package gg.projecteden.nexus.features.crates;

import com.google.common.base.Strings;
import gg.projecteden.api.common.utils.Nullables;
import gg.projecteden.crates.api.models.CrateAnimation;
import gg.projecteden.crates.api.models.CrateAnimationsAPI;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.features.menus.MenuUtils;
import gg.projecteden.nexus.features.minigames.models.perks.PerkCategory;
import gg.projecteden.nexus.features.minigames.models.perks.PerkType;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CrateOpeningException;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.perkowner.PerkOwner;
import gg.projecteden.nexus.models.perkowner.PerkOwnerService;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.nexus.utils.Utils;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

@Data
public class  CrateHandler {

	public static final Map<UUID, CrateAnimation> ANIMATIONS = new HashMap<>();

	public static void openCrate(CrateType type, ArmorStand entity, Player player, int amount, boolean useKey) {
		if (isInUse(entity)) return;

		CrateLoot loot = pickCrateLoot(type, player);
		if (loot == null) {
			if (type == CrateType.MINIGAMES)
				throw new CrateOpeningException("You already own all minigame collectibles");
			else
				throw new CrateOpeningException("There was an error while generating crate loot");
		}
		if (!canHoldItems(player, loot))
			return;

		CrateRecapBook recap = new CrateRecapBook(type);
		AtomicInteger amountRemaining = new AtomicInteger(amount);

		CrateAnimation animation;
		ItemStack itemstack = amount > 1 ? new ItemStack(Material.DIAMOND) : loot.getDisplayItem();
		String displayName = amount > 1 ? "&3Multiple Rewards" : loot.getDisplayName();
		try {
			BiFunction<Location, Consumer<Item>, Item> func = (location, item) -> {
				try {
					amountRemaining.decrementAndGet();
					recap.add(loot);
					giveItems(player, loot);

					while (amountRemaining.getAndDecrement() > 0) {
						if (!player.isOnline())
							break;
						CrateLoot _loot = pickCrateLoot(type, player);
						if (!canHoldItems(player, _loot))
							break;
						takeKey(type, player, useKey);
						recap.add(_loot);
						giveItems(player, _loot);
					}

					Consumer<Item> itemConsumer = item2 -> {
						type.handleItem(item2);
						item.accept(item2);
						item2.customName(new JsonBuilder(displayName).build());
					};
					return location.getWorld().dropItem(location, itemstack, itemConsumer::accept);
				} catch (Exception ex) {
					MenuUtils.handleException(player, Crates.PREFIX, ex);
					CrateHandler.reset(entity);
					return null;
				}
			};

			final @Nullable RegisteredServiceProvider<CrateAnimationsAPI> serviceProvider = Bukkit.getServicesManager().getRegistration(CrateAnimationsAPI.class);
			if (serviceProvider == null)
				throw new NullPointerException("CrateAnimationsAPI does not appear to be loaded");
			animation = serviceProvider.getProvider().getAnimation(type.name(), entity, func);

			if (animation == null)
				throw new NullPointerException("Could not generate crate animation object for type: " + type.name());

		} catch (Exception e) {
			e.printStackTrace();
			throw new CrateOpeningException("Unable to start animation");
		}

		try {
			ANIMATIONS.put(entity.getUniqueId(), animation);
			takeKey(type, player, useKey);
			animation.play().thenRun(() -> {
				ANIMATIONS.remove(entity.getUniqueId());

				if (amount > 1)
					recap.open(player);
			});
		} catch (Exception ex) {
			MenuUtils.handleException(player, Crates.PREFIX, ex);
			CrateHandler.reset(entity);
		}
	}

	public static boolean isInUse(Entity entity) {
		if (!ANIMATIONS.containsKey(entity.getUniqueId()))
			return false;
		return ANIMATIONS.get(entity.getUniqueId()).isActive();
	}

	private static boolean canHoldItems(Player player, CrateLoot loot) {
		if (!PlayerUtils.hasRoomFor(player, loot.getItems())) {
			PlayerUtils.send(player, Crates.PREFIX + "Please clear room in your inventory before continuing to open crates");
			return false;
		}
		return true;
	}

	public static CrateLoot pickCrateLoot(CrateType type, Player player) {
		if (type == CrateType.MINIGAMES)
			return pickMinigameLoot(player);
		Map<CrateLoot, Double> original = new HashMap<>();
		Crates.getLootByType(type).stream()
			.filter(CrateLoot::isActive)
			.forEach(crateLoot -> original.put(crateLoot, crateLoot.getWeightForPlayer(player)));

		if (original.isEmpty())
			throw new CrateOpeningException("&3Coming soon...");

		return RandomUtils.getWeightedRandom(original);
	}

	private static CrateLoot pickMinigameLoot(Player player) {
		PerkOwnerService service = new PerkOwnerService();
		PerkOwner perkOwner = service.get(player);

		// get a random perk the player doesn't own
		Map<PerkType, Double> weights = new HashMap<>();
		List<PerkType> rawUnownedPerks = Arrays.stream(PerkType.values()).filter(type -> !perkOwner.getPurchasedPerks().containsKey(type)).toList();
		if (rawUnownedPerks.isEmpty())
			return null;

		// filter out pride flag hats if possible
		List<PerkType> unownedPerks = rawUnownedPerks.stream().filter(type -> type.getPerkCategory() != PerkCategory.PRIDE_FLAG_HAT).toList();
		if (unownedPerks.isEmpty())
			unownedPerks = rawUnownedPerks;

		// weights should be inverse of the cost (i.e. cheapest is most common/highest number)
		int maxPrice = (int) Utils.getMax(unownedPerks, PerkType::getPrice).getValue();
		int minPrice = (int) Utils.getMin(unownedPerks, PerkType::getPrice).getValue();
		unownedPerks.forEach(perkType -> weights.put(perkType, (double) (maxPrice-perkType.getPrice()+minPrice)));
		PerkType perkType = RandomUtils.getWeightedRandom(weights);

		if (perkType == null)
			return null;

		String name = perkType.getName() + " " + StringUtils.camelCase(perkType.getPerkCategory());
		return new CrateLoot(-1, name, new ArrayList<>(), -1, true, CrateType.MINIGAMES,
			perkType.getPerk().getMenuItem(), Arrays.asList("mgm collectibles give %player% " + perkType.name()), false, null);
	}

	private static void giveItems(Player player, CrateLoot loot) {
		PlayerUtils.giveItems(player, loot.getItems());
		if (!Nullables.isNullOrEmpty(loot.getCommandsNoSlash()))
			loot.getCommandsNoSlash().forEach(command -> PlayerUtils.runCommandAsConsole(command.replaceAll("%player%", player.getName())));
		if (loot.isShouldAnnounce())
			Chat.Broadcast.all()
				.prefix("Crates")
				.muteMenuItem(MuteMenuItem.CRATES)
				.message(Strings.isNullOrEmpty(loot.getAnnouncement()) ?
					         "&e" + Nickname.of(player) + " &3has received a &e" + loot.getTitle() + " &3from the &e" + gg.projecteden.api.common.utils.StringUtils.camelCase(loot.getType()) + " Crate" :
					         loot.getAnnouncement()
						         .replaceAll("%player%", Nickname.of(player.getName()))
						         .replaceAll("%title%", loot.getTitle()))
				.send();
	}

	private static void takeKey(CrateType type, Player player, boolean useKey) {
		if (!useKey)
			return;
		try {
			boolean took = false;
			ItemStack key = type.getKey();
			for (ItemStack item : player.getInventory().getContents()) {
				if (gg.projecteden.nexus.utils.Nullables.isNullOrAir(item)) continue;
				if (ItemUtils.isFuzzyMatch(key, item)) {
					item.setAmount(item.getAmount() - 1);
					took = true;
					break;
				}
			}
			if (!took) throw new CrateOpeningException("no key present");
		} catch (Exception ex) {
			throw new CrateOpeningException("You must have a key in your inventory");
		}
	}

	public static void reset(Entity entity) {
		CrateAnimation animation = ANIMATIONS.get(entity.getUniqueId());
		if (animation == null)
			return;
		animation.stop();
		animation.reset();
		ANIMATIONS.remove(entity.getUniqueId());
	}
}
