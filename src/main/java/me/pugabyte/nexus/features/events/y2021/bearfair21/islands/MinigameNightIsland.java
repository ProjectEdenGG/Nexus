package me.pugabyte.nexus.features.events.y2021.bearfair21.islands;

import me.pugabyte.nexus.features.events.annotations.Region;
import me.pugabyte.nexus.features.events.models.BearFairIsland.NPCClass;
import me.pugabyte.nexus.features.events.y2021.bearfair21.islands.MinigameNightIsland.MinigameNightNPCs;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.BearFair21TalkingNPC;
import me.pugabyte.nexus.features.events.y2021.bearfair21.quests.npcs.BearFair21NPC;
import me.pugabyte.nexus.models.bearfair21.BearFair21User;
import me.pugabyte.nexus.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

// TODO BF21: Quest + Dialog
@Region("minigamenight")
@NPCClass(MinigameNightNPCs.class)
public class MinigameNightIsland implements Listener, BearFair21Island {
	private static final ItemStack hat = new ItemBuilder(Material.CYAN_STAINED_GLASS_PANE).customModelData(101).amount(1).build();

	public enum MinigameNightNPCs implements BearFair21TalkingNPC {
		AXEL(BearFair21NPC.AXEL) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				script.add("Hey! Welcome to the Game Gallery! Proud sponsor of Bear Fair 2021! ...Hold up, <player>? Is that you?");
				script.add("wait 80");
				script.add("<self> Hey, Axel!");
				script.add("wait 40");
				script.add("Yooo how ya been dude? It'd be hard to forget the hero who saved last year's arcade tourney! Thanks again for that.");
				script.add("wait 80");
				script.add("<self> Always glad to help out where I can!");
				script.add("wait 40");
				script.add("Broo, its hard to find people as dope as you these days.");
				script.add("wait 60");
				script.add("<self> Aw, thanks! So how're things at GG?");
				script.add("wait 40");
				script.add("Pretty stressful, not gonna lie. Lots of good business, but its hard to keep up with it all, being self employed, " +
						"especially during bearfair. Just barely found a few moments to come out here and help the bros get set up for our " +
						"Bear Fair Band-sesh' tonight.");
				script.add("wait 140");
				script.add("<self> Anything I can do to help?");
				script.add("wait 40");
				script.add("Nah I couldn't keep you from the bear fair celebration...");
				script.add("wait 40");
				script.add("<self> No really, I wouldn't mind.");
				script.add("wait 40");
				script.add("Really? Well if you're sure, we all could actually use more practice… Would you mind running the store for me? Just till " +
						"we close tonight; and I'll totally pay you. In fact, here.You're an official employee of GG! With your " +
						"tech skills, it'll be a breeze.");
				// TODO BF21: give hat
				script.add("wait 140");
				script.add("<self> I got you bro, practice all you need. I wanna hear an awesome song when I get back!");
				script.add("wait 80");
				script.add("Duude, you're a lifesaver!");

				return script;
			}
		},
		XAVIER(BearFair21NPC.XAVIER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();
				script.add("Sup... What, never seen a quadruple bass pedal?");
				return script;
			}
		},
		RYAN(BearFair21NPC.RYAN) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				script.add("Yo. Know any good synths?");

				return script;
			}
		},
		HEATHER(BearFair21NPC.HEATHER) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				script.add("Hey! After learning 504 bass lines, I still can't decide what style I love the most… maybe all of them...");

				return script;
			}
		},
		TRENT(BearFair21NPC.TRENT) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				script.add("Ayy yo dude. You the one I gotta talk to ‘bout fixin my xbox?");
				script.add("<self> Yep! What seems to be the problem?");
				script.add("So like, Its an xbox one, right, and I hit the power button and it like, flickers into a blue screen and shuts down.");
				script.add("<self> Yeah that’s not good… does the blue screen have an error message?");
				script.add("Yuh, I took a pic. Here, dawg, says 'Critical Error. [ses.status.psWarning:warning]: DS14-Mk2-AT shelf 1 on " +
						"channel 2a power warning for Power supply 2: critical status; DC overvoltage fault.'");
				script.add("<self> Mmm, okay, I can fix this. Let me take a look at it and I’ll be right back with you as soon as it's fixed. Shouldn’t be more than a few minutes. ");
				script.add("A'ight, thanks dawg. I’ll be right here.");
				//
				script.add("Alright, here you are. Power supply was shot. Had to replace it. Pretty simple fix so the bill won’t be too bad.");
				script.add("Yooo, sweet. Thank’s dawg! Here, you can keep the change. Peace.");
				script.add("Thanks for choosing GG!");

				return script;
			}
		},
		GARY(BearFair21NPC.GARY) {
			@Override
			public List<String> getScript(BearFair21User user) {
				List<String> script = new ArrayList<>();

				script.add("");

				return script;
			}
		};

		private final BearFair21NPC npc;
		private final List<String> script;

		@Override
		public List<String> getScript(BearFair21User user) {
			return this.script;
		}

		@Override
		public String getName() {
			return this.npc.getName();
		}

		@Override
		public int getNpcId() {
			return this.npc.getId();
		}

		MinigameNightNPCs(BearFair21NPC npc) {
			this.npc = npc;
			this.script = new ArrayList<>();
		}
	}
}
