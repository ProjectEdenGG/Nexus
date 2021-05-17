package me.pugabyte.nexus.features.commands;

import me.pugabyte.nexus.framework.commands.models.CustomCommand;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Description;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.utils.Tasks;
import org.bukkit.Location;

import java.text.DecimalFormat;

@Description("Calculate your movement speed")
public class CalcSpeedCommand extends CustomCommand {
	Location[] locations = new Location[4];

	public CalcSpeedCommand(CommandEvent event) {
		super(event);
	}

	@Path("[startDelay]")
	void speed(@Arg("3") int startDelay) {
		line();
		Tasks.Countdown.builder()
				.duration(startDelay * 20)
				.onSecond(i -> send("&3Starting in &e" + i + "&3..."))
				.onComplete(() -> {
					send("&eCalculating...");
					Tasks.Countdown.builder()
							.duration(15)
							.doZero(true)
							.onTick(i -> {
								if (i % 5 == 0)
									locations[i / 5] = location();
							})
							.onComplete(this::calculate)
							.start();
				})
				.start();
	}

	double dist1, dist2, dist3, bps, bph, kph, mph;

	void calculate() {
		dist1 = locations[0].distance(locations[1]);
		dist2 = locations[1].distance(locations[2]);
		dist3 = locations[2].distance(locations[3]);

		bps = ((dist1 + dist2 + dist3) / 3) * 4;
		bph = bps * 3600;
		kph = bps * 3.6;
		mph = kph * .621371;

		DecimalFormat nf = new DecimalFormat("#.00");

		send("&3" +
				"BPS: &e" + nf.format(bps) + "&3 | " +
				"BPH: &e" + nf.format(bph) + "&3 | " +
				"KPH: &e" + nf.format(kph) + "&3 | " +
				"MPH: &e" + nf.format(mph));
	}

}
