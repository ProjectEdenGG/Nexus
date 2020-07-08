package me.pugabyte.bncore.features.holidays.bearfair20.commands;

import me.pugabyte.bncore.framework.annotations.Disabled;
import me.pugabyte.bncore.framework.commands.models.CustomCommand;
import me.pugabyte.bncore.framework.commands.models.annotations.Path;
import me.pugabyte.bncore.framework.commands.models.annotations.Permission;
import me.pugabyte.bncore.framework.commands.models.events.CommandEvent;
import me.pugabyte.bncore.utils.Utils.RandomUtils;

@Disabled
@Permission("group.staff")
public class MakeFishCommand extends CustomCommand {

	String cmd1 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:65536}";
	String cmd2 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:17498624}";
	String cmd3 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:185532673}";
	String cmd4 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:117769472}";
	String cmd5 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:234881792}";
	String cmd6 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:168166400}";
	String cmd7 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:84869377}";
	String cmd8 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:67240448}";
	String cmd9 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:67175168}";
	String cmd10 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:17499392}";
	String cmd11 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:151257600}";
	String cmd12 = "summon minecraft:tropical_fish ~ ~ ~ {NoGravity:1b,Invulnerable:1b,PersistenceRequired:1b,NoAI:1b,Rotation:[<rot>F,0F],Variant:394240}";
	String[] cmds = {cmd1, cmd2, cmd3, cmd4, cmd5, cmd6, cmd7, cmd8, cmd9, cmd10, cmd11, cmd12};

	public MakeFishCommand(CommandEvent event) {
		super(event);
	}

	@Path()
	public void run() {
		String cmd = RandomUtils.randomElement(cmds);
		int rot = (int) player().getPlayer().getLocation().getYaw();
		cmd = cmd.replaceAll("<rot>", String.valueOf(rot));
		runCommandAsOp(cmd);
		send("Fish made.");
	}
}
