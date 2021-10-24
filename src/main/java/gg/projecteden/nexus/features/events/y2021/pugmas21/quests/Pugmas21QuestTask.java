package gg.projecteden.nexus.features.events.y2021.pugmas21.quests;

import gg.projecteden.nexus.features.events.y2021.pugmas21.models.QuestItems;
import gg.projecteden.nexus.features.events.y2021.pugmas21.models.TreeType;
import gg.projecteden.nexus.features.quests.tasks.GatherQuestTask;
import gg.projecteden.nexus.features.quests.tasks.InteractQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.IQuestTask;
import gg.projecteden.nexus.features.quests.tasks.common.QuestTask;
import gg.projecteden.utils.TimeUtils.TickTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.BELLAMY;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.CAPTAIN_NERISSA;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.CASSIA;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.CEDAR;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.FLINT;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.GLORIA;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.JUNIPER;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.MYSTERIOUS_WOMAN;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.PANSY;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.PINE;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.REED;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.ROWAN;
import static gg.projecteden.nexus.features.events.y2021.pugmas21.quests.Pugmas21NPC.WARREN;
import static gg.projecteden.nexus.features.quests.QuestReward.EVENT_TOKENS;

@Getter
@AllArgsConstructor
public enum Pugmas21QuestTask implements IQuestTask {
	CRYSTAL_REPAIR_1(InteractQuestTask.builder()
		.talkTo(GLORIA)
		.dialog(dialog -> dialog
			.npc("Well ain't you a sight for these tired old eyes.")
			.npc("I didn't think anyone would be coming this year with the storm and everything.")
			.npc("Where are my manners, my name is " + GLORIA + " and welcome to Pugmas!")
			.player("Oh... ah, hey there! My name's {{PLAYER_NAME}}. Is this weather normal for this time of year?")
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
			.player("I'll head into the forest and see if I can find a lead on this deer.")
			.npc("Oh thank you, thank you!")
		)
		.then()
		.talkTo(MYSTERIOUS_WOMAN)
		.dialog(dialog -> dialog
			.npc("Ohhh, what's this I see? A visitor? I haven't had one of those in a long time.")
			.player("Hello there, sorry to intrude. My name is {{PLAYER_NAME}}, I was told that I might be able to find the guardian out here.")
			.npc("The guardian you say? That is something I haven't heard in quite a while. What interest is the guardian to you?")
			.player("I just want to help out. A nice lady I met when I first got here asked me to try and seek the guardian out so the crystal may be restored and so that Pugmas can go ahead.")
			.npc("I'll tell you what. If you help out the people of the village, I'll tell you a bit more about both the guardian and the crystal.")
			.player("Ok, who should I help first?")
			.npc("In the distance, I can hear the sound of an elf singing. You may find them on the bridge leading to the crystal. Go and help them out, then return here.")
		)
		.build()
	),
	CLEAR_SKIES(GatherQuestTask.builder()
		.talkTo(JUNIPER)
		.dialog(dialog -> dialog
			.npc("🎵 Jingle bells, jingle bells, jingle all the way~ Oh what fun it is to ride in a one horse open slei- 🎵 AH! My bells! How long have you been standing there!?")
			.player("Somewhere around the beginning of \"Jingle Bells\"? ")
			.npc("Eh?! Why... ")
			.npc("Hmm... Actually, wait! You might be helpful after all.")
			.npc("I'm sure that you're aware of the awful storm that has been tearing everything up lately.")
			.npc("I mean It's kind of hard to miss it, unless this is normal where you're from. ")
			.npc("Anyways, that's besides the point. My point is that I need your help to get rid of it. ")
			.npc("This dumb storm has ruined everything in our village, if this keeps up then you can say goodbye to any pugmas celebrations you were looking forward to. ")
			.player("The whole reason I came here was to celebrate Pugmas, so I have to do something!")
			.npc("Hurray! Oh {{PLAYER_NAME}}, I just knew you'd help out. ")
			.npc("Now off you go! Good luck and safe travels!")
			.player("...Huh? Wait, what do you mean?")
			.npc("Is there a problem?")
			.player("Yes, you forgot to mention how to get rid of the storm?")
			.npc("You think I know? That's why I asked you for help! I guess I really should've just gone to that weather guy instead..")
			.player("Weather guy?")
			.npc("Yeah there's this strange guy who hangs out near the docs, he stays there and studies the change in weather. ")
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
			.player("Hi! Are you {{NPC_NAME}}?")
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
			.player("Okay, awesome! Thanks {{NPC_NAME}}!")
			.npc("No problem! And you know if you take a liking to the miner's life just let me know, we could always use some extra hands.")
		)
		.then()
		.talkTo(WARREN)
		.dialog(dialog -> dialog
			.player("Excuse me, are you {{NPC_NAME}} by any chance?")
			.npc("Hm? Oh, yes that's me.")
			.npc("Is there something I can help you with?")
			.player("Yes actually! I was hoping you could point me in the direction of some crystal wood.")
			.npc("Oh, yeah of course. You can find them in a cave. Do you know how to harvest them?")
			.player("Haha, um... I guess I've just been figuring these things out as I go.")
			.npc("Unfortunately when it comes to these trees, being unprepared won't do, they can be fickle things.")
			.npc("Why don't you take my spare axe from the wall? It's not the best but it should get the job done.")
			.player("Thank you {{NPC_NAME}}!")
			.npc("It's really no problem. Feel free to stop by anytime.")
		)
		.then()
		.talkTo(BELLAMY)
		.dialog(dialog -> dialog
			.npc("Ah! You're back!")
		)
		.reminder(dialog -> dialog
			// TODO
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
			.npc("Then it's settled, bring me 50 oak logs and I'll make your basket for you. ")
			.player("Okay!")
		)
		.reminder(dialog -> dialog
			// TODO
		)
		.gather(TreeType.OAK.item(50))
		.complete(dialog -> dialog
			.npc("Ah perfect! It's pretty good quality too. You sure you wanna continue being " + BELLAMY + "'s assistant? I could use someone like you working around the shop.")
			.player("I'm sorry but I think I'll have to pass.")
			.npc("Suit yourself. Just give me a few minutes.")
			// TODO Sounds
			.wait(TickTime.SECOND.x(3))
			.npc("And... done! Here you go, one wooden basket!")
			.give(QuestItems.BALLOON_BASKET)
			.player("Thanks again warren!")
		)
		.then()
		.talkTo(ROWAN)
		.dialog(dialog -> dialog
			.npc("Oh, hello there!")
			.npc("What a surprise! I wasn't expecting any customers this time of year, welcome to my costume store. ")
			.npc("Are you in need of a dazzling costume? Trying to prepare for Halloween early perhaps?")
			.player("Oh actually-")
			.npc("Just step over here and we can get you all measured up!")
			.player("Wait I don't need a-")
			.npc("Now then, did you have a specific costume in mind? We can do a dinosaur, superhero, vampire-")
			.player("I don't need a costume!!")
			.npc("..Oh, I'm sorry I got a little excited there. What is it I can do for you then? ")
			.player("I need a hot air balloon envelope and was hoping you could help.")
			.npc("Oh, you're " + BELLAMY + "'s assistant! Why didn't you just say so? I have it right here for you.")
			.npc("Now just to add this up... ")
			.npc("That will be $10,000 please.")
			.player("...What?")
			.npc("Pfft.. bahaha, I'm just joking! Sorry about that, but the look on your face was just too good.")
			.give(QuestItems.BALLOON_ENVELOPE)
			.player("Oh, okay then haha.")
			.npc("Please come again! I wouldn't want to have scared you off with that little joke. ")
		)
		.then()
		.talkTo(BELLAMY)
		.reminder(dialog -> dialog
			// TODO
		)
		.gather(QuestItems.BALLOON_BASKET, QuestItems.BALLOON_ENVELOPE)
		.complete(dialog -> dialog
			.npc("These are high quality materials so this shouldn't take too long.")
			.npc("Just gotta attach these here, put that there... and done!")
			.give(QuestItems.BALLOON)
			.npc("Now go get me that cloud in a bottle! I'll meet you by the docks with the machine so we can get rid of this storm, once and for all.")
		)
		.then()
		// TODO Hot air balloon
		//    Spawn point: /tppos -35.50 58.00 238.50 90 0 pugmas21
		.then()
		.talkTo(BELLAMY)
		.reminder(dialog -> dialog
			// TODO
		)
		.gather(QuestItems.BOTTLED_CLOUD)
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
			// TODO
		)
		.gather(Material.REDSTONE, 40)
		.complete(dialog -> dialog
			.npc("Is that the redstone? Hand it over!")
			.npc("Okay let's do this now that it's all powered up!")
			.npc("...")
			.npc("Still nothing...")
			.npc("I'm so sorry {{PLAYER_NAME}}. I made you run all over the place for nothing.")
			.npc("Sigh. I was so sure it was going to work.")
			.npc("I wanted to give you something as a thank you for your hard work but I'm afraid I don't have any money. I hope this item will be okay.")
			.give(QuestItems.CRYSTAL_PIECE)
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
			// If Player chose Pirates
			.npc("To find it, help out the Captain with their troubles. You can find them at the dock.")
			// If Player chose Elves
			.npc("To find it, head to Santa's Manor where you will find a troubled young elf.")
			// (Player heads to starting location for Holiday Heist)
		)
		.build()
	),
	HOLIDAY_HEIST_PIRATES(InteractQuestTask.builder()
		.talkTo(CAPTAIN_NERISSA)
		.dialog(dialog -> dialog
			.npc("Mm. Okay, this is really not good.")
			.npc("Oh, {{PLAYER_NAME}}, why are you just standing around? Come over here.")
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
			// Once the npc has received the items
			.npc("Perfect! Let me work my magic for you, it won't take long.")
			// Wait a few seconds
			.npc("Boom! Okay there, a perfect disguise!")
			// Give player a pair of elf ears
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
		.dialog(dialog -> dialog
			.npc("You're back! I knew you'd return!")
			.npc("And there's something in your hand, the elves did have it! You found-")
			.npc("Wait. {{PLAYER_NAME}}... what is this?")
			.npc("I said you were supposed to bring back a ribbon, how did you manage to confuse it with a recipe for pugmas cookies!?")
			.player("That's.. what you asked me to get. Do you want me to return it?")
			.npc("Of course not! Then they'd know you were snooping around!")
			.npc("Just bring it to the kitchen I suppose, I've got to focus on finding that ribbon")
		)
		.then()
		.talkTo(PANSY)
		.dialog(dialog -> dialog
			.npc("Oh, hey {{PLAYER_NAME}}! I didn't expect to see you here.")
			.npc("What's that you're holding?")
			.player("It's a cookie recipe that I accidentally stol-, I mean stumbled upon while helping around the island. " + CAPTAIN_NERISSA + " thought you could use it.")
			.npc("Really? Oh I love learning new recipes, thank you! This will certainly go to good use!")
			.npc("But wait, you said that the Captain sent you? Can you bring this to them?")
			// Give player the ribbon
			.npc("They left it here this morning and I've been meaning to return it all day.")
			.player("...You mean it was here this whole time?")
			.npc("Hmm? I'm sorry, I'm not sure what you mean by that.")
			.player("Never mind. Of course I can bring it to them.")
			.npc("Thank you!")
		)
		.then()
		.talkTo(CAPTAIN_NERISSA)
		.dialog(dialog -> dialog
			.player(PANSY + " wanted me to give this to you.")
			.npc("Is this what I think it is? The item I've been looking for all day?")
			.player("Yeah, it is. Turns out you left it in the kitchen this morning, meaning it was here this whole time!")
			.npc("Oh haha... sorry for dragging you into this. My bad memory has once again proved to be troublesome.")
			.npc("I guess I owe you some compensation for the trouble I put you through. Here, take this.")
			// Second crystal piece obtained!
		)
		.build()
	),
	HOLIDAY_HEIST_ELVES(InteractQuestTask.builder()
		.talkTo(CASSIA)
		.dialog(dialog -> dialog
			.npc("Oh sleigh bells! Just where has it gone!? ")
			.npc("Hey, you! Have you seen a recipe laying around somewhere!? ")
			.player("Nope, I just got here.")
			.npc("..Oh... What am I supposed to do then? I needed that.. Do you think those pesky pirates could have taken it? I mean, they did cause this mess in the first place.")
			.player("Have you checked everywhere else? It's not good to blame people without reason...")
			.npc("But I have a reason! Those pirates have been causing trouble ever since they've arrived! It has to be them.")
			.player("If you say so. Can I help you look for the item?")
			.npc("No need to look around here, I'm sure I know where it is now! ")
			.npc("{{PLAYER_NAME}}, do you still want to help? If so, why don't you sneak into the pirate headquarters for me! Your mission is to retrieve my recipe and return safely!")
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
			// Once the npc has received the items
			.npc("Perfect! Let me work my magic for you, it won't take long.")
			// Wait a few seconds
			.npc("Boom! Okay there, a perfect disguise!")
			// Give player a pirate hat
			.player("This looks amazing! Thank you, this is exactly what I needed.")
			.npc("Haha! Well, I'm not known as the best costume designer for nothing.")
			.npc("Do come again!")
			// Player returns to NPC Main
			.npc("Woah, that disguise is perfect! I almost mistook you for one of those pesky pirates. ")
			.npc("As soon as you're ready to begin your mission, let me know!")
			// Player right clicks NPC
			.player("I'm ready.")
			// Coordinates/the name of the area are given to the player in chat.
			// Player arrives and steps on <block>, warping them to a room where this part takes place
		)
		// Player grabs book and warps back to Main Npc
		.then()
		.talkTo(CASSIA)
		.dialog(dialog -> dialog
			.npc("You did it! I always knew you could!")
			.npc("In your hand! Is that my ribbon? I was right about those pesky pirates, I knew they couldn't be trus-")
			.npc("..Wait.. {{PLAYER_NAME}}, what is that?")
			.npc("Didn't I tell you that you were supposed to be looking for a ribbon? How did you get that confused with a recipe for Flash-Fried Filet? ")
			.player("That's.. what you asked me to get. Well, do you want me to return it? I didn't see a ribbon there.")
			.npc("Er- No! We'll get in trouble if they find out we were snooping around...")
			.npc("Why don't you go give it to our chef, " + PINE + ", I'm sure she could benefit from it. ")
		)
		.then()
		.talkTo(PINE)
		.dialog(dialog -> dialog
			.npc("Oh, hey new recruit! I didn't expect to see you here. It's nice to meet you, I'm {{NPC_NAME}}.")
			.player("Hi, I'm {{PLAYER_NAME}}. " + CASSIA + " asked me to give this to you.")
			.npc("Oh, lovely! What is it?")
			.player("It's a pirate dish recipe that I accidentally stol-, I mean stumbled upon while helping around the island. " + CASSIA + " thought you could use it.")
			.npc("Really!? I love learning new recipes, thank you so much! Pirate recipe.. It reminds me of my sibling " + PANSY + ". ")
			.npc("I'll make sure to perfect this dish! Oh, and you said " + CASSIA + "? Would you mind bringing this ribbon to them? I heard they were looking for it.")
			.npc("She left it here this morning, I've been meaning to bring it to her all day.")
			.player("Wha- it's been here the whole time!? ")
			.npc("Huh? ")
			.player("Oh it's.. Nothing. I'll bring it to her right away! ")
			.npc("Thanks a ton!")
		)
		.then()
		.talkTo(CASSIA)
		.dialog(dialog -> dialog
			.player("{{NPC_NAME}}, I found your-")
			.npc("Eh!? Is that my ribbon? Has it been here this whole time?")
			.player("Yup. " + PINE + " said you left it in the kitchen this morning...")
			.npc("Hehe, looks like my bad memory has caused problems for us once again. I'm sorry for dragging you into this.")
			.npc("Here, take this as a thank you, and an apology.")
			// Second crystal piece obtained!
		)
		.build()
	),


	// Testing
	COSTUME_STORE_GATHER_WOOD(GatherQuestTask.builder()
		.talkTo(ROWAN)
		.dialog(dialog -> dialog
			.npc("hi i need help")
			.player("ok whats up")
			.npc("gimme wood")
		)
		.reminder(dialog -> dialog
			.npc("wheres my wood")
		)
		.gather(new ItemStack(Material.OAK_LOG, 32))
		.complete(dialog -> dialog
			.npc("thanks")
			.player("np")
		)
		.reward(EVENT_TOKENS, 40)
		.build()
	),
	INVESTIGATE_PENGUIN_MAFIA(InteractQuestTask.builder()
		.talkTo(Pugmas21NPC.FISH_VENDOR)
		.dialog(dialog -> dialog
			.npc("someone stole my fish")
			.player("oh no how can i help")
			.npc("they went that way")
		)
		.reminder(dialog -> dialog
			.npc("what are you still doing here")
			.npc("they went that way")
		)
		.then()
		.talkTo(Pugmas21NPC.OMALLEY)
		.dialog(dialog -> dialog
			.player("did u see anything fishy")
			.npc("ya that penguin")
		)
		.then()
		.talkTo(Pugmas21Entity.PENGUIN_1)
		.dialog(dialog -> dialog
			.player("did u steal")
			.npc("no of course not")
			.npc("dont go upstairs, nothing suspicious up there")
		)
		.then()
		.talkTo(Pugmas21Entity.PENGUIN_2)
		.dialog(dialog -> dialog
			.player("where'd this fish come from")
			.npc("aw u caught me")
			.player("stop it")
			.npc("ok")
		)
		.then()
		.talkTo(Pugmas21NPC.FISH_VENDOR)
		.dialog(dialog -> dialog
			.player("it was the penguins")
			.player("i told them to stop")
			.npc("ok thanks")
		)
		.reward(EVENT_TOKENS, 40)
		.build()
	),
	;

	private final QuestTask<?, ?> task;

	@Override
	public QuestTask<?, ?> get() {
		return task;
	}
}
