package gg.projecteden.nexus.features.events;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.customenchants.EnchantUtils;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.RandomUtils.randomInt;
import static java.util.stream.Collectors.toList;

@Data
@Builder
public class EventBreakableBlock {
	private List<Material> blockMaterials;
	private Predicate<Block> blockPredicate;
	private List<EventResourceDrop> drops;
	private List<Material> replacementTypes;
	@Builder.Default
	private int minRegenTime = 3 * 60;
	@Builder.Default
	private int maxRegenTime = 5 * 60;
	private ToolType requiredTool;
	private ToolGrade minimumToolGrade;

	public static class EventBreakableBlockBuilder {

		public EventBreakableBlockBuilder blockMaterials(Material... materials) {
			return blockMaterials(Arrays.asList(materials));
		}

		public EventBreakableBlockBuilder blockMaterials(List<Material> materials) {
			if (blockMaterials == null)
				blockMaterials = new ArrayList<>();

			blockMaterials.addAll(materials);
			return this;
		}

		public EventBreakableBlockBuilder drops(Material material, int min, int max) {
			return drops(EventResourceDrop.builder().item(material).min(min).max(max).build());
		}

		public EventBreakableBlockBuilder drops(EventResourceDrop drop) {
			return drops(Collections.singletonList(drop));
		}

		public EventBreakableBlockBuilder drops(EventResourceDrop.EventResourceDropBuilder drop) {
			return drops(drop.build());
		}

		public EventBreakableBlockBuilder drops(List<EventResourceDrop> drops) {
			if (this.drops == null)
				this.drops = new ArrayList<>();

			this.drops.addAll(drops);
			return this;
		}

		public EventBreakableBlockBuilder replacementTypes(Material... materials) {
			return replacementTypes(Arrays.asList(materials));
		}

		public EventBreakableBlockBuilder replacementTypes(List<Material> materials) {
			if (replacementTypes == null)
				replacementTypes = new ArrayList<>();

			replacementTypes.addAll(materials);
			return this;
		}

		public EventBreakableBlockBuilder requiredTool(ToolType type, ToolGrade grade) {
			this.requiredTool = type;
			this.minimumToolGrade = grade;
			return this;
		}

	}

	@Builder
	public static class EventResourceDrop {
		private ItemStack item;
		private int min;
		private int max; // TODO Weighted?

		public ItemStack getDrops(ItemStack tool) {
			int fortuneLevel = EnchantUtils.getLevel(Enchant.FORTUNE, tool);
			int amount = randomInt(min, max) * randomInt(1, fortuneLevel + 1);
			return new ItemBuilder(item).amount(amount).build();
		}

		public static class EventResourceDropBuilder {

			public EventResourceDrop.EventResourceDropBuilder item(Material material) {
				this.item = new ItemStack(material);
				return this;
			}

			public EventResourceDrop.EventResourceDropBuilder item(ItemStack item) {
				this.item = item;
				return this;
			}

			public EventResourceDrop.EventResourceDropBuilder amount(int min, int max) {
				this.min = min;
				this.max = max;
				return this;
			}

		}

	}

	public boolean canBeMinedBy(ItemStack tool) {
		if (requiredTool == null)
			return true;

		if (tool == null)
			return false;

		if (!requiredTool.getTools().contains(tool.getType()))
			return false;

		if (minimumToolGrade != null) {
			ToolGrade toolGrade = ToolGrade.of(tool);
			if (toolGrade == null)
				return false;

			if (toolGrade.lt(minimumToolGrade))
				return false;
		}

		return true;
	}

	public List<ItemStack> getDrops(ItemStack tool) {
		return this.drops.stream().map(drop -> drop.getDrops(tool)).collect(toList());
	}

	public String getAvailableTools() {
		List<Material> tools = requiredTool.getTools(minimumToolGrade.getEqualAndHigherToolGrades());
		return tools.stream().map(StringUtils::camelCase).collect(Collectors.joining(", "));
	}

}
