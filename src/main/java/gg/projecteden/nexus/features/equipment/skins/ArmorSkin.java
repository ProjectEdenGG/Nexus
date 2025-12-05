package gg.projecteden.nexus.features.equipment.skins;

import de.tr7zw.nbtapi.NBT;
import de.tr7zw.nbtapi.handler.NBTHandlers;
import de.tr7zw.nbtapi.iface.ReadWriteNBT;
import gg.projecteden.nexus.features.resourcepack.models.CustomArmorType;
import gg.projecteden.nexus.utils.ItemBuilder;
import gg.projecteden.nexus.utils.MaterialTag;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.ApiStatus.Internal;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public enum ArmorSkin implements EquipmentSkinType {
	@Internal
	DEFAULT,
	@Internal
	INVISIBLE,
	ADAMANTITE,
	@Templateable
	AMETHYST,
	BERSERKER,
	@Templateable
	@HelmetCostume("hat/armor/bone")
	BONE,
	BROWN_BERSERK,
	@Templateable
	CHERRY,
	@Templateable
	COBALT,
	DRUID,
	FISHING,
	@Templateable
	HELLFIRE,
	JARL,
	@Templateable
	@HelmetCostume("hat/misc/hard_hat")
	MECHANICAL,
	@Templateable
	MYTHRIL,
	SCULK,
	@Templateable
	@HelmetCostume("hat/armor/shadow")
	SHADOW,
	TANK,
	THOR,
	@Templateable
	VINES,
	WARDEN,
	WITHER,
	WOLF,
	WIZARD,
	@Templateable
	EIGHT_BIT {
		@Override
		public String getTitle() {
			return "8bit";
		}
	},
	;

	public String getTitle() {
		return StringUtils.camelCase(name());
	}

	public String getBaseModel() {
		return "skins/armor/" + this.getTitle().toLowerCase().replace(' ', '_');
	}

	public CustomArmorType getCustomArmorType() {
		if (this == DEFAULT)
			return null;
		return CustomArmorType.valueOf(this.name());
	}

	public ItemBuilder apply(ItemBuilder builder) {
		if (!applies(builder))
			return builder;

		if (this == DEFAULT) {
			builder.removeModel();
			applyEquippableComponent(builder, this);
			return builder;
		}

		String armorPieceType = builder.material().name().toLowerCase().substring(builder.material().name().lastIndexOf('_') + 1);
		String model = getBaseModel() + "/" + armorPieceType;

		builder.model(model);
		applyEquippableComponent(builder, this);
		return builder;
	}

	@Override
	public ItemStack apply(ItemStack item) {
		if (!applies(item))
			return item;

		return apply(new ItemBuilder(item)).build();
	}

	public static void applyEquippableComponent(ItemBuilder builder, ArmorSkin armorSkin) {
		builder.components(nbt -> {
			if (armorSkin == DEFAULT || armorSkin == null) {
				nbt.removeKey("minecraft:equippable");

				if (nbt.hasTag("minecraft:custom_data")) {
					ReadWriteNBT custom_data = nbt.getCompound("minecraft:custom_data");
					if (custom_data.hasTag("old_trim")) {
						ReadWriteNBT old_trim = custom_data.getCompound("old_trim");
						nbt.set("minecraft:trim", old_trim, NBTHandlers.STORE_READWRITE_TAG);
						custom_data.removeKey("old_trim");
						nbt.set("minecraft:custom_data", custom_data, NBTHandlers.STORE_READWRITE_TAG);
					}
				}
			}
			else {
				ReadWriteNBT readWriteNBT = NBT.createNBTObject();
				readWriteNBT.setString("slot", getSlot(builder).name().toLowerCase());
				readWriteNBT.setString("asset_id", "minecraft:" + armorSkin.getTitle().toLowerCase().replace(' ', '_'));
				nbt.set("minecraft:equippable", readWriteNBT, NBTHandlers.STORE_READWRITE_TAG);

				if (nbt.hasTag("minecraft:trim")) {
					ReadWriteNBT oldTrim = nbt.getCompound("minecraft:trim");

					ReadWriteNBT customData = NBT.createNBTObject();
					if (nbt.hasTag("minecraft:custom_data"))
						customData = nbt.getCompound("minecraft:custom_data");

					customData.set("old_trim", oldTrim, NBTHandlers.STORE_READWRITE_TAG);
					nbt.set("minecraft:custom_data", customData, NBTHandlers.STORE_READWRITE_TAG);
					nbt.removeKey("minecraft:trim");
				}
			}
		});
	}

	public static EquipmentSlot getSlot(ItemBuilder builder) {
		if (MaterialTag.ALL_HELMETS.isTagged(builder.material()))
			return EquipmentSlot.HEAD;
		if (MaterialTag.ALL_CHESTPLATES.isTagged(builder.material()))
			return EquipmentSlot.CHEST;
		if (MaterialTag.ALL_LEGGINGS.isTagged(builder.material()))
			return EquipmentSlot.LEGS;
		return EquipmentSlot.FEET;
	}

	public static List<EquipmentSkinType> getTemplateable() {
		return Arrays.stream(values())
			.filter(armorSkin -> !armorSkin.getField().isAnnotationPresent(Internal.class))
			.filter(armorSkin -> armorSkin.getField().isAnnotationPresent(Templateable.class))
			.map(armorSkin -> (EquipmentSkinType) armorSkin)
			.toList();
	}

	@Override
	public boolean applies(ItemStack item) {
		return MaterialTag.ARMOR.isTagged(item);
	}

	public boolean applies(ItemBuilder builder) {
		return MaterialTag.ARMOR.isTagged(builder.build());
	}

	@Override
	public ItemStack getBig(ItemStack item) {
		if (item == null)
			return null;

		if (this == DEFAULT)
			return new ItemBuilder(Material.LEATHER_HORSE_ARMOR)
				.model("skins/armor/default/big/" + item.getType().name().toLowerCase())
				.hideTooltip()
				.dyeColor(new ItemBuilder(item).dyeColor())
				.build();

		ItemBuilder builder = new ItemBuilder(item);
		String toolType = builder.material().name().toLowerCase().substring(builder.material().name().lastIndexOf('_') + 1);
		String model = getBaseModel() + "/big/" + toolType;

		if (MaterialTag.ARMOR_LEATHER.isTagged(item))
			builder.dyeColor(new ItemBuilder(item).dyeColor());

		return builder.model(model).hideTooltip().build();
	}

	@Override
	public ItemStack getTemplate() {
		return new ItemBuilder(Material.PAPER)
			.name("&e" + StringUtils.camelCase(name()) + " Armor Skin")
			.model(getBaseModel() + "/template")
			.build();
	}

	public String getHelmetCostume() {
		try {
			return getField().getAnnotation(HelmetCostume.class).value();
		} catch (Exception ex) {
			return null;
		}
	}

	public static ArmorSkin of(ItemStack stack) {
		if (stack == null)
			return null;

		String model = ItemBuilder.Model.of(stack);
		if (model == null)
			return null;

		String baseModel = model.toLowerCase().substring(0, model.lastIndexOf('/'));

		for (ArmorSkin skin : values())
			if (skin.getBaseModel().equals(baseModel))
				return skin;

		return null;
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface HelmetCostume {
		String value();
	}

	@Target(ElementType.FIELD)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface Templateable {}

}
