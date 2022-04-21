package gg.projecteden.nexus.features.customblocks.models;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICraftable;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.ICustomBlock;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.IDirectional;
import gg.projecteden.nexus.features.customblocks.models.blocks.common.IDyeable;
import gg.projecteden.nexus.features.customblocks.models.blocks.compacted.ICompacted;
import gg.projecteden.nexus.features.customblocks.models.blocks.concretebricks.IConcreteBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.genericcrate.IGenericCrate;
import gg.projecteden.nexus.features.customblocks.models.blocks.lanterns.ILantern;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.carved.ICarvedPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.colored.IColoredPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.planks.vertical.IVerticalPlanks;
import gg.projecteden.nexus.features.customblocks.models.blocks.quiltedwool.IQuiltedWool;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.bricks.IStoneBricks;
import gg.projecteden.nexus.features.customblocks.models.blocks.stones.chiseled.IChiseledStone;
import gg.projecteden.nexus.features.customblocks.models.blocks.terracottashingles.ITerracottaShingles;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.RandomUtils;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.NoteBlock;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static gg.projecteden.utils.Utils.collect;

public class CustomBlockTag implements Tag<CustomBlock> {
	public static final CustomBlockTag COMPACTED = new CustomBlockTag(ICompacted.class);
	public static final CustomBlockTag CONCRETE_BRICKS = new CustomBlockTag(IConcreteBricks.class);
	public static final CustomBlockTag GENERIC_CRATES = new CustomBlockTag(IGenericCrate.class);
	public static final CustomBlockTag LANTERNS = new CustomBlockTag(ILantern.class);
	public static final CustomBlockTag COLORED_PLANKS = new CustomBlockTag(IColoredPlanks.class);
	public static final CustomBlockTag CARVED_PLANKS = new CustomBlockTag(ICarvedPlanks.class);
	public static final CustomBlockTag VERTICAL_PLANKS = new CustomBlockTag(IVerticalPlanks.class);
	public static final CustomBlockTag QUILTED_WOOL = new CustomBlockTag(IQuiltedWool.class);
	public static final CustomBlockTag STONE_BRICKS = new CustomBlockTag(IStoneBricks.class);
	public static final CustomBlockTag CHISELED_STONE = new CustomBlockTag(IChiseledStone.class);
	public static final CustomBlockTag TERRACOTTA_SHINGLES = new CustomBlockTag(ITerracottaShingles.class);

	public static final CustomBlockTag DIRECTIONAL = new CustomBlockTag(IDirectional.class);
	public static final CustomBlockTag DYEABLE = new CustomBlockTag(IDyeable.class);
	public static final CustomBlockTag CRAFTABLE = new CustomBlockTag(ICraftable.class);

	public static final CustomBlockTag ALL = new CustomBlockTag(ICustomBlock.class);

	@SneakyThrows
	public static Map<String, Tag<CustomBlock>> getApplicable(CustomBlock customBlock) {
		return collect(tags.entrySet().stream().filter(entry -> entry.getValue().isTagged(customBlock)));
	}

	@Getter
	private static final Map<String, Tag<CustomBlock>> tags = new HashMap<>() {{
		List<Field> fields = new ArrayList<>() {{
			addAll(Arrays.asList(CustomBlockTag.class.getFields()));
			addAll(Arrays.asList(Tag.class.getFields()));
		}};

		for (Field field : fields) {
			try {
				field.setAccessible(true);
				if (field.getType() == Tag.class || field.getType() == CustomBlockTag.class) {
					Tag<CustomBlock> customBlockTag = (Tag<CustomBlock>) field.get(null);

					try {
						Method isTaggedMethod = customBlockTag.getClass().getMethod("isTagged", CustomBlock.class);
						put(field.getName(), customBlockTag);
					} catch (NoSuchMethodException ignore) {
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}};

	static {
		for (Field field : CustomBlockTag.class.getFields()) {
			try {
				field.setAccessible(true);
				if (field.getType() == CustomBlockTag.class) {
					CustomBlockTag customBlockTag = (CustomBlockTag) field.get(null);

					try {
						Method isTaggedMethod = customBlockTag.getClass().getMethod("isTagged", CustomBlock.class);
						customBlockTag.key = new NamespacedKey(Nexus.getInstance(), field.getName());
					} catch (NoSuchMethodException ignore) {
					}

					customBlockTag.lock();
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}

	private final EnumSet<CustomBlock> customBlocks;
	private NamespacedKey key = null;

	public CustomBlockTag() {
		this.customBlocks = EnumSet.noneOf(CustomBlock.class);
	}

	public CustomBlockTag(NamespacedKey key) {
		this.customBlocks = EnumSet.noneOf(CustomBlock.class);
		this.key = key;
	}

	public CustomBlockTag(EnumSet<CustomBlock> customBlocks) {
		this.customBlocks = customBlocks.clone();
	}

	@SafeVarargs
	public CustomBlockTag(Tag<CustomBlock>... customBlockTags) {
		this.customBlocks = EnumSet.noneOf(CustomBlock.class);
		append(customBlockTags);
	}

	public CustomBlockTag(CustomBlock... customBlocks) {
		this.customBlocks = EnumSet.noneOf(CustomBlock.class);
		append(customBlocks);
	}

	public CustomBlockTag(String segment, CustomBlockTag.MatchMode mode) {
		this.customBlocks = EnumSet.noneOf(CustomBlock.class);
		append(segment, mode);
	}

	public CustomBlockTag(String segment, CustomBlockTag.MatchMode mode, CustomBlockTag customBlocks) {
		this.customBlocks = EnumSet.noneOf(CustomBlock.class);
		append(segment, mode, customBlocks.getValues().toArray(CustomBlock[]::new));
	}

	public CustomBlockTag(Predicate<CustomBlock> predicate) {
		this.customBlocks = EnumSet.noneOf(CustomBlock.class);
		append(predicate);
	}

	public CustomBlockTag(Class<? extends ICustomBlock> clazz) {
		this(customBlock -> clazz.isAssignableFrom(customBlock.get().getClass()));
	}

	@Override
	public NamespacedKey getKey() {
		return key;
	}

	public CustomBlockTag append(CustomBlock... customBlocks) {
		return edit(values -> values.addAll(Arrays.asList(customBlocks)));
	}

	@SafeVarargs
	public final CustomBlockTag append(Tag<CustomBlock>... customBlockTags) {
		return edit(values -> {
			for (Tag<CustomBlock> customBlockTag : customBlockTags)
				values.addAll(customBlockTag.getValues());
		});
	}

	public CustomBlockTag append(Predicate<CustomBlock> predicate) {
		return edit(values -> {
			for (CustomBlock customBlock : CustomBlock.values())
				if (predicate.test(customBlock))
					values.add(customBlock);
		});
	}

	public CustomBlockTag append(String segment, CustomBlockTag.MatchMode mode) {
		append(segment, mode, CustomBlock.values());
		return this;
	}

	public CustomBlockTag append(String segment, CustomBlockTag.MatchMode mode, CustomBlock[] customBlocks) {
		edit(values -> {
			switch (mode) {
				case PREFIX:
					for (CustomBlock customBlock : customBlocks)
						if (customBlock.name().startsWith(segment.toUpperCase()))
							values.add(customBlock);
					break;

				case SUFFIX:
					for (CustomBlock customBlock : customBlocks)
						if (customBlock.name().endsWith(segment.toUpperCase()))
							values.add(customBlock);
					break;

				case CONTAINS:
					for (CustomBlock customBlock : customBlocks)
						if (customBlock.name().contains(segment.toUpperCase()))
							values.add(customBlock);
					break;
			}
		});

		return this;
	}

	public CustomBlockTag exclude(CustomBlock... customBlocks) {
		return edit(values -> {
			for (CustomBlock customBlock : customBlocks)
				values.remove(customBlock);
		});
	}

	@SafeVarargs
	public final CustomBlockTag exclude(Tag<CustomBlock>... customBlockTags) {
		return edit(values -> {
			for (Tag<CustomBlock> customBlockTag : customBlockTags)
				values.removeAll(customBlockTag.getValues());
		});
	}

	public CustomBlockTag exclude(Predicate<CustomBlock> predicate) {
		return edit(values -> values.removeIf(predicate));
	}

	private CustomBlockTag edit(Consumer<EnumSet<CustomBlock>> consumer) {
		EnumSet<CustomBlock> customBlocks = getValues();
		consumer.accept(customBlocks);
		return locked ? new CustomBlockTag(customBlocks) : this;
	}

	public CustomBlockTag exclude(String segment, CustomBlockTag.MatchMode mode) {
		exclude(segment, mode, CustomBlock.values());
		return this;
	}

	public CustomBlockTag exclude(String segment, CustomBlockTag.MatchMode mode, CustomBlock[] customBlocks) {
		return edit(values -> {
			switch (mode) {
				case PREFIX:
					for (CustomBlock customBlock : customBlocks)
						if (customBlock.name().startsWith(segment.toUpperCase()))
							values.remove(customBlock);
					break;

				case SUFFIX:
					for (CustomBlock customBlock : customBlocks)
						if (customBlock.name().endsWith(segment.toUpperCase()))
							values.remove(customBlock);
					break;

				case CONTAINS:
					for (CustomBlock customBlock : customBlocks)
						if (customBlock.name().contains(segment.toUpperCase()))
							values.remove(customBlock);
					break;
			}
		});
	}

	@Override
	public EnumSet<CustomBlock> getValues() {
		return locked ? EnumSet.copyOf(customBlocks) : customBlocks;
	}

	public CustomBlock first() {
		return customBlocks.iterator().next();
	}

	public CustomBlock[] toArray() {
		return new ArrayList<>(customBlocks).toArray(CustomBlock[]::new);
	}

	@Override
	public boolean isTagged(CustomBlock customBlock) {
		if(customBlock == null)
			return false;

		return customBlocks.contains(customBlock);
	}

	public boolean isTagged(@Nullable ItemStack item) {
		return !Nullables.isNullOrAir(item) && isTagged(CustomBlock.fromItemstack(item));
	}

	public boolean isTagged(@NotNull Block block) {
		return isTagged(block.getBlockData());
	}

	public boolean isTagged(@NotNull BlockData block) {
		if(!(block instanceof NoteBlock noteBlock))
			return false;

		return isTagged(CustomBlock.fromNoteBlock(noteBlock));
	}

	public boolean isNotTagged(@NotNull CustomBlock customBlock) {
		return !isTagged(customBlock);
	}

	public boolean isNotTagged(@Nullable ItemStack item) {
		return !isTagged(item);
	}

	public boolean isNotTagged(@NotNull Block block) {
		return !isTagged(block);
	}

	public boolean isNotTagged(@NotNull BlockData block) {
		return !isTagged(block);
	}

	@Override
	public String toString() {
		return customBlocks.toString();
	}

	public CustomBlock random() {
		return RandomUtils.randomElement(this);
	}

	public enum MatchMode {
		PREFIX,
		SUFFIX,
		CONTAINS
	}

	private boolean locked;

	private void lock() {
		this.locked = true;
	}
}
