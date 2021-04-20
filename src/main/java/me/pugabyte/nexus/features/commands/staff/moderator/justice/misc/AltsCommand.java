package me.pugabyte.nexus.features.commands.staff.moderator.justice.misc;

import lombok.NonNull;
import me.pugabyte.nexus.Nexus;
import me.pugabyte.nexus.framework.commands.models.annotations.Arg;
import me.pugabyte.nexus.framework.commands.models.annotations.Async;
import me.pugabyte.nexus.framework.commands.models.annotations.Path;
import me.pugabyte.nexus.framework.commands.models.events.CommandEvent;
import me.pugabyte.nexus.models.punishments.Punishments;
import me.pugabyte.nexus.models.punishments.Punishments.IPHistoryEntry;
import me.pugabyte.nexus.models.punishments.PunishmentsService;

import java.io.BufferedReader;
import java.io.FileReader;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AltsCommand extends _JusticeCommand {

	public AltsCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("<player>")
	void alts(@Arg("self") Punishments player) {
		player.sendAltsMessage(this::send, () -> error("No alts found for &e" + player.getNickname()));
	}

	@Async
	@Path("import")
	void importAlts() {
		runImport();
		PunishmentsService service = new PunishmentsService();
		ExecutorService executor = Executors.newFixedThreadPool(100);

		for (Punishments player : service.getCache().values())
			executor.submit(() -> service.save(player));
	}

	void runImport() {
		String line;
		try {
			BufferedReader br = new BufferedReader(new FileReader(Nexus.getFile("alts.csv")));
			while ((line = br.readLine()) != null) {
				try {
					String[] split = line.split(",");
					Punishments.of(UUID.fromString(split[0])).getIpHistory().add(new IPHistoryEntry(split[2], LocalDateTime.parse(split[1])));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
