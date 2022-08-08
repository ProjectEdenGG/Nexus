package gg.projecteden.nexus.features.justice.misc;

import gg.projecteden.nexus.framework.commands.models.annotations.Path;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission;
import gg.projecteden.nexus.framework.commands.models.annotations.Permission.Group;
import gg.projecteden.nexus.framework.commands.models.events.CommandEvent;
import gg.projecteden.nexus.models.nerd.Nerd;
import gg.projecteden.nexus.models.nickname.Nickname;
import gg.projecteden.nexus.models.playernotes.PlayerNotes;
import gg.projecteden.nexus.models.playernotes.PlayerNotesService;
import gg.projecteden.nexus.utils.JsonBuilder;
import gg.projecteden.nexus.utils.StringUtils;
import lombok.NonNull;
import org.bukkit.entity.Player;

import java.util.List;

@Permission(Group.STAFF)
public class NotesCommand extends _JusticeCommand {

	private static final PlayerNotesService SERVICE = new PlayerNotesService();

	public NotesCommand(@NonNull CommandEvent event) { super(event); }

	@Path("<player>")
	void player(Player player) {
		PlayerNotes notes = SERVICE.get(player);
		if (notes.getEntries().isEmpty())
			error(new JsonBuilder("There are no notes for " + Nickname.of(player) + ". &eClick here to add a note")
					.suggest("/notes add " + player.getName() + " "));
		send(PREFIX + "Notes for &e" + Nickname.of(player));
		line();
		List<PlayerNotes.PlayerNoteEntry> entries = notes.getEntries();
		for (int i = 0, entriesSize = entries.size(); i < entriesSize; i++) {
			PlayerNotes.PlayerNoteEntry entry = entries.get(i);
			send("&e - " + entry.getNote());
			send(new JsonBuilder("&f  &3Added by: &e" + Nickname.of(entry.getAddedUser()))
					.group()
					.next(" &e| &c")
					.group()
					.next(StringUtils.X)
					.command("/notes remove " + player.getName() + " " + (i + 1))
					.hover("&cClick to delete"));
			line();
		}
	}

	@Path("remove <player> <id>")
	void remove(Player player, int id) {
		PlayerNotes notes = SERVICE.get(player);
		if (notes.getEntries().size() < id)
			error("Invalid entry index");
		notes.removeEntry(id - 1);
		SERVICE.save(notes);
		send(PREFIX + "Removed note #" + id);
	}

	@Path("add <player> <note...>")
	void add(Player player, String note) {
		PlayerNotes notes = SERVICE.get(player);
		notes.addEntry(player(), note);
		SERVICE.save(notes);
		send(PREFIX + "Added the note &e" + note + " &3to &e" + Nickname.of(player));
	}

	@Path("search <keyword...>")
	void search(String keyword) {
		List<PlayerNotes> notes = SERVICE.getByKeyword(keyword);
		if (notes.isEmpty())
			error("Could not find any related notes");
		send(PREFIX + "Found " + notes.size() + " results");
		line();
		for (PlayerNotes note : notes) {
			send(new JsonBuilder("&e - " + Nickname.of(note.getUuid()) + " &3| &eClick to see all notes")
					.hover("&3Click to see all notes")
					.command("/notes " + Nerd.of(note.getUuid()).getName()));
			for (PlayerNotes.PlayerNoteEntry entry : note.getEntries())
				if (entry.getNote().contains(keyword))
					send("&f  &3" + entry.getNote());
			line();
		}
	}

}
