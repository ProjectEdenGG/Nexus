package me.pugabyte.bncore.features.holidays.bearfair20.islands;

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
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

@Region("minigamenight")
@NPCClass(MinigameNightNPCs.class)
public class MinigameNightIsland implements Listener, Island {
	private static final Location arcadeSoundLoc = new Location(BearFair20.getWorld(), -1170, 141, -1716);
	private static final Location arcadeSmokeLoc1 = Utils.getCenteredLocation(new Location(BearFair20.getWorld(), -1170, 140, -1715));
	private static final Location arcadeSmokeLoc2 = Utils.getCenteredLocation(new Location(BearFair20.getWorld(), -1169, 148, -1715));
	private String arcadeRg = getRegion() + "_arcade";
	// Quest Items
	private ItemBuilder cpu = new ItemBuilder(Material.IRON_TRAPDOOR).lore(itemLore).amount(1).name("CPU");
	private ItemBuilder processor = new ItemBuilder(Material.DAYLIGHT_DETECTOR).lore(itemLore).amount(1).name("Processor");
	private ItemBuilder memoryCard = new ItemBuilder(Material.IRON_INGOT).lore(itemLore).amount(1).name("Memory Card");
	private ItemBuilder motherboard = new ItemBuilder(Material.GREEN_CARPET).lore(itemLore).amount(1).name("Motherboard");
	private ItemBuilder powerSupply = new ItemBuilder(Material.BLAST_FURNACE).lore(itemLore).amount(1).name("Power Supply");
	private ItemBuilder speaker = new ItemBuilder(Material.NOTE_BLOCK).lore(itemLore).amount(1).name("Speaker");
	private ItemBuilder hardDrive = new ItemBuilder(Material.HOPPER_MINECART).lore(itemLore).amount(1).name("Hard Drive");
	private ItemBuilder diode = new ItemBuilder(Material.REPEATER).lore(itemLore).amount(1).name("Diode");
	private ItemBuilder joystick = new ItemBuilder(Material.LEVER).lore(itemLore).amount(1).name("Joystick");
	private List<ItemBuilder> arcadePieces = Arrays.asList(cpu, processor, memoryCard, motherboard, powerSupply, speaker, hardDrive, diode, joystick);
	//
	private ItemStack fakeMotherBoard = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).lore(itemLore).amount(1).name("Motherboard").build();
	private ItemStack solderingIron = new ItemBuilder(Material.END_ROD).lore(itemLore).amount(1).name("Soldering Iron").build();

	public MinigameNightIsland() {
		BNCore.registerListener(this);
		soundTasks();
		//TODO:
		//	- When enter basement region, if play is not holding soldering iron, cancel event
		//	- Obtaining the joystick from the collector
		//
	}

	public enum MinigameNightNPCs implements TalkingNPC {
		AXEL(2755) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);
				int step = user.getQuest_MGN_Step();

				List<String> startQuest = new ArrayList<>();
				startQuest.add("TODO: Tells you about the broken arcade machine, and the sore loser");
				startQuest.add("wait 80");
				startQuest.add("TODO: Asks if you’ll fix the machine for “The Arcade Token”");
				startQuest.add("wait 80");
				startQuest.add("TODO: Tells you about the extra soldering iron in his attic, the item you need to " +
						"access the basement and use to fix the components");

				if (!user.isQuest_Main_Start())
					return Collections.singletonList("TODO: GENERIC GREETING");

				if (step >= 3) // 3 is just a guess?
					return Collections.singletonList("TODO: THANKS");

				if (step >= 1)
					return Collections.singletonList("TODO: REMINDER");

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

	private static void nextStep(Player player) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		int step = user.getQuest_SDU_Step() + 1;
		user.setQuest_SDU_Step(step);
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

		giveArcadePiece(player, piece);

	}

	public void giveArcadePiece(Player player, ItemStack piece) {
		if (piece == null) return;
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		if (user.getArcadePieces().contains(piece)) return;

		user.getArcadePieces().add(piece);
		Utils.giveItem(player, piece);
		player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 10, 2);
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
