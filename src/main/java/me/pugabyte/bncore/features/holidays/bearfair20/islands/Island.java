package me.pugabyte.bncore.features.holidays.bearfair20.islands;

import lombok.SneakyThrows;
import me.pugabyte.bncore.features.holidays.annotations.Region;
import me.pugabyte.bncore.features.holidays.bearfair20.BearFair20;
import me.pugabyte.bncore.features.holidays.bearfair20.quests.npcs.Talkers.TalkingNPC;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface Island {
	default String getRegion() {
		return BearFair20.getRegion() + "_" + getClass().getAnnotation(Region.class).value();
	}

	@SneakyThrows
	default TalkingNPC getNPC(int id) {
		Class<? extends Enum<? extends TalkingNPC>> npcGroup = getNpcGroup();
		for (Enum<? extends TalkingNPC> enumConstant : npcGroup.getEnumConstants()) {
			if (((TalkingNPC) enumConstant).getNpcId() == id)
				return (TalkingNPC) enumConstant;
		}

		return null;
	}

	default Class<? extends Enum<? extends TalkingNPC>> getNpcGroup() {
		return getClass().getAnnotation(NPCClass.class).value();
	}

	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	@interface NPCClass {
		Class<? extends Enum<? extends TalkingNPC>> value();
	}

}
