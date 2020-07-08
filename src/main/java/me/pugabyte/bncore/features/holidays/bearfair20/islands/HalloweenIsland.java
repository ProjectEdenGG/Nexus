package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import com.mewin.worldguardregionapi.events.RegionEnteredEvent;
import com.mewin.worldguardregionapi.events.RegionLeftEvent;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import lombok.Getter;
import me.pugabyte.bncore.BNCore;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.HalloweenIsland.HalloweenNPCs;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.NPCClass;
import me.pugabyte.bncore.features.holidays.bearfair20.islands.Island.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;
import me.pugabyte.bncore.models.bearfair.BearFairService;
import me.pugabyte.bncore.models.bearfair.BearFairUser;
import me.pugabyte.bncore.utils.ItemBuilder;
import me.pugabyte.bncore.utils.RandomUtils;
import me.pugabyte.bncore.utils.Tasks;
import me.pugabyte.bncore.utils.Time;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.pugabyte.bncore.features.holidays.bearfair20.BearFair20.WGUtils;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.chime;
import static me.pugabyte.bncore.features.holidays.bearfair20.quests.BFQuests.itemLore;

@Region("halloween")
@NPCClass(HalloweenNPCs.class)
public class HalloweenIsland implements Listener, Island {
	private final Map<Player, Integer> musicTaskMap = new HashMap<>();
	private final Location halloweenMusicLoc = new Location(BearFair20.getWorld(), -921, 128, -1920);
	private Sound[] halloweenSounds = {Sound.AMBIENT_CAVE, Sound.ENTITY_ELDER_GUARDIAN_DEATH, Sound.ENTITY_VEX_AMBIENT,
			Sound.ENTITY_WITCH_AMBIENT, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS,
			Sound.ENTITY_ILLUSIONER_CAST_SPELL, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, Sound.ENTITY_SHULKER_AMBIENT};
	//
	public static ItemStack atticKey = new ItemBuilder(Material.TRIPWIRE_HOOK).lore(itemLore).amount(1).name("Attic Key").build();
	private String atticRg = getRegion() + "_atticdoor";
	private Location atticDeniedLoc = new Location(BearFair20.getWorld(), -935.5, 159.5, -1916.5, -90, 32);
	//
	private String basketRg = getRegion() + "_basket";
	private static Location basketLoc = new Location(BearFair20.getWorld(), -917, 126, -1848);
	public static ItemStack basketItem;

	public HalloweenIsland() {
		BNCore.registerListener(this);
		soundTasks();

		List<ItemStack> drops = new ArrayList<>(basketLoc.getBlock().getDrops());
		basketItem = new ItemBuilder(drops.get(0)).clone().lore(itemLore).name("Basket of Halloween Candy").build();
	}

	public enum HalloweenNPCs implements TalkingNPC {
		TOUR_GUIDE(2675) {
			@Override
			public List<String> getScript(Player player) {
				BearFairService service = new BearFairService();
				BearFairUser user = service.get(player);

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Hey there! Welcome to the historic Ravens' Nest Estate Museum!");
				startQuest.add("wait 80");
				startQuest.add("Here, we safely preserve the very location where the supernatural events that lead to the kidnap and resuce of the bear nation staff occured in 2018!");
				startQuest.add("wait 120");
				startQuest.add("<self> Hey! I was wondering if you could tell me where I might find a halloween candy basket?");
				startQuest.add("wait 80");
				startQuest.add("Ah, looking for some of our famous hallowen candy, eh?");
				startQuest.add("wait 80");
				startQuest.add("Well, you've come at the perfect time, because we have a museum wide scavenger hunt happening right now, with exactly that as the prize!");
				startQuest.add("wait 120");
				startQuest.add("If you want that basket of candy, you gotta find it! Good Luck! And enjoy the museum!");

				if (!user.isQuest_Main_Start())
					return Collections.singletonList("Hello there!");

				if (user.isQuest_Halloween_Finish())
					return Collections.singletonList("Oh you've found it! Congratulations!");

				if (!user.isQuest_Halloween_Start()) {
					user.setQuest_Halloween_Start(true);
					nextStep(player); // 1
				}
				return startQuest;
			}
		},
		PROFESSOR(2671, Collections.singletonList("You know, it is said that many of these books contain scriptures and spells that summon pure evil.")),
		ARTIST(2672, Collections.singletonList("Look at the attention to detail! The skillfulness! I want to be able to paint like this one day.")),
		INVESTIGATOR(2673, Arrays.asList(
				"Hey you, yeah you, what are you doing down here alone!? Don't you know that this area is still a paranormal zone?!",
				"wait 80",
				"Yeah, that's right. Some of the evil from the events of 2018 still lingers here and I've been clearing out the remnants.",
				"wait 80",
				"Now get out of here, I've got more investigating to do here. Don't worry, the ground and second floors are safe.")
		),
		GHOST_FATHER(2674, Arrays.asList(
				"Don't bother me Celeste, I'm working. Go play with your sister.",
				"wait 80",
				"I must crack the code.")
		),
		GARDENER(2966, Collections.singletonList("Oh hey there darling, you probably shouldn't be out here at night. Be careful now!"));

		@Getter
		private final int npcId;
		@Getter
		private final List<String> script;

		HalloweenNPCs(int npcId) {
			this.npcId = npcId;
			this.script = new ArrayList<>();
		}

		HalloweenNPCs(int npcId, List<String> script) {
			this.npcId = npcId;
			this.script = script;
		}
	}

	private static void nextStep(Player player) {
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		int step = user.getQuest_Halloween_Step() + 1;
		user.setQuest_Halloween_Step(step);
		service.save(user);
	}

	@EventHandler
	public void onClickBasketHead(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block clicked = event.getClickedBlock();
		if (Utils.isNullOrAir(clicked)) return;

		ProtectedRegion skullRegion = WGUtils.getProtectedRegion(basketRg);
		if (!WGUtils.getRegionsAt(clicked.getLocation()).contains(skullRegion)) return;

		if (!clicked.getType().equals(Material.PLAYER_HEAD)) return;

		Player player = event.getPlayer();
		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);

		if (!user.isQuest_Halloween_Start() || user.getQuest_Halloween_Step() != 1) return;

		user.setQuest_Halloween_Finish(true);
		nextStep(player); // 2

		List<ItemStack> drops = new ArrayList<>(basketLoc.getBlock().getDrops());
		ItemStack basket = new ItemBuilder(drops.get(0)).clone().lore(itemLore).name("Basket of Halloween Candy").build();
		Utils.giveItem(player, basket);
		chime(player);

	}

	private void soundTasks() {
		Tasks.repeat(0, 30 * 20, () -> {
			if (RandomUtils.chanceOf(50)) {
				Sound sound = RandomUtils.randomElement(Arrays.asList(halloweenSounds));
				musicTaskMap.forEach((player, integer) -> player.playSound(player.getLocation(), sound, 10F, 0.1F));
			}
		});

		Tasks.repeat(0, 25 * 20, () -> {
			if (RandomUtils.chanceOf(25))
				musicTaskMap.forEach((player, integer) -> player.playSound(player.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 10F, 0.1F));
		});

	}

	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		ProtectedRegion region = WGUtils.getProtectedRegion(getRegion());
		if (!WGUtils.getRegionsAt(event.getPlayer().getLocation()).contains(region)) return;

		if (event.getClickedBlock() == null) return;

		Material material = event.getClickedBlock().getType();
		if (!material.equals(Material.RAIL)) return;

		Location loc = event.getClickedBlock().getLocation();
		float ran = (float) RandomUtils.randomDouble(0.0, 2.0);
		BearFair20.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 0.7F, ran);
	}

	@EventHandler
	public void onHalloweenRegionEnter(RegionEnteredEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(getRegion())) return;
		startSoundsTask(event.getPlayer());
	}

	@EventHandler
	public void onRegionExit(RegionLeftEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(getRegion())) return;
		stopSoundsTask(event.getPlayer());
	}

	private void startSoundsTask(Player player) {
		int taskId = Tasks.repeat(0, Time.SECOND.x(350), () -> {
			player.stopSound(Sound.MUSIC_DISC_13);
			player.playSound(halloweenMusicLoc, Sound.MUSIC_DISC_13, SoundCategory.AMBIENT, 7F, 0.1F);
		});

		musicTaskMap.put(player, taskId);
	}

	private void stopSoundsTask(Player player) {
		Integer taskId = musicTaskMap.remove(player);
		if (taskId != null)
			Tasks.cancel(taskId);
	}

	@EventHandler
	public void onClickAtticKey(PlayerInteractEntityEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Player player = event.getPlayer();

		ProtectedRegion region = WGUtils.getProtectedRegion(BearFair20.getRegion());
		if (!WGUtils.getRegionsAt(player.getLocation()).contains(region)) return;

		Entity clicked = event.getRightClicked();
		if (!(clicked instanceof ItemFrame)) return;

		ItemFrame itemFrame = (ItemFrame) clicked;
		ItemStack item = itemFrame.getItem();

		if (!item.equals(atticKey)) return;

		BearFairService service = new BearFairService();
		BearFairUser user = service.get(player);
		if (user.isQuest_Halloween_Key()) return;

		user.setQuest_Halloween_Key(true);
		service.save(user);

		Utils.giveItem(player, atticKey);
		chime(player);
	}

	@EventHandler
	public void onAtticRegionEnter(RegionEnteredEvent event) {
		if (event.getRegion().getId().equalsIgnoreCase(atticRg)) {
			Player player = event.getPlayer();
			if (!player.getInventory().contains(atticKey)) {
				player.teleport(atticDeniedLoc);
				BearFair20.send("&cYou need the attic key to enter", player);
			}
		}
	}
}
