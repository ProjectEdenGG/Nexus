package me.pugabyte.bncore.features.documentation.commands.models;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Data
@RequiredArgsConstructor
public class Command {
	@NonNull
	private String command, plugin;
	private String usage, description, rank;
	private Set<String> aliases = new HashSet<>();
	private boolean enabled = false;

}
