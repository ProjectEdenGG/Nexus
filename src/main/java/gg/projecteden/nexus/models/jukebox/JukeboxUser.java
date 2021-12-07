package gg.projecteden.nexus.models.jukebox;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.model.SoundCategory;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.features.commands.MuteMenuCommand.MuteMenuProvider.MuteMenuItem;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.models.mutemenu.MuteMenuUser;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@Entity(value = "jukebox_user", noClassnameStored = true)
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Converters(UUIDConverter.class)
public class JukeboxUser implements PlayerOwnedObject {
	@Id
	@NonNull
	private UUID uuid;
	private Set<String> owned = new HashSet<>();

	private String currentSong;
	private int currentTick;

	private transient SongPlayer songPlayer;
	private transient List<Integer> taskIds = new ArrayList<>();

	public boolean owns(JukeboxSong song) {
		return owned.contains(song.getName());
	}

	public void give(JukeboxSong song) {
		owned.add(song.getName());
	}

	public void play(JukeboxSong jukeboxSong) {
		play(jukeboxSong, 0);
	}

	public void play(JukeboxSong jukeboxSong, int tick) {
		cancel();

		final Song song = jukeboxSong.getSong();
		final PositionSongPlayer songPlayer = new PositionSongPlayer(song);
		updatePlayers(songPlayer);
		songPlayer.setTargetLocation(getLocation());
		songPlayer.setPlaying(true);
		songPlayer.setTick((short) tick);
		songPlayer.setCategory(SoundCategory.RECORDS);

		this.currentSong = jukeboxSong.getName();
		this.songPlayer = songPlayer;
		this.taskIds.add(Tasks.repeat(0, 1, () -> {
			if (!isOnline()) {
				cancel();
				return;
			}

			songPlayer.setTargetLocation(getLocation());
			updatePlayers(songPlayer);
		}));
	}

	private void updatePlayers(SongPlayer songPlayer) {
		for (Player player : OnlinePlayers.getAll()) {
			if (MuteMenuUser.hasMuted(player, MuteMenuItem.JUKEBOX))
				songPlayer.removePlayer(player);
			else
				songPlayer.addPlayer(player);
		}
	}

	public void preview(JukeboxSong jukeboxSong) {
		cancel();

		final Song song = jukeboxSong.getSong();
		final RadioSongPlayer songPlayer = new RadioSongPlayer(song);
		songPlayer.addPlayer(getOnlinePlayer());
		songPlayer.setStereo(true);
		songPlayer.setPlaying(true);
		songPlayer.setCategory(SoundCategory.RECORDS);

		this.songPlayer = songPlayer;
	}

	public boolean cancel() {
		currentSong = null;
		stop();
		return true;
	}

	public void stop() {
		if (songPlayer != null) {
			songPlayer.destroy();
			songPlayer = null;
		}

		taskIds.forEach(Tasks::cancel);
		taskIds.clear();
	}

	public void pause() {
		if (songPlayer != null) {
			currentTick = songPlayer.getTick();
		}

		stop();
	}

}
