package gg.projecteden.nexus.utils;

import com.google.common.base.Preconditions;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.features.minigames.models.mechanics.Mechanic;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.SneakyThrows;
import me.lexikiq.HasUniqueId;
import org.bukkit.Rotation;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.reflections.Reflections;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static gg.projecteden.nexus.utils.Nullables.isNullOrEmpty;
import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

public class Utils extends gg.projecteden.utils.Utils {

	public static void registerListeners(Package packageObject) {
		registerListeners(packageObject.getName());
	}

	public static void registerListeners(String packageName) {
		new Reflections(packageName).getSubTypesOf(Listener.class).forEach(Utils::tryRegisterListener);
	}

	public static void tryRegisterListener(Class<?> clazz) {
		if (canEnable(clazz))
			tryRegisterListener(Nexus.singletonOf(clazz));
	}

	public static void tryRegisterListener(Object object) {
		try {
			final Class<?> clazz = object.getClass();
			if (!canEnable(clazz))
				return;

			boolean hasNoArgsConstructor = Stream.of(clazz.getConstructors()).anyMatch(c -> c.getParameterCount() == 0);
			if (object instanceof Listener listener) {
				if (hasNoArgsConstructor)
					Nexus.registerListener(listener);
				else
					Nexus.warn("Cannot register listener on " + clazz.getSimpleName() + ", needs @NoArgsConstructor");
			} else if (new ArrayList<>(getAllMethods(clazz, withAnnotation(EventHandler.class))).size() > 0)
				Nexus.warn("Found @EventHandlers in " + clazz.getSimpleName() + " which does not implement Listener"
					+ (hasNoArgsConstructor ? "" : " or have a @NoArgsConstructor"));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@AllArgsConstructor
	public enum ItemFrameRotation {
		DEGREE_0(Rotation.NONE, BlockFace.NORTH),
		DEGREE_45(Rotation.CLOCKWISE_45, BlockFace.NORTH_EAST),
		DEGREE_90(Rotation.CLOCKWISE, BlockFace.EAST),
		DEGREE_135(Rotation.CLOCKWISE_135, BlockFace.SOUTH_EAST),
		DEGREE_180(Rotation.FLIPPED, BlockFace.SOUTH),
		DEGREE_225(Rotation.FLIPPED_45, BlockFace.SOUTH_WEST),
		DEGREE_270(Rotation.COUNTER_CLOCKWISE, BlockFace.WEST),
		DEGREE_315(Rotation.COUNTER_CLOCKWISE_45, BlockFace.NORTH_WEST),
		;

		@Getter
		final Rotation rotation;
		@Getter
		final BlockFace blockFace;

		public static ItemFrameRotation of(Player player) {
			BlockFace[] radial = {BlockFace.SOUTH, BlockFace.SOUTH_WEST, BlockFace.WEST, BlockFace.NORTH_WEST,
				BlockFace.NORTH, BlockFace.NORTH_EAST, BlockFace.EAST, BlockFace.SOUTH_EAST};
			float yaw = LocationUtils.normalizeYaw(player.getLocation());
			int ndx = Math.round(yaw / 45F);
			if (ndx > radial.length - 1)
				ndx = 0;

			BlockFace blockFace = radial[ndx];
			return from(blockFace);
		}

		public static ItemFrameRotation of(ItemFrame itemFrame) {
			return from(itemFrame.getRotation());
		}

		public static ItemFrameRotation from(BlockFace blockFace) {
			if (blockFace == null)
				return null;

			return Arrays.stream(values())
				.filter(itemFrameRotation -> itemFrameRotation.getBlockFace().equals(blockFace))
				.findFirst().orElse(null);
		}

		public static ItemFrameRotation from(Rotation rotation) {
			return Arrays.stream(values())
				.filter(itemFrameRotation -> itemFrameRotation.getRotation().equals(rotation))
				.findFirst().orElse(null);
		}
	}

	public enum MapRotation {
		DEGREE_0,
		DEGREE_90,
		DEGREE_180,
		DEGREE_270;

		public static MapRotation getRotation(Rotation rotation) {
			return switch (rotation) {
				case CLOCKWISE_45, FLIPPED_45 -> DEGREE_90;
				case CLOCKWISE, COUNTER_CLOCKWISE -> DEGREE_180;
				case CLOCKWISE_135, COUNTER_CLOCKWISE_45 -> DEGREE_270;
				default -> DEGREE_0;
			};
		}
	}

	public enum ActionGroup {
		CLICK_BLOCK(Action.RIGHT_CLICK_BLOCK, Action.LEFT_CLICK_BLOCK),
		CLICK_AIR(Action.RIGHT_CLICK_AIR, Action.LEFT_CLICK_AIR),
		RIGHT_CLICK(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR),
		LEFT_CLICK(Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR),
		CLICK(Action.RIGHT_CLICK_BLOCK, Action.RIGHT_CLICK_AIR, Action.LEFT_CLICK_BLOCK, Action.LEFT_CLICK_AIR),
		PHYSICAL(Action.PHYSICAL);

		final List<Action> actions;

		ActionGroup(Action... actions) {
			this.actions = Arrays.asList(actions);
		}

		public boolean applies(PlayerInteractEvent event) {
			return actions.contains(event.getAction());
		}
	}

	public static boolean equalsInvViewTitle(InventoryView view, String title) {
		String viewTitle = getInvTitle(view);

		if (isNullOrEmpty(viewTitle))
			return false;

		return viewTitle.equals(title);
	}

	public static boolean containsInvViewTitle(InventoryView view, String title) {
		String viewTitle = getInvTitle(view);

		if (isNullOrEmpty(viewTitle))
			return false;

		return viewTitle.contains(title);
	}

	private static String getInvTitle(InventoryView view) {
		String viewTitle = null;
		try {
			viewTitle = view.getTitle();
		} catch (Exception ignored) {}

		return viewTitle;
	}

	public static boolean isPrimitiveNumber(Class<?> type) {
		return Arrays.asList(Integer.TYPE, Double.TYPE, Float.TYPE, Short.TYPE, Long.TYPE, Byte.TYPE).contains(type);
	}

	@SneakyThrows
	public static Number getMaxValue(Class<?> type) {
		return (Number) getMinMaxHolder(type).getDeclaredField("MAX_VALUE").get(null);
	}

	@SneakyThrows
	public static Number getMinValue(Class<?> type) {
		return (Number) getMinMaxHolder(type).getDeclaredField("MIN_VALUE").get(null);
	}

	public static Class<?> getMinMaxHolder(Class<?> type) {
		if (Integer.class == type || Integer.TYPE == type) return Integer.class;
		if (Double.class == type || Double.TYPE == type) return Double.class;
		if (Float.class == type || Float.TYPE == type) return Float.class;
		if (Short.class == type || Short.TYPE == type) return Short.class;
		if (Long.class == type || Long.TYPE == type) return Long.class;
		if (Byte.class == type || Byte.TYPE == type) return Byte.class;
		if (BigDecimal.class == type) return Double.class;
		throw new InvalidInputException("No min/max holder defined for " + type.getSimpleName());
	}

	public static boolean isWithinBounds(double number, Class<?> type) {
		return isWithinBounds(BigDecimal.valueOf(number), type);
	}

	public static boolean isWithinBounds(BigDecimal number, Class<?> type) {
		final BigDecimal min = BigDecimal.valueOf(getMinValue(type).doubleValue());
		final BigDecimal max = BigDecimal.valueOf(getMaxValue(type).doubleValue());
		return number.compareTo(min) >= 0 && number.compareTo(max) <= 0;
	}

	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface SerializedExclude {}

	private static final ExclusionStrategy strategy = new ExclusionStrategy() {
		@Override
		public boolean shouldSkipClass(Class<?> clazz) {
			return false;
		}

		@Override
		public boolean shouldSkipField(FieldAttributes field) {
			return field.getAnnotation(SerializedExclude.class) != null;
		}
	};

	@Getter
	private static final Gson gson = new GsonBuilder().addSerializationExclusionStrategy(strategy).create();

	/**
	 * Removes the first entity in an iterable whose UUID matches {@code uuid}.
	 * @param uuid UUID to search for and remove
	 * @param from collection of entities
	 * @return whether an object was removed
	 */
	@Contract(mutates = "param2")
	public static HasUniqueId removeEntityFrom(UUID uuid, Iterable<? extends HasUniqueId> from) {
		Objects.requireNonNull(uuid, "uuid");
		Predicate<HasUniqueId> predicate = hasUUID -> hasUUID != null && hasUUID.getUniqueId().equals(uuid);
		return removeFirstIf(predicate, (Iterable<HasUniqueId>) from);
	}

	/**
	 * Removes the first entity in an iterable whose UUID matches {@code entity}'s UUID.
	 * @param entity entity to remove
	 * @param from collection of entities
	 * @return whether an object was removed
	 */
	@Contract(mutates = "param2")
	public static HasUniqueId removeEntityFrom(HasUniqueId entity, Iterable<? extends HasUniqueId> from) {
		return removeEntityFrom(Preconditions.checkNotNull(entity, "entity").getUniqueId(), from);
	}

	@Contract(value = "null, _ -> fail; _, _ -> param1", pure = true)
	public static <T> T notNull(T object, String error) {
		if (object == null)
			throw new InvalidInputException(error);
		return object;
	}

	/**
	 * Clones a collection of objects.
	 * @param list collection of clonable objects
	 * @return a new list with a clone of the input objects
	 * @throws IllegalArgumentException an object could not be cloned
	 */
	@NotNull
	public static <T extends Cloneable> List<T> clone(Iterable<T> list) throws IllegalArgumentException {
		List<T> output = new ArrayList<>();
		for (T item : list) {
			try {
				// for some reason the interface does not make the "clone" method public and instead
				// recommends you to do it, forcing this dumb workaround
				output.add((T) item.getClass().getMethod("clone").invoke(item));
			} catch (Exception e) {
				throw new IllegalArgumentException("Object failed to clone");
			}
		}
		return output;
	}

	public static <T> List<T> reverse(List<T> list) {
		Collections.reverse(list);
		return list;
	}

	public static <T> T tryCalculate(int times, Supplier<T> to) {
		int count = 0;
		while (++count <= times) {
			final T result = to.get();
			if (result != null)
				return result;
		}

		return null;
	}

	@Nullable
	@Contract("_, null -> null; _, !null -> _")
	public static <T, U extends Annotation> U getAnnotation(Class<? extends T> clazz, @Nullable Class<U> annotation) {
		if (annotation == null)
			return null;

		for (Class<? extends T> superclass : Utils.getSuperclasses(clazz))
			if (superclass.isAnnotationPresent(annotation))
				return superclass.getAnnotation(annotation);

		return null;
	}

}
