package gg.projecteden.nexus.utils;

import de.tr7zw.nbtapi.NBTItem;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.customenchants.CustomEnchants;
import gg.projecteden.nexus.features.customenchants.enchants.SoulboundEnchant;
import gg.projecteden.nexus.features.itemtags.Condition;
import gg.projecteden.nexus.features.itemtags.Rarity;
import gg.projecteden.nexus.features.recipes.functionals.Backpacks;
import gg.projecteden.nexus.features.resourcepack.models.CustomModel;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.interfaces.Colored;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.skincache.SkinCache;
import gg.projecteden.nexus.utils.SymbolBanner.Symbol;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import me.lexikiq.HasOfflinePlayer;
import me.lexikiq.HasUniqueId;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.DyeColor;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.ShulkerBox;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Axolotl;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.AxolotlBucketMeta;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.BookMeta.Generation;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static gg.projecteden.nexus.utils.ItemUtils.isNullOrAir;
import static gg.projecteden.nexus.utils.StringUtils.colorize;

@SuppressWarnings({"unused", "UnusedReturnValue", "ResultOfMethodCallIgnored", "CopyConstructorMissesField", "deprecation"})
public class ItemBuilder implements Cloneable, Supplier<ItemStack> {
	private ItemStack itemStack;
	private ItemMeta itemMeta;
	@Getter
	private final List<String> lore = new ArrayList<>();
	private boolean doLoreize = true;

	public ItemBuilder(Material material) {
		this(new ItemStack(material));
	}

	public ItemBuilder(Material material, int amount) {
		this(new ItemStack(material, amount));
	}

	public ItemBuilder(ItemBuilder itemBuilder) {
		this(itemBuilder.build());
		this.doLoreize = itemBuilder.doLoreize;
	}

	public ItemBuilder(ItemStack itemStack) {
		this.itemStack = itemStack.clone();
		this.itemMeta = itemStack.getItemMeta() == null ? null : itemStack.getItemMeta().clone();
		if (itemMeta != null && itemMeta.getLore() != null)
			this.lore.addAll(itemMeta.getLore());
	}

	public ItemBuilder material(Material material) {
		itemStack.setType(material);
		return this;
	}

	public Material material() {
		return itemStack.getType();
	}

	public ItemBuilder amount(int amount) {
		itemStack.setAmount(amount);
		return this;
	}

	public ItemBuilder color(ColorType colorType) {
		final Material newMaterial = Objects.requireNonNull(colorType.switchColor(itemStack.getType()), "Could not determine color of " + itemStack.getType());
		itemStack.setType(newMaterial);
		return this;
	}

	@Deprecated
	public ItemBuilder durability(int durability) {
		return durability(Integer.valueOf(durability).shortValue());
	}

	@Deprecated
	public ItemBuilder durability(short durability) {
		itemStack.setDurability(durability);
		return this;
	}

	public ItemBuilder damage(int damage) {
		if (!(itemMeta instanceof Damageable damageable)) throw new UnsupportedOperationException("Cannot apply durability to non-damageable item");
		damageable.setDamage(damage);
		return this;
	}

	private static Component removeItalicIfUnset(Component component) {
		if (component.decoration(TextDecoration.ITALIC) == TextDecoration.State.NOT_SET)
			component = component.decoration(TextDecoration.ITALIC, false);
		return component;
	}

	private static List<Component> removeItalicIfUnset(ComponentLike... components) {
		return AdventureUtils.asComponentList(components).stream().map(ItemBuilder::removeItalicIfUnset).collect(Collectors.toList());
	}

	private static List<Component> removeItalicIfUnset(List<? extends ComponentLike> components) {
		return AdventureUtils.asComponentList(components).stream().map(ItemBuilder::removeItalicIfUnset).collect(Collectors.toList());
	}

	public ItemBuilder name(@Nullable String displayName) {
		if (displayName == null)
			itemMeta.setDisplayName(null);
		else
			itemMeta.setDisplayName(colorize("&f" + displayName));
		return this;
	}

	public ItemBuilder name(@Nullable ComponentLike componentLike) {
		if (componentLike != null)
			itemMeta.displayName(removeItalicIfUnset(componentLike.asComponent()));
		return this;
	}

	public ItemBuilder resetName() {
		return name((String) null);
	}

	public ItemBuilder resetLore() {
		this.lore.clear();
		return this;
	}

	public ItemBuilder setLore(String... lore) {
		return setLore(List.of(lore));
	}

	public ItemBuilder setLore(List<String> lore) {
		this.lore.clear();
		if (lore != null)
			this.lore.addAll(lore);
		return this;
	}

	public ItemBuilder lore(String... lore) {
		return lore(Arrays.asList(lore));
	}

	public ItemBuilder lore(Collection<String> lore) {
		if (lore != null)
			this.lore.addAll(lore);
		return this;
	}

	// overridden by all string lore
	public ItemBuilder componentLore(ComponentLike... components) {
		itemMeta.lore(removeItalicIfUnset(components));
		return this;
	}

	// overridden by all string lore
	public ItemBuilder componentLore(List<? extends ComponentLike> components) {
		itemMeta.lore(removeItalicIfUnset(components));
		return this;
	}

	public @NotNull List<Component> componentLore() {
		return itemMeta.hasLore() ? Objects.requireNonNull(itemMeta.lore()) : new ArrayList<>();
	}

	public ItemBuilder loreize(boolean doLoreize) {
		this.doLoreize = doLoreize;
		return this;
	}

	public ItemBuilder enchant(Enchantment enchantment) {
		return enchant(enchantment, 1);
	}

	public ItemBuilder enchant(Enchantment enchantment, int level) {
		return enchant(enchantment, level, true);
	}

	public ItemBuilder enchantMax(Enchantment enchantment) {
		return enchant(enchantment, enchantment.getMaxLevel(), true);
	}

	public ItemBuilder enchant(Enchantment enchantment, int level, boolean ignoreLevelRestriction) {
		if (itemStack.getType() == Material.ENCHANTED_BOOK)
			((EnchantmentStorageMeta) itemMeta).addStoredEnchant(enchantment, level, ignoreLevelRestriction);
		else
			itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction);

		return this;
	}

	public ItemBuilder glow() {
		enchant(Enchantment.ARROW_INFINITE);
		itemFlags(ItemFlag.HIDE_ENCHANTS);
		return this;
	}

	public ItemBuilder glow(boolean glow) {
		return glow ? glow() : this;
	}

	public ItemBuilder unbreakable() {
		itemMeta.setUnbreakable(true);
		return this;
	}

	public ItemBuilder itemFlags(ItemFlag... flags) {
		itemMeta.addItemFlags(flags);
		return this;
	}

	// Custom meta types

	// Leather armor

	public Color dyeColor() {
		if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta)
			return leatherArmorMeta.getColor();
		return null;
	}

	public ItemBuilder dyeColor(Color color) {
		if (itemMeta instanceof LeatherArmorMeta leatherArmorMeta)
			leatherArmorMeta.setColor(color);
		return this;
	}

	public ItemBuilder dyeColor(Colored color) {
		return dyeColor(color.getBukkitColor());
	}

	public ItemBuilder dyeColor(String hex) {
		return dyeColor(ColorType.hexToBukkit(hex));
	}

	// Potions

	public ItemBuilder potionType(PotionType potionType) {
		return potionType(potionType, false, false);
	}

	public ItemBuilder potionType(PotionType potionType, boolean extended, boolean upgraded) {
		((PotionMeta) itemMeta).setBasePotionData(new PotionData(potionType, extended, upgraded));
		return this;
	}

	public ItemBuilder potionEffect(PotionEffectType potionEffectType) {
		return potionEffect(potionEffectType, 1, 1);
	}

	public ItemBuilder potionEffect(PotionEffectType potionEffectType, int seconds) {
		return potionEffect(potionEffectType, seconds, 1);
	}

	public ItemBuilder potionEffect(PotionEffectType potionEffectType, int seconds, int amplifier) {
		return potionEffect(new PotionEffectBuilder(potionEffectType).duration(TickTime.SECOND.x(seconds)).amplifier(amplifier - 1));
	}

	public ItemBuilder potionEffect(PotionEffectBuilder potionEffect) {
		((PotionMeta) itemMeta).addCustomEffect(potionEffect.build(), true);
		return this;
	}

	public ItemBuilder potionEffect(PotionEffect potionEffect) {
		((PotionMeta) itemMeta).addCustomEffect(potionEffect, true);
		return this;
	}

	public ItemBuilder potionEffectColor(Color color) {
		((PotionMeta) itemMeta).setColor(color);
		return this;
	}

	// Fireworks

	public ItemBuilder fireworkPower(int power) {
		((FireworkMeta) itemMeta).setPower(power);
		return this;
	}

	public ItemBuilder fireworkEffect(FireworkEffect... effect) {
		((FireworkMeta) itemMeta).addEffects(effect);
		return this;
	}

	// Skulls

	public ItemBuilder skullOwner(HasUniqueId hasUUID) {
		SkullMeta skullMeta = SkinCache.of(hasUUID).getHeadMeta();
		((SkullMeta) itemMeta).setPlayerProfile(skullMeta.getPlayerProfile());
		return this;
	}

	@Deprecated
	public ItemBuilder skullOwner(String name) {
		((SkullMeta) itemMeta).setOwner(name);
		return this;
	}

	@Deprecated
	public ItemBuilder skullOwnerActual(HasOfflinePlayer offlinePlayer) {
		((SkullMeta) itemMeta).setOwningPlayer(offlinePlayer.getOfflinePlayer());
		return this;
	}

	public static ItemBuilder fromHeadId(String id) {
		return new ItemBuilder(Nexus.getHeadAPI().getItemHead(id));
	}

	// Banners

	public ItemBuilder pattern(DyeColor color, PatternType pattern) {
		return pattern(new Pattern(color, pattern));
	}

	public ItemBuilder pattern(Pattern pattern) {
		BannerMeta bannerMeta = (BannerMeta) itemMeta;
		bannerMeta.addPattern(pattern);
		return this;
	}

	public ItemBuilder symbolBanner(char character, DyeColor patternDye) {
		return symbolBanner(Symbol.of(character), patternDye);
	}

	public ItemBuilder symbolBanner(SymbolBanner.Symbol symbol, DyeColor patternDye) {
		if (symbol == null)
			return this;

		final ColorType color = Objects.requireNonNull(ColorType.of(itemStack.getType()), "Could not determine color of " + itemStack.getType());
		return symbol.get(this, color.getDyeColor(), patternDye);
	}

	// Maps

	public ItemBuilder mapId(int id) {
		return mapId(id, null);
	}

	public ItemBuilder mapId(int id, MapRenderer renderer) {
		MapMeta mapMeta = (MapMeta) itemMeta;
		mapMeta.setMapId(id);
		MapView view = Bukkit.getServer().getMap(id);
		if (view == null) {
			Nexus.log("View for map id " + id + " is null");
		} else if (renderer != null)
			view.addRenderer(renderer);
		mapMeta.setMapView(view);
		return this;
	}

	public int getMapId() {
		MapMeta mapMeta = (MapMeta) itemMeta;
		return mapMeta.getMapId();
	}

	public ItemBuilder createMapView(World world) {
		return createMapView(world, null);
	}

	public ItemBuilder createMapView(World world, MapRenderer renderer) {
		MapMeta mapMeta = (MapMeta) itemMeta;
		MapView view = Bukkit.getServer().createMap(world);
		if (renderer != null)
			view.addRenderer(renderer);
		mapMeta.setMapView(view);
		return this;
	}

	// Shulker Boxes

	public ItemBuilder shulkerBox(ItemBuilder... builders) {
		for (ItemBuilder builder : builders)
			shulkerBox(builder.build());
		return this;
	}

	public ItemBuilder shulkerBox(ItemStack... items) {
		BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
		ShulkerBox box = (ShulkerBox) blockStateMeta.getBlockState();
		for (ItemStack item : items)
			box.getInventory().addItem(item);
		blockStateMeta.setBlockState(box);
		return this;
	}

	public List<@Nullable ItemStack> shulkerBoxContents() {
		BlockStateMeta blockStateMeta = (BlockStateMeta) itemMeta;
		ShulkerBox box = (ShulkerBox) blockStateMeta.getBlockState();
		return Arrays.asList(box.getInventory().getContents());
	}

	public List<@Nullable ItemStack> nonAirShulkerBoxContents() {
		return shulkerBoxContents().stream().filter(ItemUtils::isNotNullOrAir).collect(Collectors.toList());
	}

	// Books

	public ItemBuilder bookTitle(String title) {
		return bookTitle(new JsonBuilder(title));
	}

	public ItemBuilder bookTitle(@Nullable ComponentLike title) {
		final BookMeta bookMeta = (BookMeta) itemMeta;
		if (title != null)
			bookMeta.title(title.asComponent());
		return this;
	}

	public ItemBuilder bookAuthor(OfflinePlayer author) {
		return bookAuthor(Nickname.of(author));
	}

	public ItemBuilder bookAuthor(String author) {
		return bookAuthor(new JsonBuilder(author));
	}

	public ItemBuilder bookAuthor(@Nullable ComponentLike author) {
		final BookMeta bookMeta = (BookMeta) itemMeta;
		if (author != null)
			bookMeta.author(author.asComponent());
		return this;
	}

	public ItemBuilder bookPages(String... pages) {
		final BookMeta bookMeta = (BookMeta) itemMeta;
		bookMeta.addPages(Arrays.stream(pages).map(message -> new JsonBuilder(message).asComponent()).toArray(Component[]::new));
		return this;
	}

	public ItemBuilder bookPages(ComponentLike... pages) {
		final BookMeta bookMeta = (BookMeta) itemMeta;
		bookMeta.addPages(Arrays.stream(pages).map(ComponentLike::asComponent).toArray(Component[]::new));
		return this;
	}

	public ItemBuilder bookPages(List<Component> pages) {
		final BookMeta bookMeta = (BookMeta) itemMeta;
		bookMeta.pages(pages);
		return this;
	}

	public ItemBuilder bookGeneration(Generation generation) {
		final BookMeta bookMeta = (BookMeta) itemMeta;
		bookMeta.setGeneration(generation);
		return this;
	}

	public ItemBuilder bookMeta(BookMeta bookMeta) {
		itemMeta = bookMeta;
		return this;
	}

	public String getBookPlainContents() {
		final BookMeta bookMeta = (BookMeta) itemMeta;
		return bookMeta.pages().stream().map(AdventureUtils::asPlainText).collect(Collectors.joining(" "));
	}

	// Entities

	public ItemBuilder axolotl(Axolotl.Variant variant) {
		final AxolotlBucketMeta bucketMeta = (AxolotlBucketMeta) itemMeta;
		bucketMeta.setVariant(variant);
		customModelData(variant.ordinal());
		return this;
	}

	// NBT

	public ItemBuilder nbt(Consumer<NBTItem> consumer) {
		final NBTItem nbtItem = nbtItem();
		consumer.accept(nbtItem);
		itemStack = nbtItem.getItem();
		itemMeta = itemStack.getItemMeta();
		return this;
	}

	@NotNull
	private NBTItem nbtItem() {
		return new NBTItem(build());
	}

	public Rarity rarity() {
		final NBTItem nbtItem = nbtItem();
		if (!nbtItem.hasKey(Rarity.NBT_KEY))
			return Rarity.of(build());

		return Rarity.valueOf(nbtItem.getString(Rarity.NBT_KEY));
	}

	public ItemBuilder rarity(Rarity rarity) {
		return nbt(nbtItem -> Rarity.setNBT(nbtItem, rarity));
	}

	public Condition condition() {
		final NBTItem nbtItem = nbtItem();
		if (!nbtItem.hasKey(Condition.NBT_KEY))
			return Condition.of(build());

		return Condition.valueOf(nbtItem.getString(Condition.NBT_KEY));
	}

	public ItemBuilder attribute(Attribute attribute, AttributeModifier value) {
		itemMeta.addAttributeModifier(attribute, value);
		return this;
	}

	public ItemBuilder condition(Condition condition) {
		return nbt(nbtItem -> Condition.setNBT(nbtItem, condition));
	}

	@AllArgsConstructor
	public enum ItemSetting {
		/**
		 * Whether an item can be dropped
		 */
		DROPPABLE(true),
		/**
		 * Whether an item can be placed in an item frame
		 */
		FRAMEABLE(true),
		/**
		 * Whether an item can be placed
		 */
		PLACEABLE(true),
		/**
		 * Whether an item can be stored in containers
		 */
		STORABLE(true),
		/**
		 * Whether an item can be put in the {@code /trash}
		 */
		TRASHABLE(true),
		/**
		 * Whether an item can be sold in shops
		 */
		TRADEABLE(true) {
			@Override
			public boolean of(ItemBuilder builder, boolean orDefault) {
				if (Backpacks.isBackpack(builder.build()))
					return false;

				return super.of(builder, orDefault);
			}
		},
		;

		private final boolean orDefault;

		public String getKey() {
			return name().toLowerCase();
		}

		public boolean of(ItemBuilder builder, boolean orDefault) {
			NBTItem item = builder.nbtItem();
			if (!item.hasKey(getKey()))
				return orDefault;

			return item.getBoolean(getKey());
		}

		public final boolean of(ItemBuilder builder) {
			return of(builder, orDefault);
		}
	}

	public ItemBuilder setting(ItemSetting setting, boolean value) {
		return nbt(nbt -> nbt.setBoolean(setting.getKey(), value));
	}

	public ItemBuilder unset(ItemSetting setting) {
		return nbt(nbt -> nbt.removeKey(setting.getKey()));
	}

	public boolean is(ItemSetting setting) {
		return setting.of(this);
	}

	public boolean isNot(ItemSetting setting) {
		return !is(setting);
	}

	public boolean is(ItemSetting setting, boolean orDefault) {
		return setting.of(this, orDefault);
	}

	public ItemBuilder undroppable() {
		return setting(ItemSetting.DROPPABLE, false);
	}

	public ItemBuilder unframeable() {
		return setting(ItemSetting.FRAMEABLE, false);
	}

	public ItemBuilder unplaceable() {
		return setting(ItemSetting.PLACEABLE, false);
	}

	public ItemBuilder unstorable() {
		return setting(ItemSetting.STORABLE, false);
	}

	public ItemBuilder untrashable() {
		return setting(ItemSetting.TRASHABLE, false);
	}

	public ItemBuilder untradeable() {
		return setting(ItemSetting.TRADEABLE, false);
	}

	public ItemBuilder customModelData(int id) {
		if (id > 0)
			nbt(item -> item.setInteger(CustomModel.NBT_KEY, id));
		return this;
	}

	public int customModelData() {
		NBTItem nbtItem = nbtItem();
		final Integer customModelData = nbtItem.getInteger(CustomModel.NBT_KEY);
		return customModelData == null ? 0 : customModelData;
	}

	public static class CustomModelData {

		public static int of(ItemStack item) {
			if (isNullOrAir(item))
				return 0;

			return of(new ItemBuilder(item));
		}

		public static int of(ItemBuilder item) {
			return item.customModelData();
		}

	}

	// Use this when you don't want the glowing, infinite deaths, & no combining
	// TODO ProtocolLib instead?
	public ItemBuilder soulbound() {
		nbt(nbtItem -> nbtItem.setBoolean(SoulboundEnchant.NBT_KEY, true));
		lore("&7" + Enchant.SOULBOUND.getDisplayName(Enchant.SOULBOUND.getMaxLevel()));
		return this;
	}

	// Building //

	@Override
	public ItemStack get() {
		return build();
	}

	public ItemStack build() {
		ItemStack result = itemStack.clone();
		buildLore();
		if (itemMeta != null)
			result.setItemMeta(itemMeta.clone());
		return result;
	}

	public void buildLore() {
		if (lore.isEmpty())
			return; // don't override Component lore
		lore.removeIf(Objects::isNull);
		List<String> colorized = new ArrayList<>();
		for (String line : lore)
			if (doLoreize)
				colorized.addAll(Arrays.asList(StringUtils.loreize(colorize(line)).split("\\|\\|")));
			else
				colorized.addAll(Arrays.asList(colorize(line).split("\\|\\|")));
		itemMeta.setLore(colorized);

		itemStack.setItemMeta(itemMeta);
		CustomEnchants.update(itemStack);
		itemMeta = itemStack.getItemMeta();
	}

	@SuppressWarnings("MethodDoesntCallSuperMethod")
	public ItemBuilder clone() {
		itemStack.setItemMeta(itemMeta);
		ItemBuilder builder = new ItemBuilder(itemStack.clone());
		builder.lore(lore);
		builder.loreize(doLoreize);
		return builder;
	}

	/** Static helpers */

	public static ItemBuilder oneOf(ItemStack item) {
		return new ItemBuilder(item).amount(1);
	}

	public static ItemStack setName(ItemStack item, String name) {
		ItemMeta meta = item.getItemMeta();
		if (name == null)
			meta.setDisplayName(null);
		else
			meta.setDisplayName(colorize("&f" + name));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack addItemFlags(ItemStack item, ItemFlag... flags) {
		ItemMeta meta = item.getItemMeta();
		meta.addItemFlags(flags);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack setLore(ItemStack item, List<String> lore) {
		ItemMeta meta = item.getItemMeta();
		meta.setLore(lore.stream().map(StringUtils::colorize).collect(Collectors.toList()));
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack addLore(ItemStack item, String... lore) {
		return addLore(item, Arrays.asList(lore));
	}

	public static ItemStack addLore(ItemStack item, List<String> lore) {
		lore = lore.stream().map(StringUtils::colorize).collect(Collectors.toList());
		ItemMeta meta = item.getItemMeta();
		List<String> existing = meta.getLore();
		if (existing == null) existing = new ArrayList<>();
		existing.addAll(lore);
		meta.setLore(existing);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack setLoreLine(ItemStack item, int line, String text) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();
		if (lore == null)
			lore = new ArrayList<>();
		while (lore.size() < line)
			lore.add("");

		lore.set(line - 1, StringUtils.colorize(text));
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack removeLoreLine(ItemStack item, int line) {
		ItemMeta meta = item.getItemMeta();
		List<String> lore = meta.getLore();

		if (lore == null) throw new InvalidInputException("Item does not have lore");
		if (line - 1 > lore.size()) throw new InvalidInputException("Line " + line + " does not exist");

		lore.remove(line - 1);
		meta.setLore(lore);
		item.setItemMeta(meta);
		return item;
	}

	public static ItemStack glow(ItemStack itemStack) {
		itemStack.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
		ItemMeta meta = itemStack.getItemMeta();
		meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		itemStack.setItemMeta(meta);
		return itemStack;
	}

	public static ItemStack setDurability(ItemStack item, double percentage) {
		ItemMeta meta = item.getItemMeta();
		if (meta instanceof Damageable damageable) {
			double maxDurability = item.getType().getMaxDurability();
			double damage = (percentage / 100.0) * maxDurability;
			damageable.setDamage((int) damage);

			item.setItemMeta((ItemMeta) damageable);
		}

		return item;
	}

}
