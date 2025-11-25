package gg.projecteden.nexus.features.events.y2025.pugmas25.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.operator.WeatherCommand.FixedWeatherType;
import gg.projecteden.nexus.features.events.models.PlayerEventFishingBiteEvent;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootTime;
import gg.projecteden.nexus.features.events.models.EventFishingLoot.FishingLoot;
import gg.projecteden.nexus.features.events.models.PlayerEventFishingCaughtFishEvent;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25BiomeDistrict;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25Districts.Pugmas25District;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Pugmas25Fishing implements Listener {

	private static final Pugmas25UserService userService = new Pugmas25UserService();
	private static final ItemModelType rodReinforced = Pugmas25QuestItem.FISHING_ROD_REINFORCED.getItemModel();
	private static final ItemModelType rodGolden = Pugmas25QuestItem.FISHING_ROD_GOLDEN.getItemModel();
	private static final ItemModelType fishFinder = Pugmas25QuestItem.FISH_FINDER.getItemModel();
	private static final ItemModelType pda = Pugmas25QuestItem.PDA.getItemModel();

	public Pugmas25Fishing() {
		Nexus.registerListener(this);
	}

	public enum Pugmas25AnglerLoot {
		// River
		WIGGLY_STICK(Pugmas25District.RIVER, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_ELECTRIC_EEL),
		DRIFTWOOD_CARP(Pugmas25District.RIVER, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_FOURHORN_SCULPIN),
		CURRENTCLAW(Pugmas25District.RIVER, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_LINGCOD),
		// Lake
		PEBBLE_CHEWER_BASS(Pugmas25District.LAKE, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_SMALLMOUTH_BASS),
		GRASS_PICKEREL(Pugmas25District.LAKE, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_GRASS_PICKEREL),
		MOONFIN(Pugmas25District.LAKE, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_MOTHER_MOON),
		// Frozen Lake
		PENGFISH(Pugmas25District.FROZEN_LAKE, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_PENGFISH),
		SHIVERBACK_SALMON(Pugmas25District.FROZEN_LAKE, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_IRONFISH),
		FROSTBITE_MINNOW(Pugmas25District.FROZEN_LAKE, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_FROZEN_BONEMINNOW),
		// Hot Springs
		SCALDING_SNAPPER(Pugmas25District.HOT_SPRINGS, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_RED_SNAPPER),
		THERMO_TROUT(Pugmas25District.HOT_SPRINGS, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_RAINBOW_TROUT),
		OBSTER(Pugmas25District.HOT_SPRINGS, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_OBSTER),
		// Fairgrounds
		CLOWNFISH(Pugmas25District.FAIRGROUNDS, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_TROPICAL_FISH),
		COTTON_CANDY_GOLDFISH(Pugmas25District.FAIRGROUNDS, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_COTTON_CANDY_GOLDFISH),
		TRICKSTER_KOI(Pugmas25District.FAIRGROUNDS, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_KOI),
		// Sawmill
		SPLINTERTAIL(Pugmas25District.SAWMILL, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_SABLEFISH),
		SAWTOOTH_CARP(Pugmas25District.SAWMILL, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_SAWFISH),
		WOODWORM_EEL(Pugmas25District.SAWMILL, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_EEL),
		// Caves
		ROCKTOPUS(Pugmas25District.CAVES, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_OCTOPUS),
		CAVESHROOM(Pugmas25District.CAVES, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_SEASHROOM_RAINBOW),
		SKULLFIN(Pugmas25District.CAVES, EventFishingLootTime.DAY, ItemModelType.FISHING_LOOT_SKULLFIN),
		VAMPIRE_SQUID(Pugmas25District.CAVES, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_VAMPIRE_SQUID),
		ZOMBIEFISH(Pugmas25District.CAVES, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_ZOMBIEFISH),
		// Caves - Dripstone
		ECHO_EEL(Pugmas25District.CAVES, Pugmas25BiomeDistrict.DRIPSTONE, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_LAMPREY),
		SIPHONFIN(Pugmas25District.CAVES, Pugmas25BiomeDistrict.DRIPSTONE, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_LEECH),
		// Caves - Lush
		SLIMEFISH(Pugmas25District.CAVES, Pugmas25BiomeDistrict.LUSH, EventFishingLootTime.BOTH, ItemModelType.FISHING_LOOT_SLIMEFISH),
		PIRANHA(Pugmas25District.CAVES, Pugmas25BiomeDistrict.LUSH, EventFishingLootTime.NIGHT, ItemModelType.FISHING_LOOT_PIRANHA),
		;

		private final String modelId;
		@Getter
		private final String customName;
		private final FishingLoot loot;

		Pugmas25AnglerLoot(Pugmas25District district, EventFishingLootTime time, ItemModelType model) {
			this((player) -> Pugmas25District.of(player.getLocation()) == district, model, time);
		}

		Pugmas25AnglerLoot(Pugmas25District district, Pugmas25BiomeDistrict biomeDistrict, EventFishingLootTime time, ItemModelType model) {
			this((player) -> Pugmas25District.of(player.getLocation()) == district
				&& Pugmas25BiomeDistrict.of(player.getLocation()) == biomeDistrict, model, time);
		}

		Pugmas25AnglerLoot(Predicate<Player> predicate, ItemModelType model, EventFishingLootTime time) {
			this.modelId = model.getModel();
			this.customName = StringUtils.camelCase(this);
			//
			this.loot = new FishingLoot(name(), EventFishingLootCategory.SPECIAL, Material.LEATHER_HORSE_ARMOR, modelId, 5,
				customName, "&7Quest Fish", time, null, predicate);
		}

		public ItemStack getItem() {
			return loot.getItem();
		}

		public boolean matches(ItemStack itemStack) {
			return new ItemBuilder(itemStack).model().equalsIgnoreCase(modelId);
		}
	}

	public static int getLuck(Player player) {
		int luck = 0;

		// Weather
		if (FixedWeatherType.of(player.getWorld()) != FixedWeatherType.CLEAR)
			luck += 5;

		// Tools - In hand
		ItemStack tool = ItemUtils.getTool(player);
		if (Nullables.isNotNullOrAir(tool)) {
			ItemModelType toolModel = ItemModelType.of(tool);
			if (toolModel != null) {
				if (rodReinforced == toolModel)
					luck += 5;
				else if (rodGolden == toolModel)
					luck += 15;
			}
		}

		// Tools - In inventory
		boolean hasFishFinder = false;
		for (ItemStack content : player.getInventory().getContents()) {
			if (isNullOrAir(content))
				continue;

			ItemModelType itemModel = ItemModelType.of(content);
			if (!hasFishFinder) {
				if (fishFinder == itemModel || pda == itemModel) {
					hasFishFinder = true;
					luck += 5;
				}
			}
		}

		return luck;
	}

	@EventHandler
	public void onFishBite(PlayerEventFishingBiteEvent event) {
		Player player = event.getPlayer();

		if (!Pugmas25.get().shouldHandle(player))
			return;

		Pugmas25User user = userService.get(player);
		int luck = Pugmas25Fishing.getLuck(player);

		int anglerLuck = luck;
		if (user.isHasCaughtAnglerQuestLoot())
			anglerLuck = (int) Math.ceil(anglerLuck / 3.0);

		Pugmas25AnglerLoot anglerLoot = Pugmas25Config.get().getAnglerQuestFish();
		if (anglerLoot != null && !anglerLoot.loot.applies(player))
			anglerLoot = null;

		user.sendMessage("DEBUG: Luck = " + luck + " | Angler Luck = " + anglerLuck); // TODO: REMOVE
		List<ItemStack> resultLoot = new ArrayList<>();
		for (ItemStack itemStack : event.getLoot()) {
			// Replace loot with angler quest fish
			if (anglerLoot != null && RandomUtils.chanceOf(anglerLuck)) {
				user.sendMessage("DEBUG: Replaced loot with angler quest fish"); // TODO: REMOVE
				resultLoot.add(anglerLoot.getItem());
				continue;
			}

			// Replace loot with treasure
			if (RandomUtils.chanceOf(luck)) {
				FishingLoot treasureLoot = null; // TODO: CRATES
				if (treasureLoot != null) {
					user.sendMessage("DEBUG: Replaced loot with treasure"); // TODO: REMOVE
					resultLoot.add(treasureLoot.getItem());
					continue;
				}
			}

			resultLoot.add(itemStack);
		}

		event.setLoot(resultLoot);
	}

	@EventHandler
	public void on(PlayerEventFishingCaughtFishEvent event) {
		Player player = event.getPlayer();

		if (!Pugmas25.get().isAtEvent(player))
			return;

		Pugmas25AnglerLoot anglerLoot = Pugmas25Config.get().getAnglerQuestFish();
		if (anglerLoot == null)
			return;

		Pugmas25User user = userService.get(player);
		if (user.isHasCaughtAnglerQuestLoot())
			return;

		for (ItemStack item : event.getLoot()) {
			if (Nullables.isNullOrAir(item))
				continue;

			if (anglerLoot.matches(item)) {
				user.setHasCaughtAnglerQuestLoot(true);
				userService.save(user);
			}
		}
	}
}
