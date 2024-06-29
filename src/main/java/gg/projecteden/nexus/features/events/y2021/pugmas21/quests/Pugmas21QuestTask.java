package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.api.common.utils.TimeUtils.TickTime;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.TreeType;
import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.InteractQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask.TaskBuilder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21Entity.FISH_PILE;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21Entity.GUARDIAN;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21Entity.PENGUIN_1;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21Entity.PENGUIN_2;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.BELLAMY;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.CAPTAIN_NERISSA;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.CASSIA;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.CEDAR;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.ELDEN;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.EVE;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.FISH_VENDOR;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.FLINT;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.GLORIA;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.JUNIPER;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.MYSTERIOUS_WOMAN;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.OMALLEY;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.PANSY;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.PINE;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.REED;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.ROWAN;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.WARREN;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.BALLOON;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.BALLOON_BASKET;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.BALLOON_ENVELOPE;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.BOTTLED_CLOUD;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.CRYSTAL;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.CRYSTAL_PIECE;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.ELF_EARS;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.PIRATE_HAT;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.PUGMAS_COOKIE_RECIPE;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21QuestItem.RIBBON;
import static gg.projecteden.nexus.features.quests.interactable.instructions.Dialog.Variable.NPC_NAME;
import static gg.projecteden.nexus.features.quests.interactable.instructions.Dialog.Variable.PLAYER_NAME;

@Getter
@AllArgsConstructor
public enum Pugmas21QuestTask implements IQuestTask {
	CRYSTAL_REPAIR_1(InteractQuestTask.builder()
		.talkTo(GLORIA)
		.dialog(dialog -> dialog
			.npc("Well ain't you a sight for these tired old eyes.")
			.npc("I didn't think anyone would be coming this year with the storm and everything.")
			.npc("Where are my manners, my name is " + GLORIA + " and welcome to Pugmas!")
			.player("Oh... ah, hey there! My name's " + PLAYER_NAME + ". Is this weather normal for this time of year?")
			.npc("No, it isn't, or at least it didn't use to be.")
			.npc("Ever since that incident with the crystal, the weather has been out of control and everything seems to be falling apart!")
			.player("I'm sorry to hear that...")
			.npc("I'm afraid the only one who would be able to fix the crystal is its guardian...")
			.npc("Though, you seem like a fairly nice person. Maybe you could help find them.")
			.npc("Would you mind helping an old lady out?")
			.player("Of course! I'm more than happy to help.")
			.npc("Could you go around and ask the people of the town if they've heard about a deer in the woods? These legs ain't what they used to be.")
			.player("Sure thing. I'll come back if I find anything.")
		)
		.then()
		.talkTo(CEDAR)
		.dialog(dialog -> dialog
			.npc("Ugh! I forgot my grocery list again...")
			.player("Excuse me, have you heard talk of any deer in the woods?")
			.npc("Deer in the woods?... Where else would deer live?")
			.player("Apologies, I mean.. any strange deer? Like some sort of creature?")
			.npc("Oh, that old rumour.")
			.npc("The kids like to play around in the woods, they make up plans to try and catch the \"Deer Spirit\".")
			.npc("I don't know where in the woods it is though, I hardly have time to listen to the children's silly stories.")
			.player("Thank you anyways..")
		)
		.then()
		.talkTo(REED)
		.dialog(dialog -> dialog
			.player("I'm sorry to bother you, but do you know where I can find the \"Deer Spirit\"?")
			.npc("Deer Spirit you say? I'm sorry to say but you're wasting your time searching for that. It's nothing more than a made up story.")
			.player("Hmm, okay. Well thank you anyway.")
		)
		.then()
		.talkTo(Pugmas21NPC.ESTELLE)
		.dialog(dialog -> dialog
			.player("Pardon me, but have you heard of where the \"Deer Spirit\" is in the forest? You might know of it from a children's story.")
			.npc("Oh, yes I think I heard the children talk about it while heading to the forest in the south-east.")
			.player("Thank you!")
		)
		.then()
		.talkTo(GLORIA)
		.dialog(dialog -> dialog
			.npc("Oh, you're back! Did you find out anything?")
			.player("Yes actually, I was able to find out that there have apparently been sightings of something in the forest to the south-east of here.")
			.npc("That's excellent news!")
			.npc("Would you mind going and checking on these rumours to see if you can find anything there?")
			.npc("I fear for what will happen to this place, and to Pugmas, if the crystal isn't repaired soon.")
			// TODO Mysterious Woman isnt in the forest
			.player("I'll head into the forest and see if I can find a lead on this deer.")
			.npc("Oh thank you, thank you!")
		)
		.then()
		.talkTo(MYSTERIOUS_WOMAN)
		.dialog(dialog -> dialog
			.npc("Ohhh, what's this I see? A visitor? I haven't had one of those in a long time.")
			.player("Hello there, sorry to intrude. My name is " + PLAYER_NAME + ", I was told that I might be able to find the guardian out here.")
			.npc("The guardian you say? That is something I haven't heard in quite a while. What interest is the guardian to you?")
			.player("I just want to help out. A nice lady I met when I first got here asked me to try and seek the guardian out so the crystal may be restored and so that Pugmas can go ahead.")
			.npc("I'll tell you what. If you help out the people of the village, I'll tell you a bit more about both the guardian and the crystal.")
			.player("Ok, who should I help first?")
			.npc("In the distance, I can hear the sound of an elf singing. You may find them on the bridge leading to the crystal. Go and help them out, then return here.")
		)
	),
	CLEAR_SKIES(GatherQuestTask.builder()
		.talkTo(JUNIPER)
		.dialog(dialog -> dialog
			.npc("🎵 Jingle bells, jingle bells, jingle all the way~ Oh what fun it is to ride in a one horse open slei- 🎵 AH! My bells! How long have you been standing there!?")
			.player("Somewhere around the beginning of \"Jingle Bells\"?")
			.npc("Eh?! Why...")
			.npc("Hmm... Actually, wait! You might be helpful after all.")
			.npc("I'm sure that you're aware of the awful storm that has been tearing everything up lately.")
			.npc("I mean It's kind of hard to miss it, unless this is normal where you're from.")
			.npc("Anyways, that's besides the point. My point is that I need your help to get rid of it.")
			.npc("This dumb storm has ruined everything in our village, if this keeps up then you can say goodbye to any pugmas celebrations you were looking forward to.")
			.player("The whole reason I came here was to celebrate Pugmas, so I have to do something!")
			.npc("Hurray! Oh " + PLAYER_NAME + ", I just knew you'd help out.")
			.npc("Now off you go! Good luck and safe travels!")
			.player("...Huh? Wait, what do you mean?")
			.npc("Is there a problem?")
			.player("Yes, you forgot to mention how to get rid of the storm?")
			.npc("You think I know? That's why I asked you for help! I guess I really should've just gone to that weather guy instead..")
			.player("Weather guy?")
			.npc("Yeah there's this strange guy who hangs out near the docs, he stays there and studies the change in weather.")
			// TODO Remove dont get along
			.npc("I know he would be the best to ask about something like this, but frankly I don't want to talk to him. We just.. Don't get along.")
			.npc("Oh I know! How about you go talk to him, I mean you said you would help so this is your issue now, right?")
			.player("I don't think that's how it works-")
			.npc("Now just head right down to the docs and take a right. Have fun!")
		)
		.then()
		.talkTo(BELLAMY)
		.dialog(dialog -> dialog
			.npc("Ahh!")
			.npc("Oh my gosh, you just about gave me a heart attack!")
			// TODO This is not inside a home
			.npc("You honestly can't just barge into someone's home uninvited without at least knocking first.")
			.player("Sorry it's just this is kinda important.")
			.npc("*sigh* Even so, urgency does not warrant disrespect.")
			.npc("How am I able to help?")
			.player("I need help getting rid of the storm and I thought you could help")
			.npc("Hmm, get rid of it? I mean it's not impossible but it may be difficult")
			.npc("This weather is truly like nothing else I've seen before so I don't know how much my prior knowledge will be of help")
			.npc("However, if it truly is just an overly aggressive storm then... I think I have an idea, but I'm going to need your help.")
			.player("I'd be happy to help.")
			.npc("Perfect! I've been wanting to make this thing for a while now, but I've never had the resources to do it. I'm happy to finally have an assistant.")
			.player("Okay I wouldn't go that far-")
			.npc("I think we should start with some iron and crystal wood for now")
			.player("Hold on! You haven't even told me what this is for yet!")
			.npc("Oh I guess I did forget the part didn't I")
			.npc("Well since a storm is defined as a violent atmospheric disturbance, characterized by low barometric pressure, cloud cover, precipitation, strong wind-")
			.player("I'm sorry what?")
			.npc("Ummm... so essentially...how to explain this in simple terms?")
			.npc("Basically for storm to be storm, storm need clouds, get rid of clouds, storm go poof")
			.player("Okay you didn't need to go that simple-")
			.npc("Whatever. The items you're fetching are the necessary materials to create a machine capable of blowing away even the strongest of clouds.")
			.npc("Get me 32 iron, and 64 crystal wood to start. You'll have to talk to " + FLINT + " in the mines and the lumberjack, " + WARREN + ".")
			.player("Got it!")
		)
		.then()
		.talkTo(FLINT)
		.dialog(dialog -> dialog
			.player("Hi! Are you " + NPC_NAME + "?")
			.npc("Haha, that's me! Are you in need of some ores kiddo?")
			.player("Yes, I need some iron and I was hoping you could help me out.")
			.npc("Hmm, iron...")
			.npc("Ugh sorry kid, seems im fresh out of iron at the moment.")
			.player("Are you sure? It's really important that I get this iron.")
			.npc("I suppose I could theoretically get more but I have so much work to do right now.")
			.npc("Hey, why don't you get it yourself? The veins right over there, you won't have to walk far.")
			.npc("Just take this pick and swing at it, I'm sure you'll do great!")
			.give(Material.IRON_PICKAXE)
			.player("Wait, am I allowed to keep this?")
			.npc("Of course! It's just a rusty old spare. I was probably gonna get rid of it at some point so I'm glad to see it get some use.")
			.player("Okay, awesome! Thanks " + NPC_NAME + "!")
			.npc("No problem! And you know if you take a liking to the miner's life just let me know, we could always use some extra hands.")
		)
		.then()
		.talkTo(WARREN)
		.dialog(dialog -> dialog
			.player("Excuse me, are you " + NPC_NAME + " by any chance?")
			.npc("Hm? Oh, yes that's me.")
			.npc("Is there something I can help you with?")
			.player("Yes actually! I was hoping you could point me in the direction of some crystal wood.")
			.npc("Oh, yeah of course. You can find them in a cave. Do you know how to harvest them?")
			.player("Haha, um... I guess I've just been figuring these things out as I go.")
			.npc("Unfortunately when it comes to these trees, being unprepared won't do, they can be fickle things.")
			.npc("Why don't you take my spare axe from the wall? It's not the best but it should get the job done.")
			.player("Thank you " + NPC_NAME + "!")
			.npc("It's really no problem. Feel free to stop by anytime.")
		)
		.then()
		.talkTo(BELLAMY)
		.dialog(dialog -> dialog
			.npc("Ah! You're back!")
		)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(new ItemStack(Material.IRON_INGOT, 32), TreeType.CRYSTAL.item(64))
		.complete(dialog -> dialog
			.npc("These materials look perfect.")
			.player("Awesome does this mean I'm done here?")
			.npc("Haha, no. Not at all.")
			.npc("We're only just getting started with the base machine, the most important part is yet to come")
			.player("Most important part?")
			.npc("Yep! I need you to take this container and collect a cloud sample for me. As in, put a cloud in a bottle for me.")
			.player("So you just want me to shove part of a storm cloud into this jar...")
			.npc("Exactly! The data it can provide is essential for calibrating the machine.")
			.player("Wait a second, how am I supposed to get up there?")
			.npc("Hmm, well... since the storm has left most flying machinery useless, I'd say a hot air balloon should do nicely.")
			.player("Where do I get a hot air balloon from?")
			.npc("You will most likely have to make one. It shouldn't be too hard though, get me the materials and I can put it together for you")
			.npc("Go see " + WARREN + " about the wooden basket, ask " + ROWAN + " in the costume store about the fabric for the actual balloon, and you can mine up some coal for fuel; 64 should be enough.")
		)
		.then()
		.talkTo(WARREN)
		.dialog(dialog -> dialog
			.npc("Hey there, I wasn't expecting you back so soon.")
			.npc("When " + BELLAMY + " called saying I should be expecting their assistant to stop by I thought it would be someone else")
			.player("Are they still saying I'm their assistant?")
			.npc("Oh, are you not the person I'm waiting for?")
			.player("No I am")
			.player("Well, back to business. Is the basket ready?")
			.npc("About that... " + BELLAMY + " hung up before I could explain that I'm completely out of oak logs at the moment")
			.npc("I'm far too busy to add another expedition into my schedule for a single request.")
			.npc("I was dreading having to turn " + BELLAMY + "'s assistant away after they made the trip all the way here, however considering it's you...")
			.npc("Were you able to successfully get that crystal wood?")
			.player("Oh, yes! Thanks again for the help with that.")
			.npc("Then it's settled, bring me 50 oak logs and I'll make your basket for you.")
			.player("Okay!")
		)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(TreeType.OAK.item(50))
		.complete(dialog -> dialog
			.npc("Ah perfect! It's pretty good quality too. You sure you wanna continue being " + BELLAMY + "'s assistant? I could use someone like you working around the shop.")
			.player("I'm sorry but I think I'll have to pass.")
			.npc("Suit yourself. Just give me a few minutes.")
			// TODO Sounds
			.pause(TickTime.SECOND.x(3))
			.npc("And... done! Here you go, one wooden basket!")
			.give(BALLOON_BASKET.get())
			.player("Thanks again warren!")
		)
		.then()
		.talkTo(ROWAN)
		.dialog(dialog -> dialog
			.npc("Oh, hello there!")
			.npc("What a surprise! I wasn't expecting any customers this time of year, welcome to my costume store.")
			.npc("Are you in need of a dazzling costume? Trying to prepare for Halloween early perhaps?")
			.player("Oh actually-")
			.npc("Just step over here and we can get you all measured up!")
			.player("Wait I don't need a-")
			.npc("Now then, did you have a specific costume in mind? We can do a dinosaur, superhero, vampire-")
			.player("I don't need a costume!!")
			.npc("..Oh, I'm sorry I got a little excited there. What is it I can do for you then?")
			.player("I need a hot air balloon envelope and was hoping you could help.")
			.npc("Oh, you're " + BELLAMY + "'s assistant! Why didn't you just say so? I have it right here for you.")
			.npc("Now just to add this up...")
			.npc("That will be $10,000 please.")
			.player("...What?")
			.npc("Pfft.. bahaha, I'm just joking! Sorry about that, but the look on your face was just too good.")
			.give(BALLOON_ENVELOPE.get())
			.player("Oh, okay then haha.")
			.npc("Please come again! I wouldn't want to have scared you off with that little joke.")
		)
		.then()
		.talkTo(BELLAMY)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(BALLOON_BASKET.get(), BALLOON_ENVELOPE.get())
		.complete(dialog -> dialog
			.npc("These are high quality materials so this shouldn't take too long.")
			.npc("Just gotta attach these here, put that there... and done!")
			.give(BALLOON.get())
			.npc("Now go get me that cloud in a bottle! I'll meet you by the docks with the machine so we can get rid of this storm, once and for all.")
		)
		.then()
		// TODO Hot air balloon
		//    Spawn point: /tppos -35.50 58.00 238.50 90 0 pugmas21
		.then()
		.talkTo(BELLAMY)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(BOTTLED_CLOUD.get())
		.complete(dialog -> dialog
			.npc("Took you long enough. I suppose now we are all set.")
			.npc("Just gotta turn this on and...")
			.npc("Nothing?!")
			.npc("No that can't be, my plan was perfect and yet the clouds didn't budge")
			.npc("I need more power.")
		)
		.then()
		.talkTo(BELLAMY)
		.dialog(dialog -> dialog
			.npc("You! Bring me 40 pieces of redstone, and be quick about it.")
			.player("Okay.")
		)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(Material.REDSTONE, 40)
		.complete(dialog -> dialog
			.npc("Is that the redstone? Hand it over!")
			.npc("Okay let's do this now that it's all powered up!")
			.npc("...")
			.npc("Still nothing...")
			.npc("I'm so sorry " + PLAYER_NAME + ". I made you run all over the place for nothing.")
			.npc("Sigh. I was so sure it was going to work.")
			.npc("I wanted to give you something as a thank you for your hard work but I'm afraid I don't have any money. I hope this item will be okay.")
			.give(CRYSTAL_PIECE.get())
			.npc("I don't know if it's just a shiny rock or whether it has some importance, but I hope you enjoy it.")
			.player("Oh, uhm.. Thanks, I guess?") // TODO Be more grateful
			.npc("You're welcome! Also feel free to come help again, you are my assistant after all!")
			.player("I'm not your assistant...")
			.npc("If you say so.")
		)
		.then()
		.talkTo(MYSTERIOUS_WOMAN)
		.dialog(dialog -> dialog
			.npc("Welcome back! I see you were successful in helping them out. It does me good to once again see others having fun.")
			.player("I'm glad that I could help, and look, I was given one of the crystal shards!")
			.npc("Perfect, now let me tell you a little about the Guardian...")
			.npc("Long ago, there was not just one guardian, but a dozen.")
			.npc("Over time, they slowly left, one after another, each returning to the spirit realm, till only one remained.")
			.npc("This guardian was their leader, the strongest of the bunch, and was tasked with defending the crystal on their own.")
			.npc("For hundreds of years, they continued to do so, watching as each year Pugmas went ahead unaffected.")
			.npc("But eventually they began to grow distant and eventually faded into myth...")
			.player("Wow, that's amazing! What happened to them? Why did they disappear?")
			.npc("If you wish to hear more, bring me the second piece of the crystal.")
//			.condition(quester -> Pugmas21QuestLine.of(quester) == Pugmas21QuestLine.PIRATES)
//				.npc("To find it, help out the Captain with their troubles. You can find them at the dock.")
//			.endCondition()
//			.condition(quester -> Pugmas21QuestLine.of(quester) == Pugmas21QuestLine.ELVES)
//				.npc("To find it, head to Santa's Manor where you will find a troubled young elf.")
//			.endCondition()
		)
	),
	HOLIDAY_HEIST_PIRATES(GatherQuestTask.builder()
		.talkTo(CAPTAIN_NERISSA)
		.dialog(dialog -> dialog
			.npc("Mm. Okay, this is really not good.")
			.npc("Oh, " + PLAYER_NAME + ", why are you just standing around? Come over here.")
			.npc("Have you seen a recipe lying around? I swear it was just here!")
			.player("I don't think so?")
			.npc("Well it certainly didn't just walk away! It had to have gone somewhere-")
			.npc("Ugh, I bet it had something to do with those elves. Blaming us for the storm and now this! They really have it out for us.")
			.npc("Sigh... why don't you help me out? I mean it beats just wandering around doing nothing, no?")
			.npc("If it was them, they probably took it back to their base.")
			.npc("Hmm.. you can't go looking like that though. You need a disguise.")
			.npc("Why don't you visit the costume store? I'm sure they'll be able to put something together for you.")
		)
		.then()
		.talkTo(ROWAN)
		.dialog(dialog -> dialog
			.npc("Oh, hi there, apologies, I wasn't expecting you to be back so soon.")
			.player("Ah, sorry, I didn't mean to disturb you.")
			.npc("No, no, it's a welcome surprise! Are you in need of another hot air balloon envelope or do you require something else?")
			.player("Well, I need an elf disguise and I was told you may be able to help me.")
			.npc("Oh, what a fun request! I've certainly never got that one before.")
			.npc("Why don't you bring me 32 string, 64 gold, and 45 crystal wood, and I'll put something together for you.")
			.npc("I know these materials may seem strange to you, but for the best looking costumes, a little bit of magic is needed.")
		)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(new ItemStack(Material.STRING, 32), new ItemStack(Material.GOLD_INGOT, 64), TreeType.CRYSTAL.item(45))
		.complete(dialog -> dialog
			.npc("Perfect! Let me work my magic for you, it won't take long.")
			// TODO Sounds
			.npc("Boom! Okay there, a perfect disguise!")
			.give(ELF_EARS.get())
			.player("This looks amazing! Thank you, this is exactly what I needed.")
			.npc("Haha! Well, I'm not known as the best costume designer for nothing.")
			.npc("Do come again!")
		)
		.then()
		.talkTo(CAPTAIN_NERISSA)
		.dialog(dialog -> dialog
			.npc("Looks like you managed to get the disguise okay, you look just like an elf!")
			.npc("Well, when you're ready, let me know and I'll show you the way.")
			.player("I'm ready.")
		)
		// Coordinates/the name of the area are given to the player in chat.
		// Player arrives and steps on <block>, warping them to a room where this part takes place

		// Part 1: (Inspired by wynncrafts saving the cows quest)
		//    There will be a guard walking around and the player will have to avoid stepping in their line of sight while making their way to the other side of the room
		// Part 2:
		//    In the next room the player will have to complete a simple parkour to get to the other side
		// Part 3:
		//    Grab book sitting on pedestal/lectern in third room
		//    Grabbing the book warps the player back to the main npc
		.then()
		.talkTo(CAPTAIN_NERISSA)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(PUGMAS_COOKIE_RECIPE.get())
		.take(false)
		.complete(dialog -> dialog
			.npc("You're back! I knew you'd return!")
			.npc("And there's something in your hand, the elves did have it! You found-")
			.npc("Wait. " + PLAYER_NAME + "... what is this?")
			.npc("I said you were supposed to bring back a ribbon, how did you manage to confuse it with a recipe for pugmas cookies!?")
			.player("That's.. what you asked me to get. Do you want me to return it?")
			.npc("Of course not! Then they'd know you were snooping around!")
			.npc("Just bring it to the kitchen I suppose, I've got to focus on finding that ribbon")
		)
		.then()
		.talkTo(PANSY)
		.gather(PUGMAS_COOKIE_RECIPE.get())
		.dialog(dialog -> dialog
			.npc("Oh, hey " + PLAYER_NAME + "! I didn't expect to see you here.")
			.npc("What's that you're holding?")
			.player("It's a cookie recipe that I accidentally stol-, I mean stumbled upon while helping around the island. " + CAPTAIN_NERISSA + " thought you could use it.")
			.npc("Really? Oh I love learning new recipes, thank you! This will certainly go to good use!")
			.npc("But wait, you said that the Captain sent you? Can you bring this to them?")
			.give(RIBBON.get())
			.npc("They left it here this morning and I've been meaning to return it all day.")
			.player("...You mean it was here this whole time?")
			.npc("Hmm? I'm sorry, I'm not sure what you mean by that.")
			.player("Never mind. Of course I can bring it to them.")
			.npc("Thank you!")
		)
		.then()
		.talkTo(CAPTAIN_NERISSA)
		.gather(RIBBON.get())
		.dialog(dialog -> dialog
			.player(PANSY + " wanted me to give this to you.")
			.npc("Is this what I think it is? The item I've been looking for all day?")
			.player("Yeah, it is. Turns out you left it in the kitchen this morning, meaning it was here this whole time!")
			.npc("Oh haha... sorry for dragging you into this. My bad memory has once again proved to be troublesome.")
			.npc("I guess I owe you some compensation for the trouble I put you through. Here, take this.")
			.give(CRYSTAL_PIECE.get())
			// TODO Thanks
		)
	),
	HOLIDAY_HEIST_ELVES(GatherQuestTask.builder()
		.talkTo(CASSIA)
		.dialog(dialog -> dialog
			.npc("Oh sleigh bells! Just where has it gone!?")
			.npc("Hey, you! Have you seen a recipe laying around somewhere!?")
			.player("Nope, I just got here.")
			.npc("..Oh... What am I supposed to do then? I needed that.. Do you think those pesky pirates could have taken it? I mean, they did cause this mess in the first place.")
			.player("Have you checked everywhere else? It's not good to blame people without reason...")
			.npc("But I have a reason! Those pirates have been causing trouble ever since they've arrived! It has to be them.")
			.player("If you say so. Can I help you look for the item?")
			.npc("No need to look around here, I'm sure I know where it is now!")
			.npc("" + PLAYER_NAME + ", do you still want to help? If so, why don't you sneak into the pirate headquarters for me! Your mission is to retrieve my recipe and return safely!")
			.player("What!? How am I supposed to do that without being caught, I don't exactly look the part of a pirate...")
			.npc("Oh... I know! There's a costume designer just nearby, if you ask nicely I'm sure they'll put together an amazing pirate disguise you can use.")
			.npc("Just head out the front entrance and head left until you see a cottage, that's where you'll find them.")
		)
		.then()
		.talkTo(ROWAN)
		.dialog(dialog -> dialog
			.npc("Oh, hi there, apologies, I wasn't expecting you to be back so soon.")
			.player("Ah, sorry, I didn't mean to disturb you.")
			.npc("No, no, it's a welcome surprise! Are you in need of another hot air balloon envelope or do you require something else?")
			.player("Well, I need a pirate disguise and I was told you may be able to help me.")
			.npc("Oh, what a fun request! I've certainly never got that one before.")
			.npc("Why don't you bring me 32 string, 64 gold, and 45 crystal wood, and I'll put something together for you.")
			.npc("I know these materials may seem strange to you, but for the best looking costumes, a little bit of magic is needed.")
		)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(new ItemStack(Material.STRING, 32), new ItemStack(Material.GOLD_INGOT, 64), TreeType.CRYSTAL.item(45))
		.complete(dialog -> dialog
			.npc("Perfect! Let me work my magic for you, it won't take long.")
			// TODO Sounds
			.npc("Boom! Okay there, a perfect disguise!")
			.give(PIRATE_HAT.get()) // TODO force wearing?
			.player("This looks amazing! Thank you, this is exactly what I needed.")
			.npc("Haha! Well, I'm not known as the best costume designer for nothing.")
			.npc("Do come again!")
		)
		.then()
		.talkTo(CASSIA)
		.dialog(dialog -> dialog
			.npc("Woah, that disguise is perfect! I almost mistook you for one of those pesky pirates.")
			.npc("As soon as you're ready to begin your mission, let me know!")
			.player("I'm ready.")
			// Coordinates/the name of the area are given to the player in chat.
			// Player arrives and steps on <block>, warping them to a room where this part takes place
			// Player grabs book and warps back to Main Npc
		)
		.then()
		.talkTo(CASSIA)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(PUGMAS_COOKIE_RECIPE.get())
		.take(false)
		.dialog(dialog -> dialog
			.npc("You did it! I always knew you could!")
			.npc("In your hand! Is that my ribbon? I was right about those pesky pirates, I knew they couldn't be trus-")
			.npc("..Wait.. " + PLAYER_NAME + ", what is that?")
			.npc("Didn't I tell you that you were supposed to be looking for a ribbon? How did you get that confused with a recipe for Flash-Fried Filet?")
			.player("That's.. what you asked me to get. Well, do you want me to return it? I didn't see a ribbon there.")
			.npc("Er- No! We'll get in trouble if they find out we were snooping around...")
			.npc("Why don't you go give it to our chef, " + PINE + ", I'm sure she could benefit from it.")
		)
		.then()
		.talkTo(PINE)
		.gather(PUGMAS_COOKIE_RECIPE.get())
		.dialog(dialog -> dialog
			.npc("Oh, hey new recruit! I didn't expect to see you here. It's nice to meet you, I'm " + NPC_NAME + ".")
			.player("Hi, I'm " + PLAYER_NAME + ". " + CASSIA + " asked me to give this to you.")
			.npc("Oh, lovely! What is it?")
			.player("It's a pirate dish recipe that I accidentally stol-, I mean stumbled upon while helping around the island. " + CASSIA + " thought you could use it.")
			.npc("Really!? I love learning new recipes, thank you so much! Pirate recipe.. It reminds me of my sibling " + PANSY + ".")
			.npc("I'll make sure to perfect this dish! Oh, and you said " + CASSIA + "? Would you mind bringing this ribbon to them? I heard they were looking for it.")
			.npc("She left it here this morning, I've been meaning to bring it to her all day.")
			.give(RIBBON.get())
			.player("Wha- it's been here the whole time!?")
			.npc("Huh?")
			.player("Oh it's.. Nothing. I'll bring it to her right away!")
			.npc("Thanks a ton!")
		)
		.then()
		.talkTo(CASSIA)
		.gather(RIBBON.get())
		.dialog(dialog -> dialog
			.player(NPC_NAME + ", I found your-")
			.npc("Eh!? Is that my ribbon? Has it been here this whole time?")
			.player("Yup. " + PINE + " said you left it in the kitchen this morning...")
			.npc("Hehe, looks like my bad memory has caused problems for us once again. I'm sorry for dragging you into this.")
			.npc("Here, take this as a thank you, and an apology.")
			.give(CRYSTAL_PIECE.get())
			// TODO Thanks
		)
	),
	CRYSTAL_REPAIR_2(GatherQuestTask.builder()
		.talkTo(MYSTERIOUS_WOMAN)
		.dialog(dialog -> dialog
			// (When player returns after finishing Holiday Heist)
			.npc("You've returned, and with the second crystal shard I see!")
			.player("Yes, it was quite an.... interesting experience, but I'm glad to have another piece of the crystal. Now, would you mind telling me more about the Guardian?")
			.npc("Okay, okay. You young folk are so impatient these days. Now, where did I stop... oh that's right, I was talking about the last Guardian.")
			.npc("For generation after generation, the Guardian stood watch over the Crystal, protecting from all who wished to interfere with Pugmas. But after a while, the spirit started to get bored.")
			.npc("No longer did they smile, or dance around with their friends, as they had all gone. Now, only the villagers remained, who merely stood at a distance and looked on in awe.")
			.npc("Eventually, the Guardian started to become bored with only guarding the crystal, but unable to join its friends in the spirit world, decided to try and make its own fun in this one.")
			.npc("The dark energy in the crystal, no longer being bound and feeling the change in the Guardian, shot outwards and shattered the crystal, leaving it in three pieces.")
			.npc("Ever since, the storm has only been getting worse and worse, and will not be stopped until the crystal is once more whole.")
			.player("That's incredible! But... How do you know all of this?")
			.npc("Well, I suppose it's only fair to tell you who I really am, after all the joy you have brought me with your tasks.")
		)
		.then()
		.talkTo(GUARDIAN)
		.dialog(dialog -> dialog
			// The Mysterious Woman suddenly turns into the Guardian, a magnificent crystal reindeer.
			.npc(GUARDIAN, "The reason I know all of this is because I am the Guardian.")
			.player("Why show yourself now? Will you help me fix the crystal?")
			.npc(GUARDIAN, "Of course I will. As to why I decided to show myself now? It's because of the joy you have brought me! I haven't had this much fun in centuries! I have greatly enjoyed watching you as you traversed the town, and helped its people solve their problems.")
			.npc(GUARDIAN, "Now, I think I've had my fun. Let me combined those two shards you have brought me with the one I kept from the explosion")
			.player("Wait, you had one of the shards this entire time!? Why didn't you tell me that? I thought I was going to have to go and find the last one.")
			.npc(GUARDIAN, "Hahaha, well where is the fun in that? Now then, here you go. Take those shards to the Pugmas Shrine. I'll be waiting for you there so we can cleanse them of the dark energy.")
			.give(CRYSTAL_PIECE.get())
			.player("Thank you for this!")
			.npc(GUARDIAN, "Good, you're here. Now let's see if I can remember the ritual... ah, that's right! To rid the crystals of the dark energy, we need three things.")
			.npc(GUARDIAN, "An emerald from the mountains, a shimmering crystal log from the deepest caves, a rare drop from the magical trees, and the finest wool which has a chance of dropping from the local sheep.")
			.npc(GUARDIAN, "Once you have all three items, return here and we can begin the ritual.")
			.player("Ok, I'll go and look for the ingredients.")
		)
		.reminder(dialog -> dialog
			.npc("TODO Reminder") // TODO
		)
		.gather(new ItemStack(Material.EMERALD), TreeType.CRYSTAL.item(), new ItemStack(Material.WHITE_WOOL))
		.take(false)
		.complete(dialog -> dialog
			.player("I have found all the ingredients. What do we do next?")
			.npc(GUARDIAN, "Now, we merely have to combine the ingredients with the crystal shards in the water here. Its purity will aid in the process of cleaning the crystals and repairing them.")
			.npc(GUARDIAN, "Chuck everything into the water and watch the magic happen!")
			// The Player goes over to the body of water and chucks the ingredients and the crystal shards into the body of water located at the shrine. Particle effects will happen, showing that the crystal is being cleansed. Once it's over, a single Repaired Crystal will be in the water
			.npc(GUARDIAN, "The ritual is complete! Grab the crystal and take it to the central island so that the Crystal can be whole once more.")
			.player("That was amazing! Thank you for all the help.")
			.npc(GUARDIAN, "It was my absolute pleasure. Go now, and remember, have a Merry Pugmas!")
			.give(CRYSTAL.get())
			// Player is given a Repaired Crystal and now must travel to the central island. Once there, the player should be able to interact with something here, allowing the crystal to be used. The skies will clear up and everything will be as it once was!
		)
	),
	PARTY_PANIC(GatherQuestTask.builder()
		.talkTo(GLORIA)
		.dialog(dialog -> dialog
			.npc("Oh, I'm so glad I caught you! I've been meaning to ask you about something.")
			.npc("I know you must be tired after all the work you put into fixing the crystal, but I have one more favor to ask of you.")
			.player("How can I help?")
			.npc("Well, with the storm destroying everything in its path, you could say we were focused on other things.")
			.npc("Unfortunately this has meant leaving our annual pugmas celebration to be forgotten amongst the chaos.")
			.npc("After all we've dealt with this year, cancelling the celebration would really dampen everyone's spirit. They've been looking forward to this for a while now.")
			.npc("I mean we could all use a bit of cheering up.")
			.player("So what exactly are you asking me to do?")
			.npc("Nothing much. I just need you to plan the entire party for pugmas.")
			.npc("You know, simple stuff, like getting the materials for it, inviting people, setting up catering, music, venue, funding, decorations, entertainment-")
			.player("I'm sorry what?")
			.npc("Did you need me to repeat that?")
			.player("No, no. What I mean is that you're asking for a lot to be done in such a small amount of time.")
			.npc("I believe you're capable enough. I mean, you did fix the crystal after all!")
			.player("I'll pass-")
			.npc("Wait no!")
			.npc("Please don't go, we really need your help here.")
			.npc("Those plans were just wishful thinking on my part. It shouldn't be much work for you I promise!")
			.player("Okay, fine. I'll do my best.")
			.npc("Thank you!")
			.npc("Hmm let's see, would you be willing to handle the venue, decorations, and food?")
			.player("I suppose. Where would you like me to start?")
			.npc("That's up to you.")
			.npc("I'd ask Elden the builder about the venue, Rowan about decorations, and Eve in the penguin cafe about food.")
			.npc("I'll let them know of your arrival in advance, no need to worry.")
			.player("Okay thanks.")
			.npc("No, thank you! I'm truly in your debt here.")
		)

		// Builder section

		.then()
		.talkTo(ELDEN)
		.dialog(dialog -> dialog
			.npc("Oh, you're the person who's been helping out around here, right?")
			.player("Yep!")
			.npc("Awesome. I was so worried about whether or not it would be cancelled this year.")
			.npc("I think Gloria said you needed a good venue? Luckily that shouldn't be too hard.")
			.npc("It's tradition to have it in the centre of the village around the christmas tree.")
			.npc("Fortunately, I've been working on fixing it up for a while now. *Sigh* The storm really did a number on the place")
			.npc("I'm only missing a few materials at this point. If you get those for me, your party venue will be fixed and cleaned up in no time.")
			.player("That doesn't seem too hard, what materials do you need?")
			.npc("50 of each wood and iron should do. I've got everything else already.")
			.player("50!? Okay... I'll be right back.")

			// Upon giving the materials to Elden
			.npc("Oh that really was quick!")
			.npc("These look perfect, I'll start working right away.")
			.player("Thank you Elden!")
			.npc("It's really no problem! Now I won't keep you here as I'm sure you have other things to do.")
		)

		// Costume Npc section

		.then()
		.talkTo(ROWAN)
		.dialog(dialog -> dialog
			.npc("Ah welcome back [player name]")
			.npc("I must say, I believe you alone have given me more business during these past few weeks than I have gotten all year.")
			.npc("Now word is you need some decorations for your little pugmas party project?")
			.player("You heard correctly! That isn't too much trouble is it?")
			.npc("Not at all, not at all.")
			.npc("On the contrary, I actually started working on them as soon as Gloria gave me a call.")
			.npc("In fact they are almost done, except for one small detail.")
			.player("And what's that?")
			.npc("I'm missing something to make these banners shine.")
			.npc("Mind fetching that for me?")
			.player("Sure! What item am I looking for?")
			.npc("For this particular order could you fetch me 32 diamonds?")
			.player("Diamonds? Okay.")

			// Upon giving the diamond to the npc
			.npc("Ah perfect!")
			.npc("Now these are decorations worthy of a pugmas celebration.")
			.npc("Here, take them, and feel free to come again.")
			.player("I certainly will! Thanks Rowan.")
		)

		// Penguin Cafe section

		.then()
		.talkTo(EVE)
		.dialog(dialog -> dialog
			.npc("Honk")
			.player("Huh?")
			.npc("My bad, I forgot not everyone speaks penguin.")
			.npc("You must be " + PLAYER_NAME + ", right? We were expecting you.")
			.npc("Unfortunately you came a bit sooner than we expected and we are still preparing everything.")
			.npc("Hey you know what?")
			.npc("Would you mind helping me out with something quickly? I'm sure it would beat waiting around for the food. I'll even give you a free hot chocolate for the trouble!")
			.player("It's a deal! What do you need help with?")
			.npc("Oh thank you! I've been so busy here at the cafe that I haven't gotten the chance to get my best friend a pugmas gift.")
			.npc("My original plan was to make them their favorite cookies but I'm fresh out of fish.")
			.player("...Fish? For cookies?")
			.npc("Of course! What else would you put in cookies?")
			.player("Chocolate chips?!?")
			.npc("Oh haha, I forgot to mention this is for my penguin. You needn't worry about me eating fish cookies, however they are on our menu for anyone who's interested.")
			.player("Well as long as I don't have to eat them-")
			.player("Did you have a specific type you need?")
			.npc("Yes! Would you mind getting me 32 cod and 16 salmon? Those are their favorite.")
			.player("Consider it done.")

			// Once player gives fish to npc
			.npc("Hey there! Perfect timing!")
			.npc("Your food and hot chocolate are both ready. Thank you so much for getting those fish for me!")
			.player("Of course! It was no problem at all.")
			.npc("I hope to see you again soon!")
		)

		// Upon returning to Gloria after preparing everything. Decorations will now be visible to the player
		.then()
		.talkTo(GLORIA)
		.dialog(dialog -> dialog
			.npc("Welcome back " + PLAYER_NAME + "!")
			.npc("I trust that your return means everything has been prepared?")
			.player("Yep! It's all done.")
			.npc("Thank you so much! I knew I could count on you.")
			.npc("I prepared this little gift for you as a thank you for all you did to save pugmas.")
			// player receives candy cane gun and gains access to the pugmas radio
			.player("Oh wow, thanks " + NPC_NAME + "! I really appreciate that.")
			.npc("Aww, it's really no big deal compared to everything you've done for us.")
			.npc("I hope you have an amazing pugmas! Relax and enjoy the party, you did put a lot of work into it after all.")
		)
	),
	PENGUIN_MAFIA(InteractQuestTask.builder()
		.talkTo(FISH_VENDOR)
		.dialog(dialog -> dialog
			.npc("Oh jingle my bells! They found our stock again... tsk")
			.player("Is something wrong?")
			.npc("Er- Hello there, if you're here for fish then I'm sorry to say that we're all out.")
			.player("...Isn't fish the only thing you sell at this booth?")
			.npc("Yes.. That is true.")
			.player("How are you out of fish? The village is right by the ocean..")
			.npc("Well, between you and me there's a bit of thief going around in our village. It's strange though, because the only thing they're stealing is fish.")
			.player("Uhuh")
			.npc("Hey.. Now that I think about it, you look familiar. Are you " + PLAYER_NAME + "? The one that's been helping everyone repair the crystal and save Pugmas?! Oh, please, won't you help me catch this fish thief as well?")
			.player("I'm not sure I'd be much help, I don't even know where to start.")
			.npc("Easy! The thief went North, right into the trees!")
			.player("Ah.. guess I'm helping now. Hey, if you saw where they went, why didn't you follow them?")
			.npc("...")
			.npc("Good luck!")
		)
		// Player then walks north until they enter the forest, where they will see a trail of fish and bones leading to a pile of half eaten fish at -114 51 123
		.then()
		.talkTo(FISH_PILE)
		.dialog(dialog -> dialog
			.player("Who was eating all that raw fish?... It looks like the trail points into town.")
		)
		// Player follows where the trail ends and approaches NPC O'Malley, player right clicks NPC -16 51 63
		.then()
		.talkTo(OMALLEY)
		.dialog(dialog -> dialog
			.player("Excuse me, have you seen anything fishy going on around here as of late?")
			.npc("Huh? Y'know now that you mention it, that penguin was awfully suspicious carrying that big bag o' fish.")
			.player("Penguin??")
			.npc("Yeah, y'know, from the penguin cafe, just east of here? It's my favourite part bout this place.. Best place we've ever docked")
		)
		// The player takes off into the direction of the penguin cafe, heading east, a pile of fish bones can be found at 95, 52, 65 pointing towards the cafe
		// The player enters the cafe and talks to Penguin
		.then()
		.talkTo(PENGUIN_1)
		.dialog(dialog -> dialog
			.player("Honk.. honk?")
			.npc("EXCUSE ME!?")
			.player("AHH")
			.npc("AHHHH... WHY ARE WE SCREAMING?")
			.player("...A penguin.. That speaks english...?")
			.npc("Excuse you, all of the penguin population here speaks english. Except for watermelon, we don't talk about watermelon... Anywho, haven't you seen a bilingual penguin before!?")
			.player("No.. I haven't.. Though I heard that this cafe has really delish fish, where do you guys get your stock from?")
			.npc("Uhm.. normal fish getting places, like the ocean... There's nothing suspicious about our fish or the upstairs of the cafe, what's your problem?")
			.player("I didn't say anything was suspicious.. I was just curious. Thank you for the help..")
		)
		// Player goes to the upstairs of the cafe and right clicks the penguin up there
		.then()
		.talkTo(PENGUIN_2)
		.dialog(dialog -> dialog
			.npc("Honk! You're not supposed to be up here! What are you doing?")
			.player("I'm here to ask about the fish being stolen from the market...")
			.npc("Oh... then why are you here?")
			.player("Because there's a trail of fish and bones leading from the market to the cafe...")
			.npc("Jingle bells! My fish addiction gave me away..")
			.player("Why are you stealing from the market, anyways? There are a bunch of fishing spots around here, there's no need to steal from someone who's just trying to make a living.")
			.npc("Well, there's no hiding now, you've got me! It was indeed I, the leader of the penguin mafia, who has been having all the fish stolen! It was all in preparation for the penguin take over, it's too late to stop us now!")
			.player("Penguin.. Mafia? Do you know what a mafia is? And a penguin takeover?")
			.npc("Yes! How dare you mock our intelligence and look down upon the leader! We, the penguins, will control all of the fish in this area! We will satisfy all our customers!")
			.player("But.. why? This is so confusing...")
			.npc("...Isn't it obvious? It's for the most evil reason of all! The penguin cafe must supply all its customers with delicious snacks and Pugmas cheer!")
			.npc("They'll never see it coming! Before they know it, they'll all be enjoying the most delicious fish meals, and soon? Pugmas will be saved!")
			.player("Can't you do that without, you know, stealing from a local fish vendor at the market?")
			.npc("...Maybe? *sigh* You just don't get it!")
			.npc("Ever since the storm appeared, more and more of the fish have been getting scared away..")
			.player("Even so, won't stealing the fish from people just make them upset? Where's the Pugmas cheer in that?")
			.npc("I guess you're right.. But how will everyone be happy for Pugmas again?")
			.player("...Everyone's trying to help each other out and fix things for Pugmas, that's a start. I think that sharing some of that fish with the vendor in the market would be a great place for you to start.")
			.npc("...Honk, if you say so.. Then perhaps I can find it in my heart to offer that vendor some mercy.. Just this once.")
			.player("No more fish stealing? Just regular fishing?")
			.npc("If fishing is what will incite Pugmas cheer then.. Yes! All penguins on duty, get your fishing rods! Eve can handle the cafe.")
		)
	)
	;

	private final TaskBuilder<?, ?, ?> task;

	@Override
	public TaskBuilder<?, ?, ?> builder() {
		return task;
	}
}
