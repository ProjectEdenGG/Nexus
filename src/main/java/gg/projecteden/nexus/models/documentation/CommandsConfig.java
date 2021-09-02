package gg.projecteden.nexus.models.documentation;

import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.annotations.Async;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Confirm;
import gg.projecteden.nexus.framework.commands.models.annotations.Cooldown;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.DescriptionExtra;
import gg.projecteden.nexus.framework.commands.models.annotations.HideFromHelp;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.persistence.serializer.mongodb.LocationConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Data
@Builder
@Entity(value = "commands", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters({UUIDConverter.class, LocationConverter.class})
public class CommandsConfig implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private List<CommandConfig> commands = new ArrayList<>();

	public CommandConfig get(String plugin, String command) {
		for (CommandConfig _command : commands)
			if (_command.getCommand().equalsIgnoreCase(command))
				if (_command.getPlugin().equalsIgnoreCase(plugin))
					return _command;
		return null;
	}

	public boolean isDuplicate(CommandConfig command) {
		return get(command.getPlugin(), command.getCommand()) != null;
	}

	public void add(CommandConfig command) {
		if (isDuplicate(command))
			return;

		commands.add(command);
	}

	public void addAll(List<CommandConfig> previousCommands) {
		for (CommandConfig previousCommand : previousCommands)
			if (!isDuplicate(previousCommand))
				commands.add(previousCommand);
	}

	@Data
	@NoArgsConstructor
	public static class CommandConfig {
		@NonNull
		private String plugin, command;
		private String description, descriptionExtra, permission;
		private Set<String> aliases = new HashSet<>();
		private Set<CommandPath> paths = new HashSet<>();
		private boolean confirmationMenu;
		private int cooldown;
		private boolean cooldownGlobal;
		private String cooldownBypass;
		private boolean enabled = true;

		public CommandConfig(String plugin, String name) {
			PluginCommand pluginCommand = Bukkit.getServer().getPluginCommand(name);
			if (pluginCommand != null) {
				name = pluginCommand.getName();
				plugin = pluginCommand.getPlugin().getName();
				description = pluginCommand.getDescription();
				aliases = new HashSet<>(pluginCommand.getAliases());
			}

			this.plugin = plugin;
			this.command = name;
		}

		public CommandConfig(Plugin plugin, CustomCommand command) {
			this.plugin = plugin.getName();
			this.command = command.getName().toLowerCase();

			Class<? extends CustomCommand> clazz = command.getClass();

			Aliases aliases = clazz.getAnnotation(Aliases.class);
			if (aliases != null)
				this.aliases = Set.of(aliases.value());

			Description description = clazz.getAnnotation(Description.class);
			if (description != null)
				this.description = description.value();
			DescriptionExtra descriptionExtra = clazz.getAnnotation(DescriptionExtra.class);
			if (descriptionExtra != null)
				this.descriptionExtra = descriptionExtra.value();

			Permission permission = clazz.getAnnotation(Permission.class);
			if (permission != null)
				this.permission = permission.value();

			Cooldown cooldown = clazz.getAnnotation(Cooldown.class);
			this.cooldown = 0;
			if (cooldown != null) {
				this.cooldown = cooldown.value().x(cooldown.x());
				this.cooldownGlobal = cooldown.global();
				this.cooldownBypass = cooldown.bypass();
			}

			this.confirmationMenu = clazz.getAnnotation(Confirm.class) != null;

			this.paths = command.getPathMethods().stream()
					.filter(method -> method.getAnnotation(HideFromHelp.class) == null)
					.filter(method -> !method.getAnnotation(Path.class).value().equals("help"))
					.map(method -> new CommandPath(this, method))
					.collect(Collectors.toSet());
		}

		@Data
		@NoArgsConstructor
		public static class CommandPath {
			@NonNull
			private String path;
			private String description, descriptionExtra, permission;
			private boolean async;
			private int cooldown;
			private boolean cooldownGlobal;
			private String cooldownBypass;
			private Set<CommandPathArgument> arguments = new HashSet<>();

			public CommandPath(CommandConfig commandConfig, Method method) {
				Path path = method.getAnnotation(Path.class);
				this.path = path.value();

				this.async = method.getAnnotation(Async.class) != null;

				Description description = method.getAnnotation(Description.class);
				if (description != null)
					this.description = description.value();
				DescriptionExtra descriptionExtra = method.getAnnotation(DescriptionExtra.class);
				if (descriptionExtra != null)
					this.descriptionExtra = descriptionExtra.value();

				Permission permission = method.getAnnotation(Permission.class);
				if (permission != null) {
					if (permission.absolute() || commandConfig.getPermission() == null)
						this.permission = permission.value();
					else
						this.permission = commandConfig.getPermission() + "." + permission.value();
				}

				Cooldown cooldown = method.getAnnotation(Cooldown.class);
				this.cooldown = 0;
				if (cooldown != null) {
					this.cooldown = cooldown.value().x(cooldown.x());
					this.cooldownGlobal = cooldown.global();
					this.cooldownBypass = cooldown.bypass();
				}

				this.arguments = Arrays.stream(method.getParameters())
						.map(CommandPathArgument::new)
						.collect(Collectors.toSet());
			}

			@Data
			@NoArgsConstructor
			public static class CommandPathArgument {
				private String defaultValue, permission, minMaxBypass, regex;
				private double min, max;
				private boolean switchArg;
				private char shorthand;

				public CommandPathArgument(Parameter parameter) {
					Arg arg = parameter.getAnnotation(Arg.class);
					if (arg != null) {
						this.defaultValue = arg.value();
						this.permission = arg.permission();
						this.min = arg.min();
						this.max = arg.max();
						this.minMaxBypass = arg.minMaxBypass();
						this.regex = arg.regex();
					}

					Switch switchArg = parameter.getAnnotation(Switch.class);
					if (switchArg != null) {
						this.switchArg = true;
						this.shorthand = switchArg.shorthand();
					}
				}

			}

		}

	}

}
