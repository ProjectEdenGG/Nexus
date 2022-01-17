package gg.projecteden.nexus.features.commands.staff.admin;

import gg.projecteden.mongodb.models.scheduledjobs.ScheduledJobs;
import gg.projecteden.mongodb.models.scheduledjobs.ScheduledJobsRunner;
import gg.projecteden.mongodb.models.scheduledjobs.ScheduledJobsService;
import gg.projecteden.mongodb.models.scheduledjobs.common.AbstractJob;
import gg.projecteden.mongodb.models.scheduledjobs.common.AbstractJob.JobStatus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.ConverterFor;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.TabCompleterFor;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.framework.exceptions.postconfigured.InvalidInputException;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.Data;
import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aliases("scheduledjob")
@Permission(Group.ADMIN)
public class ScheduledJobsCommand extends CustomCommand {
	private static final ScheduledJobsService service = new ScheduledJobsService();
	private static final ScheduledJobs jobs = service.getApp();

	public ScheduledJobsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	static {
		ScheduledJobsRunner.start();
	}

	@Override
	public void _shutdown() {
		ScheduledJobsRunner.stop();
	}

	@Data
	private class JobType {
		@NonNull
		private Class<? extends AbstractJob> clazz;
	}

	@Path("schedule <job> <time> <data...>")
	void schedule(JobType jobType, LocalDateTime timestamp, String data) {
		final Class<? extends AbstractJob> job = jobType.getClazz();
		final Constructor<AbstractJob>[] constructors = (Constructor<AbstractJob>[]) job.getDeclaredConstructors();
		if (constructors.length == 0)
			throw new InvalidInputException(job.getSimpleName() + " does not have any constructors");

		if (data == null)
			data = "";

		Exception lastException = null;

		Function<Stream<Class<?>>, String> parameterFormatter = parameters -> parameters
			.map(parameter -> parameter.getSimpleName() + ".class")
			.collect(Collectors.joining("&f, &e"));

		Function<Constructor<?>, String> constructorFormatter = constructor -> " &cnew " + job.getSimpleName() + "(&e"
			+ parameterFormatter.apply(Arrays.stream(constructor.getParameters()).map(Parameter::getType)) + "&c);";

		constructorLoop: for (Constructor<AbstractJob> constructor : constructors) {
			final int parameterCount = constructor.getParameterCount();
			if (parameterCount != 0) {
				String[] args = data.split(" ");
				if (parameterCount != args.length)
					continue;

				Object[] parameters = new Object[parameterCount];
				for (int i = 0; i < args.length; i++) {
					try {
						parameters[i] = convert(args[i], null, constructor.getParameters()[i], event, true);
					} catch (Exception ex) {
						lastException = ex;
						continue constructorLoop;
					}
				}

				try {
					final AbstractJob abstractJob = constructor.newInstance(parameters);
					abstractJob.schedule(timestamp);
					send(StringUtils.toPrettyString(abstractJob));
					return;
				} catch (Exception ex) {
					if (ex instanceof IllegalArgumentException && ex.getMessage().contains("argument type mismatch"))
						throw new InvalidInputException(constructorFormatter.apply(constructor) + " does not match provided ("
							+ parameterFormatter.apply(Arrays.stream(parameters).map(Object::getClass)) + ")");
					rethrow(ex);
				}
			}
 		}

 		if (lastException != null)
 			rethrow(lastException);

 		send(PREFIX + "Could not find a matching constructor, options are:");
		for (Constructor<?> constructor : constructors)
			send(constructorFormatter.apply(constructor));
	}

	@Path("stats")
	void stats() {
		if (jobs.getJobs().isEmpty())
			error("No jobs found");

		send(PREFIX + "Stats");
		for (JobStatus status : JobStatus.values()) {
			final Set<AbstractJob> list = jobs.get(status);
			if (list.isEmpty())
				continue;

			send("&e " + camelCase(status) + " &7- &3" + list.size());
		}
	}

	@ConverterFor(JobType.class)
	JobType convertToJobClass(String value) {
		return AbstractJob.getSubclasses().stream()
			.filter(clazz -> clazz.getSimpleName().equalsIgnoreCase(value))
			.findFirst()
			.map(JobType::new)
			.orElseThrow(() -> new InvalidInputException("Job class from &e" + value + " &cnot found"));
	}

	@TabCompleterFor(JobType.class)
	List<String> tabCompleteJobClass(String filter) {
		return AbstractJob.getSubclasses().stream()
			.map(Class::getSimpleName)
			.filter(className -> className.toLowerCase().startsWith(filter.toLowerCase()))
			.toList();
	}

}
