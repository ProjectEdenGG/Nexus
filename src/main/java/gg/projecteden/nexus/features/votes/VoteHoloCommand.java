package gg.projecteden.nexus.features.votes;

import gg.projecteden.nexus.Nexus;
import gg.projecteden.nexus.framework.commands.models.CustomCommand;
import gg.projecteden.nexus.framework.commands.models.annotations.Description;
import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.WikiConfig;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import lombok.NonNull;
import tech.blastmc.holograms.api.HologramsAPI;
import tech.blastmc.holograms.api.models.Hologram;

import static gg.projecteden.nexus.features.listeners.Restrictions.isPerkAllowedAt;

@Permission("vote.holo")
@Description("Modify the hologram you received from vote rewards")
@WikiConfig(rank = "Guest", feature = "Vote")
public class VoteHoloCommand extends CustomCommand {

	public VoteHoloCommand(@NonNull CommandEvent event) {
		super(event);
	}

	@Path("create <text...>")
	@Description("Create a hologram")
	void create(String text) {
		if (!isPerkAllowedAt(player(), location()))
			error("Holograms cannot be created here");

		try {
			HologramsAPI.builder()
				.id("voteholo_" + uuid())
				.lines(text)
				.location(location())
				.persistent(true)
				.build().spawn();
		} catch (Exception ex) {
			Nexus.log(ex.getMessage());
			error("You already have a hologram. Use &e/voteholo edit");
		}

		send(PREFIX + "Created. Edit with &c/voteholo edit <text...>");
	}

	@Path("edit <text...>")
	@Description("Edit your hologram")
	void edit(String text) {
		Hologram hologram = getHologram();

		hologram.setLine(0, text);
		hologram.save();

		send(PREFIX + "Edited");
	}

	@Path("delete")
	@Description("Delete your hologram")
	void delete() {
		getHologram().remove();

		send(PREFIX + "Deleted");
	}

	@Path("tphere")
	@Description("Summon your hologram")
	void tphere() {
		if (!isPerkAllowedAt(player(), location()))
			error("Holograms cannot be teleported here");

		getHologram().setLocation(location());
		getHologram().save();
	}

	private Hologram getHologram() {
		Hologram hologram = HologramsAPI.byId(world(), "voteholo_" + uuid());
		if (hologram == null)
			error("Could not find your hologram. Are you in the right world?");
		return hologram;
	}
}
