package me.pugabyte.nexus.utils;

import com.google.common.base.Strings;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import eden.annotations.Disabled;
import lombok.Getter;
import me.pugabyte.nexus.Nexus;
import org.bukkit.Material;
import org.bukkit.Rotation;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryView;
import org.objenesis.ObjenesisStd;
import org.reflections.Reflections;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static org.reflections.ReflectionUtils.getAllMethods;
import static org.reflections.ReflectionUtils.withAnnotation;

public class Utils extends eden.utils.Utils {

	public static EntityType getSpawnEggType(Material type) {
		return EntityType.valueOf(type.toString().split("_SPAWN_EGG")[0]);
	}

	public static Material getSpawnEgg(EntityType type) {
		return Material.valueOf(type.toString() + "_SPAWN_EGG");
	}

	public static void registerListeners(String packageName) {
		new Reflections(packageName).getSubTypesOf(Listener.class).forEach(listener -> {
			try {
				if (listener.getAnnotation(Disabled.class) == null)
					Nexus.registerListener(new ObjenesisStd().newInstance(listener));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		});
	}

	public static void tryRegisterListener(Class<?> clazz) {
		tryRegisterListener(new ObjenesisStd().newInstance(clazz));
	}

	public static void tryRegisterListener(Object object) {
		try {
			final Class<?> clazz = object.getClass();
			if (!canEnable(clazz))
				return;

			boolean hasNoArgsConstructor = Stream.of(clazz.getConstructors()).anyMatch(c -> c.getParameterCount() == 0);
			if (object instanceof Listener listener) {
				if (hasNoArgsConstructor)
					Nexus.registerListener(listener.getClass().newInstance());
				else
					Nexus.warn("Cannot register listener on " + clazz.getSimpleName() + ", needs @NoArgsConstructor");
			} else if (new ArrayList<>(getAllMethods(clazz, withAnnotation(EventHandler.class))).size() > 0)
				Nexus.warn("Found @EventHandlers in " + clazz.getSimpleName() + " which does not implement Listener"
						+ (hasNoArgsConstructor ? "" : " or have a @NoArgsConstructor"));
		} catch (Exception ex) {
			ex.printStackTrace();
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

		if (Strings.isNullOrEmpty(viewTitle))
			return false;

		return viewTitle.equals(title);
	}

	public static boolean containsInvViewTitle(InventoryView view, String title) {
		String viewTitle = getInvTitle(view);

		if (Strings.isNullOrEmpty(viewTitle))
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

}
