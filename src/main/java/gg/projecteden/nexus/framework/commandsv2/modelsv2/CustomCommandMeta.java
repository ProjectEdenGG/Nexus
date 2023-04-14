package gg.projecteden.nexus.framework.commandsv2.modelsv2;

import com.google.errorprone.annotations.Var;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Fallback;
import gg.projecteden.nexus.framework.commandsv2.annotations.command.Redirects.Redirect;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.Cooldown;
import gg.projecteden.nexus.framework.commandsv2.annotations.shared.WikiConfig;
import gg.projecteden.nexus.framework.commandsv2.models.CustomCommand;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.CooldownMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMetaInstance.PathMetaInstance.VariableArgumentMetaInstance;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common.AbstractArgumentValidator;
import lombok.Builder;
import lombok.Data;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Data
public class CustomCommandMeta {
	protected String name;
	protected List<String> aliases;
	protected Class<? extends CustomCommand> clazz;
	protected CustomCommand instance;
	protected boolean async;
	protected String permission;
	protected List<PathMeta> paths;
	protected String description;
	protected String descriptionExtra;
	protected Cooldown cooldown;
	protected boolean doubleSlash;
	protected List<Redirect> redirects;
	protected WikiConfig wikiConfig;
	protected boolean hideFromWiki;
	protected Fallback fallback;

	public List<String> getAllAliases() {
		return new ArrayList<>(this.aliases) {{ add(name); }}.stream()
			.map(String::toLowerCase)
			.collect(Collectors.toList());
	}

	@Data
	public class PathMeta {
		protected Method method;
		protected List<ArgumentMeta> arguments;
		protected String permission;
		protected String description;
		protected String descriptionExtra;
		protected boolean ignoreTabComplete;
		protected String ignoreTabCompleteBypass;
		protected boolean hideFromHelp;
		protected boolean hideFromWiki;
		protected Cooldown cooldown;
		protected boolean confirmMenu;
		protected WikiConfig wikiConfig;

		public List<LiteralArgumentMeta> getLiterals() {
			return arguments.stream()
				.filter(argumentMeta -> argumentMeta instanceof LiteralArgumentMeta)
				.map(argumentMeta -> (LiteralArgumentMeta) argumentMeta)
				.toList();
		}

		public List<VariableArgumentMeta> getVariables() {
			return arguments.stream()
				.filter(argumentMeta -> argumentMeta instanceof VariableArgumentMeta)
				.map(argumentMeta -> (VariableArgumentMeta) argumentMeta)
				.toList();
		}

		public String getUsage() {
			return arguments.stream().map(ArgumentMeta::getUsage).collect(Collectors.joining(" "));
		}

		public List<VariableArgumentMeta> getRequired() {
			return getVariables().stream().filter(VariableArgumentMeta::isRequired).toList();
		}

		@Data
		public abstract class ArgumentMeta {
			protected String name;

			public abstract String getUsage();

		}

		@Data
		public class LiteralArgumentMeta extends ArgumentMeta {

			@Override
			public String getUsage() {
				return name;
			}

		}

		@Data
		public class VariableArgumentMeta extends ArgumentMeta {
			protected Class<?> type;
			protected Class<?> erasureType;
			protected String defaultValue;
			protected boolean vararg;
			protected boolean required;
			protected String permission;
			protected boolean switchArgument;
			protected Character switchShorthand;
			protected int context;
			protected Class<?> tabCompleter;
			protected boolean stripColor;
			protected List<AbstractArgumentValidator> validators = new ArrayList<>();

			@Override
			public String getUsage() {
				String usage = name;

				if (vararg)
					usage += "...";

				if (switchArgument)
					usage = "--" + usage;

				if (!isNullOrEmpty(defaultValue))
					usage += "=" + defaultValue;

				if (required)
					usage = "<" + usage + ">";
				else
					usage = "[" + usage + "]";

				return usage;
			}

		}
	}
}
