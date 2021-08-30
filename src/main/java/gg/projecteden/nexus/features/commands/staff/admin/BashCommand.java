package gg.projecteden.nexus.features.commands.staff.admin;

import com.google.common.base.Strings;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Async;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.utils.StringUtils;
import gg.projecteden.utils.Utils;
import lombok.NonNull;

@Permission("group.admin")
public class BashCommand extends CustomCommand {

	public BashCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<command...>")
	@Async
	void run(String command) {
		send(tryExecute(command));
	}

	public static String tryExecute(String command) {
		String PREFIX = StringUtils.getPrefix("Bash");
		try {
			String result = Utils.bash(command);
			if (Strings.isNullOrEmpty(result))
				return PREFIX + "Command executed successfully";
			else
				return PREFIX + "&7" + result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

}
