package gg.projecteden.nexus.framework.commandsv2.modelsv2;

import gg.projecteden.nexus.framework.commandsv2.events.CommandEvent;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.ArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.LiteralArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.CustomCommandMeta.PathMeta.VariableArgumentMeta;
import gg.projecteden.nexus.framework.commandsv2.modelsv2.validators.common.AbstractArgumentValidator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import static gg.projecteden.api.common.utils.Nullables.isNullOrEmpty;

@Data
@AllArgsConstructor
public class CustomCommandMetaInstance {
	protected CustomCommandMeta commandMeta;
	protected CommandEvent event;

	@Data
	@AllArgsConstructor
	public class PathMetaInstance {
		protected PathMeta pathMeta;
		protected List<ArgumentMetaInstance> argumentMetaInstances;

		public CommandEvent getEvent() {
			return event;
		}

		public String getUsage() {
			return argumentMetaInstances.stream().map(ArgumentMetaInstance::getUsage).collect(Collectors.joining(" ")).trim();
		}

		public List<VariableArgumentMetaInstance> getVariableArgumentMetaInstances() {
			return argumentMetaInstances.stream()
				.filter(argumentMetaInstance -> argumentMetaInstance instanceof VariableArgumentMetaInstance)
				.map(argumentMetaInstance -> (VariableArgumentMetaInstance) argumentMetaInstance)
				.toList();
		}

		public void validate() {
			argumentMetaInstances.forEach(argumentMetaInstance -> {
				if (argumentMetaInstance instanceof VariableArgumentMetaInstance variableArgumentMetaInstance) {
					variableArgumentMetaInstance.validate();
				}
			});
		}

		@Data
		@NoArgsConstructor
		public abstract class ArgumentMetaInstance {
			protected String input;
			protected String originalInput;

			public ArgumentMetaInstance(String input) {
				this.input = input;
				this.originalInput = input;
			}

			abstract ArgumentMetaInstance getArgumentMeta();

			public CommandEvent getEvent() {
				return event;
			}

			public abstract String getUsage();
		}

		@Data
		public class LiteralArgumentMetaInstance extends ArgumentMetaInstance {
			protected LiteralArgumentMeta argumentMeta;

			public LiteralArgumentMetaInstance(LiteralArgumentMeta argumentMeta, String input) {
				super(input);
				this.argumentMeta = argumentMeta;
			}

			@Override
			public String getUsage() {
				return argumentMeta.getName();
			}

		}

		@Data
		public class VariableArgumentMetaInstance extends ArgumentMetaInstance {
			protected VariableArgumentMeta argumentMeta;

			public VariableArgumentMetaInstance(VariableArgumentMeta argumentMeta, String input) {
				super(input);
				this.argumentMeta = argumentMeta;
			}

			public void validate() {
				for (AbstractArgumentValidator validator : argumentMeta.getValidators())
					input = validator.validate(this);
			}

			@Override
			public String getUsage() {
				if (event != null && !event.getSender().hasPermission(argumentMeta.getPermission()))
					return "";

				return argumentMeta.getUsage();

			}

		}
	}

	@Data
	@Builder
	public static class CooldownMeta {
		private long ticks;
		private boolean global;
		private String bypassPermission;
	}
}
