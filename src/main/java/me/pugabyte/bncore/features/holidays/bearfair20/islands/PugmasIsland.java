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
import org.bukkit.Sound;
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
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

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
	//
	private List<Location> presents = new ArrayList<>();
	private static int waitTime = 5;
	private static String acceptQuest = "    &f[&aClick to accept quest&f]";

	public PugmasIsland() {
		BNCore.registerListener(this);
		//
		presents.addAll(presents_grinch);
		presents.addAll(presents_house);
		//
		effectTasks();

		// TODO:
		//  - Make sure each present head can only be picked up once, including the random one under the tree
		//	- add lore to the picked up presents depending on the quest you're on,
		//		if you switch quests, remove all presents from player's inventory and data from the player of the other quest
		//	- only show effects for unfound presents
		// 	- Dialogs & wait times
	}

	public enum PugmasNPCs implements TalkingNPC {
		MAYOR(2959) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_Pugmas_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("TODO: Accept Mayor Quest?");

				if (!user.isQuest_Main_Start())
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (step >= 21) {
					Tasks.wait(waitTime, () -> {
						JsonBuilder json = new JsonBuilder(acceptQuest).command("bearfair quests pugmas switch_mayor").hover("Switch to Mayor's quest");
						json.send(player);
					});
					return Collections.singletonList("TODO: Switch to Mayor Quest?");
				}

				if (step >= 11)
					return Collections.singletonList("TODO: REMINDER");

				//
				Tasks.wait(waitTime, () -> {
					JsonBuilder json = new JsonBuilder(acceptQuest).command("bearfair quests pugmas accept_mayor").hover("Accept Mayor's quest");
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

				List<String> startQuest = new ArrayList<>();
				startQuest.add("TODO: Accept Grinch Quest?");

				if (!user.isQuest_Main_Start())
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (step >= 21)
					return Collections.singletonList("TODO: REMINDER ");

				if (step >= 11) {
					Tasks.wait(waitTime, () -> {
						JsonBuilder json = new JsonBuilder(acceptQuest).command("bearfair quests pugmas switch_grinch").hover("Switch to Grinch's quest");
						json.send(player);
					});
					return Collections.singletonList("TODO: Switch to Grinch Quest?");
				}

				//
				Tasks.wait(waitTime, () -> {
					JsonBuilder json = new JsonBuilder(acceptQuest).command("bearfair quests pugmas accept_grinch").hover("Accept Grinch's quest");
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
								if (step >= 21)
									mayorQuest = false;

								// Only shows particle effect for active quest
								if (mayorQuest) {
									presents_grinch.forEach(present -> {
										Location loc = Utils.getCenteredLocation(present);
										loc.setY(loc.getBlockY() + 0.25);
										player.spawnParticle(Particle.VILLAGER_HAPPY, loc, 10, 0.25, 0.25, 0.25, 0.01);
									});
								} else {
									presents_house.forEach(present -> {
										Location loc = Utils.getCenteredLocation(present);
										loc.setY(loc.getBlockY() + 0.25);
										player.spawnParticle(Particle.VILLAGER_HAPPY, loc, 10, 0.25, 0.25, 0.25, 0.01);
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
		if (!isPresentHead(clicked)) return;

		Player player = event.getPlayer();
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);

		// Prevents picking up heads of inactive quest
		int step = user.getQuest_Pugmas_Step();
		boolean isActiveQuest = false;

		if (step >= 21 && isGrinchQuestPresent(clicked)) {
			isActiveQuest = true;
		} else if (step >= 11 && step < 19 && !isGrinchQuestPresent(clicked)) {
			isActiveQuest = true;
		}
		if (!isActiveQuest) return;
		//

		List<ItemStack> drops = new ArrayList<>(clicked.getDrops());
		ItemStack present = new ItemBuilder(drops.get(0)).clone().lore(itemLore).name("Present").build();

		Utils.giveItem(player, present);
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1, 2);
		event.setCancelled(true);
	}

	private boolean isPresentHead(Block block) {
		Location loc = block.getLocation();
		if (presents_grinch.contains(loc))
			return true;

		return isGrinchQuestPresent(block);
	}

	private boolean isGrinchQuestPresent(Block block) {
		Location loc = block.getLocation();
		if (presents_house.contains(loc))
			return true;

		ProtectedRegion region = WGUtils.getProtectedRegion(presents_treeRg);
		if (WGUtils.getRegionsAt(loc).contains(region))
			return true;

		return false;
	}

	public static void switchQuest(Player player, Boolean mayor) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		user.setQuest_Pugmas_Start(true);
		if (mayor) {
			user.setQuest_Pugmas_Step(11);
			player.sendMessage("Accepted Mayor's quest");
		} else {
			user.setQuest_Pugmas_Step(21);
			player.sendMessage("Accepted Grinch's quest");
		}
		service.save(user);
	}
}
