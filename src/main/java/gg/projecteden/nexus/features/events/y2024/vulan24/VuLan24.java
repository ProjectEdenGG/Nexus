package gg.projecteden.nexus.features.events.y2024.vulan24;

import gg.projecteden.nexus.features.events.EdenEvent;
import gg.projecteden.nexus.features.events.EventBreakableBlock;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24Entity;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24NPC;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestItem;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestReward;
import gg.projecteden.nexus.features.events.y2024.vulan24.quests.VuLan24QuestTask;
import gg.projecteden.nexus.features.quests.QuestConfig;
import gg.projecteden.nexus.framework.annotations.Date;
import gg.projecteden.nexus.models.warps.WarpType;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;

@QuestConfig(
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

	@Override
	protected void registerBreakableBlocks() {
		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.CLAY)
			.drops(Material.CLAY_BALL, 1, 3)
			.replacementTypes(Material.GRAVEL)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.STONE)
			.drops(Material.STONE, 1, 1)
			.replacementTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.WOODEN)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.TUFF)
			.drops(Material.TUFF, 1, 1)
			.replacementTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.WOODEN)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)
			.drops(Material.COAL, 1, 3)
			.replacementTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE)
			.drops(Material.COAL, 1, 3)
			.replacementTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE)
			.drops(Material.RAW_COPPER, 1, 3)
			.replacementTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE)
			.drops(Material.RAW_GOLD, 1, 2)
			.replacementTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE)
			.drops(Material.RAW_IRON, 1, 2)
			.replacementTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.STONE)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE)
			.drops(Material.DIAMOND, 1, 2)
			.replacementTypes(Material.COBBLESTONE)
			.requiredTool(ToolType.PICKAXE, ToolGrade.IRON)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.WHEAT)
			.drops(Material.WHEAT, 1, 3)
			.sound(Sound.BLOCK_CROP_BREAK)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.CARROTS)
			.drops(Material.CARROT, 1, 3)
			.sound(Sound.BLOCK_CROP_BREAK)
		);

		registerBreakableBlock(EventBreakableBlock.builder()
			.blockMaterials(Material.SUGAR_CANE)
			.drops(Material.SUGAR_CANE, 1, 1)
			.sound(Sound.BLOCK_GRASS_BREAK, 1, .8f)
		);
	}

}
