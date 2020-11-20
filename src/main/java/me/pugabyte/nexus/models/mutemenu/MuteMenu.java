package me.pugabyte.nexus.models.mutemenu;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
public class MuteMenu {

	@NonNull
	private String uuid;
	private boolean global = false;
	private boolean local = false;
	private boolean minigames = false;
	private boolean creative = false;
	private boolean skyblock = false;
	private boolean auto = false;
	private boolean jq = false;
	private boolean firstJoin = false;
	private boolean jqSounds = false;
	private boolean AFK = false;
	private boolean minigameAnnouncements = false;
}
