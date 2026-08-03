package gg.projecteden.nexus.features.mcmmo.resetnew.skills.alchemy;

import com.gmail.nossr50.config.skills.alchemy.PotionConfig;
import com.gmail.nossr50.datatypes.skills.alchemy.AlchemyPotion;
import com.gmail.nossr50.events.fake.FakeBrewEvent;
import com.gmail.nossr50.mcMMO;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.gmail.nossr50.util.ContainerMetadataUtils;
import gg.projecteden.nexus.Nexus;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

// thanks chat
public final class AdvancedAlchemyHandler implements Listener {

	public static final String PERMISSION = "nexus.advancedalchemy";

	private static final String KEY_PREFIX = "NEXUS_";
	private static final int MAX_ALCHEMY_TIER = 8;
	private static final int EXTENDED_TWO_MULTIPLIER = 2;

	public AdvancedAlchemyHandler() {
		register();
	}

	public static int register() {
		PotionConfig config = mcMMO.p.getPotionConfig();
		Map<String, AlchemyPotion> potions = getAlchemyPotions(config);

		// Reload safe
		unregisterPotions(potions);

		registerIngredient(config, Material.GLOWSTONE);
		registerIngredient(config, Material.REDSTONE_BLOCK);

		int registered = 0;

		/*
		 * Copy the entries because new potions are added to the original map
		 * while this loop runs.
		 */
		for (Map.Entry<String, AlchemyPotion> entry : new ArrayList<>(potions.entrySet())) {
			String baseKey = entry.getKey();
			AlchemyPotion base = entry.getValue();

			/*
			 * Vanilla modifiers are applied before gunpowder, so only register
			 * drinkable potion recipes here.
			 */
			if (base.toItemStack(1).getType() != Material.POTION)
				continue;

			AlchemyPotion upgraded = getChild(potions, base, Material.GLOWSTONE_DUST);
			AlchemyPotion extended = getChild(potions, base, Material.REDSTONE);

			/*
			 * Avoid treating unrelated recipes such as:
			 * Water Bottle + Glowstone Dust -> Thick Potion
			 * as genuine potion upgrades.
			 */
			if (upgraded != null && !isUpgrade(base, upgraded))
				upgraded = null;

			if (extended != null && !isExtension(base, extended))
				extended = null;

			/*
			 * Potion II + Glowstone Block -> Potion III
			 */
			if (upgraded != null) {
				String levelThreeKey = customKey(baseKey, "III");
				ItemStack levelThree = createPotion(
					config,
					upgraded,
					increaseAmplifiers(getEffects(upgraded)),
					displayName(baseKey, " III")
				);

				if (registerPotion(potions, levelThreeKey, levelThree))
					registered++;

				setChild(upgraded, Material.GLOWSTONE, levelThreeKey);
			}

			/*
			 * Potion II + Redstone Block -> Potion II Extended
			 * Extended Potion + Glowstone Block -> Potion II Extended
			 */
			if (upgraded != null && extended != null) {
				String upgradedExtendedKey = customKey(baseKey, "II_EXTENDED");
				ItemStack upgradedExtended = createPotion(
					config,
					upgraded,
					combineUpgradeAndExtension(upgraded, extended),
					displayName(baseKey, " II (Extended)")
				);

				if (registerPotion(potions, upgradedExtendedKey, upgradedExtended))
					registered++;

				setChild(upgraded, Material.REDSTONE_BLOCK, upgradedExtendedKey);
				setChild(extended, Material.GLOWSTONE, upgradedExtendedKey);
			}

			/*
			 * Extended Potion + Redstone Block -> Extended II Potion
			 */
			if (extended != null) {
				String extendedTwoKey = customKey(baseKey, "EXTENDED_II");
				ItemStack extendedTwo = createPotion(
					config,
					extended,
					multiplyDurations(getEffects(extended), EXTENDED_TWO_MULTIPLIER),
					displayName(baseKey, " (Extended II)")
				);

				if (registerPotion(potions, extendedTwoKey, extendedTwo))
					registered++;

				setChild(extended, Material.REDSTONE_BLOCK, extendedTwoKey);
			}
		}

		Nexus.log("Registered %d custom mcMMO alchemy potions".formatted(registered));
		return registered;
	}

	@SuppressWarnings("unchecked")
	private static Map<String, AlchemyPotion> getAlchemyPotions(PotionConfig config) {
		try {
			Field field = PotionConfig.class.getDeclaredField("alchemyPotions");
			field.setAccessible(true);

			Object value = field.get(config);
			if (!(value instanceof Map<?, ?>))
				throw new IllegalStateException("PotionConfig#alchemyPotions was not a Map");

			return (Map<String, AlchemyPotion>) value;
		} catch (ReflectiveOperationException ex) {
			throw new IllegalStateException("Could not access PotionConfig#alchemyPotions", ex);
		}
	}

	private static void unregisterPotions(Map<String, AlchemyPotion> potions) {
		/*
		 * Remove references from mcMMO potions to our custom potion keys.
		 */
		for (AlchemyPotion potion : potions.values())
			potion.getAlchemyPotionChildren().entrySet().removeIf(entry -> entry.getValue().startsWith(KEY_PREFIX));

		/*
		 * Remove the drinkable, splash, and lingering custom potions themselves.
		 */
		potions.keySet().removeIf(key -> key.startsWith(KEY_PREFIX));
	}

	private static void registerIngredient(PotionConfig config, Material material) {
		ItemStack ingredient = new ItemStack(material);

		/*
		 * mcMMO builds each cumulative tier list during startup with addAll().
		 * Adding to tier one afterward does not update the already-built higher
		 * tier lists, so every eligible tier must be updated.
		 */
		for (int tier = 1; tier <= MAX_ALCHEMY_TIER; tier++) {
			List<ItemStack> ingredients = config.getIngredients(tier);

			if (ingredients.stream().noneMatch(ingredient::isSimilar))
				ingredients.add(ingredient.clone());
		}
	}

	private static AlchemyPotion getChild(Map<String, AlchemyPotion> potions, AlchemyPotion input, Material ingredient) {
		String childName = getChildName(input, ingredient);
		return childName == null ? null : potions.get(childName);
	}

	private static String getChildName(AlchemyPotion potion, Material ingredient) {
		ItemStack ingredientItem = new ItemStack(ingredient);

		for (Map.Entry<ItemStack, String> entry : potion.getAlchemyPotionChildren().entrySet())
			if (entry.getKey().isSimilar(ingredientItem))
				return entry.getValue();

		return null;
	}

	private static void setChild(AlchemyPotion input, Material ingredient, String outputKey) {
		ItemStack ingredientItem = new ItemStack(ingredient);
		Map<ItemStack, String> children = input.getAlchemyPotionChildren();

		children.entrySet().removeIf(entry -> entry.getKey().isSimilar(ingredientItem));
		children.put(ingredientItem, outputKey);
	}

	private static boolean registerPotion(Map<String, AlchemyPotion> potions, String key, ItemStack item) {
		boolean newlyRegistered = !potions.containsKey(key);

		AlchemyPotion potion = new AlchemyPotion(key, item, new HashMap<>());
		potions.put(key, potion);

		if (item.getType() == Material.POTION)
			registerSplashAndLingeringPotion(potions, potion, key);

		return newlyRegistered;
	}

	private static void registerSplashAndLingeringPotion(Map<String, AlchemyPotion> potions, AlchemyPotion potion, String key) {
		String splashKey = key + "_SPLASH";
		String lingeringKey = key + "_LINGERING";

		ItemStack splashItem = changePotionType(potion.toItemStack(1), Material.SPLASH_POTION);
		ItemStack lingeringItem = changePotionType(potion.toItemStack(1), Material.LINGERING_POTION);

		AlchemyPotion splashPotion = new AlchemyPotion(splashKey, splashItem, new HashMap<>());
		AlchemyPotion lingeringPotion = new AlchemyPotion(lingeringKey, lingeringItem, new HashMap<>());

		potions.put(splashKey, splashPotion);
		potions.put(lingeringKey, lingeringPotion);

		setChild(potion, Material.GUNPOWDER, splashKey);
		setChild(splashPotion, Material.DRAGON_BREATH, lingeringKey);
	}

	private static ItemStack changePotionType(ItemStack source, Material type) {
		ItemStack result = new ItemStack(type, source.getAmount());
		result.setItemMeta(source.getItemMeta());
		return result;
	}

	private static ItemStack createPotion(PotionConfig config, AlchemyPotion source, List<PotionEffect> effects, String name) {
		ItemStack item = source.toItemStack(1);
		PotionMeta meta = (PotionMeta) item.getItemMeta();

		/*
		 * Mundane has no built-in effects and, as mcMMO itself notes, works as
		 * the modern replacement for the old uncraftable potion type.
		 */
		meta.setBasePotionType(PotionType.MUNDANE);
		meta.clearCustomEffects();

		for (PotionEffect effect : effects)
			meta.addCustomEffect(effect, true);

		Color color = config.generateColor(effects);
		if (color != null)
			meta.setColor(color);

		meta.displayName(Component.text(name).decoration(TextDecoration.ITALIC, false));
		item.setItemMeta(meta);
		return item;
	}

	private static List<PotionEffect> getEffects(AlchemyPotion potion) {
		PotionMeta meta = potion.getAlchemyPotionMeta();
		Map<PotionEffectType, PotionEffect> effects = new LinkedHashMap<>();

		PotionType baseType = meta.getBasePotionType();
		if (baseType != null)
			for (PotionEffect effect : baseType.getPotionEffects())
				effects.put(effect.getType(), effect);

		/*
		 * Custom effects take precedence over matching base effects.
		 */
		for (PotionEffect effect : meta.getCustomEffects())
			effects.put(effect.getType(), effect);

		return new ArrayList<>(effects.values());
	}

	private static boolean isUpgrade(AlchemyPotion input, AlchemyPotion output) {
		List<PotionEffect> inputEffects = getEffects(input);
		List<PotionEffect> outputEffects = getEffects(output);

		if (inputEffects.isEmpty() || inputEffects.size() != outputEffects.size())
			return false;

		boolean amplifierIncreased = false;

		for (PotionEffect inputEffect : inputEffects) {
			PotionEffect outputEffect = findEffect(outputEffects, inputEffect.getType());

			if (outputEffect == null || outputEffect.getAmplifier() < inputEffect.getAmplifier())
				return false;

			if (outputEffect.getAmplifier() > inputEffect.getAmplifier())
				amplifierIncreased = true;
		}

		return amplifierIncreased;
	}

	private static boolean isExtension(AlchemyPotion input, AlchemyPotion output) {
		List<PotionEffect> inputEffects = getEffects(input);
		List<PotionEffect> outputEffects = getEffects(output);

		if (inputEffects.isEmpty() || inputEffects.size() != outputEffects.size())
			return false;

		boolean durationIncreased = false;

		for (PotionEffect inputEffect : inputEffects) {
			PotionEffect outputEffect = findEffect(outputEffects, inputEffect.getType());

			if (outputEffect == null || outputEffect.getAmplifier() != inputEffect.getAmplifier())
				return false;

			if (outputEffect.getDuration() < inputEffect.getDuration())
				return false;

			if (outputEffect.getDuration() > inputEffect.getDuration())
				durationIncreased = true;
		}

		return durationIncreased;
	}

	private static List<PotionEffect> increaseAmplifiers(List<PotionEffect> effects) {
		List<PotionEffect> result = new ArrayList<>();

		for (PotionEffect effect : effects)
			result.add(copyEffect(effect, effect.getDuration(), effect.getAmplifier() + 1));

		return result;
	}

	private static List<PotionEffect> combineUpgradeAndExtension(AlchemyPotion upgraded, AlchemyPotion extended) {
		List<PotionEffect> upgradedEffects = getEffects(upgraded);
		List<PotionEffect> extendedEffects = getEffects(extended);
		List<PotionEffect> result = new ArrayList<>();

		for (PotionEffect upgradedEffect : upgradedEffects) {
			PotionEffect extendedEffect = findEffect(extendedEffects, upgradedEffect.getType());

			if (extendedEffect == null)
				throw new IllegalStateException("Missing extended effect for %s".formatted(upgradedEffect.getType()));

			result.add(copyEffect(
				upgradedEffect,
				extendedEffect.getDuration(),
				upgradedEffect.getAmplifier()
			));
		}

		return result;
	}

	private static List<PotionEffect> multiplyDurations(List<PotionEffect> effects, int multiplier) {
		List<PotionEffect> result = new ArrayList<>();

		for (PotionEffect effect : effects) {
			int duration = (int) Math.min(Integer.MAX_VALUE, (long) effect.getDuration() * multiplier);
			result.add(copyEffect(effect, duration, effect.getAmplifier()));
		}

		return result;
	}

	private static PotionEffect findEffect(List<PotionEffect> effects, PotionEffectType type) {
		for (PotionEffect effect : effects)
			if (effect.getType().equals(type))
				return effect;

		return null;
	}

	private static PotionEffect copyEffect(PotionEffect effect, int duration, int amplifier) {
		return new PotionEffect(
			effect.getType(),
			duration,
			amplifier,
			effect.isAmbient(),
			effect.hasParticles(),
			effect.hasIcon()
		);
	}

	private static String customKey(String baseKey, String suffix) {
		return KEY_PREFIX + baseKey + "_" + suffix;
	}

	private static String displayName(String baseKey, String suffix) {
		String rawName = baseKey.startsWith("POTION_OF_") ? baseKey.substring("POTION_OF_".length()) : baseKey;

		String name = Arrays.stream(rawName.split("_"))
			.map(word -> word.substring(0, 1) + word.substring(1).toLowerCase(Locale.ENGLISH))
			.collect(Collectors.joining(" "));

		if (rawName.equals("TURTLE_MASTER"))
			name = "the Turtle Master";

		return "Potion of " + name + suffix;
	}

	private static final Set<Material> RESTRICTED_INGREDIENTS = Set.of(
		Material.REDSTONE_BLOCK,
		Material.GLOWSTONE
	);

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onBrew(BrewEvent event) {
		if (!(event instanceof FakeBrewEvent))
			return;

		ItemStack ingredient = event.getContents().getIngredient();
		if (ingredient == null || !RESTRICTED_INGREDIENTS.contains(ingredient.getType()))
			return;

		if (!(event.getBlock().getState() instanceof BrewingStand brewingStand))
			return;

		OfflinePlayer owner = ContainerMetadataUtils.getContainerOwner(brewingStand);
		Player player = owner == null ? null : owner.getPlayer();

		if (player == null || !player.hasPermission(PERMISSION))
			event.setCancelled(true);
	}

}
