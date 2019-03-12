package me.pugabyte.bncore.features.documentation.commands.models;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@RequiredArgsConstructor
public class Command {
	@Getter
	@Setter
	@NonNull
	private String command, plugin;
	@Getter
	@Setter
	private String usage, description, rank;
	@Getter
	@Setter
	private Set<String> aliases = new HashSet<>();
	@Getter
	@Setter
	private boolean enabled = false;

}
