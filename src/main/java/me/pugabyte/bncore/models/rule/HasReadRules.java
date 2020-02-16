package me.pugabyte.bncore.models.rule;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.persistence.Table;

@Data
@NoArgsConstructor
@RequiredArgsConstructor
@Table(name = "has_read_rules")
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
