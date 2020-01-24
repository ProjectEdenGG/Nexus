package me.pugabyte.bncore.features.commands;

import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils;
import org.bukkit.Location;

import java.text.DecimalFormat;

public class CalcSpeedCommand extends CustomCommand {

	public CalcSpeedCommand(CommandEvent event) {
		super(event);
	}

	@Path
	void speed() {
		new Countdown1(3);
	}

	private class Countdown1 {
		private int taskId;
		private int seconds;

		Countdown1(int seconds) {
			this.seconds = seconds;
			start();
		}

		void start() {
			taskId = Utils.repeat(0, 20, () -> {
				if (seconds < 1) {
					send("&3Calculating...");
					new Countdown2();
					stop();
				} else {
					send("&3Starting in " + seconds + "...");
				}
				seconds--;
			});
		}

		void stop() {
			Utils.cancelTask(taskId);
		}
	}

	Location[] locations = new Location[4];

	private class Countdown2 {
		private int taskId;
		private int loops = 0;

		Countdown2() {
			start();
		}

		void start() {
			taskId = Utils.repeat(0, 5, () -> {
				locations[loops] = player().getLocation();
				if (loops == 3) {
					calculate();
					stop();
				}
				loops++;
			});
		}

		void stop() {
			Utils.cancelTask(taskId);
		}
	}

	double dist1, dist2, dist3, average, bph, kph, mph;

	void calculate() {
		dist1 = locations[0].distance(locations[1]);
		dist2 = locations[1].distance(locations[2]);
		dist3 = locations[2].distance(locations[3]);

		average = ((dist1 + dist2 + dist3) / 3) * 4;
		bph = average * 3600;
		kph = average * 3.6;
		mph = kph * .621371;

		DecimalFormat nf = new DecimalFormat("#.00");

		send("&3Average: &e" + nf.format(average) + "&3 | BPH: &e" + nf.format(bph) + "&3 | KPH: &e" + nf.format(kph) + " &3 | MPH: &e" + nf.format(mph));
	}

}
