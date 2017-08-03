package edu.ucsc.cross.hse.core.framework.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/*
 * Annotation to identify a function call that yields a component to be added to
 * the library. The HSE is capable of many custom libraries, these just make up
 * ssome of the foundations for models. Functions with these annotations are
 * automatically scanned and available in the user library.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(
{ ElementType.METHOD, })
public @interface LibraryDefinition
{

	String label(); // name that will appear in libraries for the component
}
