package gg.projecteden.nexus.features.events;

import gg.projecteden.api.common.utils.StringUtils;
import gg.projecteden.nexus.features.customenchants.EnchantUtils;
import gg.projecteden.nexus.models.scheduledjobs.jobs.BlockRegenJob;
import gg.projecteden.nexus.utils.Enchant;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.RandomUtils;
import gg.projecteden.nexus.utils.ToolType;
import gg.projecteden.nexus.utils.ToolType.ToolGrade;
import lombok.Builder;
import lombok.Data;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
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
public class EventBreakable {
	private List<Material> blockMaterials;
	private Predicate<Block> blockPredicate;
	@Builder.Default
	private Sound sound = Sound.BLOCK_STONE_BREAK;
	@Builder.Default
	private float volume = 1;
	@Builder.Default
	private float pitch = 1;
	private List<EventResourceDrop> drops;
	@Builder.Default
	private int expChance = 100;
	private int minExp;
	private int maxExp;
	private List<Material> placeholderTypes;
	private int minPlaceholderDelay;
	private int maxPlaceholderDelay;
	@Builder.Default
	private int minRegenerationDelay = 3 * 60;
	@Builder.Default
	private int maxRegenerationDelay = 5 * 60;
	private ToolType requiredTool;
	private ToolGrade minimumToolGrade;

	public void giveExp(Player player) {
		if (minExp != 0 && maxExp != 0) {
			if (RandomUtils.chanceOf(expChance)) {
				int exp = RandomUtils.randomInt(minExp, maxExp);
				player.giveExp(exp, true);
			}
		}
	}

	public void regen(Block block) {
		regen(Collections.singletonList(block));
	}

	public void regen(List<Block> blocks) {
		final int regenDelay = randomInt(minRegenerationDelay, maxRegenerationDelay);
		final int placeholderDelay = randomInt(minPlaceholderDelay, maxPlaceholderDelay);

		for (Block block : blocks) {
			new BlockRegenJob(block.getLocation(), block.getType()).schedule(regenDelay);

			if (placeholderTypes == null)
				placeholderTypes = new ArrayList<>();
			if (placeholderTypes.isEmpty())
				placeholderTypes.add(Material.AIR);

			var placeholder = RandomUtils.randomElement(placeholderTypes);
			if (placeholder == Material.COBBLESTONE && block.getType().name().contains("DEEPSLATE"))
				placeholder = Material.COBBLED_DEEPSLATE;

			if (placeholderDelay == 0)
				block.setType(placeholder);
			else
				new BlockRegenJob(block.getLocation(), placeholder).schedule(placeholderDelay);
		}
	}

	public void giveDrops(Player player) {
		PlayerUtils.giveItems(player, getDrops(player.getInventory().getItemInMainHand()));
		giveExp(player);
	}

	public static class EventBreakableBuilder {

		public EventBreakableBuilder blockMaterials(Material... materials) {
			return blockMaterials(Arrays.asList(materials));
		}

		public EventBreakableBuilder blockMaterials(List<Material> materials) {
			if (blockMaterials == null)
				blockMaterials = new ArrayList<>();

			blockMaterials.addAll(materials);
			return this;
		}

		public EventBreakableBuilder sound(Sound sound) {
			return sound(sound, 1, 1);
		}

		public EventBreakableBuilder sound(Sound sound, float volume, float pitch) {
			this.sound$value = sound;
			this.sound$set = true;
			this.volume$value = volume;
			this.volume$set = true;
			this.pitch$value = pitch;
			this.pitch$set = true;
			return this;
		}

		public EventBreakableBuilder drops(Material material, int min, int max) {
			return drops(EventResourceDrop.builder().item(material).min(min).max(max).build());
		}

		public EventBreakableBuilder drops(EventResourceDrop drop) {
			return drops(Collections.singletonList(drop));
		}

		public EventBreakableBuilder drops(EventResourceDrop.EventResourceDropBuilder drop) {
			return drops(drop.build());
		}

		public EventBreakableBuilder drops(List<EventResourceDrop> drops) {
			if (this.drops == null)
				this.drops = new ArrayList<>();

			this.drops.addAll(drops);
			return this;
		}

		public EventBreakableBuilder exp(int expChance, int minExp, int maxExp) {
			this.expChance$value = expChance;
			this.expChance$set = true;
			this.minExp = minExp;
			this.maxExp = maxExp;
			return this;
		}

		public EventBreakableBuilder placeholderTypes(Material... materials) {
			return placeholderTypes(Arrays.asList(materials));
		}

		public EventBreakableBuilder placeholderTypes(List<Material> materials) {
			if (placeholderTypes == null)
				placeholderTypes = new ArrayList<>();

			placeholderTypes.addAll(materials);
			return this;
		}

		public EventBreakableBuilder regenerationDelay(int min, int max) {
			this.minRegenerationDelay$value = min;
			this.minRegenerationDelay$set = true;
			this.maxRegenerationDelay$value = max;
			this.maxRegenerationDelay$set = true;
			return this;
		}

		public EventBreakableBuilder placeholderDelay(int min, int max) {
			this.minPlaceholderDelay = min;
			this.maxPlaceholderDelay = max;
			return this;
		}

		public EventBreakableBuilder requiredTool(ToolType type, ToolGrade grade) {
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
		@Builder.Default
		private boolean useFortune = true;

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

	public boolean isCorrectTool(ItemStack tool) {
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
