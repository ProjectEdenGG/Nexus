package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.MinigameNightIsland.MinigameNightNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.arcademachine.ArcadeMachineMenu;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

@Region("minigamenight")
@NPCClass(MinigameNightNPCs.class)
public class MinigameNightIsland implements Listener, Island {
	private static Location arcadeSoundLoc = new Location(BearFair20.getWorld(), -1170, 141, -1716);
	private static Location arcadeSmokeLoc1 = Utils.getCenteredLocation(new Location(BearFair20.getWorld(), -1170, 140, -1715));
	private static Location arcadeSmokeLoc2 = Utils.getCenteredLocation(new Location(BearFair20.getWorld(), -1169, 148, -1715));
	private String arcadeRg = getRegion() + "_arcade";
	private String basementRg = getRegion() + "_basement";
	private String solderRg = getRegion() + "_solder";
	private static Location basementExit = new Location(BearFair20.getWorld(), -1183, 142, -1755, 0, 0);
	private static boolean activeSolder = false;
	// Quest Items
	private static ItemBuilder cpu = new ItemBuilder(Material.IRON_TRAPDOOR).lore(itemLore).amount(1).name("CPU");
	private static ItemBuilder processor = new ItemBuilder(Material.DAYLIGHT_DETECTOR).lore(itemLore).amount(1).name("Processor");
	private static ItemBuilder memoryCard = new ItemBuilder(Material.IRON_INGOT).lore(itemLore).amount(1).name("Memory Card");
	private static ItemBuilder motherboard = new ItemBuilder(Material.GREEN_CARPET).lore(itemLore).amount(1).name("Motherboard");
	private static ItemBuilder powerSupply = new ItemBuilder(Material.BLAST_FURNACE).lore(itemLore).amount(1).name("Power Supply");
	private static ItemBuilder speaker = new ItemBuilder(Material.NOTE_BLOCK).lore(itemLore).amount(1).name("Speaker");
	private static ItemBuilder hardDrive = new ItemBuilder(Material.HOPPER_MINECART).lore(itemLore).amount(1).name("Hard Drive");
	private static ItemBuilder diode = new ItemBuilder(Material.REPEATER).lore(itemLore).amount(1).name("Diode");
	public static ItemBuilder joystick = new ItemBuilder(Material.LEVER).lore(itemLore).amount(1).name("Joystick");
	private static List<ItemBuilder> arcadePieces = Arrays.asList(cpu, processor, memoryCard, motherboard, powerSupply, speaker, hardDrive, diode, joystick);
	//
	private ItemStack fakeMotherBoard = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).lore(itemLore).amount(1).name("Motherboard").build();
	private ItemStack solderingIron = new ItemBuilder(Material.END_ROD).lore(itemLore).amount(1).name("Soldering Iron").build();
	//
	public static ItemStack arcadeToken = new ItemBuilder(Material.SUNFLOWER).lore(itemLore).amount(1).name("Arcade Token").build();


	public MinigameNightIsland() {
		BNCore.registerListener(this);
		soundTasks();
		// TODO: dialog & wait times
	}

	public enum MinigameNightNPCs implements TalkingNPC {
		AXEL(2755) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_MGN_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Hey bro, welcome to the Game Gallery! Yo, you happen to see a pouty kid wearing a red hoodie anywhere over on the main island? No? Bummer man... ");
				startQuest.add("wait 80");
				startQuest.add("I was hosting an arcade tournament last night on that big arcade cabinet outside and it was his turn when he got all crazy and rage-quitted the whole party after getting a game over.");
				startQuest.add("wait 80");
				startQuest.add("Dude trashed the machine and stole a bunch of its parts. Anyway, I was just wondering if, like, he had bailed or was gonna come clean or whatever...");
				startQuest.add("wait 80");
				startQuest.add("But bro, you look pretty bored. Check out our library of classic and epic games! Or hey, if you're feelin' up for a *real* quest, maybe you could do me a solid and help fix the busted arcade machine?");
				startQuest.add("wait 80");
				startQuest.add("I've got this super rare arcade token I'd be willing to part with if you'd help.");
				startQuest.add("wait 80");
				startQuest.add("<self> Sure!");
				startQuest.add("wait 80");
				startQuest.add("Yo, dude, that's like, totally awesome of you! Now I don't have any new parts for the arcade cabinet but I do have this cool machine in the basement of the Game Gallery that should be able to fix up the busted parts.");
				startQuest.add("wait 80");
				startQuest.add("We just gotta find em first. Before we go hunting though, you'll need a soldering iron. I keep a spare one in my attic. Don't worry bro, this island is chill, the doors unlocked. Just uh, don't touch my gaming rig. I'll be here if you need any help.");

				Map<ItemStack, String> hintMap = new HashMap<>();
				String hint_cpu = "CPU Hint"; // Next to arcade machine
				String hint_processor = "TODO Processor Hint"; // Roof of game gallery
				String hint_memoryCard = "TODO MemoryCard Hint"; // At the roller coaster station
				String hint_motherboard = "TODO MotherBoard Hint"; // Quarry cave/pit
				String hint_powerSupply = "TODO PowerSupply Hint"; // Bottom of lake
				String hint_speaker = "TODO Speaker Hint"; // inside of Frogger tent
				String hint_hardDrive = "TODO HardDrive Hint"; // inside of Observatory
				String hint_diode = "TODO Diode Hint"; // Behind Reflection game
				String hint_joystick = "TODO Joystick Hint"; // Trade with the Collector NPC
				hintMap.put(cpu.build(), hint_cpu);
				hintMap.put(processor.build(), hint_processor);
				hintMap.put(memoryCard.build(), hint_memoryCard);
				hintMap.put(motherboard.build(), hint_motherboard);
				hintMap.put(powerSupply.build(), hint_powerSupply);
				hintMap.put(speaker.build(), hint_speaker);
				hintMap.put(hardDrive.build(), hint_hardDrive);
				hintMap.put(diode.build(), hint_diode);
				hintMap.put(joystick.build(), hint_joystick);

				if (!user.isQuest_Main_Start())
					return Collections.singletonList("Sup dude!");

				if (step >= 2 && user.isQuest_MGN_Finish()) {
					if (!user.isQuest_MGN_Finish()) {
						user.setQuest_MGN_Finish(true);
						service.save(user);
						Utils.giveItem(player, arcadeToken);
						player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
					}
					return Collections.singletonList("Yo, thanks again for fixing the Arcade Cabinet! Next tourney is gonna be awesome!");
				}

				if (step >= 1) {
					List<ItemStack> allPieces = new ArrayList<>();
					arcadePieces.forEach(piece -> allPieces.add(piece.build()));
					List<ItemStack> foundPieces = user.getArcadePieces();
					List<ItemStack> missingPieces = new ArrayList<>();
					allPieces.forEach(piece -> {
						if (!foundPieces.contains(piece))
							missingPieces.add(piece);
					});
					if (missingPieces.size() > 0) {
						String hint = hintMap.get(Utils.getRandomElement(missingPieces));
						return Collections.singletonList(hint);
					} else {
						return Collections.singletonList("TODO: you've found all parts, make sure to repair them and fix the arcade machine");
					}
				}

				user.setQuest_MGN_Start(true);
				nextStep(player); // 1
				return startQuest;
			}
		};

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		MinigameNightNPCs(int npcId) {
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}

		MinigameNightNPCs(int npcId, List<String> script) {
			this.npcId = npcId;
			this.script = script;
		}
	}

	public static void nextStep(Player player) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		int step = user.getQuest_MGN_Step() + 1;
		user.setQuest_MGN_Step(step);
		service.save(user);

	}

	@EventHandler
	public void onClickArcadeMachine(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block clicked = event.getClickedBlock();
		if (Utils.isNullOrAir(clicked)) return;

		ProtectedRegion region = WGUtils.getProtectedRegion(arcadeRg);
		if (!WGUtils.getRegionsAt(clicked.getLocation()).contains(region)) return;

		event.setCancelled(true);
		Player player = event.getPlayer();
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);

		if (!user.isQuest_MGN_Start()) return;

		new ArcadeMachineMenu().open(player, null);
	}

	@EventHandler
	public void onClickArcadePiece(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();

		ProtectedRegion region = WGUtils.getProtectedRegion(BearFair20.getRegion());
		if (!WGUtils.getRegionsAt(player.getLocation()).contains(region)) return;

		Entity clicked = event.getRightClicked();
		if (!(clicked instanceof ItemFrame)) return;

		ItemFrame itemFrame = (ItemFrame) clicked;
		ItemStack item = itemFrame.getItem();

		ItemStack piece = null;
		if (item.equals(fakeMotherBoard))
			piece = motherboard.clone().name("Broken Motherboard").build();
		else if (item.equals(solderingIron)) {
			piece = solderingIron.clone();
		} else {
			for (ItemBuilder arcadePiece : arcadePieces) {
				ItemStack arcadePieceItem = arcadePiece.build();
				if (arcadePieceItem.equals(item)) {
					String pieceName = arcadePieceItem.getItemMeta().getDisplayName();
					piece = arcadePiece.clone().name("Broken " + pieceName).build();
				}
			}
		}

		if (piece == null) return;
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		ItemStack arcadePiece = getArcadePiece(piece);
		if (user.getArcadePieces().contains(arcadePiece)) return;

		user.getArcadePieces().add(arcadePiece);
		service.save(user);
		Utils.giveItem(player, piece);
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);

	}

	@EventHandler
	public void onRegionEnter(RegionEnteredEvent event) {
		String regionId = event.getRegion().getId();
		if (!regionId.equalsIgnoreCase(basementRg)) return;

		Player player = event.getPlayer();
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);

		if (user.isQuest_MGN_Start() && player.getInventory().contains(solderingIron)) return;

		BearFair20.send("TODO: Need soldering iron to enter", player);
		player.teleport(basementExit);
	}

	@EventHandler
	public void onClickSolder(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block clicked = event.getClickedBlock();
		if (Utils.isNullOrAir(clicked)) return;

		ProtectedRegion region = WGUtils.getProtectedRegion(solderRg);
		if (!WGUtils.getRegionsAt(clicked.getLocation()).contains(region)) return;

		event.setCancelled(true);
		Player player = event.getPlayer();
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);

		if (!user.isQuest_MGN_Start()) return;

		ItemStack tool = Utils.getTool(player);
		if (tool == null || tool.getItemMeta() == null || !BearFair20.isBFItem(tool)) return;
		String toolName = tool.getItemMeta().getDisplayName();
		if (!toolName.contains("Broken")) return;

		// Fixing broken quest item
		if (activeSolder) return;
		activeSolder = true;
		player.getInventory().remove(tool);

		ArmorStand armorStand = null;
		for (Entity nearbyEntity : player.getNearbyEntities(7, 7, 7)) {
			if (nearbyEntity instanceof ArmorStand && WGUtils.getRegionsAt(nearbyEntity.getLocation()).contains(region)) {
				armorStand = (ArmorStand) nearbyEntity;
				break;
			}
		}

		if (armorStand == null) return;
		solderItem(tool, armorStand, player);
	}

	private ItemStack getArcadePiece(ItemStack brokenPiece) {
		for (ItemBuilder arcadePiece : arcadePieces) {
			ItemStack piece = arcadePiece.clone().build();
			if (brokenPiece.getType().equals(piece.getType()))
				return piece;
		}
		return new ItemStack(Material.AIR);
	}

	private void solderItem(ItemStack piece, ArmorStand armorStand, Player player) {
		ItemStack air = new ItemStack(Material.AIR);

		armorStand.getEquipment().setItemInMainHand(piece);
		Location loc = new Location(BearFair20.getWorld(), -1182, 137, -1756);
		loc = Utils.getCenteredLocation(loc);
		loc.setY(loc.getBlockY() + 0.5);
		Location finalLoc = loc;
		World world = loc.getWorld();

		//
		Tasks.wait(Time.SECOND.x(5), () -> {
			armorStand.getEquipment().setItemInMainHand(air);
			Utils.giveItem(player, getArcadePiece(piece));
			Tasks.wait(10, () -> activeSolder = false);
			world.playSound(finalLoc, Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
		});
		//

		Tasks.wait(5, () -> {
			world.playSound(finalLoc, Sound.BLOCK_ANVIL_USE, 0.3F, 0.1F);
			world.playSound(finalLoc, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.5F, 1F);
			Tasks.wait(20, () -> world.playSound(finalLoc, Sound.BLOCK_REDSTONE_TORCH_BURNOUT, 0.5F, 1F));
		});

		for (int i = 0; i < 10; i++) {
			Tasks.wait(i * 5, () -> world.spawnParticle(Particle.LAVA, finalLoc, 5, 0, 0, 0));
		}
	}

	@EventHandler
	public void onCloseInventory(InventoryCloseEvent event) {
		if (!event.getInventory().getType().equals(InventoryType.MERCHANT)) return;
		if (!(event.getPlayer() instanceof Player)) return;

		Player player = (Player) event.getPlayer();
		ProtectedRegion region = WGUtils.getProtectedRegion(BearFair20.getRegion());
		if (!WGUtils.getRegionsAt(player.getLocation()).contains(region)) return;

		if (player.getInventory().contains(joystick.clone().build())) {
			BearFairService service = new BearFairService();
			BearFairUser user = service.get(player);
			user.getArcadePieces().add(joystick.clone().build());
			service.save(user);
		}
	}

	private void soundTasks() {
		Tasks.repeat(0, Time.SECOND.x(5), () -> Bukkit.getOnlinePlayers().stream()
				.filter(player -> BearFair20.getWGUtils().getRegionsLikeAt(player.getLocation(), getRegion()).size() > 0)
				.forEach(MinigameNightIsland::playArcadeEffects));
	}

	private static void playArcadeEffects(Player player) {
		int ran;
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		boolean completedQuest = user.isQuest_MGN_Finish();

		if (!completedQuest) {
			// Sounds
			player.playSound(arcadeSoundLoc, Sound.BLOCK_CAMPFIRE_CRACKLE, 1F, 1F);
			Tasks.wait(Time.SECOND.x(1) + 10, () -> player.playSound(arcadeSoundLoc, Sound.BLOCK_CAMPFIRE_CRACKLE, 1F, 1F));
			Tasks.wait(Time.SECOND.x(3), () -> player.playSound(arcadeSoundLoc, Sound.BLOCK_CAMPFIRE_CRACKLE, 1F, 1F));

			ran = Utils.randomInt(0, 40);
			Tasks.wait(ran, () -> player.playSound(arcadeSoundLoc, Sound.ITEM_CROSSBOW_LOADING_MIDDLE, 1F, 2F));

			ran = Utils.randomInt(0, 40);
			Tasks.wait(ran, () -> player.playSound(arcadeSoundLoc, Sound.UI_CARTOGRAPHY_TABLE_TAKE_RESULT, 1F, 2F));

			// Particles
			int amount = Utils.randomInt(3, 10);
			for (int i = 0; i < amount; i++) {
				int wait = Utils.randomInt(10, 20);
				Tasks.wait(i * wait, () -> {
					player.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, arcadeSmokeLoc1, 0, 0, 0.05, 0, 1);
					player.spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, arcadeSmokeLoc2, 0, 0, 0.05, 0, 1);
				});
			}

		} else {
			player.playSound(arcadeSoundLoc, Sound.BLOCK_BEACON_AMBIENT, 1F, 1F);
		}
	}

}
