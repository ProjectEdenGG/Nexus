package gg.projecteden.nexus.features.noteblocks;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.framework.features.Feature;
import lombok.SneakyThrows;
import org.bukkit.NamespacedKey;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CustomBlocks extends Feature {
	private static final Map<Class<? extends ICustomBlock>, ICustomBlock> blocks = new HashMap<>();

	@SneakyThrows
	public static ICustomBlock get(Class<? extends ICustomBlock> clazz) {
		ICustomBlock block = clazz.getConstructor(NamespacedKey.class).newInstance(getKey(clazz));
		CustomBlocks.getBlocksMap().put(clazz, block);
		return blocks.computeIfAbsent(clazz, $ -> blocks.put(clazz, block));
	}

	private static final List<ICustomBlock> values = new ArrayList<>();

	static {
		try {
			for (Field field : ICustomBlock.class.getDeclaredFields())
				if (field.get(null) instanceof ICustomBlock ICustomBlock)
					values.add(ICustomBlock);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static List<ICustomBlock> values() {
		return values;
	}

	static Map<Class<? extends ICustomBlock>, ICustomBlock> getBlocksMap() {
		return blocks;
	}

	@NotNull
	private static NamespacedKey getKey(Class<? extends ICustomBlock> block) {
		return getKey(block.getSimpleName().replace("CustomBlock", "").toLowerCase());
	}

	@NotNull
	private static NamespacedKey getKey(String id) {
		final NamespacedKey key = NamespacedKey.fromString(id, Nexus.getInstance());
		if (key == null)
			throw new InvalidInputException("[CustomBlocks] Could not generate NamespacedKey for " + id);
		return key;
	}
}
