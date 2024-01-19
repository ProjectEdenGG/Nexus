package gg.projecteden.nexus.features.mobheads;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.mobheads.common.HeadConfig;
import gg.projecteden.nexus.features.mobheads.common.MobHead;
import gg.projecteden.nexus.features.mobheads.common.MobHeadVariant;
import gg.projecteden.nexus.features.mobheads.variants.*;
import gg.projecteden.nexus.models.mobheads.MobHeadChanceConfigService;
import gg.projecteden.nexus.utils.ItemBuilder;
import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.entity.*;
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

import static gg.projecteden.api.common.utils.Nullables.isNotNullOrEmpty;
import static gg.projecteden.api.common.utils.StringUtils.camelCase;
import static gg.projecteden.nexus.utils.Nullables.isNotNullOrAir;
import static gg.projecteden.nexus.utils.Nullables.isNullOrAir;
import static gg.projecteden.nexus.utils.RandomUtils.randomElement;

@Getter
public enum MobHeadType implements MobHead {
	@HeadConfig(headId = "51477")
	ALLAY,

	@HeadConfig(headId = "41592", variantClass = AxolotlVariant.class)
	AXOLOTL(
		entity -> AxolotlVariant.of((Axolotl) entity),
		(entity, type) -> ((Axolotl) entity).setVariant((Axolotl.Variant) type),
		() -> randomElement(Axolotl.Variant.class)
	),

	@HeadConfig(headId = "26033")
	BAT,

	@HeadConfig(headId = "31260")
	BEE,

	@HeadConfig(headId = "322")
	BLAZE,

	@HeadConfig(headId = "58939")
	CAMEL,

	@HeadConfig(headId = "14189", variantClass = CatVariant.class)
	CAT(
		entity -> CatVariant.of((Cat) entity),
		(entity, type) -> ((Cat) entity).setCatType((Cat.Type) type),
		() -> randomElement(Cat.Type.class)
	),

	@HeadConfig(headId = "315")
	CAVE_SPIDER,

	@HeadConfig(headId = "336")
	CHICKEN,

	@HeadConfig(headId = "17898")
	COD,

	@HeadConfig(headId = "22866")
	COW,

	@HeadConfig(headType = Material.CREEPER_HEAD, variantClass = CreeperVariant.class)
	CREEPER(
		entity -> CreeperVariant.of((Creeper) entity),
		(entity, type) -> ((Creeper) entity).setPowered((boolean) type),
		() -> randomElement(true, false)
	),

	@HeadConfig(headId = "16799")
	DOLPHIN,

	@HeadConfig(headId = "18144")
	DONKEY,

	@HeadConfig(headId = "15967")
	DROWNED,

	@HeadConfig(headId = "25357")
	ELDER_GUARDIAN,

	@HeadConfig(headType = Material.DRAGON_HEAD)
	ENDER_DRAGON,

	@HeadConfig(headId = "318")
	ENDERMAN,

	@HeadConfig(headId = "18427")
	ENDERMITE,

	@HeadConfig(headId = "3862")
	EVOKER,

	@HeadConfig(headId = "33923", variantClass = FoxVariant.class)
	FOX(
		entity -> FoxVariant.of((Fox) entity),
		(entity, type) -> ((Fox) entity).setFoxType((Fox.Type) type),
		() -> randomElement(Fox.Type.class)
	),

	@HeadConfig(headId = "51343", variantClass = FrogVariant.class)
	FROG(
		entity -> FrogVariant.of((Frog) entity),
		(entity, type) -> ((Frog) entity).setVariant((Frog.Variant) type),
		() -> randomElement(Frog.Variant.class)
	),

	@HeadConfig(headId = "321")
	GHAST,

	@HeadConfig(headId = "40441")
	GLOW_SQUID,

	@HeadConfig(headId = "25213")
	GOAT,

	@HeadConfig(headId = "3135")
	GUARDIAN,

	@HeadConfig(headId = "34783")
	HOGLIN,

	@HeadConfig(headId = "1154", variantClass = HorseVariant.class)
	HORSE(
		entity -> HorseVariant.of((Horse) entity),
		(entity, type) -> ((Horse) entity).setColor((Horse.Color) type),
		() -> randomElement(Horse.Color.class)
	),

	@HeadConfig(headId = "37860")
	HUSK,

	@HeadConfig(headId = "35706")
	ILLUSIONER,

	@HeadConfig(headId = "33179")
	IRON_GOLEM,

	@HeadConfig(headId = "25376", variantClass = LlamaVariant.class)
	LLAMA(
		entity -> LlamaVariant.of((Llama) entity),
		(entity, type) -> ((Llama) entity).setColor((Llama.Color) type),
		() -> randomElement(Llama.Color.class)
	),

	@HeadConfig(headId = "323")
	MAGMA_CUBE,

	@HeadConfig(headId = "339", variantClass = MooshroomVariant.class)
	MUSHROOM_COW(
		entity -> MooshroomVariant.of((MushroomCow) entity),
		(entity, type) -> ((MushroomCow) entity).setVariant((MushroomCow.Variant) type),
		() -> randomElement(MushroomCow.Variant.class)
	),

	@HeadConfig(headId = "3918")
	MULE,

	@HeadConfig(headId = "340")
	OCELOT,

	@HeadConfig(headId = "6538", variantClass = PandaVariant.class)
	PANDA(
		entity -> PandaVariant.of((Panda) entity),
		(entity, type) -> ((Panda) entity).setMainGene((Panda.Gene) type),
		() -> randomElement(Panda.Gene.class)
	),

	@HeadConfig(headId = "34702", variantClass = ParrotVariant.class)
	PARROT(
		entity -> ParrotVariant.of((Parrot) entity),
		(entity, type) -> ((Parrot) entity).setVariant((Parrot.Variant) type),
		() -> randomElement(Parrot.Variant.class)
	),

	@HeadConfig(headId = "18091")
	PHANTOM,

	@HeadConfig(headId = "337")
	PIG,

	@HeadConfig(headId = "37258")
	PIGLIN,

	@HeadConfig(headId = "38372")
	PIGLIN_BRUTE,

	@HeadConfig(headId = "25149")
	PILLAGER,

	@HeadConfig
	PLAYER,

	@HeadConfig(headId = "18379")
	POLAR_BEAR,

	@HeadConfig(headId = "17900")
	PUFFERFISH,

	@HeadConfig(headId = "3933", variantClass = RabbitVariant.class)
	RABBIT(
		entity -> RabbitVariant.of((Rabbit) entity),
		(entity, type) -> ((Rabbit) entity).setRabbitType((Rabbit.Type) type),
		() -> randomElement(Rabbit.Type.class)
	),

	@HeadConfig(headId = "28196")
	RAVAGER,

	@HeadConfig(headId = "31623")
	SALMON,

	@HeadConfig(headId = "334", variantClass = SheepVariant.class)
	SHEEP(
		entity -> SheepVariant.of((Sheep) entity),
		(entity, type) -> ((Sheep) entity).setColor((DyeColor) type),
		() -> randomElement(DyeColor.class)
	),

	@HeadConfig(headId = "38317")
	SHULKER,

	@HeadConfig(headId = "3936")
	SILVERFISH,

	@HeadConfig(headType = Material.SKELETON_SKULL)
	SKELETON,

	@HeadConfig(headId = "6013")
	SKELETON_HORSE,

	@HeadConfig(headId = "17992")
	SLIME,

	@HeadConfig(headId = "60630")
	SNIFFER,

	@HeadConfig(headId = "30000", variantClass = SnowmanVariant.class)
	SNOWMAN(
		entity -> SnowmanVariant.of((Snowman) entity),
		(entity, type) -> ((Snowman) entity).setDerp((boolean) type),
		() -> randomElement(true, false)
	),

	@HeadConfig(headId = "317")
	SPIDER,

	@HeadConfig(headId = "12237")
	SQUID,

	@HeadConfig(headId = "22401")
	STRAY,

	@HeadConfig(headId = "35431")
	STRIDER,

	@HeadConfig(headId = "50682")
	TADPOLE,

	@HeadConfig(headId = "26960", variantClass = TraderLlamaVariant.class)
	TRADER_LLAMA(
		entity -> TraderLlamaVariant.of((TraderLlama) entity),
		(entity, type) -> ((TraderLlama) entity).setColor((TraderLlama.Color) type),
		() -> randomElement(TraderLlama.Color.class)
	),

	@HeadConfig(headId = "30233", variantClass = TropicalFishVariant.class)
	TROPICAL_FISH(
		entity -> TropicalFishVariant.random(),
		(entity, type) -> {},
		() -> null
	),

	@HeadConfig(headId = "17929")
	TURTLE,

	@HeadConfig(headId = "3080")
	VEX,

	@HeadConfig(headId = "12199", variantClass = VillagerVariant.class)
	VILLAGER(
		entity -> VillagerVariant.of((Villager) entity),
		(entity, type) -> ((Villager) entity).setVillagerType((Villager.Type) type),
		() -> randomElement(Villager.Type.class)
	),

	@HeadConfig(headId = "28323")
	VINDICATOR,

	@HeadConfig(headId = "25676")
	WANDERING_TRADER,

	@HeadConfig(headId = "52282")
	WARDEN,

	@HeadConfig(headId = "35861")
	WITCH,

	@HeadConfig(headId = "22399")
	WITHER,

	@HeadConfig(headType = Material.WITHER_SKELETON_SKULL)
	WITHER_SKELETON,

	@HeadConfig(headId = "38471")
	WOLF,

	@HeadConfig(headId = "35932")
	ZOGLIN,

	@HeadConfig(headType = Material.ZOMBIE_HEAD)
	ZOMBIE,

	@HeadConfig(headId = "33747")
	ZOMBIE_HORSE,

	@HeadConfig(headId = "27600", variantClass = ZombieVillagerVariant.class)
	ZOMBIE_VILLAGER(
		entity -> ZombieVillagerVariant.of((ZombieVillager) entity),
		(entity, type) -> ((ZombieVillager) entity).setVillagerType((Villager.Type) type),
		() -> randomElement(Villager.Type.class)
	),

	@HeadConfig(headId = "37253")
	ZOMBIFIED_PIGLIN,
	;

	private final String headId;
	private final Material headType;
	private final EntityType entityType;
	private final Class<? extends MobHeadVariant> variantClass;
	private final Function<Entity, MobHeadVariant> variantConverter;
	private final BiConsumer<Entity, Object> variantSetter;
	private final Supplier<?> randomVariant;
	private final ItemStack baseSkull;
	private final ItemStack namedSkull;

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
		this.entityType = EntityType.valueOf(name());
		this.variantClass = getHeadConfig().variantClass();
		this.variantConverter = variantConverter;
		this.variantSetter = variantSetter;
		this.randomVariant = randomVariant;

		if (isNotNullOrEmpty(headId) || isNotNullOrAir(headType)) {
			this.baseSkull = isNullOrAir(headType) ? Nexus.getHeadAPI().getItemHead(headId) : new ItemStack(headType);
			this.namedSkull = new ItemBuilder(this.baseSkull).name("&e" + getDisplayName() + " Head").lore("&3Mob Head").build();
		} else {
			if (!"PLAYER".equals(name()))
				Nexus.warn("No generic skull for MobType " + camelCase(this) + " defined");
			this.baseSkull = null;
			this.namedSkull = null;
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
		return new MobHeadChanceConfigService().get0().getChances().getOrDefault(this, 0d);
	}

	@Nullable
	public static MobHeadType of(EntityType from) {
		return Arrays.stream(values()).filter(entry -> from == entry.getEntityType()).findFirst().orElse(null);
	}

	@Nullable
	public static MobHeadType of(String id) {
		for (MobHeadType type : values()) {
			if (type.hasVariants()) {
				for (MobHeadVariant variant : type.getVariants()) {
					if (variant == null || variant.getHeadId() == null)
						continue;

					if (variant.getHeadId().equalsIgnoreCase(id))
						return type;
				}
			} else if (type.getHeadId().equalsIgnoreCase(id)) {
				return type;
			}
		}

		return null;
	}

	@Override
	public @NotNull MobHeadType getType() {
		return this;
	}


	public @Nullable ItemStack getNamedSkull() {
		if (namedSkull == null)
			return null;
		return namedSkull.clone();
	}

	public @Nullable ItemStack getBaseSkull() {
		if (baseSkull == null)
			return null;
		return baseSkull.clone();
	}

	public ItemStack getSkull(MobHeadVariant variant) {
		return variant == null ? namedSkull : variant.getNamedSkull();
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

	private static final List<EntityType> EXCLUDED_TYPES = List.of(EntityType.ARMOR_STAND, EntityType.GIANT, EntityType.NPC);

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
