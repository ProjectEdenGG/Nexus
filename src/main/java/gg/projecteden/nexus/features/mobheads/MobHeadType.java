package gg.projecteden.nexus.features.mobheads;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mobheads.common.HeadConfig;
import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.features.mobheads.variants.AxolotlVariant;
import gg.projecteden.nexus.features.mobheads.variants.CatVariant;
import gg.projecteden.nexus.features.mobheads.variants.CreeperVariant;
import gg.projecteden.nexus.features.mobheads.variants.FoxVariant;
import gg.projecteden.nexus.features.mobheads.variants.HorseVariant;
import gg.projecteden.nexus.features.mobheads.variants.LlamaVariant;
import gg.projecteden.nexus.features.mobheads.variants.MooshroomVariant;
import gg.projecteden.nexus.features.mobheads.variants.PandaVariant;
import gg.projecteden.nexus.features.mobheads.variants.ParrotVariant;
import gg.projecteden.nexus.features.mobheads.variants.RabbitVariant;
import gg.projecteden.nexus.features.mobheads.variants.SheepVariant;
import gg.projecteden.nexus.features.mobheads.variants.SnowmanVariant;
import gg.projecteden.nexus.features.mobheads.variants.TraderLlamaVariant;
import gg.projecteden.nexus.features.mobheads.variants.TropicalFishVariant;
import gg.projecteden.nexus.features.mobheads.variants.VillagerVariant;
import gg.projecteden.nexus.features.mobheads.variants.ZombieVillagerVariant;
import gg.projecteden.nexus.models.mobheads.MobHeadChanceConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.Axolotl;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fox;
import org.bukkit.entity.Horse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Parrot;
import org.bukkit.entity.Rabbit;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Snowman;
import org.bukkit.entity.TraderLlama;
import org.bukkit.entity.Villager;
import org.bukkit.entity.ZombieVillager;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.RandomUtils.randomElement;
import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static gg.projecteden.api.common.utils.StringUtils.camelCase;

@Getter
public enum MobHeadType implements MobHead {
	@HeadConfig(headId = "41592", entityType = EntityType.AXOLOTL, variantClass = AxolotlVariant.class)
	AXOLOTL(
		entity -> AxolotlVariant.of((Axolotl) entity),
		(entity, type) -> ((Axolotl) entity).setVariant((Axolotl.Variant) type),
		() -> randomElement(Axolotl.Variant.class)
	),

	@HeadConfig(headId = "26033", entityType = EntityType.BAT)
	BAT,

	@HeadConfig(headId = "31260", entityType = EntityType.BEE)
	BEE,

	@HeadConfig(headId = "322", entityType = EntityType.BLAZE)
	BLAZE,

	@HeadConfig(headId = "14189", entityType = EntityType.CAT, variantClass = CatVariant.class)
	CAT(
		entity -> CatVariant.of((Cat) entity),
		(entity, type) -> ((Cat) entity).setCatType((Cat.Type) type),
		() -> randomElement(Cat.Type.class)
	),

	@HeadConfig(headId = "315", entityType = EntityType.CAVE_SPIDER)
	CAVE_SPIDER,

	@HeadConfig(headId = "336", entityType = EntityType.CHICKEN)
	CHICKEN,

	@HeadConfig(headId = "17898", entityType = EntityType.COD)
	COD,

	@HeadConfig(headId = "22866", entityType = EntityType.COW)
	COW,

	@HeadConfig(headType = Material.CREEPER_HEAD, entityType = EntityType.CREEPER, variantClass = CreeperVariant.class)
	CREEPER(
		entity -> CreeperVariant.of((Creeper) entity),
		(entity, type) -> ((Creeper) entity).setPowered((boolean) type),
		() -> randomElement(true, false)
	),

	@HeadConfig(headId = "16799", entityType = EntityType.DOLPHIN)
	DOLPHIN,

	@HeadConfig(headId = "18144", entityType = EntityType.DONKEY)
	DONKEY,

	@HeadConfig(headId = "15967", entityType = EntityType.DROWNED)
	DROWNED,

	@HeadConfig(headId = "25357", entityType = EntityType.ELDER_GUARDIAN)
	ELDER_GUARDIAN,

	@HeadConfig(headType = Material.DRAGON_HEAD, entityType = EntityType.ENDER_DRAGON)
	ENDER_DRAGON,

	@HeadConfig(headId = "318", entityType = EntityType.ENDERMAN)
	ENDERMAN,

	@HeadConfig(headId = "18427", entityType = EntityType.ENDERMITE)
	ENDERMITE,

	@HeadConfig(headId = "3862", entityType = EntityType.EVOKER)
	EVOKER,

	@HeadConfig(headId = "33923", entityType = EntityType.FOX, variantClass = FoxVariant.class)
	FOX(
		entity -> FoxVariant.of((Fox) entity),
		(entity, type) -> ((Fox) entity).setFoxType((Fox.Type) type),
		() -> randomElement(Fox.Type.class)
	),

	@HeadConfig(headId = "321", entityType = EntityType.GHAST)
	GHAST,

	@HeadConfig(headId = "40441", entityType = EntityType.GLOW_SQUID)
	GLOW_SQUID,

	@HeadConfig(headId = "25213", entityType = EntityType.GOAT)
	GOAT,

	@HeadConfig(headId = "3135", entityType = EntityType.GUARDIAN)
	GUARDIAN,

	@HeadConfig(headId = "34783", entityType = EntityType.HOGLIN)
	HOGLIN,

	@HeadConfig(headId = "1154", entityType = EntityType.HORSE, variantClass = HorseVariant.class)
	HORSE(
		entity -> HorseVariant.of((Horse) entity),
		(entity, type) -> ((Horse) entity).setColor((Horse.Color) type),
		() -> randomElement(Horse.Color.class)
	),

	@HeadConfig(headId = "37860", entityType = EntityType.HUSK)
	HUSK,

	@HeadConfig(headId = "35706", entityType = EntityType.ILLUSIONER)
	ILLUSIONER,

	@HeadConfig(headId = "33179", entityType = EntityType.IRON_GOLEM)
	IRON_GOLEM,

	@HeadConfig(headId = "25376", entityType = EntityType.LLAMA, variantClass = LlamaVariant.class)
	LLAMA(
		entity -> LlamaVariant.of((Llama) entity),
		(entity, type) -> ((Llama) entity).setColor((Llama.Color) type),
		() -> randomElement(Llama.Color.class)
	),

	@HeadConfig(headId = "323", entityType = EntityType.MAGMA_CUBE)
	MAGMA_CUBE,

	@HeadConfig(headId = "339", entityType = EntityType.MUSHROOM_COW, variantClass = MooshroomVariant.class)
	MUSHROOM_COW(
		entity -> MooshroomVariant.of((MushroomCow) entity),
		(entity, type) -> ((MushroomCow) entity).setVariant((MushroomCow.Variant) type),
		() -> randomElement(MushroomCow.Variant.class)
	),

	@HeadConfig(headId = "3918", entityType = EntityType.MULE)
	MULE,

	@HeadConfig(headId = "340", entityType = EntityType.OCELOT)
	OCELOT,

	@HeadConfig(headId = "6538", entityType = EntityType.PANDA, variantClass = PandaVariant.class)
	PANDA(
		entity -> PandaVariant.of((Panda) entity),
		(entity, type) -> ((Panda) entity).setMainGene((Panda.Gene) type),
		() -> randomElement(Panda.Gene.class)
	),

	@HeadConfig(headId = "34702", entityType = EntityType.PARROT, variantClass = ParrotVariant.class)
	PARROT(
		entity -> ParrotVariant.of((Parrot) entity),
		(entity, type) -> ((Parrot) entity).setVariant((Parrot.Variant) type),
		() -> randomElement(Parrot.Variant.class)
	),

	@HeadConfig(headId = "18091", entityType = EntityType.PHANTOM)
	PHANTOM,

	@HeadConfig(headId = "337", entityType = EntityType.PIG)
	PIG,

	@HeadConfig(headId = "37258", entityType = EntityType.PIGLIN)
	PIGLIN,

	@HeadConfig(headId = "38372", entityType = EntityType.PIGLIN_BRUTE)
	PIGLIN_BRUTE,

	@HeadConfig(headId = "25149", entityType = EntityType.PILLAGER)
	PILLAGER,

	@HeadConfig(entityType = EntityType.PLAYER)
	PLAYER,

	@HeadConfig(headId = "18379", entityType = EntityType.POLAR_BEAR)
	POLAR_BEAR,

	@HeadConfig(headId = "17900", entityType = EntityType.PUFFERFISH)
	PUFFERFISH,

	@HeadConfig(headId = "3933", entityType = EntityType.RABBIT, variantClass = RabbitVariant.class)
	RABBIT(
		entity -> RabbitVariant.of((Rabbit) entity),
		(entity, type) -> ((Rabbit) entity).setRabbitType((Rabbit.Type) type),
		() -> randomElement(Rabbit.Type.class)
	),

	@HeadConfig(headId = "28196", entityType = EntityType.RAVAGER)
	RAVAGER,

	@HeadConfig(headId = "31623", entityType = EntityType.SALMON)
	SALMON,

	@HeadConfig(headId = "334", entityType = EntityType.SHEEP, variantClass = SheepVariant.class)
	SHEEP(
		entity -> SheepVariant.of((Sheep) entity),
		(entity, type) -> ((Sheep) entity).setColor((DyeColor) type),
		() -> randomElement(DyeColor.class)
	),

	@HeadConfig(headId = "38317", entityType = EntityType.SHULKER)
	SHULKER,

	@HeadConfig(headId = "3936", entityType = EntityType.SILVERFISH)
	SILVERFISH,

	@HeadConfig(headType = Material.SKELETON_SKULL, entityType = EntityType.SKELETON)
	SKELETON,

	@HeadConfig(headId = "6013", entityType = EntityType.SKELETON_HORSE)
	SKELETON_HORSE,

	@HeadConfig(headId = "17992", entityType = EntityType.SLIME)
	SLIME,

	@HeadConfig(headId = "30000", entityType = EntityType.SNOWMAN, variantClass = SnowmanVariant.class)
	SNOWMAN(
		entity -> SnowmanVariant.of((Snowman) entity),
		(entity, type) -> ((Snowman) entity).setDerp((boolean) type),
		() -> randomElement(true, false)
	),

	@HeadConfig(headId = "317", entityType = EntityType.SPIDER)
	SPIDER,

	@HeadConfig(headId = "12237", entityType = EntityType.SQUID)
	SQUID,

	@HeadConfig(headId = "22401", entityType = EntityType.STRAY)
	STRAY,

	@HeadConfig(headId = "35431", entityType = EntityType.STRIDER)
	STRIDER,

	@HeadConfig(headId = "26960", entityType = EntityType.TRADER_LLAMA, variantClass = TraderLlamaVariant.class)
	TRADER_LLAMA(
		entity -> TraderLlamaVariant.of((TraderLlama) entity),
		(entity, type) -> ((TraderLlama) entity).setColor((TraderLlama.Color) type),
		() -> randomElement(TraderLlama.Color.class)
	),

	@HeadConfig(headId = "30233", entityType = EntityType.TROPICAL_FISH, variantClass = TropicalFishVariant.class)
	TROPICAL_FISH(
		entity -> TropicalFishVariant.random(),
		(entity, type) -> {},
		() -> null
	),

	@HeadConfig(headId = "17929", entityType = EntityType.TURTLE)
	TURTLE,

	@HeadConfig(headId = "3080", entityType = EntityType.VEX)
	VEX,

	@HeadConfig(headId = "12199", entityType = EntityType.VILLAGER, variantClass = VillagerVariant.class)
	VILLAGER(
		entity -> VillagerVariant.of((Villager) entity),
		(entity, type) -> ((Villager) entity).setVillagerType((Villager.Type) type),
		() -> randomElement(Villager.Type.class)
	),

	@HeadConfig(headId = "28323", entityType = EntityType.VINDICATOR)
	VINDICATOR,

	@HeadConfig(headId = "25676", entityType = EntityType.WANDERING_TRADER)
	WANDERING_TRADER,

	@HeadConfig(headId = "35861", entityType = EntityType.WITCH)
	WITCH,

	@HeadConfig(headId = "22399", entityType = EntityType.WITHER)
	WITHER,

	@HeadConfig(headType = Material.WITHER_SKELETON_SKULL, entityType = EntityType.WITHER_SKELETON)
	WITHER_SKELETON,

	@HeadConfig(headId = "38471", entityType = EntityType.WOLF)
	WOLF,

	@HeadConfig(headId = "35932", entityType = EntityType.ZOGLIN)
	ZOGLIN,

	@HeadConfig(headType = Material.ZOMBIE_HEAD, entityType = EntityType.ZOMBIE)
	ZOMBIE,

	@HeadConfig(headId = "33747", entityType = EntityType.ZOMBIE_HORSE)
	ZOMBIE_HORSE,

	@HeadConfig(headId = "27600", entityType = EntityType.ZOMBIE_VILLAGER, variantClass = ZombieVillagerVariant.class)
	ZOMBIE_VILLAGER(
		entity -> ZombieVillagerVariant.of((ZombieVillager) entity),
		(entity, type) -> ((ZombieVillager) entity).setVillagerType((Villager.Type) type),
		() -> randomElement(Villager.Type.class)
	),

	@HeadConfig(headId = "37253", entityType = EntityType.ZOMBIFIED_PIGLIN)
	ZOMBIFIED_PIGLIN,
	;

	private final String headId;
	private final Material headType;
	private final EntityType entityType;
	private final Class<? extends MobHeadVariant> variantClass;
	private final Function<Entity, MobHeadVariant> variantConverter;
	private final BiConsumer<Entity, Object> variantSetter;
	private final Supplier<?> randomVariant;
	private final ItemStack genericSkull;

	MobHeadType() {
		this(null, null, null);
	}

	MobHeadType(
		Function<Entity, MobHeadVariant> variantConverter,
		BiConsumer<Entity, Object> variantSetter,
		Supplier<?> randomVariant
	) {
		this.headId = getHeadConfig().headId();
		this.headType = getHeadConfig().headType();
		this.entityType = getHeadConfig().entityType();
		this.variantClass = getHeadConfig().variantClass();
		this.variantConverter = variantConverter;
		this.variantSetter = variantSetter;
		this.randomVariant = randomVariant;

		if (isNotNullOrEmpty(headId) || isNotNullOrAir(headType)) {
			ItemStack skull = isNullOrAir(headType) ? Nexus.getHeadAPI().getItemHead(headId) : new ItemStack(headType);
			this.genericSkull = new ItemBuilder(skull).name("&e" + getDisplayName() + " Head").lore("&3Mob Head").build();
		} else {
			if (!"PLAYER".equals(name()))
				Nexus.warn("No generic skull for MobType " + camelCase(this) + " defined");
			this.genericSkull = null;
		}
	}

	private static final List<MobHead> allMobHeads = new ArrayList<>();

	public static List<MobHead> getAllMobHeads() {
		if (allMobHeads.isEmpty())
			for (MobHeadType type : MobHeadType.values())
				if (type.hasVariants())
					allMobHeads.addAll(type.getVariants());
				else
					allMobHeads.add(type);

		return allMobHeads;
	}

	public double getChance() {
		return new MobHeadChanceConfigService().get0().getChances().get(this);
	}

	@Nullable
	public static MobHeadType of(EntityType from) {
		return Arrays.stream(values()).filter(entry -> from.equals(entry.getEntityType())).findFirst().orElse(null);
	}

	@Override
	public @NotNull MobHeadType getType() {
		return this;
	}

	public @Nullable ItemStack getSkull() {
		if (genericSkull == null)
			return null;
		return genericSkull.clone();
	}

	public ItemStack getSkull(MobHeadVariant variant) {
		return variant == null ? genericSkull : variant.getSkull();
	}

	public boolean hasVariants() {
		return getVariantClass() != null && getVariantClass() != MobHeadVariant.class;
	}

	public List<MobHeadVariant> getVariants() {
		if (!hasVariants())
			return Collections.emptyList();

		return List.of(getVariantClass().getEnumConstants());
	}

	@Nullable
	public MobHeadVariant getVariant(Entity entity) {
		if (variantConverter == null)
			return null;

		return variantConverter.apply(entity);
	}

	private static final List<EntityType> EXCLUDED_TYPES = List.of(EntityType.ARMOR_STAND, EntityType.GIANT);

	public static List<EntityType> getExpectedTypes() {
		List<EntityType> expectedTypes = new ArrayList<>(Arrays.asList(EntityType.values()));
		expectedTypes.removeIf(entityType -> {
			if (entityType.getEntityClass() == null)
				return true;
			if (!LivingEntity.class.isAssignableFrom(entityType.getEntityClass()))
				return true;
			if (EXCLUDED_TYPES.contains(entityType))
				return true;

			return false;
		});

		return expectedTypes;
	}

	public static List<EntityType> getMissingTypes() {
		final List<EntityType> expectedTypes = getExpectedTypes();

		for (MobHeadType mobHeadType : MobHeadType.values())
			expectedTypes.remove(mobHeadType.getEntityType());

		return expectedTypes;
	}

	@SneakyThrows
	public Field getField() {
		return getClass().getField(name());
	}

	public HeadConfig getHeadConfig() {
		return getField().getAnnotation(HeadConfig.class);
	}

}
