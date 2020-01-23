package me.pugabyte.bncore.models.rules;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
public class HasReadRules {
	@NonNull
	private String uuid;
	private boolean main;
	private boolean community1;
	private boolean community2;
	private boolean community3;
	private boolean streaming;
	private boolean survival;
	private boolean minigames;
	private boolean creative;
	private boolean skyblock;

}
