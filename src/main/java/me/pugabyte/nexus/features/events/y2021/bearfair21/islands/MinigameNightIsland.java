package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import eden.utils.TimeUtils.Time;
import fr.minuskube.inv.ClickableItem;
import fr.minuskube.inv.SmartInventory;
import fr.minuskube.inv.content.InventoryContents;
import fr.minuskube.inv.content.InventoryProvider;
import fr.minuskube.inv.content.SlotPos;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.models.QuestStage;
import me.pugabyte.nexus.features.events.models.Talker;
import me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21;
import me.pugabyte.nexus.features.events.y2021.bearfair21.Quests;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.MinigameNightIsland.MinigameNightNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.features.menus.MenuUtils;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.models.bearfair21.BearFair21UserService;
import me.pugabyte.nexus.utils.BlockUtils;
import me.pugabyte.nexus.utils.ItemBuilder;
import me.pugabyte.nexus.utils.LocationUtils;
import me.pugabyte.nexus.utils.PlayerUtils;
import me.pugabyte.nexus.utils.Tasks;
import me.pugabyte.nexus.utils.Utils.ActionGroup;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.List;

import static me.pugabyte.nexus.features.events.y2021.bearfair21.BearFair21.getWGUtils;
import static me.pugabyte.nexus.utils.ItemUtils.isNullOrAir;
import static me.pugabyte.nexus.utils.ItemUtils.isTypeAndNameEqual;

// TODO BF21: Quest + Dialog
@Region("minigamenight")
@NPCClass(MinigameNightNPCs.class)
public class MinigameNightIsland implements BearFair21Island {
	static BearFair21UserService userService = new BearFair21UserService();

	private static final ItemStack hat = new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).customModelData(101).amount(1).build();
	private static final Location phoneLoc = new Location(BearFair21.getWorld(), -191, 143, -194);

	public MinigameNightIsland() {
		Nexus.registerListener(this);
	}

	public enum MinigameNightNPCs implements BearFair21TalkingNPC {
		XAVIER(BearFair21NPC.XAVIER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("Sup... What, never seen a quadruple bass pedal?");
				return script;
			}
		},
		RYAN(BearFair21NPC.RYAN) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				script.add("Yo. Know any good synths?");

				return script;
			}
		},
		HEATHER(BearFair21NPC.HEATHER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				script.add("Hey! After learning 504 bass lines, I still can't decide what style I love the most… maybe all of them...");

				return script;
			}
		},
		AXEL(BearFair21NPC.AXEL) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				switch (user.getQuestStage_MGN()) {
					case NOT_STARTED -> {
						int wait = 0;
						script.add("Hey! Welcome to the Game Gallery! Proud sponsor of Bear Fair 2021! ...Hold up, <player>? Is that you?");
						script.add("wait 80");
						script.add("<self> Hey, Axel!");
						script.add("wait 40");
						script.add("Yooo how ya been dude? It'd be hard to forget the hero who saved last year's arcade tourney! Thanks again for that.");
						script.add("wait 80");
						script.add("<self> Always glad to help out where I can!");
						script.add("wait 40");
						script.add("Broo, its hard to find people as dope as you these days.");
						script.add("wait 40");
						wait += (80 + 40 + 80 + 40 + 40);
						script.add("<self> Aw, thanks! So how're things at GG?");
						script.add("wait 40");
						script.add("Pretty stressful, not gonna lie. Lots of good business, but its hard to keep up with it all, being self employed, especially during Bear Fair.");
						script.add("wait 100");
						script.add("Just barely found a few moments to come out here and help the bros get set up for our Bear Fair Band-sesh' tonight.");
						script.add("wait 100");
						script.add("<self> Anything I can do to help?");
						script.add("wait 40");
						wait += (40 + 100 + 100 + 40);
						script.add("Nah I couldn't keep you from the bear fair celebration...");
						script.add("wait 50");
						script.add("<self> No really, I wouldn't mind.");
						script.add("wait 40");
						script.add("Really? Well if you're sure, we all could actually use more practice... Would you mind running the store for me?");
						script.add("wait 80");
						script.add("Just till we close tonight; and I'll totally pay you. In fact, here...");
						script.add("wait 60");
						wait += (50 + 40 + 80 + 60);
						Tasks.wait(wait, () -> Quests.giveItem(user, hat));

						script.add("You're an official employee of GG! With your tech skills, it'll be a breeze.");
						script.add("wait 60");
						script.add("<self> I got you bro, practice all you need. I wanna hear an awesome song when I get back!");
						script.add("wait 60");
						script.add("Duude, you're a lifesaver!");
						wait += (60 + 60);

						user.setQuestStage_MGN(QuestStage.STARTED);
						userService.save(user);
						return script;
					}

					case STARTED -> {
						script.add("TODO - Reminder");
						return script;
					}

					case COMPLETE -> {
						script.add("TODO - Completed");
						return script;
					}
				}

				script.add("TODO - Hello");
				return script;
			}
		},
		// TODO: UPDATE DIALOG
		TRENT(BearFair21NPC.MGN_CUSTOMER_1) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (user.getQuestStage_MGN() == QuestStage.STEP_ONE) {
					script.add("Ayy yo dude. You the one I gotta talk to ‘bout fixin my xbox?");
					script.add("<self> Yep! What seems to be the problem?");
					script.add("So like, Its an xbox one, right, and I hit the power button and it like, flickers into a blue screen and shuts down.");
					script.add("<self> Yeah that’s not good… does the blue screen have an error message?");
					script.add("Yuh, I took a pic. Here, dawg, says 'Critical Error. [ses.status.psWarning:warning]: DS14-Mk2-AT shelf 1 on " +
						"channel 2a power warning for Power supply 2: critical status; DC overvoltage fault.'");
					script.add("<self> Mmm, okay, I can fix this. Let me take a look at it and I’ll be right back with you as soon as it's fixed. Shouldn’t be more than a few minutes. ");
					script.add("A'ight, thanks dawg. I’ll be right here.");

					PlayerUtils.giveItem(user.getOnlinePlayer(), brokenXBox);
					user.setQuestStage_MGN(QuestStage.STEP_TWO);
					userService.save(user);
				} else if (user.getQuestStage_MGN() == QuestStage.STEP_THREE) {
					script.add("<self> Alright, here you are. Power supply was shot. Had to replace it. Pretty simple fix so the bill won’t be too bad.");
					script.add("Yooo, sweet. Thank’s dawg! Here, you can keep the change. Peace.");
					script.add("<self> Thanks for choosing GG!");

					user.setQuestStage_MGN(QuestStage.STEP_FOUR);
					userService.save(user);
				}

				return script;
			}
		},
		FREDRICKSON(BearFair21NPC.MGN_CUSTOMER_2) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				if (user.getQuestStage_MGN() == QuestStage.STEP_FOUR) {
					script.add("<self> Thanks for calling the Game Gallery, how can I help?");
					script.add("Hello, this is Ben Fredrickson. I’m calling about a laptop I recently purchased for my son. " +
						"I travel a great deal and I intended it to be a birthday gift for him when I returned home. " +
						"Unfortunately, it appears to have been damaged by improper handling on my last flight, as it won’t boot up. " +
						"I’m doing business in the area and had my assistant drop off the laptop in your mailbox earlier today. " +
						"I was hoping you could find out what’s wrong with it and remedy the problem?");
					script.add("<self> Of course sir, I’ll take a look at it.");
					script.add("Wonderful, once it's fixed, if you could keep it in your back room, I’ll be back by in the next few days to pick it up.");
					script.add("<self> No problem sir, I’ll call as soon as it's ready.");
					user.setQuestStage_MGN(QuestStage.STEP_FIVE);
					userService.save(user);
				} else if (user.getQuestStage_MGN() == QuestStage.STEP_SIX) {
					script.add("This is Fredrickson.");
					script.add("<self> Ok Mr. Fredrickson, the laptop is ready. The motherboard and screen were cracked and had to be replaced but it works perfectly now.");
					script.add("Thank you so much! I knew I could count on an establishment of your caliber! Expect me back by the fifth.");
					script.add("<self> Glad to be of service! Thanks for choosing the Game Gallery!");
					user.setQuestStage_MGN(QuestStage.STEP_SEVEN);
					userService.save(user);
				}

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

		MinigameNightNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}

	/**
	 * on player enter GG Store region, and mgn step == started, set step to 1, and spawn Trent.
	 * <p>
	 * on player fix customer 1 problem, set step to 2.
	 */

	// XBox

	@Getter private static final ItemStack brokenXBox = new ItemBuilder(Nexus.getHeadAPI().getItemHead("43417")).name("&cTrent's Broken XBox One").build();
	@Getter private static final ItemStack fixedXBox = new ItemBuilder(Nexus.getHeadAPI().getItemHead("43417")).name("&aTrent's Fixed XBox One").build();
	@Getter private static final ItemStack brokenPowerSupply = new ItemBuilder(Material.NETHERITE_INGOT).name("&cTrent's Broken XBox One Power Supply").build();
	@Getter private static final ItemStack fixedPowerSupply = new ItemBuilder(Material.NETHERITE_INGOT).name("&aTrent's Fixed XBox One Power Supply").build();

	@EventHandler
	public void onRightClickXBox(PlayerInteractEvent event) {
		if (!BearFair21.canDoBearFairQuest(event.getPlayer())) return;
		if (!ActionGroup.CLICK.applies(event) || isNullOrAir(event.getItem())) return;
		if (!isTypeAndNameEqual(brokenXBox, event.getItem())) return;

		new XBoxMenu().open(event.getPlayer());
	}

	public static class XBoxMenu extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
				.provider(this)
				.title("XBox Parts")
				.size(3, 9)
				.build()
				.open(viewer, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);
			contents.set(1, 1, ClickableItem.empty(nameItem(Material.GREEN_CARPET, "&fMotherboard")));
			contents.set(1, 5, ClickableItem.empty(nameItem(Material.FIREWORK_STAR, "&fCPU")));
			contents.set(1, 7, ClickableItem.empty(nameItem(Material.DAYLIGHT_DETECTOR, "&fHard Drive")));

			fixableItem(player, contents, SlotPos.of(1, 3), "XBox", "Power Supply", brokenPowerSupply, fixedPowerSupply);
		}
	}

	private final String solderRegion = getRegion("solder");
	private static boolean activeSolder = false;

	@EventHandler
	public void onClickSolder(PlayerInteractEvent event) {
		if (!BearFair21.canDoBearFairQuest(event.getPlayer())) return;
		Block clicked = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(clicked)) return;
		ProtectedRegion region = getWGUtils().getProtectedRegion(solderRegion);
		if (!getWGUtils().isInRegion(clicked.getLocation(), region)) return;

		event.setCancelled(true);
		Player player = event.getPlayer();
		BearFair21User user = new BearFair21UserService().get(player);

		boolean fixingXBox = user.getQuestStage_MGN() == QuestStage.STEP_TWO && isTypeAndNameEqual(brokenPowerSupply, event.getItem());
		boolean fixingLaptop = user.getQuestStage_MGN() == QuestStage.STEP_FIVE && (isTypeAndNameEqual(brokenScreen, event.getItem()) || isTypeAndNameEqual(brokenMotherboard, event.getItem()));
		if (!(fixingLaptop || fixingXBox)) return;

		if (activeSolder) return;
		activeSolder = true;
		player.getInventory().removeItem(event.getItem());

		ArmorStand armorStand = null;
		for (Entity nearbyEntity : player.getNearbyEntities(7, 7, 7)) {
			if (nearbyEntity instanceof ArmorStand && getWGUtils().getRegionsAt(nearbyEntity.getLocation()).contains(region)) {
				armorStand = (ArmorStand) nearbyEntity;
				break;
			}
		}

		if (armorStand == null) return;
		solderItem(armorStand, player);
	}

	private void solderItem(ArmorStand armorStand, Player player) {
		ItemStack air = new ItemStack(Material.AIR);

		armorStand.setItem(EquipmentSlot.HAND, brokenPowerSupply);
		Location loc = new Location(BearFair21.getWorld(), -192, 137, -194);
		loc = LocationUtils.getCenteredLocation(loc);
		loc.setY(loc.getBlockY() + 0.5);
		Location finalLoc = loc;
		World world = loc.getWorld();

		Tasks.wait(5, () -> {
			world.playSound(finalLoc, Sound.BLOCK_ANVIL_USE, 0.3F, 0.1F);
			world.playSound(finalLoc, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.5F, 1F);
			Tasks.wait(20, () -> world.playSound(finalLoc, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.5F, 1F));
		});

		for (int i = 0; i < 10; i++) {
			Tasks.wait(i * 5, () -> world.spawnParticle(Particle.LAVA, finalLoc, 5, 0, 0, 0));
		}

		Tasks.wait(Time.SECOND.x(5), () -> {
			armorStand.setItem(EquipmentSlot.HAND, air);
			PlayerUtils.giveItem(player, fixedPowerSupply);
			Tasks.wait(10, () -> activeSolder = false);
			new BearFair21UserService().edit(player, user -> user.setQuestStage_MGN(QuestStage.STEP_THREE));
			world.playSound(finalLoc, Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
		});
	}

	// Laptop

	private final String phoneRegion = getRegion("phone");
	private final String mailboxRegion = getRegion("mailbox");
	private static final ItemStack brokenLaptop = new ItemBuilder(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE).customModelData(1).name("&cFredrickson's Broken Laptop").build();
	private static final ItemStack fixedLaptop = new ItemBuilder(Material.POLISHED_BLACKSTONE_PRESSURE_PLATE).customModelData(1).name("&aFredrickson's Fixed Laptop").build();
	private static final ItemStack brokenScreen = new ItemBuilder(Material.GLASS_PANE).name("&cFredrickson's Broken Laptop Screen").build();
	private static final ItemStack fixedScreen = new ItemBuilder(Material.GLASS_PANE).name("&aFredrickson's Fixed Laptop Screen").build();
	private static final ItemStack brokenMotherboard = new ItemBuilder(Material.GREEN_CARPET).name("&cFredrickson's Broken Laptop Motherboard").build();
	private static final ItemStack fixedMotherboard = new ItemBuilder(Material.GREEN_CARPET).name("&aFredrickson's Fixed Laptop Motherboard").build();

	@EventHandler
	public void onClickPhone(PlayerInteractEntityEvent event) {
		if (BearFair21.isNotAtBearFair(event)) return;
		if (EquipmentSlot.HAND != event.getHand()) return;
		Entity entity = event.getRightClicked();
		if (entity.getType() != EntityType.ITEM_FRAME) return;
		if (!getWGUtils().isInRegion(entity.getLocation(), phoneRegion)) return;

		final BearFair21User user = new BearFair21UserService().get(event.getPlayer());
		if (user.getQuestStage_MGN() == QuestStage.STEP_FOUR) {
			// TODO Stop phone ringing
			Talker.sendScript(event.getPlayer(), MinigameNightNPCs.FREDRICKSON);
		} else if (user.getQuestStage_MGN() == QuestStage.STEP_SIX) {
			// TODO Ring phone
			Talker.sendScript(event.getPlayer(), MinigameNightNPCs.FREDRICKSON);
		}
	}

	@EventHandler
	public void onClickMailbox(PlayerInteractEvent event) {
		if (BearFair21.isNotAtBearFair(event)) return;
		if (EquipmentSlot.HAND != event.getHand()) return;
		Block clicked = event.getClickedBlock();
		if (BlockUtils.isNullOrAir(clicked) || clicked.getType() != Material.BARRIER) return;
		if (!getWGUtils().isInRegion(clicked.getLocation(), mailboxRegion)) return;

		if (!event.getPlayer().getInventory().containsAtLeast(brokenLaptop, 1))
			Quests.giveItem(event.getPlayer(), brokenLaptop);
	}

	@EventHandler
	public void onRightClickLaptop(PlayerInteractEvent event) {
		if (!BearFair21.canDoBearFairQuest(event.getPlayer())) return;
		if (!ActionGroup.CLICK.applies(event) || isNullOrAir(event.getItem())) return;
		if (!isTypeAndNameEqual(brokenLaptop, event.getItem())) return;

		new LaptopMenu().open(event.getPlayer());
	}

	public static class LaptopMenu extends MenuUtils implements InventoryProvider {

		@Override
		public void open(Player viewer, int page) {
			SmartInventory.builder()
				.provider(this)
				.title("Laptop Parts")
				.size(3, 9)
				.build()
				.open(viewer, page);
		}

		@Override
		public void init(Player player, InventoryContents contents) {
			addCloseItem(contents);
			contents.set(0, 3, ClickableItem.empty(nameItem(Material.NETHERITE_INGOT, "Power Supply")));
			contents.set(1, 7, ClickableItem.empty(nameItem(Material.FIREWORK_STAR, "CPU")));
			contents.set(2, 3, ClickableItem.empty(nameItem(Material.LIGHT_GRAY_CARPET, "Keyboard")));
			contents.set(2, 5, ClickableItem.empty(nameItem(Material.IRON_TRAPDOOR, "Optical Drive")));

			fixableItem(player, contents, SlotPos.of(0, 5), "Laptop", "Screen", brokenScreen, fixedScreen);
			fixableItem(player, contents, SlotPos.of(1, 1), "Laptop", "Motherboard", brokenMotherboard, fixedMotherboard);
		}
	}

	protected static void fixableItem(Player player, InventoryContents contents, SlotPos slot, String fixing, String name, ItemStack broken, ItemStack fixed) {
		final BearFair21UserService userService = new BearFair21UserService();
		final BearFair21User user = userService.get(player);
		final PlayerInventory inv = player.getInventory();

		if (inv.containsAtLeast(broken, 1) || inv.containsAtLeast(fixed, 1)) {
			contents.set(slot, ClickableItem.from(new ItemBuilder(Material.BARRIER).name("&f" + name).build(), e -> {
				if (fixed.equals(player.getItemOnCursor())) {
					player.setItemOnCursor(new ItemStack(Material.AIR));
					contents.set(slot, ClickableItem.empty(fixed));

					Runnable finalize = () -> Tasks.wait(Time.SECOND, () -> {
						player.closeInventory();
						inv.removeItem(broken);
						Quests.giveItem(player, fixed);
					});

					if ("Laptop".equals(fixing)) {
						if ("Screen".equals(name))
							user.setMgn_laptopScreen(true);
						else if ("Motherboard".equals(name))
							user.setMgn_laptopMotherboard(true);

						userService.save(user);

						if (user.isMgn_laptopScreen() && user.isMgn_laptopMotherboard())
							finalize.run();
					} else
						finalize.run();
				}
			}));
		} else {
			contents.set(slot, ClickableItem.empty(broken));
			contents.setEditable(slot, true);
		}
	}

}
