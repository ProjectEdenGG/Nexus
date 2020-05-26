package me.pugabyte.bncore.features.store;

import lombok.SneakyThrows;
import me.pugabyte.bncore.features.store.annotations.Commands.Command;
import me.pugabyte.bncore.features.store.annotations.ExpirationCommands.ExpirationCommand;
import me.pugabyte.bncore.features.store.annotations.ExpirationDays;
import me.pugabyte.bncore.features.store.annotations.Id;
import me.pugabyte.bncore.features.store.annotations.Permissions.Permission;
import me.pugabyte.bncore.utils.StringUtils;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum Package {

	@Id("2589641")
	CUSTOM_DONATION,

	@Id("1922887")
	@Permission("set.my.prefix")
	CUSTOM_PREFIX_LIFETIME,

	@Id("2730030")
	@Permission("set.my.prefix")
	@ExpirationDays(30)
	@ExpirationCommand("/prefix expire [player]")
	CUSTOM_PREFIX_ONE_MONTH,

	@Id("2019251")
	@Permission("automaticinventory.sortinventory")
	@Permission("automaticinventory.sortchests")
	@Permission("automaticinventory.quickdeposit")
	@Permission("automaticinventory.depositall")
	@Permission("automaticinventory.autocraft")
	AUTO_SORT_LIFETIME,

	@Id("2729981")
	@Permission("automaticinventory.sortinventory")
	@Permission("automaticinventory.sortchests")
	@Permission("automaticinventory.quickdeposit")
	@Permission("automaticinventory.depositall")
	@Permission("automaticinventory.autocraft")
	@ExpirationDays(30)
	AUTO_SORT_ONE_MONTH,

	@Id("2965488")
	@Permission("jq.custom")
	CUSTOM_JOIN_QUIT_MESSAGES_LIFETIME,

	@Id("2965489")
	@Permission("jq.custom")
	@ExpirationDays(30)
	CUSTOM_JOIN_QUIT_MESSAGES_ONE_MONTH,

	@Id("2982264")
	@Permission("durabilitywarning.use")
	DURABILITY_WARNING_LIFETIME,

	@Id("2982265")
	@Permission("durabilitywarning.use")
	@ExpirationDays(30)
	DURABILITY_WARNING_ONE_MONTH,

	@Id("3239567")
	@Permission("emoticons.use")
	EMOTES,

	@Id("3218615")
	@Permission("wings.use")
	@Permission("wings.style.*")
	PARTICLE_WINGS,

	@Id("2019259")
	@Command("/permhelper vaults add [player] 1")
	VAULTS,

	@Id("2019261")
	@Command("/permhelper homes add [player] 5")
	FIVE_SETHOMES,

	@Id("2559650")
	@Permission("citizens.help")
	@Permission("citizens.npc.create")
	@Permission("citizens.npc.create.*")
	@Permission("citizens.npc.profession")
	@Permission("citizens.npc.remove")
	@Permission("citizens.npc.rename")
	@Permission("citizens.npc.edit.equip")
	@Permission("citizens.npc.select")
	@Permission("citizens.npc.skin")
	@Permission("citizens.npc.lookclose")
	@Permission("citizens.npc.skeletontype")
	@Permission("citizens.npc.zombiemodifier")
	@Permission("citizens.npc.age")
	@Permission("citizens.npc.tphere")
	@Permission("citizens.npc.type")
	@Permission("citizens.npc.power")
	@Permission("citizens.npc.edit.path")
	@Permission("citizens.npc.edit.text")
	@Command("/permhelper npcs add [player] 1")
	NPC,

	@Id("2019264")
	@Permission("essentials.skull")
	DONOR_SKULL,

	@Id("2496109")
	@Permission("essentials.hat")
	HAT,

	@Id("2019265")
	@Permission("essentials.ptime")
	PTIME,

	@Id("2559439")
	@Permission("itemname.use")
	ITEM_NAME,

	@Id("2495885")
	@Permission("firework.launch")
	FIREWORKS,

	@Id("2678902")
	@Permission("fireworkbow.single")
	FIREWORK_BOW_SINGLE,

	@Id("2678893")
	@Permission("fireworkbow.infinite")
	FIREWORK_BOW_INFINITE,

	@Id("2495909")
	@Command("/permhelper plots add [player] 1")
	CREATIVE_PLOTS,

	@Id("2495900")
	@Permission("rainbowarmour.use")
	RAINBOW_ARMOUR,

	@Id("2886239")
	@Permission("invis.armour")
	INVISIBLE_ARMOUR,

	@Id("2856645")
	@Permission("rainbow.beacon")
	RAINBOW_BEACON,

	@Id("2495867")
	@Permission("pets.type.chicken")
	@Permission("pets.type.chicken.hat")
	@Permission("pets.type.chicken.data.*")
	@Permission("pets.type.cow")
	@Permission("pets.type.cow.hat")
	@Permission("pets.type.cow.data.*")
	@Permission("pets.type.mooshroom")
	@Permission("pets.type.mooshroom.hat")
	@Permission("pets.type.mooshroom.data.*")
	@Permission("pets.type.sheep")
	@Permission("pets.type.sheep.hat")
	@Permission("pets.type.sheep.data.*")
	@Permission("pets.type.pig")
	@Permission("pets.type.pig.hat")
	@Permission("pets.type.pig.data.*")
	@Permission("pets.type.rabbit")
	@Permission("pets.type.rabbit.hat")
	@Permission("pets.type.rabbit.data.*")
	@Permission("pets.type.bee")
	@Permission("pets.type.bee.hat")
	@Permission("pets.type.bee.data.*")
	PETS_FARM,

	@Id("2495869")
	@Permission("pets.type.fox")
	@Permission("pets.type.fox.hat")
	@Permission("pets.type.fox.data.*")
	@Permission("pets.type.ocelot")
	@Permission("pets.type.ocelot.hat")
	@Permission("pets.type.ocelot.data.*")
	@Permission("pets.type.wolf")
	@Permission("pets.type.wolf.hat")
	@Permission("pets.type.wolf.data.*")
	@Permission("pets.type.cat")
	@Permission("pets.type.cat.hat")
	@Permission("pets.type.cat.data.*")
	@Permission("pets.type.parrot")
	@Permission("pets.type.parrot.hat")
	@Permission("pets.type.parrot.data.*")
	@Permission("pets.type.bat")
	@Permission("pets.type.bat.hat")
	@Permission("pets.type.bat.data.*")
	@Permission("pets.type.panda")
	@Permission("pets.type.panda.hat")
	@Permission("pets.type.panda.data.*")
	PETS_CUTIES,

	@Id("2495876")
	@Permission("pets.type.wanderingtrader")
	@Permission("pets.type.wanderingtrader.hat")
	@Permission("pets.type.wanderingtrader.data.*")
	@Permission("pets.type.vex")
	@Permission("pets.type.vex.hat")
	@Permission("pets.type.vex.data.*")
	@Permission("pets.type.villager")
	@Permission("pets.type.villager.hat")
	@Permission("pets.type.villager.data.*")
	@Permission("pets.type.zombievillager")
	@Permission("pets.type.zombievillager.hat")
	@Permission("pets.type.zombievillager.data.*")
	@Permission("pets.type.evoker")
	@Permission("pets.type.evoker.hat")
	@Permission("pets.type.evoker.data.*")
	@Permission("pets.type.illusioner")
	@Permission("pets.type.illusioner.hat")
	@Permission("pets.type.illusioner.data.*")
	@Permission("pets.type.ravager")
	@Permission("pets.type.ravager.hat")
	@Permission("pets.type.ravager.data.*")
	@Permission("pets.type.vindicator")
	@Permission("pets.type.vindicator.hat")
	@Permission("pets.type.vindicator.data.*")
	@Permission("pets.type.pillager")
	@Permission("pets.type.pillager.hat")
	@Permission("pets.type.pillager.data.*")
	PETS_NATIVES,

	@Id("3919092")
	@Permission("pets.type.dolphin")
	@Permission("pets.type.dolphin.hat")
	@Permission("pets.type.dolphin.data.*")
	@Permission("pets.type.squid")
	@Permission("pets.type.squid.hat")
	@Permission("pets.type.squid.data.*")
	@Permission("pets.type.turtle")
	@Permission("pets.type.turtle.hat")
	@Permission("pets.type.turtle.data.*")
	@Permission("pets.type.drowned")
	@Permission("pets.type.drowned.hat")
	@Permission("pets.type.drowned.data.*")
	@Permission("pets.type.guardian")
	@Permission("pets.type.guardian.hat")
	@Permission("pets.type.guardian.data.*")
	@Permission("pets.type.polarbear")
	@Permission("pets.type.polarbear.hat")
	@Permission("pets.type.polarbear.data.*")
	@Permission("pets.type.pufferfish")
	@Permission("pets.type.pufferfish.hat")
	@Permission("pets.type.pufferfish.data.*")
	@Permission("pets.type.cod")
	@Permission("pets.type.cod.hat")
	@Permission("pets.type.cod.data.*")
	@Permission("pets.type.salmon")
	@Permission("pets.type.salmon.hat")
	@Permission("pets.type.salmon.data.*")
	PETS_AQUATIC,

	@Id("2495873")
	@Permission("pets.type.witherskeleton")
	@Permission("pets.type.witherskeleton.hat")
	@Permission("pets.type.witherskeleton.data.*")
	@Permission("pets.type.ghast")
	@Permission("pets.type.ghast.hat")
	@Permission("pets.type.ghast.data.*")
	@Permission("pets.type.blaze")
	@Permission("pets.type.blaze.hat")
	@Permission("pets.type.blaze.data.*")
	@Permission("pets.type.magmacube")
	@Permission("pets.type.magmacube.hat")
	@Permission("pets.type.magmacube.data.*")
	@Permission("pets.type.hoglin")
	@Permission("pets.type.hoglin.hat")
	@Permission("pets.type.hoglin.data.*")
	@Permission("pets.type.piglin")
	@Permission("pets.type.piglin.hat")
	@Permission("pets.type.piglin.data.*")
	@Permission("pets.type.zoglin")
	@Permission("pets.type.zoglin.hat")
	@Permission("pets.type.zoglin.data.*")
	@Permission("pets.type.strider")
	@Permission("pets.type.strider.hat")
	@Permission("pets.type.strider.data.*")
	@Permission("pets.type.pigzombie")
	@Permission("pets.type.pigzombie.hat")
	@Permission("pets.type.pigzombie.data.*")
	PETS_NETHER,

	@Id("2495872")
	@Permission("pets.type.cavespider")
	@Permission("pets.type.cavespider.hat")
	@Permission("pets.type.cavespider.data.*")
	@Permission("pets.type.creeper")
	@Permission("pets.type.creeper.hat")
	@Permission("pets.type.creeper.data.*")
	@Permission("pets.type.skeleton")
	@Permission("pets.type.skeleton.hat")
	@Permission("pets.type.skeleton.data.*")
	@Permission("pets.type.spider")
	@Permission("pets.type.spider.hat")
	@Permission("pets.type.spider.data.*")
	@Permission("pets.type.witch")
	@Permission("pets.type.witch.hat")
	@Permission("pets.type.witch.data.*")
	@Permission("pets.type.zombie")
	@Permission("pets.type.zombie.hat")
	@Permission("pets.type.zombie.data.*")
	@Permission("pets.type.husk")
	@Permission("pets.type.husk.hat")
	@Permission("pets.type.husk.data.*")
	@Permission("pets.type.stray")
	@Permission("pets.type.stray.hat")
	@Permission("pets.type.stray.data.*")
	PETS_MONSTERS,

	@Id("2495871")
	@Permission("pets.type.horse")
	@Permission("pets.type.horse.mount")
	@Permission("pets.type.horse.hat")
	@Permission("pets.type.horse.data.*")
	@Permission("pets.type.skeletonhorse")
	@Permission("pets.type.skeletonhorse.mount")
	@Permission("pets.type.skeletonhorse.hat")
	@Permission("pets.type.skeletonhorse.data.*")
	@Permission("pets.type.zombiehorse")
	@Permission("pets.type.zombiehorse.mount")
	@Permission("pets.type.zombiehorse.hat")
	@Permission("pets.type.zombiehorse.data.*")
	@Permission("pets.type.donkey")
	@Permission("pets.type.donkey.mount")
	@Permission("pets.type.donkey.hat")
	@Permission("pets.type.donkey.data.*")
	@Permission("pets.type.mule")
	@Permission("pets.type.mule.mount")
	@Permission("pets.type.mule.hat")
	@Permission("pets.type.mule.data.*")
	@Permission("pets.type.llama")
	@Permission("pets.type.llama.mount")
	@Permission("pets.type.llama.hat")
	@Permission("pets.type.llama.data.*")
	@Permission("pets.type.traderllama")
	@Permission("pets.type.traderllama.mount")
	@Permission("pets.type.traderllama.hat")
	@Permission("pets.type.traderllama.data.*")
	PETS_MOUNTS,

	@Id("2495870")
	@Permission("pets.type.enderman")
	@Permission("pets.type.enderman.hat")
	@Permission("pets.type.enderman.data.*")
	@Permission("pets.type.endermite")
	@Permission("pets.type.endermite.hat")
	@Permission("pets.type.endermite.data.*")
	@Permission("pets.type.shulker")
	@Permission("pets.type.shulker.hat")
	@Permission("pets.type.shulker.data.*")
	@Permission("pets.type.phantom")
	@Permission("pets.type.phantom.hat")
	@Permission("pets.type.phantom.data.*")
	@Permission("pets.type.silverfish")
	@Permission("pets.type.silverfish.hat")
	@Permission("pets.type.silverfish.data.*")
	@Permission("pets.type.slime")
	@Permission("pets.type.slime.hat")
	@Permission("pets.type.slime.data.*")
	@Permission("pets.type.snowman")
	@Permission("pets.type.snowman.hat")
	@Permission("pets.type.snowman.data.*")
	@Permission("pets.type.irongolem")
	@Permission("pets.type.irongolem.hat")
	@Permission("pets.type.irongolem.data.*")
	PETS_OTHER,

	@Id("2496219")
	@Permission("miniaturepets.pet.BB8")
	@Permission("miniaturepets.pet.Bee")
	@Permission("miniaturepets.pet.Boxer")
	@Permission("miniaturepets.pet.Camera")
	@Permission("miniaturepets.pet.Chimp")
	@Permission("miniaturepets.pet.Duck")
	@Permission("miniaturepets.pet.earth")
	@Permission("miniaturepets.pet.FacebookLogo")
	@Permission("miniaturepets.pet.Frog")
	@Permission("miniaturepets.pet.Giraffe")
	@Permission("miniaturepets.pet.Gorilla")
	@Permission("miniaturepets.pet.hamster")
	@Permission("miniaturepets.pet.InstagramLogo")
	@Permission("miniaturepets.pet.King")
	@Permission("miniaturepets.pet.Koala")
	@Permission("miniaturepets.pet.LionCub")
	@Permission("miniaturepets.pet.Lion")
	@Permission("miniaturepets.pet.Milker")
	@Permission("miniaturepets.pet.Milk")
	@Permission("miniaturepets.pet.MiniMe")
	@Permission("miniaturepets.pet.Moon")
	@Permission("miniaturepets.pet.Pug")
	@Permission("miniaturepets.pet.Panda")
	@Permission("miniaturepets.pet.Penguin")
	@Permission("miniaturepets.pet.PerryThePlatipus")
	@Permission("miniaturepets.pet.PolarBearCub")
	@Permission("miniaturepets.pet.Princess")
	@Permission("miniaturepets.pet.Pug")
	@Permission("miniaturepets.pet.Rapina")
	@Permission("miniaturepets.pet.Robo")
	@Permission("miniaturepets.pet.Snowglobe")
	@Permission("miniaturepets.pet.Soccer")
	@Permission("miniaturepets.pet.Summerglobe")
	@Permission("miniaturepets.pet.Sun")
	@Permission("miniaturepets.pet.TigerCub")
	@Permission("miniaturepets.pet.Tiger")
	@Permission("miniaturepets.pet.Turtle")
	@Permission("miniaturepets.pet.TwitchLogo")
	@Permission("miniaturepets.pet.TwitterLogo")
	@Permission("miniaturepets.pet.YouTubeLogo")
	@Permission("miniaturepets.pet.ZombieHead")
	@Permission("miniaturepets.pet.ZombieStatue")
	@Permission("miniaturepets.pet.ZombieStatueAir")
	@Permission("miniaturepets.pet.footballplayer")
	@Permission("miniaturepets.pet.footballplayermini")
	@Permission("pokeblocks.mob.magifish")
	@Permission("pokeblocks.mob.bulbasaur")
	@Permission("minime.edsheeran")
	@Permission("quickpets.ownerpet")
	@Permission("MinaturePets.Giraffe")
	@Permission("puffle.pet.blue")
	@Permission("minecraft.steve")
	@Permission("MinaturePets.Witherboss")
	@Permission("minaturepets.voltorb")
	@Permission("MiniaturePets.Koala")
	@Permission("pokeblocks.mob.pikachu")
	@Permission("miniaturepets.BearCub")
	@Permission("miniaturepets.Cat")
	@Permission("miniaturepets.Dipper")
	@Permission("miniaturepets.Tech")
	@Permission("miniaturepets.FrostySnowman")
	@Permission("miniaturepets.FrosytFriend")
	@Permission("miniaturepets.GrunkleStan")
	@Permission("miniaturepets.Jake")
	@Permission("miniaturepets.King")
	@Permission("miniaturepets.Mabel")
	@Permission("miniaturepets.Ocelot")
	@Permission("miniaturepets.Princess")
	@Permission("miniaturepets.Squirrel")
	MINIATURE_PETS,

	@Id("2495938")
	@Permission("libsdisguises.disguise.cow.setBaby.setBurning.setArrowsSticking.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.chicken.setArrowsSticking.setBurning.setBaby.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.pig.setSaddled.setArrowsSticking.setBurning.setBaby.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.sheep.setBaby.setColor.setSheared.setArrowsSticking.setBurning.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.mushroom_cow.setArrowsSticking.setBurning.setBaby.setViewSelfDisguise")
	DISGUISES_FARM_ANIMALS,

	@Id("2495940")
	@Permission("libsdisguises.disguise.ocelot.setArrowsSticking.setBurning.setBaby.setType.setSitting.setTamed.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.rabbit.setBaby.setType.setArrowsSticking.setBurning.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.wolf.setBaby.setSneaking.setTamed.setAngry.setBegging.setCollarColor.setArrowsSticking.setBurning.setSitting.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.parrot.setArrowsSticking.setBurning.setViewSelfDisguise.setBaby.setSitting.setTamed.setVariant")
	DISGUISES_DOMESTIC_ANIMALS,

	@Id("2495941")
	@Permission("libsdisguises.disguise.villager.setArrowsSticking.setBurning.setBaby.setProfession.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.iron_golem.setArrowsSticking.setBurning.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.snowman.setArrowsSticking.setBurning.setHat.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.squid.setArrowsSticking.setBurning.setViewSelfDisguise")
	DISGUISES_LEFTOVERS,

	@Id("2495942")
	@Permission("libsdisguises.disguise.mule.setArrowsSticking.setBurning.setBaby.setCarryingChest.setColor.setGrazing.setHorseArmor.setMouthOpen.setRearing.setSaddled.setStyle.setTamed.setVariant.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.horse.setArrowsSticking.setBurning.setBaby.setCarryingChest.setColor.setGrazing.setHorseArmor.setMouthOpen.setRearing.setSaddled.setStyle.setTamed.setVariant.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.donkey.setArrowsSticking.setBurning.setBaby.setCarryingChest.setColor.setGrazing.setHorseArmor.setMouthOpen.setRearing.setSaddled.setStyle.setTamed.setVariant.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.llama.setArrowsSticking.setBurning.setBaby.setCarryingChest.setColor.setGrazing.setCarpet.setMouthOpen.setRearing.setSaddled.setStyle.setTamed.setViewSelfDisguise")
	DISGUISES_RIDABLE_ANIMALS,

	@Id("2495944")
	@Permission("libsdisguises.disguise.zombie.setArrowsSticking.setBurning.setArmor.setItemInMainHand.setItemInOffHand.setAggressive.setBaby.setProfession.setShaking.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.skeleton.setArrowsSticking.setBurning.setArmor.setItemInMainHand.setItemInOffHand.setSwingArms.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.cave_spider.setArrowsSticking.setBurning.setClimbing.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.spider.setArrowsSticking.setBurning.setClimbing.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.creeper.setIgnited.setPowered.setBurning.setArrowsSticking.setBurning.setViewSelfDisguise")
	DISGUISES_MONSTERS,

	@Id("2495945")
	@Permission("libsdisguises.disguise.blaze.setArrowsSticking.setBurning.setBlazing.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.pigman.setArrowsSticking.setBurning.setItemInMainHand.setItemInOffHand.setAggressive.setBaby.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.ghast.setArrowsSticking.setBurning.setAggressive.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.magma_cube.setArrowsSticking.setBurning.setSize.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.wither_skeleton.setArrowsSticking.setBurning.setSwingArms.setItemInMainHand.setItemInOffHand.setArmor.setViewSelfDisguise")
	DISGUISES_NETHER,

	@Id("2495946")
	@Permission("libsdisguises.disguise.enderman.setArrowsSticking.setBurning.setAggressive.setItemInMainHand.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.endermite.setArrowsSticking.setBurning.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.shulker.setArrowsSticking.setBurning.setFacingDirection.setShieldHeight.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.silverfish.setArrowsSticking.setBurning.setViewSelfDisguise")
	DISGUISES_END_CREATURES,

	@Id("2495947")
	@Permission("libsdisguises.disguise.bat.setArrowsSticking.setBurning.setHanging.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.witch.setArrowsSticking.setBurning.setAggressive.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.slime.setArrowsSticking.setBurning.setSize.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.stray.setArrowsSticking.setBurning.setSwingArms.setArmor.setItemInMainHand.setItemInOffHand.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.husk.setArrowsSticking.setBurning.setBaby.setProfession.setShaking.setAggressive.setArmor.setViewSelfDisguise")
	DISGUISES_SPOOKIES,

	@Id("2495948")
	@Permission("libsdisguises.disguise.evoker.setArrowsSticking.setBurning.setSpellTicks.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.vex.setArrowsSticking.setBurning.setAngry.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.vindicator.setArrowsSticking.setBurning.setJohnny.setViewSelfDisguise")
	@Permission("libsdisguises.disguise.illusioner.setArrowsSticking.setBurning.setSpellTicks.setViewSelfDisguise")
	DISGUISES_NEW_GENERATION_MONSTERS;

	@SneakyThrows
	public Field getField() {
		return getClass().getDeclaredField(name());
	}

	public String getId() {
		return getField().getAnnotation(Id.class).value();
	}

	public List<String> getPermissions() {
		return Arrays.stream(getField().getAnnotationsByType(Permission.class))
				.map(Permission::value)
				.collect(Collectors.toList());
	}

	public List<String> getCommands() {
		return Arrays.stream(getField().getAnnotationsByType(Command.class))
				.map(Command::value)
				.map(StringUtils::trimFirst)
				.collect(Collectors.toList());
	}

	public int getExpirationDays() {
		if (getField().getAnnotation(ExpirationDays.class) != null)
			return getField().getAnnotation(ExpirationDays.class).value();
		return -1;
	}

	public List<String> getExpirationCommands() {
		return Arrays.stream(getField().getAnnotationsByType(ExpirationCommand.class))
				.map(ExpirationCommand::value)
				.map(StringUtils::trimFirst)
				.collect(Collectors.toList());
	}

	public static Package getPackage(String id) {
		for (Package value : values())
			if (value.getId().equals(id))
				return value;
		return null;
	}

}
