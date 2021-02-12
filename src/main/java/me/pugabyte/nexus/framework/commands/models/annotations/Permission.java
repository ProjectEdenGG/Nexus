package me.pugabyte.nexus.framework.commands.models.annotations;

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
		group.moderator	    - mod+
		group.builder       - builder/architect
		group.staff         - staff + builders
		group.nonstaff      - guest - veteran

	Rank Exclusive:
		rank.<rankname>

	Ex:
		ladder.trusted = trusted and above
		ladder.builder = builder, architect, operator, admin, owner
	Rank Ladder:
           ladder.owner
           ladder.admin
           ladder.operator
             /        \
  ladder.moderator   ladder.architect
            |        ladder.builder
             \        /
           ladder.veteran
           ladder.elite
           ladder.trusted
           ladder.member
           ladder.guest
 */
