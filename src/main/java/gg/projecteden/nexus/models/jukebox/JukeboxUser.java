package gg.projecteden.nexus.models.jukebox;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.songplayer.PositionSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.RadioSongPlayer;
import com.xxmicloxx.NoteBlockAPI.songplayer.SongPlayer;
import dev.morphia.annotations.Converters;
import dev.morphia.annotations.Entity;
import dev.morphia.annotations.Id;
import gg.projecteden.mongodb.serializers.UUIDConverter;
import gg.projecteden.nexus.models.PlayerOwnedObject;
import gg.projecteden.nexus.utils.PlayerUtils.OnlinePlayers;
import gg.projecteden.nexus.utils.Tasks;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;
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
	private List<String> owned = new ArrayList<>();

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
		OnlinePlayers.getAll().forEach(songPlayer::addPlayer);
		songPlayer.setTargetLocation(getLocation());
		songPlayer.setPlaying(true);
		songPlayer.setTick((short) tick);

		this.currentSong = jukeboxSong.getName();
		this.songPlayer = songPlayer;
		this.taskIds.add(Tasks.wait(song.getLength(), this::cancel));
		this.taskIds.add(Tasks.repeat(0, 1, () -> {
			if (!isOnline()) {
				cancel();
				return;
			}

			OnlinePlayers.getAll().forEach(songPlayer::addPlayer);
			songPlayer.setTargetLocation(getLocation());
		}));
	}

	public void preview(JukeboxSong jukeboxSong) {
		cancel();

		final Song song = jukeboxSong.getSong();
		final RadioSongPlayer songPlayer = new RadioSongPlayer(song);
		songPlayer.addPlayer(getOnlinePlayer());
		songPlayer.setStereo(true);
		songPlayer.setPlaying(true);

		this.songPlayer = songPlayer;
		this.taskIds.add(Tasks.wait(song.getLength(), this::cancel));
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
