package gg.projecteden.nexus.features.commands.staff;

import de.tr7zw.nbtapi.NBTEntity;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.logging.log4j.util.TriConsumer;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Permission(Group.STAFF)
public class EntityNBTCommand extends CustomCommand {

	public EntityNBTCommand(CommandEvent event) {
		super(event);
	}

	@Path
	@Description("View the NBT data of your target entity")
	void nbt() {
		send(new NBTEntity(getTargetEntityRequired()).asNBTString());
	}

	@Path("nearest")
	@Description("View the NBT data of the nearest entity")
	void nearest() {
		send(new NBTEntity(getNearestEntityRequired()).asNBTString());
	}

	@Path("uuid [--nearest]")
	@Description("View the UUID of your target entity")
	void getUuid(@Switch boolean nearest) {
		final UUID uuid;
		if (nearest)
			uuid = getNearestEntityRequired().getUniqueId();
		else
			uuid = getTargetEntityRequired().getUniqueId();
		send(json("&e" + uuid).copy(uuid.toString()));
	}

	@Path("set <key> <type> <value>")
	@Description("Set an NBT key on your target entity")
	void set(NamespacedKey key, PersistentDataTypeType type, String value) {
		type.getConsumer().accept(getTargetEntityRequired().getPersistentDataContainer(), key, value);
		send(PREFIX + "Set nbt key &e" + key + " &3to (&e" + camelCase(type) + "&3) &e" + value);
	}

	@Path("unset <key>")
	@Description("Remove an NBT key on your target entity")
	void nbt(NamespacedKey key) {
		getTargetEntityRequired().getPersistentDataContainer().remove(key);
		send(PREFIX + "Removed nbt key &e" + key);
	}

	@Getter
	@AllArgsConstructor
	private enum PersistentDataTypeType {
		BYTE((container, key, value) -> container.set(key, PersistentDataType.BYTE, Byte.parseByte(value))),
		SHORT((container, key, value) -> container.set(key, PersistentDataType.SHORT, Short.parseShort(value))),
		INTEGER((container, key, value) -> container.set(key, PersistentDataType.INTEGER, Integer.parseInt(value))),
		LONG((container, key, value) -> container.set(key, PersistentDataType.LONG, Long.parseLong(value))),
		FLOAT((container, key, value) -> container.set(key, PersistentDataType.FLOAT, Float.parseFloat(value))),
		DOUBLE((container, key, value) -> container.set(key, PersistentDataType.DOUBLE, Double.parseDouble(value))),
		STRING((container, key, value) -> container.set(key, PersistentDataType.STRING, value)),
		;

		private final TriConsumer<PersistentDataContainer, NamespacedKey, String> consumer;
	}

	@ConverterFor(NamespacedKey.class)
	NamespacedKey convertToNamespacedKey(String value) {
		if (!value.contains(":"))
			error("Key must contain a namespace (&enamespace:key&c)");

		final String[] split = value.split(":", 2);
		return new NamespacedKey(split[0], split[1]);
	}

	@TabCompleterFor(NamespacedKey.class)
	List<String> tabCompleteNamespacedKey(String filter) {
		final Entity target = getTargetEntity();
		if (target == null)
			return Collections.emptyList();

		return target.getPersistentDataContainer().getKeys().stream()
			.map(NamespacedKey::asString)
			.filter(key -> key.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

}
