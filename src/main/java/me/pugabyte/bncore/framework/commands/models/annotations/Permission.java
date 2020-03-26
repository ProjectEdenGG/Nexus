package me.pugabyte.bncore.framework.commands.models.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Permission {
	String value();

	boolean absolute() default false;
}

/*
	Groups:
		group.admin         - admin+
		group.seniorstaff   - operator+
		group.moderator     - moderator+
		group.builder       - builder/architect
		group.staff         - staff + builders

	Rank Exclusive:
		rank.<rankname>

	Ex:
		ladder.trusted = trusted and above
		ladder.builder = builder, architect, operator, admin, owner
	Rank Ladder:
           ladder.guest
           ladder.member
           ladder.trusted
           ladder.elite
           ladder.veteran
             /        \
  ladder.moderator   ladder.builder
            |        ladder.architect
             \        /
           ladder.operator
           ladder.admin
           ladder.owner

 */
