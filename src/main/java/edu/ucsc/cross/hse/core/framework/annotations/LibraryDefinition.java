package edu.ucsc.cross.hse.core.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(
{ ElementType.METHOD, })
public @interface LibraryDefinition
{

	String label();
}
