package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.models.EventBreakable;
import gg.projecteden.nexus.features.events.y2024.vulan24.menus.VuLan24Menus;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24Entity;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24Quest;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestTask;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
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
	start = @Date(m = 8, d = 1, y = 2024), // TODO If ready in time, change to 7/15
	end = @Date(m = 8, d = 31, y = 2024),
	world = "vu_lan",
	region = "vu_lan",
	warpType = WarpType.VULAN24
)
public class VuLan24 extends EdenEvent {
	private static VuLan24 instance;

	@Getter
	private static final String storeRegionFlorist = "vu_lan_decor_store_florist";
	@Getter
	private static final String storeRegionMarket = "vu_lan_decor_store_market";

	public VuLan24() {
		instance = this;
	}

	public static VuLan24 get() {
		return instance;
	}

	public static final List<LootTables> ARCHAEOLOGY_LOOT_TABLES = List.of(
		LootTables.OCEAN_RUIN_COLD_ARCHAEOLOGY,
		LootTables.OCEAN_RUIN_WARM_ARCHAEOLOGY,
		LootTables.TRAIL_RUINS_ARCHAEOLOGY_RARE,
		LootTables.TRAIL_RUINS_ARCHAEOLOGY_COMMON,
		LootTables.DESERT_PYRAMID_ARCHAEOLOGY,
		LootTables.DESERT_WELL_ARCHAEOLOGY
	);

	@Override
	public void registerInteractHandlers() {
		handleInteract(VuLan24NPC.BOAT_SALESMAN, (player, npc) -> VuLan24Menus.getBoatPicker().open(player));
		handleInteract(VuLan24NPC.TOUR_GUIDE, (player, npc) -> VuLan24Menus.getGuideShop().open(player));
		handleInteract(VuLan24NPC.MINER, (player, npc) -> VuLan24Menus.getMinerShop().open(player));
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

}
