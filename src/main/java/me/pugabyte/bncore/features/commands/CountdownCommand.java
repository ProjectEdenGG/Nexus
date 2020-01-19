package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;

public class CountdownCommand extends CustomCommand {

	public CountdownCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void countdown() {
		new Countdown();
	}

	private class Countdown {
		private int seconds = 5;
		private int taskId;

		Countdown() {
			start();
		}

		void start() {
			taskId = Utils.repeat(0, 20, () -> {
				if (seconds == 0) {
					runCommand("ch qm l Go!");
					stop();
					return;
				}

				runCommand("ch qm l " + seconds-- + "...");
			});
		}

		void stop() {
			Utils.cancelTask(taskId);
		}
	}
}
