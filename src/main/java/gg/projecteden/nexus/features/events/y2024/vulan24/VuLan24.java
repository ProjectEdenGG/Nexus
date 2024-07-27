package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.api.common.annotations.Environments;
import gg.projecteden.api.common.utils.Env;
import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.models.EventBreakable;
import gg.projecteden.nexus.features.events.models.EventPlaceable;
import gg.projecteden.nexus.features.events.y2024.vulan24.models.VuLan24BoatTracker;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24Entity;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24Quest;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestTask;
import gg.projecteden.nexus.features.listeners.events.LivingEntityKilledByPlayerEvent;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.features.quests.interactable.instructions.Dialog;
import gg.projecteden.nexus.features.regionapi.events.player.PlayerEnteredRegionEvent;
import gg.projecteden.nexus.features.resourcepack.models.CustomMaterial;
import gg.projecteden.nexus.features.resourcepack.models.font.CustomEmoji;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.scheduledjobs.jobs.VuLan24LanternAnimationJob;
import gg.projecteden.nexus.models.vulan24.VuLan24ConfigService;
import gg.projecteden.nexus.models.vulan24.VuLan24UserService;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.TitleBuilder;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pillager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.loot.LootTables;

import java.util.List;

import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventDefaultFishingLoot.STONEFISH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.FISH;
import static gg.projecteden.nexus.features.events.models.EventFishingLoot.EventFishingLootCategory.JUNK;

@QuestConfig(
	quests = VuLan24Quest.class,
	tasks = VuLan24QuestTask.class,
	npcs = VuLan24NPC.class,
	entities = VuLan24Entity.class,
	items = VuLan24QuestItem.class,
	rewards = VuLan24QuestReward.class,
	start = @Date(m = 8, d = 1, y = 2024),
	end = @Date(m = 8, d = 31, y = 2024),
	world = "vu_lan",
	region = "vu_lan",
	warpType = WarpType.VULAN24
)
@Environments(Env.PROD)
public class VuLan24 extends EdenEvent {
	private static VuLan24 instance;

	public VuLan24() {
		instance = this;
	}

	public static VuLan24 get() {
		return instance;
	}

	@Override
	public void onStart() {
		super.onStart();
		customGenericGreetings = List.of(
			"Xin Chao stranger!",
			"Cau Chao!",
			"Tieng Chao traveler. Welcome to Vinh Luc!"
		);
		new VuLan24BoatTracker();
		new VuLan24LanternAnimationJob();
	}

	public static final List<LootTables> ARCHAEOLOGY_LOOT_TABLES = List.of(
		LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY,
		LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY,
		LootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE,
		LootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON,
		LootTables.DESERT_PYRAMID_ARCHAEOLOGY,
		LootTables.DESERT_WELL_ARCHAEOLOGY
	);

	@EventHandler
	public void onRightClick(PlayerInteractEvent event) {
		if (!VuLan24.get().isInRegion(event.getPlayer(), "vulan_lanternanimation_place"))
			return;

		if (CustomMaterial.of(event.getPlayer().getInventory().getItemInMainHand()) != CustomMaterial.of(VuLan24QuestItem.PAPER_LANTERN_FLOATING.get()))
			return;

		event.setCancelled(true);
		event.getPlayer().getInventory().getItemInMainHand().subtract(1);
		new VuLan24ConfigService().edit0(user -> user.setLanterns(user.getLanterns() + 1));
	}

	@EventHandler
	public void on(LivingEntityKilledByPlayerEvent event) {
		if (!shouldHandle(event.getAttacker()))
			return;

		final LivingEntity entity = event.getEntity();
		if (entity.getType() == EntityType.RAVAGER)
			event.getAttacker().getWorld().dropItem(entity.getLocation(), VuLan24QuestItem.RAVAGER_DROP.get());
		else if (entity.getType() == EntityType.PILLAGER)
			if (MaterialTag.BANNERS.isTagged(((Pillager) entity).getEquipment().getHelmet().getType()))
				event.getAttacker().getWorld().dropItem(entity.getLocation(), VuLan24QuestItem.CAPTAIN_DROP.get());
			else
				event.getAttacker().getWorld().dropItem(entity.getLocation(), VuLan24QuestItem.PILLAGER_DROP.get());
	}

	@EventHandler
	public void on(PlayerEnteredRegionEvent event) {
		if (!event.getRegion().getId().equals("avontyre_vu_lan_ship"))
			return;

		final VuLan24UserService userService = new VuLan24UserService();
		if (!userService.get(event.getPlayer()).isReadyToVisit())
			return;

		new TitleBuilder()
			.title(CustomEmoji.SCREEN_BLACK.getChar())
			.fade(TickTime.TICK.x(10))
			.players(event.getPlayer())
			.stay(TickTime.TICK.x(10))
			.send()
			.thenRun(() -> {
				WarpType.VULAN24.get("spawn").teleportAsync(event.getPlayer());
				userService.edit(event.getPlayer(), user -> user.setVisited(true));
			});
	}

	@Override
	public void registerInteractHandlers() {
		handleInteract(VuLan24NPC.BOAT_SALESMAN, (player, npc) -> VuLan24Menus.getBoatPicker().open(player));
		handleInteract(VuLan24NPC.TOUR_GUIDE, (player, npc) -> VuLan24Menus.getGuideShop().open(player));
		handleInteract(VuLan24NPC.MINER, (player, npc) -> VuLan24Menus.getMinerShop().open(player));
		handleInteract(VuLan24NPC.HAT_SALESMAN, (player, npc) -> VuLan24Menus.getBambooHatShop(player).open(player));
		handleInteract(VuLan24NPC.CAPTAIN_LAI_AVONTYRE, (player, npc) -> {
			final VuLan24UserService userService = new VuLan24UserService();
			if (userService.get(player).isReadyToVisit())
				new Dialog(npc)
					.npc("Climb aboard when you are ready to visit Vinh Luc for the Vu Lan Festival!")
					.send(player);
			else
				new Dialog(npc)
					.npc("Xin Chao adventurer! We arrive from the far east with goods and wares that your eyes have never seen! We had heard of this place and thought we'd stop by on our way back to Vietuda!")
					.player("Welcome to Avontyre! I can't say I've heard of Vietuda.")
					.npc("Well, specifically, we're headed back to Vinh Luc, an island south of the mainland.")
					.npc("We're collecting some goods and materials to bring back to the island for the Vu Lan Festival that's going to start in a few days. You should visit!")
					.player("That sounds awesome! I'd love to go... but I don't have a ship... or a boat.")
					.npc("Not to worry, If you'd like, feel free to come back with us! Our island would love to have you. Climb aboard when you are ready.")
					.thenRun(quester -> userService.edit(quester, user -> user.setReadyToVisit(true)))
					.send(player);
			}
		);
		handleInteract(VuLan24NPC.CAPTAIN_LAI_VINH_LUC, (player, npc) -> new Dialog(npc)
			.npc("Ah... home sweet home. I hope this year's Vu Lan is just as great as the last!")
			.send(player)
		);
		handleInteract(VuLan24NPC.MAYOR_HOA, (player, npc) -> new Dialog(npc)
			.npc("Xin Chao! Welcome to Vinh Luc, the isle of tombs! I hope you have a lovely stay here for the festival!")
			.npc("Please take your time to explore our beautiful island. There are plenty of things to do here and everyone is so welcoming!")
			.player("Thank you! There’s a lot to do… where should I start?")
			.npc("I recommend you talk to Anh first to find out about the Community Quest! Then, visit the quest boards for more.")
			.send(player)
		);
		handleInteract(VuLan24NPC.FLORIST, (player, npc) -> new Dialog(npc)
			.npc("The Banyan Tree is a symbol of Vietudan villages! The Snake plant is even better but nothing takes the cake like a China Rose. Beautiful flowers!")
			.send(player)
		);
		handleInteract(VuLan24NPC.STONE_MASON, (player, npc) -> new Dialog(npc)
			.npc("Looking for work? Visit Truong near the building site! He's always after some help.")
			.send(player)
		);
		handleInteract(VuLan24NPC.PLUNDERED_VILLAGE_VILLAGER, (player, npc) -> new Dialog(npc)
			.npc("You! These bandits sacked our village on the eve of Vu Lan! Can you help? Find Xuam in the Market!")
			.send(player)
		);
		handleInteract(VuLan24NPC.PLUNDERED_VILLAGE_FARMER, (player, npc) -> new Dialog(npc)
			.npc("I can't wait to harvest these carrots. If I can get enough gold from the caves I could even make gold ones this year!")
			.send(player)
		);
//		handleInteract(VuLan24NPC.STUDENT, (player, npc) -> new Dialog(npc).npc("").send(Quester.of(player)));
//		handleInteract(VuLan24NPC.CULTURE_GUY, (player, npc) -> new Dialog(npc).npc("").send(Quester.of(player)));
	}

	@Override
	protected void registerFishingLoot() {
		registerFishingLoot(FISH, JUNK);
		getFishingLoot(STONEFISH).setMaxY(75);
	}

	@Override
	protected void registerBreakableBlocks() {
		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.CLAY)
			.drops(Material.CLAY_BALL, 1, 3)
			.placeholderTypes(Material.GRAVEL)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.STONE)
			.drops(Material.STONE, 1, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.WOODEN)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.TUFF)
			.drops(Material.TUFF, 1, 1)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.WOODEN)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)
			.drops(Material.COAL, 1, 3)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)
			.drops(Material.COAL, 1, 3)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)
			.drops(Material.RAW_COPPER, 1, 3)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE)
			.drops(Material.RAW_GOLD, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE)
			.drops(Material.RAW_IRON, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)
			.drops(Material.DIAMOND, 1, 2)
			.placeholderTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.WHEAT)
			.drops(Material.WHEAT, 1, 3)
			.sound(Sound.BLOCK_CROP_BREAK)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.CARROTS)
			.drops(Material.CARROT, 1, 3)
			.sound(Sound.BLOCK_CROP_BREAK)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.SUGAR_CANE)
			.drops(Material.SUGAR_CANE, 1, 1)
			.sound(Sound.BLOCK_GRASS_BREAK, 1f, .8f)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(MaterialTag.LEAVES)
			.drops(Material.STICK, 1, 2)
			.drops(Material.APPLE, 1, 1, 5)
			.sound(Sound.BLOCK_GRASS_BREAK, 1f, .8f)
		);

		registerBreakable(EventBreakable.builder()
			.blockMaterials(Material.COBWEB)
			.requiredTool(ToolType.SWORD)
			.sound(Sound.BLOCK_STONE_BREAK, 1f, .8f) // TODO 1.21 new sound BLOCK_COBWEB_BREAK
		);
	}

	@Override
	protected void registerPlaceableBlocks() {
		registerPlaceable(EventPlaceable.builder()
			.blockMaterials(Material.SUSPICIOUS_SAND)
		);
	}

}
