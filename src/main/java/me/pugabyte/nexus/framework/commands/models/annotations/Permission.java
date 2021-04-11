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

	Ladder: test if someone has a rank or greater, according to the below hierarchy
	Rank Exclusive: test if someone has specifically the rank <rankname> with rank.<rankname>

	Ex:
		ladder.trusted = trusted and above
		ladder.builder = builder, architect, operator, admin, owner
		rank.trusted = only trusted, not elite or above
	Rank Ladder:
           ladder.owner
           ladder.admin
           ladder.operator
             /        \
  ladder.moderator   ladder.architect
            |        ladder.builder
             \        /
           ladder.noble
           ladder.veteran
           ladder.elite
           ladder.trusted
           ladder.member
           ladder.guest
 */
