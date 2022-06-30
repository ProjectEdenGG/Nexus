package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.custommodels.CustomModelConfigService;
import gg.projecteden.nexus.utils.ColorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class CustomModelConverterCommand extends CustomCommand implements Listener {

	public CustomModelConverterCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("compute")
	void compute() {
		new CustomModelConfigService().edit0(config -> {
			config.setNewModels(new ConcurrentHashMap<>());
			for (CustomModel model : ResourcePack.getModels().values())
				config.getNewModels().computeIfAbsent(model.getMaterial(), $ -> new ConcurrentHashMap<>()).put(model.getData(), model.getId());
		});
	}

	@Path("convert <material> <data>")
	void convert(Material material, int data) {
		final CustomModel newModel = CustomModel.convert(material, data);
		if (newModel == null)
			error("Unknown custom model");

		send(newModel.getMaterial() + " " + newModel.getData());
	}

	@Path("tables [radius]")
	void convert_tables(@Arg("200") int radius) {
		int converted = 0;
		for (ItemFrame itemFrame : location().getNearbyEntitiesByType(ItemFrame.class, radius)) {
			final ItemStack item = itemFrame.getItem();
			if (isNullOrAir(item))
				continue;

			if (!item.getType().equals(Material.SCAFFOLDING))
				continue;

			final TableSize size = TableSize.ofOld(item);
			if (size == null)
				continue;

			itemFrame.setItem(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
				.modelId(size.getNewId())
				.dyeColor(ColorType.hexToBukkit("#F4C57A"))
				.build());
			++converted;
		}

		send(PREFIX + "Converted " + converted + " tables");
	}

	@Getter
	@AllArgsConstructor
	private enum TableSize {
		_1x1(30, 300),
		_1x2(31, 301),
		_2x2(32, 302),
		_2x3(33, 303),
		_3x3(34, 304),
		;

		private final int oldId;
		private final int newId;

		public static TableSize ofOld(ItemStack item) {
			return ofOld(ModelId.of(item));
		}

		public static TableSize ofOld(int modelId) {
			for (TableSize size : values())
				if (modelId == size.getOldId())
					return size;

			return null;
		}
	}

	@Path("balloons [radius]")
	void convert_balloons(@Arg("200") int radius) {
		int converted = 0;
		for (ItemFrame itemFrame : location().getNearbyEntitiesByType(ItemFrame.class, radius)) {
			final ItemStack item = itemFrame.getItem();
			if (isNullOrAir(item))
				continue;

			if (item.getType() != Material.STICK)
				continue;

			final BalloonSize size = BalloonSize.ofOld(item);
			if (size == null)
				continue;

			final BalloonColor color = BalloonColor.ofOld(item);

			itemFrame.setItem(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
				.modelId(size.getNewId())
				.dyeColor(color.getColor())
				.build());
			++converted;
		}

		send(PREFIX + "Converted " + converted + " balloons");
	}

	@Getter
	@AllArgsConstructor
	private enum BalloonSize {
		TALL(2, 15, 5),
		MEDIUM(16, 29, 4),
		SHORT(30, 43, 3),
		;

		private final int oldMin;
		private final int oldMax;
		private final int newId;

		public static BalloonSize ofOld(ItemStack item) {
			return ofOld(ModelId.of(item));
		}

		public static BalloonSize ofOld(int modelId) {
			for (BalloonSize size : values())
				if (modelId >= size.oldMin && modelId <= size.oldMax)
					return size;

			return null;
		}
	}

	@Getter
	@AllArgsConstructor
	private enum BalloonColor {
		RED("#fb5449"),
		ORANGE("#fd9336"),
		YELLOW("#ffea00"),
		LIME("#55ed57"),
		GREEN("#359c27"),
		CYAN("#00aa94"),
		LIGHT_BLUE("#55ffed"),
		BLUE("#5c6bd8"),
		PURPLE("#ac5cd8"),
		MAGENTA("#d85cd3"),
		PINK("#ff9ccf"),
		BROWN("#7a4d35"),
		BLACK("#1e1e1e"),
		WHITE("#ffffff"),
		;

		private final String hex;

		private Color getColor() {
			return ColorType.hexToBukkit(hex);
		}

		public static BalloonColor ofOld(ItemStack item) {
			return ofOld(ModelId.of(item));
		}

		public static BalloonColor ofOld(int modelId) {
			return BalloonColor.values()[(modelId - 2) % 14];
		}
	}

	// Potions
	@Path("potions [radius]")
	void convert_potions(@Arg("200") int radius) {
		int converted = 0;
		for (ItemFrame itemFrame : location().getNearbyEntitiesByType(ItemFrame.class, radius)) {
			final ItemStack item = itemFrame.getItem();
			if (isNullOrAir(item))
				continue;

			final PotionSize size = PotionSize.ofOld(item);
			if (size != null) {
				itemFrame.setItem(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
					.modelId(size.getNewId())
					.dyeColor(size.getLeatherColor(ModelId.of(item)))
					.build());
				++converted;
			}

			final PotionGroup group = PotionGroup.ofOld(item);
			if (group != null) {
				itemFrame.setItem(new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
					.modelId(group.getNewId())
					.dyeColor(group.getLeatherColor())
					.build());
				++converted;
			}
		}

		send(PREFIX + "Converted " + converted + " potions");
	}

	private static final List<String> rainbow = List.of("ff0000", "fd9336", "ffea00", "00ff00", "216118", "00ff99", "00fbff", "0095ff", "5d00ff", "aa00ff", "ff00ea");

	@Getter
	@AllArgsConstructor
	private enum PotionSize {
		SMALL_1(101, 111, 6, rainbow),
		SMALL_2(113, 123, 7, rainbow),
		SMALL_3(125, 135, 8, rainbow),
		MEDIUM_1(137, 147, 9, rainbow),
		MEDIUM_2(149, 159, 10, rainbow),
		MEDIUM_3(161, 171, 11, rainbow),
		MEDIUM_4(173, 183, 12, rainbow),
		MEDIUM_5(185, 195, 13, rainbow),
		BOTTLE(209, 211, 14, List.of("0095ff", "ff0000", "ff00ea")),
		TEAR(201, 203, 15, List.of("0095ff", "00ff00", "00ff99")),
		DONUT(205, 207, 16, List.of("0095ff", "ffea00", "aa00ff")),
		SKULL(213, 214, 17, List.of("216118", "240015")),
		_234(303, 305, 18, List.of("ff0000", "0095ff", "ffea00")),
		_678(307, 309, 21, List.of("ff0000", "0095ff", "00ff99")),
		_101112(311, 313, 26, List.of("ff0000", "0095ff", "ff00ea")),
		;

		private final int oldMin;
		private final int oldMax;
		private final int newId;
		private final List<String> colors;

		public static PotionSize ofOld(ItemStack item) {
			return ofOld(ModelId.of(item));
		}

		public static PotionSize ofOld(int modelId) {
			for (PotionSize size : values())
				if (modelId >= size.oldMin && modelId <= size.oldMax)
					return size;

			return null;
		}

		public Color getLeatherColor(int modelData) {
			return ColorType.hexToBukkit(getColors().get(modelData - getOldMin()));
		}
	}

	@Getter
	@AllArgsConstructor
	private enum PotionGroup {
		TINY_1(300, 27),
		TINY_2(301, 28),
		_13(314, 29),
		_14(315, 30),
		_15(316, 31),
		_16(317, 32),
		_17(318, 33),
		_18(319, 34),
		_19(320, 35),
		_20(321, 36),
		;

		private final int oldId;
		private final int newId;
		private final String hexColor = "ffffff";

		public static PotionGroup ofOld(ItemStack item) {
			return ofOld(ModelId.of(item));
		}

		public static PotionGroup ofOld(int modelId) {
			for (PotionGroup size : values())
				if (modelId == size.oldId)
					return size;

			return null;
		}

		public Color getLeatherColor() {
			return ColorType.hexToBukkit(hexColor);
		}
	}

}

