package gg.projecteden.nexus.framework.commandsv2;

import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.models.ICustomCommand;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandRegistry;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
public class Commands {
	private final Plugin plugin;
	private final List<String> paths;
	private final CommandMapUtils mapUtils;
	private final CustomCommandRegistry registry;
	private final CommandListener listener;
	public static final String VALID_COMMAND_PATTERN = "(\\/){1,2}[\\w\\-]+";

	public Commands(Plugin plugin, String path) {
		this(plugin, Collections.singletonList(path));
	}

	public Commands(Plugin plugin, List<String> paths) {
		this.plugin = plugin;
		this.paths = paths;
		this.mapUtils = new CommandMapUtils(plugin);
		this.registry = new CustomCommandRegistry(this);
		this.listener = new CommandListener(this);
		plugin.getServer().getPluginManager().registerEvents(listener, plugin);
	}

	public <C extends CustomCommand> Set<CustomCommandMeta> getUniqueCommands() {
		return new HashSet<>(registry.getRegisteredCommandsByAlias().values());
	}

	public CustomCommandMeta get(String alias) {
		return registry.getRegisteredCommandsByAlias().getOrDefault(alias.toLowerCase(), null);
	}

	public CustomCommandMeta get(Class<? extends CustomCommand> clazz) {
		return registry.getRegisteredCommandsByAlias().values().stream().filter(meta -> meta.getClazz() == clazz).findFirst().orElse(null);
	}

	public static String prettyName(ICustomCommand customCommand) {
		return prettyName(customCommand.getClass());
	}

	public static String prettyName(Class<? extends ICustomCommand> clazz) {
		return clazz.getSimpleName().replaceAll("Command$", "");
	}

	public static String getPrefix(ICustomCommand customCommand) {
		return getPrefix(customCommand.getClass());
	}

	public static String getPrefix(Class<? extends ICustomCommand> clazz) {
		return StringUtils.getPrefix(prettyName(clazz));
	}

}
