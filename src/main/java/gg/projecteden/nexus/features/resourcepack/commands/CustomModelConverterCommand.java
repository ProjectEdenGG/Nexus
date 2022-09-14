package gg.projecteden.nexus.features.resourcepack.commands;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.clientside.models.ClientSideArmorStand;
import gg.projecteden.nexus.features.clientside.models.ClientSideItemFrame;
import gg.projecteden.nexus.features.clientside.models.IClientSideEntity.ClientSideEntityType;
import gg.projecteden.nexus.features.resourcepack.ResourcePack;
import gg.projecteden.nexus.features.resourcepack.decoration.DecorationType;
import gg.projecteden.nexus.features.resourcepack.decoration.types.Dyeable;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.clientside.ClientSideConfig;
import gg.projecteden.nexus.models.clientside.ClientSideConfigService;
import gg.projecteden.nexus.models.custommodels.CustomModelConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.ItemBuilder.ModelId;
import gg.projecteden.nexus.utils.Nullables;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@NoArgsConstructor
@Permission(Group.ADMIN)
public class CustomModelConverterCommand extends CustomCommand implements Listener {
	private final ClientSideConfigService configService = new ClientSideConfigService();
	private final ClientSideConfig config = configService.get0();

	public CustomModelConverterCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		Tasks.repeat(0, TickTime.MINUTE, () -> {
			for (World world : Bukkit.getWorlds()) {
				for (Entity entity : world.getEntities()) {
					if (!entity.isValid())
						continue;

					switch (entity.getType()) {
						case ITEM_FRAME -> ToDyeable.updateItemFrame((ItemFrame) entity);
						case ARMOR_STAND -> ToDyeable.updateArmorStand((ArmorStand) entity);
					}
				}
			}
		});
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

	@Path("toDyeable clientside")
	void convert_toDyeableClientside() {
		int converted = 0;
		for (var clientSideEntitiy : ClientSideConfig.getAllEntities()) {
			switch (clientSideEntitiy.getType()) {
				case ITEM_FRAME -> {
					ClientSideItemFrame itemFrame = ((ClientSideItemFrame) clientSideEntitiy);
					final ToDyeable toDyeable = ToDyeable.ofOld(itemFrame.content());
					if (toDyeable != null) {
						ClientSideConfig.delete(clientSideEntitiy);

						ItemFrame _itemframe = (ItemFrame) clientSideEntitiy.spawn();
						_itemframe.setItem(toDyeable.getNewItem());

						ClientSideConfig.createEntity(ClientSideEntityType.createFrom(_itemframe));
						_itemframe.remove();
						converted++;
					}
				}

				case ARMOR_STAND -> {
					ClientSideArmorStand armorStand = ((ClientSideArmorStand) clientSideEntitiy);
					Map<EquipmentSlot, ItemStack> equipment = armorStand.equipment();
					boolean respawn = false;
					for (EquipmentSlot slot : equipment.keySet()) {
						final ToDyeable toDyeable = ToDyeable.ofOld(equipment.get(slot));
						if (toDyeable != null) {
							respawn = true;
							equipment.put(slot, toDyeable.getNewItem());
							converted++;
						}
					}

					if (respawn) {
						ClientSideConfig.delete(clientSideEntitiy);

						ArmorStand _armorStand = (ArmorStand) clientSideEntitiy.spawn();
						equipment.forEach(_armorStand::setItem);

						ClientSideConfig.createEntity(ClientSideEntityType.createFrom(_armorStand));
						_armorStand.remove();
					}
				}
			}
		}
		ClientSideConfig.save();

		send(PREFIX + "Converted " + converted + " clientside items to dyeable");
	}

	@Path("toDyeable <radius>")
	void convert_toDyeable(@Arg("200") int radius) {
		int converted = 0;
		for (ItemFrame itemFrame : location().getNearbyEntitiesByType(ItemFrame.class, radius)) {
			converted += ToDyeable.updateItemFrame(itemFrame);
		}

		for (ArmorStand armorStand : location().getNearbyEntitiesByType(ArmorStand.class, radius)) {
			converted += ToDyeable.updateArmorStand(armorStand);
		}

		send(PREFIX + "Converted " + converted + " items to dyeable");
	}


	@AllArgsConstructor
	private enum ToDyeable {
		EGGNOG(6042, DecorationType.PUNCHBOWL_EGGNOG),
		SIDE_CRANBERRIES(6043, DecorationType.SIDE_SAUCE_CRANBERRIES),
		CAKE_RED_VELVET(6049, DecorationType.CAKE_BATTER_RED_VELVET),
		CAKE_VANILLA(6050, DecorationType.CAKE_BATTER_VANILLA),
		CAKE_CHOCOLATE(6053, DecorationType.CAKE_BATTER_CHOCOLATE),
		PIE_PECAN(6060, DecorationType.PIE_ROUGH_PECAN),
		PIE_CHOCO(6058, DecorationType.PIE_SMOOTH_CHOCOLATE),
		PIE_LEMON(6059, DecorationType.PIE_SMOOTH_LEMON),
		PIE_PUMPKIN(6061, DecorationType.PIE_SMOOTH_PUMPKIN),
		PIE_APPLE(6055, DecorationType.PIE_LATTICED_APPLE),
		PIE_BLUEBERRY(6056, DecorationType.PIE_LATTICED_BLUEBERRY),
		PIE_CHERRY(6057, DecorationType.PIE_LATTICED_CHERRY),
		;


		private final int oldId;
		private final DecorationType type;

		public Color getLeatherColor() {
			if (type.getConfig() instanceof Dyeable dyeable)
				return dyeable.getColor();

			return Color.RED;
		}

		public ItemStack getNewItem() {
			return new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
				.modelId(type.getConfig().getModelId())
				.dyeColor(getLeatherColor())
				.build();
		}

		public static ToDyeable ofOld(ItemStack itemStack) {
			int modelId = ModelId.of(itemStack);
			for (ToDyeable dyeable : values()) {
				if (modelId == dyeable.oldId)
					return dyeable;
			}
			return null;
		}

		public static int updateItemFrame(ItemFrame itemFrame) {
			if (itemFrame == null)
				return 0;

			ItemStack itemStack = itemFrame.getItem();
			if (Nullables.isNullOrAir(itemStack))
				return 0;

			final ToDyeable toDyeable = ofOld(itemStack);
			if (toDyeable == null)
				return 0;

			itemFrame.setItem(toDyeable.getNewItem());

			return 1;
		}

		public static int updateArmorStand(ArmorStand armorStand) {
			int converted = 0;
			if (armorStand == null)
				return converted;

			for (EquipmentSlot slot : EquipmentSlot.values()) {
				final ItemStack itemStack = armorStand.getItem(slot);
				if (Nullables.isNullOrAir(itemStack))
					continue;

				final ToDyeable toDyeable = ofOld(itemStack);
				if (toDyeable == null)
					continue;

				armorStand.setItem(slot, toDyeable.getNewItem());
				converted++;
			}

			return converted;
		}
	}

}

