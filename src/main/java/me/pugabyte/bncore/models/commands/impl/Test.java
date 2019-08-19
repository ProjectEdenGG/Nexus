package me.pugabyte.bncore.models.commands.impl;

import lombok.NoArgsConstructor;
import me.pugabyte.bncore.models.commands.models.CustomCommand;
import me.pugabyte.bncore.models.commands.models.annotations.Aliases;
import me.pugabyte.bncore.models.commands.models.annotations.Arg;
import me.pugabyte.bncore.models.commands.models.annotations.Path;
import me.pugabyte.bncore.models.commands.models.annotations.Permission;
import me.pugabyte.bncore.models.commands.models.events.CommandEvent;
import org.bukkit.entity.Player;

@Aliases({"test", "test2"})
@Permission("test.use")
@NoArgsConstructor
public class Test extends CustomCommand {

	public Test(CommandEvent event) {
		super(event);
	}

	@Path
	void main() {
		sender().sendMessage("default handler");
	}

	@Path("(hello|hello2)")
	void hello() {
		sender().sendMessage("Hello, World!");
	}

	@Path("msg {player} {string...}")
	@Permission("msg")
	void message(@Arg Player recipient, @Arg String message) {
		recipient.sendMessage("Message from " + player().getName() + ": " + message);
		player().sendMessage("Sent message to " + recipient.getName() + ": " + message);
	}

	@Path("add {double} {double}")
	void add(@Arg double num1, @Arg("2") double num2) {
		console().sendMessage("Result of " + num1 + " + " + num2 + ": " + (num1 + num2));
	}

	@Path("mod")
	void mod() {
		player().sendMessage("Mod help menu");
	}

	@Path("mod set {player}")
	void set(@Arg Player player) {
		player().sendMessage("Mod action: set");
	}

	@Path("mod (del|delete|remove) {player}")
	void delete(@Arg Player player) {
		player().sendMessage("Mod action: delete");
	}

	@Path("edit")
	void edit() {
		player().sendMessage("Edit help menu");
	}

	@Path("edit {player} about {string...}")
	void about(@Arg Player player, @Arg String about) {
		player().sendMessage("Edit action: about");
	}

	@Path("edit {player} name {string...}")
	void name(@Arg Player player, @Arg String name) {
		player().sendMessage("Mod action: name");
	}

}
