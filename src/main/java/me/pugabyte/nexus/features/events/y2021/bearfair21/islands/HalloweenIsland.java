package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Quests;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.HalloweenIsland.HalloweenNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.nexus.features.events.models.QuestStage.STEP_ONE;
import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.isAtBearFair;

// TODO BF21: Testing
@Region("halloween")
@NPCClass(HalloweenNPCs.class)
public class HalloweenIsland implements Listener, BearFair21Island {
	static BearFair21UserService userService = new BearFair21UserService();

	private static final ItemBuilder cookies = new ItemBuilder(Material.COOKIE).name("Grandma's Homemade Cookies").customModelData(1).amount(16);
	//
	private static final ItemBuilder chocolate = new ItemBuilder(Material.COCOA_BEANS).name("Chocolate Bar").customModelData(1);
	private static final ItemBuilder milk = new ItemBuilder(Material.MILK_BUCKET).name("Milk Carton").customModelData(1);
	private static final ItemBuilder flour = new ItemBuilder(Material.WHEAT).name("Bag of Flour").customModelData(1);
	//
	private static final Location location_chocolate = new Location(BearFair21.getWorld(), 101, 105, -332);
	private static final Location location_milk = new Location(BearFair21.getWorld(), 100, 105, -332);
	private static final Location location_flour = new Location(BearFair21.getWorld(), 99, 105, -332);

	public HalloweenIsland() {
		Nexus.registerListener(this);
	}

	public enum HalloweenNPCs implements BearFair21TalkingNPC {
		JOSE(BearFair21NPC.JOSE) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				QuestStage questStage = user.getQuestStage_Halloween();
				if (questStage.isInProgress() && questStage != QuestStage.STEPS_DONE)
					questStage = QuestStage.STARTED;

				switch (questStage) {
					case NOT_STARTED, STARTED -> {
						script.add("Hello <player>, and welcome to our small village.");
						script.add("wait 60");
						script.add("I wish you would see a more happy side of the town, but me madre recently left us.");
						script.add("wait 80");
						script.add("<self> I'm very sorry for your loss.");
						script.add("wait 60");
						script.add("My son is having a birthday and he really looked forward to &oAna&f's, his grandmother's homemade cookies.");
						script.add("wait 100");
						script.add("It won’t feel like a real birthday without them..");
						script.add("wait 80");
						script.add("Hmmm... I know you just came here. But, can you help us get the recipe, so we can bake some?");
						script.add("wait 80");
						script.add("<self> Of course! How can I walk away knowing not having these cookies would be a complete birthday disaster!");
						script.add("wait 100");
						script.add("I heard &oSantiago&f, our village priest, talk about someone visiting the underworld recently.");
						script.add("wait 80");
						script.add("You can find him at the church.");

						user.setQuestStage_Halloween(QuestStage.STARTED);
						userService.save(user);
						return script;
					}
					case STEPS_DONE -> {
						List<ItemBuilder> required = Collections.singletonList(cookies.clone());

						if (!Quests.hasAllItemsLikeFrom(user, required)) {
							script.add("Did you find the recipe for the cookies yet?");
							return script;
						}

						Quests.removeItems(user, required);

						int wait;
						script.add("Aaah madres cookies!! I see you got them, mucho gracias!");
						script.add("wait 60");
						script.add("These smell so good. I can’t wait to eat them all!");
						script.add("wait 60");
						script.add("<self> Wait a second, aren't those for the birthday party?");
						script.add("wait 60");
						script.add("Oh yes.. My son’s birthday party. I will share these cookies with him.. Yes..");
						script.add("wait 80");
						script.add("<self> For some reason that doesn't give me much confidence.");
						script.add("wait 60");
						script.add("Here, have this as a thank you for bringing me.. I mean us, these cookies!");
						wait = (60 + 60 + 60 + 80 + 60);
						Tasks.wait(wait, () -> Quests.giveKey(user));

						script.add("wait 60");
						script.add("You’re always welcome here again, amigo!");

						user.setQuestStage_Halloween(QuestStage.COMPLETE);
						userService.save(user);
						return script;
					}
				}

				script.add("TODO BF21 - Greeting");
				return script;
			}
		},
		SANTIAGO(BearFair21NPC.SANTIAGO) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				switch (user.getQuestStage_Halloween()) {
					case STARTED, STEP_ONE -> {
						script.add("Welcome. What’s your name, child?");
						script.add("wait 60");
						script.add("Nice to meet you, <player>. How may I help you?");
						script.add("wait 80");
						script.add("<self> I'm on a quest to find the recipe for Jose's moms cookies, for it seems she took the recipe to the grave.");
						script.add("wait 100");
						script.add("<self> Do you know if its at all possible to get it?");
						script.add("wait 60");
						script.add("Ah, I see. Yes, she left us very recently. You'll need to visit &oAna&f in the underworld.");
						script.add("wait 80");
						script.add("If you want to see her, you need to follow her path. If and when you find her, please wish her well from me.");
						script.add("wait 100");
						script.add("<self> of course! Thank you for all your help Santiago.");
						script.add("wait 60");
						script.add("To enter the underworld, simply hop in this casket. The path you seek should reveal itself when inside.");

						user.setQuestStage_Halloween(STEP_ONE);
						userService.save(user);
						return script;
					}
				}

				script.add("TODO BF21 - Greeting");
				return script;
			}
		},
		ANA(BearFair21NPC.ANA) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				switch (user.getQuestStage_Halloween()) {
					case STEP_ONE, STEP_TWO -> {
						script.add("Ohohoho. I haven’t felt this alive in so long. My body feels so light and young.");
						script.add("wait 80");
						script.add("Hmm? Oh hello little one.");
						script.add("wait 40");
						script.add("<self> Hello ma'am, I was sent here by Jose. He's asked me to retrieve your cookie recipie in order to prepare for his son's birthday!");
						script.add("wait 120");
						script.add("<self> Everyone misses you greatly so I'm sure the cookies would help cheer them up.");
						script.add("wait 80");
						script.add("Ah, my son &oJosé &fsent you? Aaah, mmm yes my cookies. He did love them a lot.");
						script.add("wait 80");
						script.add("The recipe? Jajaja, that recipe is my little secret. But you know what, if you get me the ingredients I will make some for you.");
						script.add("wait 120");
						script.add("Awesome! Ingredients coming right up...");
						script.add("wait 40");
						script.add("I'll need a carton of milk, a bar of chocolate and a bag of flour, por favor. Look around in the houses down here.");

						user.setQuestStage_Halloween(QuestStage.STEP_TWO);
						userService.save(user);
						return script;
					}

					case STEP_THREE -> {
						List<ItemBuilder> required = Arrays.asList(milk.clone(), chocolate.clone(), flour.clone());

						if (!Quests.hasAllItemsLikeFrom(user, required)) {
							script.add("I need a carton of milk, some chocolate and bag of flour. Look around in the houses down here!");
							return script;
						}

						Quests.removeItems(user, required);

						int wait;
						script.add("Gracias!");
						script.add("wait 40");
						script.add("Give me a moment and I will make those cookies.");
						script.add("wait 40");
						script.add("....");
						script.add("wait 60");
						script.add("...");
						script.add("wait 60");
						script.add("Aaaand, done. Here, please bring these to my son, &oJosé&f.");
						wait = (40 + 40 + 60 + 60);
						Tasks.wait(wait, () -> Quests.giveItem(user, cookies.clone().build()));

						script.add("wait 40");
						script.add("Take care, young one!");
						script.add("wait 40");
						script.add("<self> Thank you Ana, Im sure this will help raise everyone's spirits!");

						user.setQuestStage_Halloween(QuestStage.STEPS_DONE);
						userService.save(user);
						return script;
					}
				}

				script.add("TODO BF21 - Greeting");
				return script;
			}
		},
		;

		private final BearFair21NPC npc;
		private final List<String> script;

		@Override
		public List<String> getScript(BearFair21User user) {
			return this.script;
		}

		@Override
		public String getName() {
			return this.npc.getName();
		}

		@Override
		public int getNpcId() {
			return this.npc.getId();
		}

		HalloweenNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onInteract(PlayerInteractEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;

		Block block = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(block)) return;

		BearFair21User user = userService.get(event.getPlayer());
		if (user.getQuestStage_Halloween() != QuestStage.STEP_TWO) return;

		event.setCancelled(true);

		checkLocation(user, block.getLocation());
		checkLocation(user, block.getRelative(event.getBlockFace()).getLocation());
	}

	@EventHandler
	public void onInteractItemFrame(PlayerInteractEntityEvent event) {
		if (!EquipmentSlot.HAND.equals(event.getHand())) return;

		Player player = event.getPlayer();
		if (!isAtBearFair(player)) return;

		Entity clicked = event.getRightClicked();
		if (!(clicked instanceof ItemFrame itemFrame)) return;

		BearFair21User user = userService.get(event.getPlayer());
		if (user.getQuestStage_Halloween() != QuestStage.STEP_TWO) return;

		event.setCancelled(true);

		checkLocation(user, itemFrame.getLocation());
	}

	private static void checkLocation(BearFair21User user, Location location) {
		if (!user.isChocolate() && LocationUtils.isFuzzyEqual(location_chocolate, location)) {
			user.setChocolate(true);
			userService.save(user);
			Quests.giveItem(user, chocolate.clone().build());
		} else if (!user.isFlour() && LocationUtils.isFuzzyEqual(location_flour, location)) {
			user.setFlour(true);
			userService.save(user);
			Quests.giveItem(user, flour.clone().build());
		} else if (!user.isMilk() && LocationUtils.isFuzzyEqual(location_milk, location)) {
			user.setMilk(true);
			userService.save(user);
			Quests.giveItem(user, milk.clone().build());
		}

		if (user.isChocolate() && user.isFlour() && user.isMilk()) {
			user.setQuestStage_Halloween(QuestStage.STEP_THREE);
			userService.save(user);
		}
	}


}
