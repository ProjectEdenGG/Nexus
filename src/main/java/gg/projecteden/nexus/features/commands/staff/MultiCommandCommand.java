package gg.projecteden.nexus.features.commands.staff;

import gg.projecteden.api.common.utils.UUIDUtils;
import gg.projecteden.api.interfaces.HasUniqueId;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Aliases;
import gg.projecteden.nexus.framework.commands.models.annotations.Arg;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.annotations.Switch;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.PlayerUtils;
import gg.projecteden.nexus.utils.Tasks;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;

import java.net.http.WebSocket.Listener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

@Aliases("mcmd")
@NoArgsConstructor
@Permission(Group.STAFF)
public class MultiCommandCommand extends CustomCommand implements Listener {
	private static final Map<UUID, List<Integer>> TASK_IDS = new HashMap<>();

	public MultiCommandCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<commands...> [--asOp]")
	@Description("Run multiple commands at once separated by ' ;; '")
	void run(
		String input,
		@Arg(permission = Group.ADMIN) @Switch boolean asOp
	) {
		runMultiCommand(asOp, input.split(" ;; "));
	}

	public static void run(CommandSender sender, List<String> commands, boolean asOp) {
		if (commands.isEmpty())
			return;

		AtomicInteger wait = new AtomicInteger(0);
		commands.forEach(command -> {
			if (command.toLowerCase().matches("^wait \\d+$"))
				wait.getAndAdd(Integer.parseInt(command.toLowerCase().replace("wait ", "")));
			else {
				int taskId = Tasks.wait(wait.getAndAdd(3), () -> {
					if (sender instanceof Player player && !player.isOnline())
						return;

					if (asOp)
						PlayerUtils.runCommandAsOp(sender, command);
					else
						PlayerUtils.runCommand(sender, command);
				});

				UUID uuid = UUIDUtils.UUID0;
				if (sender instanceof HasUniqueId player)
					uuid = player.getUniqueId();

				TASK_IDS.computeIfAbsent(uuid, $ -> new ArrayList<>()).add(taskId);
			}
		});
	}

	@EventHandler
	public void on(PlayerQuitEvent event) {
		if (!TASK_IDS.containsKey(event.getPlayer().getUniqueId()))
			return;

		TASK_IDS.remove(event.getPlayer().getUniqueId()).forEach(Tasks::cancel);
	}

}
