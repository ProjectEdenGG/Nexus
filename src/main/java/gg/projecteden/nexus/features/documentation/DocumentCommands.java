package gg.projecteden.nexus.features.documentation;

import com.destroystokyo.paper.event.server.AsyncTabCompleteEvent;
import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.Commands;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.models.documentation.CommandsConfig;
import gg.projecteden.nexus.models.documentation.CommandsConfig.CommandConfig;
import gg.projecteden.nexus.models.documentation.CommandsConfig.CommandConfig.CommandPath;
import gg.projecteden.nexus.models.documentation.CommandsConfig.CommandConfig.CommandPath.CommandPathArgument;
import gg.projecteden.nexus.models.documentation.CommandsConfigService;
import gg.projecteden.nexus.utils.IOUtils;
import gg.projecteden.nexus.utils.Tasks;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class DocumentCommands {
	private final CommandsConfigService service = new CommandsConfigService();
	private final CommandsConfig commandsConfig = service.get0();

	public DocumentCommands() {
		Tasks.waitAsync(20, () -> {
			List<CommandConfig> previousCommands = new ArrayList<>(commandsConfig.getCommands());
			commandsConfig.getCommands().clear();

			findCustomCommands();
			findExternalCommands();

			previousCommands.forEach(previous -> previous.setEnabled(false));
			commandsConfig.addAll(previousCommands);

			service.save(commandsConfig);
			Nexus.log("Documented " + commandsConfig.getCommands().size() + " commands");

			generateCsv();
		});
	}

	private void generateCsv() {
		List<String> headers = Arrays.asList(
				"plugin",
				"command",
				"aliases",
				"description",
				"descriptionExtra",
				"permission",
				"cooldown",
				"isCooldownGlobal",
				"cooldownBypassPermission",
				"path",
				"path_description",
				"path_descriptionExtra",
				"path_permission",
				"path_cooldown",
				"path_isCooldownGlobal",
				"path_cooldownBypassPermission",
				"path_arg_defaultValue",
				"path_arg_permission",
				"path_arg_min",
				"path_arg_max",
				"path_arg_minMaxBypassPermission",
				"path_arg_regex",
				"path_arg_isSwitch",
				"path_arg_switchShorthand"
		);

		IOUtils.getLogsFile("commands.csv").delete();

		IOUtils.csvAppend("commands", "\"" + String.join("\",\"", headers) + "\"");

		for (CommandConfig command : commandsConfig.getCommands()) {
			List<String> columns = new ArrayList<>();
			columns.add(command.getPlugin());
			columns.add(command.getCommand());
			columns.add(String.join(", ", command.getAliases()));
			columns.add(command.getDescription());
			columns.add(command.getDescriptionExtra());
			columns.add(command.getPermission());

			if (command.getCooldown() > 0) {
				columns.add(String.valueOf(command.getCooldown()));
				columns.add(String.valueOf(command.isCooldownGlobal()));
				columns.add(command.getCooldownBypass());
			} else {
				columns.add("");
				columns.add("");
				columns.add("");
			}

			var commandColumns = new ArrayList<>(columns);

			for (CommandPath path : command.getPaths()) {
				columns = new ArrayList<>(commandColumns);
				columns.add(path.getPath());
				columns.add(path.getDescription());
				columns.add(path.getDescriptionExtra());
				columns.add(path.getPermission());
				if (command.getCooldown() > 0) {
					columns.add(String.valueOf(path.getCooldown()));
					columns.add(String.valueOf(path.isCooldownGlobal()));
					columns.add(path.getCooldownBypass());
				} else {
					columns.add("");
					columns.add("");
					columns.add("");
				}

				for (CommandPathArgument argument : path.getArguments()) {
					columns.add(argument.getDefaultValue());
					columns.add(argument.getPermission());
					columns.add(String.valueOf(argument.getMin()));
					columns.add(String.valueOf(argument.getMax()));
					columns.add(argument.getMinMaxBypass());
					columns.add(argument.getRegex());
					columns.add(String.valueOf(argument.isSwitchArg()));
					if (argument.isSwitchArg() && argument.getShorthand() != '-')
						columns.add(String.valueOf(argument.getShorthand()));
					else
						columns.add("");
				}

				IOUtils.csvAppend("commands", "\"" + String.join("\",\"", columns) + "\"");
			}
		}

		Nexus.log("commands.csv regenerated");
	}

	public void findCustomCommands() {
		for (CustomCommand command : Commands.getCommands().values())
			commandsConfig.add(new CommandConfig(Nexus.getInstance(), command));
	}

	private void findExternalCommands() {
		AsyncTabCompleteEvent tabEvent = new AsyncTabCompleteEvent(Bukkit.getConsoleSender(), "", true, null);
		tabEvent.callEvent();

		List<String> eventCompletions = tabEvent.getCompletions();
		List<String> completions = eventCompletions.stream()
				.filter(completion -> completion.contains(":"))
				.collect(Collectors.toList());

		for (String commandString : completions) {
			String[] split = commandString.split(":");
			if (split.length == 2 && split[0].length() > 0)
				commandsConfig.add(new CommandConfig(split[0], split[1]));
		}
	}
}
