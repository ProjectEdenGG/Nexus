package gg.projecteden.nexus.features.crates;

import com.google.common.base.Strings;
import gg.projecteden.crates.api.CrateAnimationsAPI;
import gg.projecteden.crates.api.models.CrateAnimation;
import gg.projecteden.crates.api.models.CrateAnimationType;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.chat.Chat;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.framework.exceptions.postconfigured.CrateOpeningException;
import gg.projecteden.nexus.models.crate.CrateConfig.CrateLoot;
import gg.projecteden.nexus.models.crate.CrateType;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
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

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;
import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@Data
public class  CrateHandler {

	public static final Map<UUID, CrateAnimation> ANIMATIONS = new HashMap<>();

	public static void openCrate(CrateType type, ArmorStand entity, Player player, int amount) {
		Nexus.debug(player.getName() + " is opening a " + camelCase(type) + " crate");
		if (isInUse(entity)) return;
		Nexus.debug("The crate is not in use");

		CrateLoot loot = pickCrateLoot(type);
		if (!canHoldItems(player, loot))
			return;

		CrateRecapBook recap = new CrateRecapBook(type);
		AtomicInteger amountRemaining = new AtomicInteger(amount);

		Nexus.debug("The player can hold the items");
		CrateAnimation animation;
		ItemStack itemstack = amount > 1 ? new ItemStack(Material.DIAMOND) : loot.getDisplayItem();
		String displayName = amount > 1 ? "&3Multiple Rewards" : loot.getDisplayName();
		try {
			BiFunction<Location, Consumer<Item>, Item> func = (location, item) -> {
				amountRemaining.decrementAndGet();
				recap.add(loot);
				giveItems(player, loot);

				while (amountRemaining.getAndDecrement() > 0) {
					CrateLoot _loot = pickCrateLoot(type);
					if (!canHoldItems(player, _loot))
						break;
					takeKey(type, player);
					recap.add(_loot);
					giveItems(player, _loot);
				}

				Consumer<Item> itemConsumer = item2 -> {
					type.handleItem(item2);
					item.accept(item2);
					item2.customName(new JsonBuilder(displayName).build());
				};
				return location.getWorld().dropItem(location, itemstack, itemConsumer::accept);
			};
			final @Nullable RegisteredServiceProvider<CrateAnimationsAPI> serviceProvider = Bukkit.getServicesManager().getRegistration(CrateAnimationsAPI.class);
			if (serviceProvider == null)
				throw new NullPointerException("CrateAnimationsAPI does not appear to be loaded");
			animation = serviceProvider.getProvider().getAnimation(CrateAnimationType.valueOf(type.name()), entity, func);
			if (animation == null)
				throw new NullPointerException("Could not generate crate animation object for type: " + type.name());
		} catch (Exception e) {
			e.printStackTrace();
			throw new CrateOpeningException("Unable to start animation");
		}

		Nexus.debug("Starting animation");
		try {
			ANIMATIONS.put(entity.getUniqueId(), animation);
			takeKey(type, player);
			animation.play().thenRun(() -> {
				ANIMATIONS.remove(entity.getUniqueId());

				if (amount > 1)
					recap.open(player);

				Nexus.debug("Finished animation, ending");
			});
		} catch (Throwable ex) {
			ex.printStackTrace();
			animation.stop();
			animation.reset();
		}
	}

	private static boolean isInUse(Entity entity) {
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

	private static CrateLoot pickCrateLoot(CrateType type) {
		Map<CrateLoot, Double> original = new HashMap<>();
		Crates.getLootByType(type).stream()
			.filter(CrateLoot::isActive)
			.forEach(crateLoot -> original.put(crateLoot, crateLoot.getWeight()));

		if (original.isEmpty())
			throw new CrateOpeningException("&3Coming soon...");

		return RandomUtils.getWeightedRandom(original);
	}

	private static void giveItems(Player player, CrateLoot loot) {
		PlayerUtils.giveItems(player, loot.getItems());
		if (!isNullOrEmpty(loot.getCommandsNoSlash()))
			loot.getCommandsNoSlash().forEach(command -> PlayerUtils.runCommandAsConsole(command.replaceAll("%player%", player.getName())));
		if (loot.isShouldAnnounce())
			Chat.Broadcast.all()
				.prefix("Crates")
				.muteMenuItem(MuteMenuItem.CRATES)
				.message(Strings.isNullOrEmpty(loot.getAnnouncement()) ?
					         "&e" + Nickname.of(player) + " &3has received a &e" + loot.getTitle() + " &3from the &e" + camelCase(loot.getType()) + " Crate" :
					         loot.getAnnouncement()
						         .replaceAll("%player%", Nickname.of(player.getName()))
						         .replaceAll("%title%", loot.getTitle()))
				.send();
	}

	private static void takeKey(CrateType type, Player player) {
		try {
			boolean took = false;
			ItemStack key = type.getKey();
			for (ItemStack item : player.getInventory().getContents()) {
				if (isNullOrAir(item)) continue;
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
		if (!ANIMATIONS.containsKey(entity.getUniqueId()))
			return;
		ANIMATIONS.get(entity.getUniqueId()).reset();
	}
}
