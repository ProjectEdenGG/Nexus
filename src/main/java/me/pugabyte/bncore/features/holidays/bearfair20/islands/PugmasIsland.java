package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.PugmasIsland.PugmasNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.JsonBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.chime;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;
import static me.pugabyte.bncore.utils.StringUtils.colorize;
import static me.pugabyte.bncore.utils.StringUtils.stripColor;

@Region("pugmas")
@NPCClass(PugmasNPCs.class)
public class PugmasIsland implements Listener, Island {

	private Location present_grinch_1 = new Location(BearFair20.getWorld(), -1057, 126, -1905);
	private Location present_grinch_2 = new Location(BearFair20.getWorld(), -1058, 126, -1898);
	private Location present_grinch_3 = new Location(BearFair20.getWorld(), -1060, 125, -1899);
	private Location present_grinch_4 = new Location(BearFair20.getWorld(), -1055, 129, -1900);
	private Location present_grinch_5 = new Location(BearFair20.getWorld(), -1057, 129, -1906);
	private Location present_grinch_6 = new Location(BearFair20.getWorld(), -1080, 126, -1891);
	//
	private Location present_house_1 = new Location(BearFair20.getWorld(), -1045, 158, -1922);
	private Location present_house_2 = new Location(BearFair20.getWorld(), -1071, 141, -1912);
	private Location present_house_3 = new Location(BearFair20.getWorld(), -1071, 161, -1936);
	private Location present_house_4 = new Location(BearFair20.getWorld(), -1094, 140, -1898);
	private Location present_house_5 = new Location(BearFair20.getWorld(), -1104, 145, -1869);
	private Location present_house_6 = new Location(BearFair20.getWorld(), -1092, 144, -1842);
	//
	private List<Location> presents_grinch = new ArrayList<>(Arrays.asList(present_grinch_1, present_grinch_2,
			present_grinch_3, present_grinch_4, present_grinch_5, present_grinch_6));
	private List<Location> presents_house = new ArrayList<>(Arrays.asList(present_house_1, present_house_2,
			present_house_3, present_house_4, present_house_5, present_house_6));
	private String presents_treeRg = getRegion() + "_tree";
	private Location treeLoc = new Location(BearFair20.getWorld(), -1053, 140, -1869);
	//
	private List<Location> presents = new ArrayList<>();
	private static String acceptQuest = "    &f[&aClick to accept quest&f]";
	private static Location presentLoc = new Location(BearFair20.getWorld(), -1064, 127, -1847);

	public PugmasIsland() {
		BNCore.registerListener(this);
		presents.addAll(presents_grinch);
		presents.addAll(presents_house);
		effectTasks();

		// TODO:
		// 	- Dialog wait times
	}

	public enum PugmasNPCs implements TalkingNPC {
		MAYOR(2959) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_Pugmas_Step();
				int presents = user.getPresentLocs().size();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Hey there friendo! Would you mind doing me a little favor?");
				startQuest.add("wait 80");
				startQuest.add("I need someone to sneak into the Grinch's cave, and take back 6 presents that he stole.");
				startQuest.add("wait 80");
				startQuest.add("I'd do it myself, but he scares me.");
				startQuest.add("wait 80");
				startQuest.add("So, what do you say?");

				List<String> thanks = new ArrayList<>();
				thanks.add("Oh my goodness, Thank you so much for helping me out!");
				thanks.add("wait 80");
				thanks.add("You've saved our Christmas!");

				if (!user.isQuest_Main_Start())
					return Collections.singletonList("Happy holidays!");

				if (user.isQuest_Pugmas_Finish()) {
					if (step == 22)
						return Collections.singletonList("You're as bad as they come, almost the Grinch himself!");
					else
						return thanks;
				}

				if (step == 21) {
					if (user.getPresentLocs().size() > 0 || user.isQuest_Pugmas_Switched())
						return Collections.singletonList("Have you come back to steal more of my presents? Well? What are you waiting for?");

					Tasks.wait(340, () -> {
						JsonBuilder json = new JsonBuilder(acceptQuest).command("bearfair quests switch_mayor").hover("Switch to Mayor's quest");
						json.send(player);
					});

					List<String> switchQuest = new ArrayList<>(startQuest);
					switchQuest.add("wait 80");
					switchQuest.add("But let it be known, you can only switch quests once.");
					return switchQuest;
				}

				if (step == 11) {
					if (presents == 6) {
						nextStep(player); // 12
						ArrayList<String> completeQuest = new ArrayList<>(thanks);
						completeQuest.add("wait 80");
						completeQuest.add("Here, take this as a token of my gratitude.");
						return completeQuest(player, completeQuest);
					} else
						return Arrays.asList(
								"Hey there friendo! Oh you need a reminder? No problem...",
								"wait 80",
								"I just need you to sneak into the Grinch's cave and take back those 6 presents he stole."
						);
				}

				//
				Tasks.wait(260, () -> {
					JsonBuilder json = new JsonBuilder(acceptQuest).command("bearfair quests accept_mayor").hover("Accept Mayor's quest");
					json.send(player);
				});
				return startQuest;
			}
		},
		GRINCH(2958) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_Pugmas_Step();
				int presents = user.getPresentLocs().size();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("How dare you enter THE GRINCH'S LAIR?! *Max growls at you*");
				startQuest.add("wait 80");
				startQuest.add("Actually! Help me in meeting my quota of spreading sadness? I'd do it myself but I'm busy:");
				startQuest.add("wait 80");
				startQuest.add("At 4:00, I'll wallow in self-pity.");
				startQuest.add("wait 40");
				startQuest.add("4:30, stare into the abyss.");
				startQuest.add("wait 40");
				startQuest.add("5:00, solve world hunger, tell no one. ");
				startQuest.add("wait 40");
				startQuest.add("5:30, jazzercize; ");
				startQuest.add("wait 40");
				startQuest.add("6:30, dinner with me. I can’t cancel that again.");
				startQuest.add("wait 40");
				startQuest.add("See, I’m booked. So, I'll need you to sneak into each cabin in the town and steal one present, and grab a random one from under the tree, and give them to me.");
				startQuest.add("wait 120");
				startQuest.add("So, what do you say?");

				List<String> greeting = new ArrayList<>();
				greeting.add("How dare you enter THE GRINCH'S LAIR?! &lTHE IMPUDENCE! THE AUDACITY! THE UNMITIGATED GALL!");
				greeting.add("wait 80");
				greeting.add("You've called down the thunder. Now, get ready... FOR THE &lBOOOOOOM!&f Gaze into the face... of fear. ");
				greeting.add("wait 80");
				greeting.add("&lBOOGA-BOOGA!");

				List<String> thanks = new ArrayList<>();
				thanks.add("Well done, <player>! Serves them right, those yuletide-loving... sickly-sweet, nog-sucking cheer mongers! ");
				thanks.add("wait 80");
				thanks.add("*Picks up an onion* I really don't like 'em. Mm-mm. No, I don't. *Eats the onion*");

				if (!user.isQuest_Main_Start())
					return greeting;

				if (user.isQuest_Pugmas_Finish()) {
					if (step == 22)
						return thanks;
					else
						return greeting;
				}

				if (step == 21) {
					if (presents == 7) {
						nextStep(player); // 22
						ArrayList<String> completeQuest = new ArrayList<>(thanks);
						completeQuest.add("wait 80");
						completeQuest.add("Why don't you take one of these as a reward, I certainly won't be needing it.");
						return completeQuest(player, completeQuest);
					} else
						return Arrays.asList(
								"Have you already forgotten my SIMPLE REQUEST? Fine, I'll explain it AGAIN.",
								"wait 80",
								"Sneak into each cabin in the town and steal one present, and grab a random one from under the tree, and give them to me.");
				}

				if (step == 11) {
					if (user.getPresentLocs().size() > 0 || user.isQuest_Pugmas_Switched())
						return Collections.singletonList("If you utter so much as one syllable about this, I’LL HUNT YOU DOWN AND GUT YOU LIKE A FISH! If you’d like to fax me, press the star key.");

					Tasks.wait(540, () -> {
						JsonBuilder json = new JsonBuilder(acceptQuest).command("bearfair quests switch_grinch").hover("Switch to Grinch's quest");
						json.send(player);
					});

					List<String> switchQuest = new ArrayList<>(startQuest);
					switchQuest.add("wait 20");
					switchQuest.add("But let it be known, you can only switch quests once.");
					return switchQuest;
				}

				//
				Tasks.wait(500, () -> {
					JsonBuilder json = new JsonBuilder(acceptQuest).command("bearfair quests accept_grinch").hover("Accept Grinch's quest");
					json.send(player);
				});
				return startQuest;
			}
		};

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		PugmasNPCs(int npcId) {
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}

		PugmasNPCs(int npcId, List<String> script) {
			this.npcId = npcId;
			this.script = script;
		}
	}

	private static void nextStep(Player player) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		int step = user.getQuest_Pugmas_Step() + 1;
		user.setQuest_Pugmas_Step(step);
		service.save(user);
	}

	private void effectTasks() {
		Tasks.repeat(0, Time.SECOND.x(3), () -> Bukkit.getOnlinePlayers().stream()
				.filter(player -> BearFair20.getWGUtils().getRegionsLikeAt(player.getLocation(), getRegion()).size() > 0)
				.forEach(player -> {
							BearFairService service = new BearFairService();
							BearFairUser user = service.get(player);
							int step = user.getQuest_Pugmas_Step();
							boolean startedQuest = user.isQuest_Pugmas_Start();
							boolean mayorQuest = true;

							if (startedQuest) {
								if (step == 21)
									mayorQuest = false;

								// Only shows particle effect for active quest
								if (mayorQuest) {
									presents_grinch.forEach(present -> {
										if (!isFoundPresent(present, player)) {
											Location loc = Utils.getCenteredLocation(present);
											loc.setY(loc.getBlockY() + 0.25);
											player.spawnParticle(Particle.VILLAGER_HAPPY, loc, 10, 0.25, 0.25, 0.25, 0.01);
										}
									});
								} else {
									if (!hasFoundTreePresent(player))
										player.spawnParticle(Particle.VILLAGER_HAPPY, treeLoc, 50, 2, 1, 2, 0.01);

									presents_house.forEach(present -> {
										if (!isFoundPresent(present, player)) {
											Location loc = Utils.getCenteredLocation(present);
											loc.setY(loc.getBlockY() + 0.25);
											player.spawnParticle(Particle.VILLAGER_HAPPY, loc, 10, 0.25, 0.25, 0.25, 0.01);
										}
									});
								}
							}
						}
				));
	}

	@EventHandler
	void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block clicked = event.getClickedBlock();
		if (Utils.isNullOrAir(clicked)) return;

		ProtectedRegion region = WGUtils.getProtectedRegion(getRegion());
		if (!WGUtils.getRegionsAt(clicked.getLocation()).contains(region)) return;

		if (!clicked.getType().equals(Material.PLAYER_HEAD)) return;

		Player player = event.getPlayer();
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);

		if (!isPresentHead(clicked, player)) return;

		// Prevents picking up heads of inactive quest
		int step = user.getQuest_Pugmas_Step();
		boolean isActiveQuest = false;

		if (step == 21 && isGrinchQuestPresent(clicked)) {
			isActiveQuest = true;
		} else if (step == 11 && !isGrinchQuestPresent(clicked)) {
			isActiveQuest = true;
		}
		if (!isActiveQuest) return;
		//

		user.getPresentLocs().add(clicked.getLocation());
		service.save(user);

		List<ItemStack> drops = new ArrayList<>(clicked.getDrops());
		ItemStack present = new ItemBuilder(drops.get(0)).clone().lore(itemLore).name("Present").build();

		Utils.giveItem(player, present);
		chime(player);
		event.setCancelled(true);
	}

	//

	private boolean isPresentHead(Block block, Player player) {
		// If already found, return false
		if (isFoundPresent(block.getLocation(), player))
			return false;

		if (isTreePresent(block.getLocation()) && hasFoundTreePresent(player))
			return false;
		//

		Location loc = block.getLocation();
		if (presents_grinch.contains(loc))
			return true;

		return isGrinchQuestPresent(block);
	}

	private boolean isGrinchQuestPresent(Block block) {
		Location loc = block.getLocation();
		if (presents_house.contains(loc))
			return true;

		return isTreePresent(block.getLocation());
	}

	private boolean isTreePresent(Location location) {
		ProtectedRegion treeRg = WGUtils.getProtectedRegion(presents_treeRg);
		return WGUtils.getRegionsAt(location).contains(treeRg);
	}

	private boolean isFoundPresent(Location location, Player player) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		return user.getPresentLocs().contains(location);
	}

	private boolean hasFoundTreePresent(Player player) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		for (Location presentLoc : user.getPresentLocs()) {
			if (isTreePresent(presentLoc))
				return true;
		}
		return false;
	}

	//

	public static void switchQuest(Player player, boolean mayor) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		user.setQuest_Pugmas_Switched(true);
		if (mayor)
			user.setQuest_Pugmas_Step(11);
		else
			user.setQuest_Pugmas_Step(21);
		service.save(user);

		player.sendMessage(colorize("&b&lYOU &8> &fSure!"));
		chime(player);
	}

	public static void acceptQuest(Player player, boolean mayor) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		user.setQuest_Pugmas_Start(true);
		if (mayor)
			user.setQuest_Pugmas_Step(11);
		else
			user.setQuest_Pugmas_Step(21);
		service.save(user);

		player.sendMessage(colorize("&b&lYOU &8> &fSure!"));
		chime(player);
	}

	//

	private static List<String> completeQuest(Player player, List<String> thanks) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		user.setQuest_Pugmas_Finish(true);
		service.save(user);

		for (ItemStack content : player.getInventory().getContents()) {
			if (BearFair20.isBFItem(content) && stripColor(content.getItemMeta().getDisplayName()).contains("Present"))
				player.getInventory().remove(content);
		}

		List<ItemStack> drops = new ArrayList<>(presentLoc.getBlock().getDrops());
		ItemStack present = new ItemBuilder(drops.get(0)).clone().lore(itemLore).name("Present").build();
		Tasks.wait(Time.SECOND.x(9), () -> {
			chime(player);
			Utils.giveItem(player, present);
		});

		return thanks;
	}
}
