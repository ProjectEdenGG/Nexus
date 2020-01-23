package me.pugabyte.bncore.features.tameables.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.With;
import org.bukkit.OfflinePlayer;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class TameablesAction {
	@NonNull
	private TameablesActionType type;
	@With
	private OfflinePlayer player;


}
