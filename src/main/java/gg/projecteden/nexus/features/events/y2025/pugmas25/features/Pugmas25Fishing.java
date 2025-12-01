package gg.projecteden.nexus.features.events.y2025.pugmas25.features;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.commands.staff.operator.WeatherCommand.FixedWeatherType;
import gg.projecteden.nexus.features.events.models.PlayerEventFishingBiteEvent;
import gg.projecteden.nexus.features.events.models.PlayerEventFishingCaughtFishEvent;
import gg.projecteden.nexus.features.events.y2025.pugmas25.Pugmas25;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25AnglerLoot;
import gg.projecteden.nexus.features.events.y2025.pugmas25.models.Pugmas25SidebarLine;
import gg.projecteden.nexus.features.events.y2025.pugmas25.quests.Pugmas25QuestItem;
import gg.projecteden.nexus.features.quests.CommonQuestItem;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.resourcepack.models.ItemModelType;
import gg.projecteden.nexus.models.eventuser.EventUserService;
import gg.projecteden.nexus.models.pugmas25.Pugmas25Config;
import gg.projecteden.nexus.models.pugmas25.Pugmas25User;
import gg.projecteden.nexus.models.pugmas25.Pugmas25UserService;
import gg.projecteden.nexus.models.quests.Quester;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemUtils;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.NonNull;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

public class Pugmas25Fishing implements Listener {

	private static final Pugmas25UserService userService = new Pugmas25UserService();
	private static final ItemModelType rodWooden = Pugmas25QuestItem.FISHING_ROD_WOOD.getItemModel();
	private static final ItemModelType rodReinforced = Pugmas25QuestItem.FISHING_ROD_REINFORCED.getItemModel();
	private static final ItemModelType rodGolden = Pugmas25QuestItem.FISHING_ROD_GOLDEN.getItemModel();
	private static final ItemModelType fishFinder = Pugmas25QuestItem.FISH_FINDER.getItemModel();
	private static final ItemModelType pda = Pugmas25QuestItem.PDA.getItemModel();
	private static final String anglerHat = Pugmas25QuestItem.ANGLER_HAT.getItemBuilder().model();
	private static final String anglerVest = Pugmas25QuestItem.ANGLER_VEST.getItemBuilder().model();
	private static final String anglerPants = Pugmas25QuestItem.ANGLER_PANTS.getItemBuilder().model();

	public Pugmas25Fishing() {
		Nexus.registerListener(this);
	}

	public static void giveRewards(Dialog dialog, Pugmas25User user, Quester quester) {
		// Generic reward
		dialog.npc("Thank you so much, here’s your reward!");
		new EventUserService().edit(quester, eventUser -> eventUser.giveTokens(5));

		int timesCompleted = user.getCompletedAnglerQuests();

		// Nth reward
		if (timesCompleted == 3) {
			dialog.npc("Have this fancy Gold Watch! Now you won’t have to squint at the sky and guess whether it’s day or night!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.GOLD_WATCH.get());
		} else if (timesCompleted == 6) {
			dialog.npc("Take this Angler Hat, it'll help the fish like you more! ...Probably.");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.ANGLER_HAT.get());
		} else if (timesCompleted == 12) {
			dialog.npc("Here’s an Angler Vest to match your hat! Now you’re really starting to look like a master angler!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.ANGLER_VEST.get());
		} else if (timesCompleted == 18) {
			dialog.npc("You're really committed! Here, have these Angler Pants! Now you’ve got the full Angler look!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.ANGLER_PANTS.get());
		} else if (timesCompleted == 26) {
			dialog.npc("You’ve earned something special! This Reinforced Rod adds more luck AND lets you reel in Iron Trunks!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.FISHING_ROD_REINFORCED.get());
		} else if (timesCompleted == 38) {
			dialog.npc("You’re a legendary angler! Take this Golden Fishing Rod, it's super lucky, super shiny, AND it pulls up Diamond Trunks!");
			PlayerUtils.giveItem(quester, Pugmas25QuestItem.FISHING_ROD_GOLDEN.get());
		}

		// Tool chance reward or nth
		tryGiveFishingTool(dialog, quester, timesCompleted, 8, Pugmas25SidebarLine.FISHING_LUCK); // FISHING_POCKET_GUIDE
		tryGiveFishingTool(dialog, quester, timesCompleted, 15, Pugmas25SidebarLine.HEIGHT); // SEXTANT
		tryGiveFishingTool(dialog, quester, timesCompleted, 22, Pugmas25SidebarLine.WEATHER); // WEATHER_RADIO
	}

	private static void tryGiveFishingTool(Dialog dialog, Quester quester, int timesCompleted, int guaranteed, Pugmas25SidebarLine sidebarLine) {
		if (sidebarLine.canRender(quester.getPlayer()))
			return;

		if (timesCompleted == guaranteed || RandomUtils.chanceOf(5)) {
			dialog.npc("Also, take this too! It should make fishing a little easier!");
			PlayerUtils.giveItem(quester, sidebarLine.getSpecificItem().get());
		}
	}

	public static void getAnglerReaction(Dialog dialog, Pugmas25AnglerLoot questFish) {
		if (RandomUtils.chanceOf(50)) {
			dialog
				.npc("You did?! Let me see!")
				.npc("Wow! A " + questFish.getCustomName() + "!");
		} else {
			dialog
				.npc("NO WAY—YOU REALLY CAUGHT IT?!")
				.npc("That’s totally the " + questFish.getCustomName() + "!");
		}
	}

	public static int getLuck(Player player) {
		int luck = 0;

		// Weather
		if (FixedWeatherType.of(player.getWorld()) != FixedWeatherType.CLEAR)
			luck += 5;

		// Armor
		ItemStack helmet = player.getInventory().getHelmet();
		if (Nullables.isNotNullOrAir(helmet) && new ItemBuilder(helmet).model().equalsIgnoreCase(anglerHat))
			luck += 4;

		ItemStack chestplate = player.getInventory().getChestplate();
		if (Nullables.isNotNullOrAir(chestplate) && new ItemBuilder(helmet).model().equalsIgnoreCase(anglerVest))
			luck += 4;

		ItemStack leggings = player.getInventory().getLeggings();
		if (Nullables.isNotNullOrAir(leggings) && new ItemBuilder(helmet).model().equalsIgnoreCase(anglerPants))
			luck += 4;

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

		int anglerLuck = Math.max(luck, 5);
		if (user.isCaughtAnglerQuestLoot())
			anglerLuck = (int) Math.ceil(anglerLuck / 3.0);

		Pugmas25AnglerLoot anglerLoot = Pugmas25Config.get().getAnglerQuestFish();
		if (anglerLoot != null && !anglerLoot.getLoot().applies(player))
			anglerLoot = null;

		List<ItemStack> resultLoot = new ArrayList<>();
		for (ItemStack itemStack : event.getLoot()) {
			// Replace loot with angler quest fish
			if (anglerLoot != null && RandomUtils.chanceOf(anglerLuck)) {
				resultLoot.add(anglerLoot.getItem());
				continue;
			}

			// Replace loot with treasure
			if (RandomUtils.chanceOf(luck)) {
				ItemStack treasureLoot = getFishingTreasure(player, luck);
				resultLoot.add(treasureLoot);
				continue;
			}

			resultLoot.add(itemStack);
		}

		event.setLoot(resultLoot);
	}

	private @NonNull ItemStack getFishingTreasure(Player player, int luck) {
		List<ItemStack> possibleTreasure = new ArrayList<>();

		if (Pugmas25QuestItem.LUCKY_HORSESHOE.isInInventoryOf(player))
			luck += 10;
		else
			possibleTreasure.add(Pugmas25QuestItem.LUCKY_HORSESHOE.get());

		// Trunks
		ItemStack tool = ItemUtils.getTool(player);
		ItemModelType toolModel = ItemModelType.of(tool);
		if (Nullables.isNotNullOrAir(tool) && toolModel != null) {
			if (rodWooden == toolModel)
				possibleTreasure.add(Pugmas25QuestItem.TRUNK_IRON.get());
			else if (rodReinforced == toolModel)
				possibleTreasure.add(Pugmas25QuestItem.TRUNK_GOLD.get());
			else if (rodGolden == toolModel)
				possibleTreasure.add(Pugmas25QuestItem.TRUNK_DIAMOND.get());
		}

		// Ingots
		possibleTreasure.add(new ItemBuilder(Material.GOLD_NUGGET).amount(Pugmas25.getLuckyAmount(4, 10, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.GOLD_INGOT).amount(Pugmas25.getLuckyAmount(2, 8, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.IRON_NUGGET).amount(Pugmas25.getLuckyAmount(3, 7, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.IRON_INGOT).amount(Pugmas25.getLuckyAmount(2, 6, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.DIAMOND).amount(Pugmas25.getLuckyAmount(1, 6, luck)).build());
		possibleTreasure.add(new ItemBuilder(Material.EMERALD).amount(Pugmas25.getLuckyAmount(3, 8, luck)).build());

		// Tools
		possibleTreasure.add(new ItemBuilder(Material.DIAMOND_PICKAXE).build());
		possibleTreasure.add(new ItemBuilder(Material.IRON_PICKAXE).build());

		// Misc
		if (luck > 20) {
			if (!CommonQuestItem.DISCOUNT_CARD.isInInventoryOf(player))
				possibleTreasure.add(CommonQuestItem.DISCOUNT_CARD.get());

			possibleTreasure.add(Pugmas25QuestItem.SLOT_MACHINE_TOKEN.get());
			possibleTreasure.add(Pugmas25GiftGiver.getGift(player, 1));
			possibleTreasure.add(new ItemBuilder(Material.NETHERITE_SCRAP).amount(Pugmas25.getLuckyAmount(1, 2, luck)).build());
		}

		return RandomUtils.randomElement(possibleTreasure);
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
		if (user.isCaughtAnglerQuestLoot())
			return;

		for (ItemStack item : event.getLoot()) {
			if (Nullables.isNullOrAir(item))
				continue;

			if (anglerLoot.matches(item)) {
				user.setCaughtAnglerQuestLoot(true);
				userService.save(user);
			}
		}
	}
}
