package gg.projecteden.nexus.features.events.y2020.bearfair20.islands;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.events.annotations.Region;
import gg.projecteden.nexus.features.events.models.BearFairIsland;
import gg.projecteden.nexus.features.events.models.BearFairIsland.NPCClass;
import gg.projecteden.nexus.features.events.models.Talker.TalkingNPC;
import gg.projecteden.nexus.features.events.y2020.bearfair20.BearFair20;
import gg.projecteden.nexus.features.events.y2020.bearfair20.islands.HalloweenIsland.HalloweenNPCs;
import gg.projecteden.nexus.features.events.y2020.bearfair20.quests.BFQuests;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerLeftRegionEvent;
import gg.projecteden.nexus.models.bearfair20.BearFair20User;
import gg.projecteden.nexus.models.bearfair20.BearFair20UserService;
import gg.projecteden.nexus.utils.*;
import lombok.Getter;
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

import java.util.*;

@Region("halloween")
@NPCClass(HalloweenNPCs.class)
public class HalloweenIsland implements Listener, BearFairIsland {

	@Override
	public String getEventRegion() {
		return BearFair20.getRegion();
	}

	private final Map<Player, Integer> musicTaskMap = new HashMap<>();
	private final Location halloweenMusicLoc = new Location(BearFair20.getWorld(), -921, 128, -1920);
	private Sound[] halloweenSounds = {Sound.AMBIENT_CAVE, Sound.ENTITY_ELDER_GUARDIAN_DEATH, Sound.ENTITY_VEX_AMBIENT,
			Sound.ENTITY_WITCH_AMBIENT, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS,
			Sound.ENTITY_ILLUSIONER_CAST_SPELL, Sound.ENTITY_ELDER_GUARDIAN_AMBIENT, Sound.ENTITY_SHULKER_AMBIENT};
	//
	public static ItemStack atticKey = new ItemBuilder(Material.TRIPWIRE_HOOK).lore(BFQuests.itemLore).amount(1).name("Attic Key").build();
	private String atticRg = getRegion() + "_atticdoor";
	private Location atticDeniedLoc = new Location(BearFair20.getWorld(), -935.5, 159.5, -1916.5, -90, 32);
	//
	private String basketRg = getRegion() + "_basket";
	private static Location basketLoc = new Location(BearFair20.getWorld(), -917, 126, -1848);
	public static ItemStack basketItem;

	public HalloweenIsland() {
		Nexus.registerListener(this);
		soundTasks();

		List<ItemStack> drops = new ArrayList<>(basketLoc.getBlock().getDrops());
		if (!drops.isEmpty())
			basketItem = new ItemBuilder(drops.get(0)).clone().lore(BFQuests.itemLore).name("Basket of Halloween Candy").build();
	}

	public enum HalloweenNPCs implements TalkingNPC {
		TOUR_GUIDE(2675) {
			@Override
			public List<String> getScript(Player player) {
				BearFair20UserService service = new BearFair20UserService();
				BearFair20User user = service.get(player);

				List<String> startQuest = new ArrayList<>();
				startQuest.add("Hey there! Welcome to the historic Ravens' Nest Estate Museum!");
				startQuest.add("wait 80");
				startQuest.add("Here, we safely preserve the very location where the supernatural events that lead to the kidnap and resuce of the Project Eden staff occured in 2018!");
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

		@Override
		public String getName() {
			return this.name();
		}

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
		BearFair20UserService service = new BearFair20UserService();
		BearFair20User user = service.get(player);
		int step = user.getQuest_Halloween_Step() + 1;
		user.setQuest_Halloween_Step(step);
		service.save(user);
	}

	@EventHandler
	public void onClickBasketHead(PlayerInteractEvent event) {
		if (event.getHand() != EquipmentSlot.HAND) return;

		Block clicked = event.getClickedBlock();
		if (Nullables.isNullOrAir(clicked)) return;

		ProtectedRegion skullRegion = BearFair20.worldguard().getProtectedRegion(basketRg);
		if (!BearFair20.worldguard().getRegionsAt(clicked.getLocation()).contains(skullRegion)) return;

		if (!BearFair20.enableQuests) return;
		if (!clicked.getType().equals(Material.PLAYER_HEAD)) return;

		Player player = event.getPlayer();
		BearFair20UserService service = new BearFair20UserService();
		BearFair20User user = service.get(player);

		if (!user.isQuest_Halloween_Start() || user.getQuest_Halloween_Step() != 1) return;

		user.setQuest_Halloween_Finish(true);
		nextStep(player); // 2

		List<ItemStack> drops = new ArrayList<>(basketLoc.getBlock().getDrops());
		ItemStack basket = new ItemBuilder(drops.get(0)).clone().lore(BFQuests.itemLore).name("Basket of Halloween Candy").build();
		PlayerUtils.giveItem(player, basket);
		BFQuests.chime(player);

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

		ProtectedRegion region = BearFair20.worldguard().getProtectedRegion(getRegion());
		if (!BearFair20.worldguard().getRegionsAt(event.getPlayer().getLocation()).contains(region)) return;

		if (event.getClickedBlock() == null) return;

		Material material = event.getClickedBlock().getType();
		if (!material.equals(Material.RAIL)) return;

		Location loc = event.getClickedBlock().getLocation();
		float ran = (float) RandomUtils.randomDouble(0.0, 2.0);
		BearFair20.getWorld().playSound(loc, Sound.BLOCK_NOTE_BLOCK_PLING, 0.7F, ran);
	}

	@EventHandler
	public void onHalloweenRegionEnter(PlayerEnteredRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(getRegion())) return;
		startSoundsTask(event.getPlayer());
	}

	@EventHandler
	public void onRegionExit(PlayerLeftRegionEvent event) {
		if (!event.getRegion().getId().equalsIgnoreCase(getRegion())) return;
		stopSoundsTask(event.getPlayer());
	}

	private void startSoundsTask(Player player) {
		int taskId = Tasks.repeat(0, TickTime.SECOND.x(350), () -> {
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

		ProtectedRegion region = BearFair20.worldguard().getProtectedRegion(BearFair20.getRegion());
		if (!BearFair20.worldguard().getRegionsAt(player.getLocation()).contains(region)) return;

		if (!BearFair20.enableQuests) return;

		Entity clicked = event.getRightClicked();
		if (!(clicked instanceof ItemFrame itemFrame)) return;

		ItemStack item = itemFrame.getItem();

		if (!item.equals(atticKey)) return;

		BearFair20UserService service = new BearFair20UserService();
		BearFair20User user = service.get(player);
		if (user.isQuest_Halloween_Key()) return;

		user.setQuest_Halloween_Key(true);
		service.save(user);

		PlayerUtils.giveItem(player, atticKey);
		BFQuests.chime(player);
	}

	@EventHandler
	public void onAtticRegionEnter(PlayerEnteredRegionEvent event) {
		if (event.getRegion().getId().equalsIgnoreCase(atticRg)) {
			if (!BearFair20.enableQuests) return;
			Player player = event.getPlayer();
			if (!player.getInventory().contains(atticKey)) {
				player.teleportAsync(atticDeniedLoc);
				BearFair20.send("&cYou need the attic key to enter", player);
			}
		}
	}
}
