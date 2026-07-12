package gg.projecteden.nexus.models.store;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.api.interfaces.DatabaseObject;
import gg.projecteden.api.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.Nexus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import me.libraryaddict.disguise.disguisetypes.DisguiseType;
import me.libraryaddict.disguise.utilities.params.ParamInfoManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Data
@Entity(value = "disguise_permission_config", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class DisguisePermissionConfig implements DatabaseObject {
	@Id
	@NonNull
	private UUID uuid;
	private Map<String, List<DisguiseType>> packages = new ConcurrentHashMap<>();
	private Map<String, List<String>> enabledWatchers = new ConcurrentHashMap<>();

	public void generate() {
		for (var pack : packages.keySet()) {
			List<String> lines = new ArrayList<>();
			lines.add("name: store.disguises." + pack.toLowerCase());
			lines.add("permissions:");
			var types = packages.get(pack);
			for (var type : types) {
				var methods = ParamInfoManager.getDisguiseWatcherMethods(type.getWatcherClass(), true);
				var keys = enabledWatchers.keySet().stream().filter(className -> Arrays.stream(methods).anyMatch(method -> method.getWatcherClass().getSimpleName().equals(className))).collect(Collectors.toList());
				List<String> watchers = new ArrayList<>();
				for (var key : keys)
					watchers.addAll(enabledWatchers.get(key));
				lines.add("- libsdisguises.disguise." + type.name().toLowerCase() + "." + String.join(".", watchers));
			}
			Nexus.log("");
			Nexus.log("plugins/LuckPerms/yaml-storage/groups/store.disguises." + pack.toLowerCase() + ".yml");
			Nexus.log(String.join("\n", lines));
		}
	}
}
