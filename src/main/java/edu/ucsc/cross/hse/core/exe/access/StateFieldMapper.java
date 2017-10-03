package edu.ucsc.cross.hse.core.exe.access;

//
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.XMLParser;
import com.be3short.data.cloning.ObjectCloner;
import com.be3short.data.file.general.FileSystemInteractor;
import edu.emory.mathcs.backport.java.util.Arrays;
import edu.ucsc.cross.hse.core.obj.structure.HybridSystem;
import edu.ucsc.cross.hse.core.obj.structure.State;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.Priority;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.Utils;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

public class StateFieldMapper
{

	private static ArrayList<Field> skipFields = getSkipFields();
	public static Reflections reflections = getReflections();
	public static HashMap<Class<?>, ArrayList<Field>> elements = getClassFieldsMappingOfType(State.class);

	private static ArrayList<Field> getSkipFields()
	{
		ArrayList<Field> fieldz = new ArrayList<Field>();
		fieldz.addAll(Arrays.asList(State.class.getDeclaredFields()));
		return fieldz;
	}

	private static Reflections getReflections()
	{
		// StaticLoggerBinder.getSingleton().setMavenLog(new MvnLog());
		Reflections.log = null;
		// MySimpleLoggerFactory.defaultLogLevel = 100;
		Reflections reflections = new Reflections();
		// System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

		// MySimpleLogger logger = (MySimpleLogger) reflections.log;
		// logger.setLogLevel(00);
		// log.
		// LogManager.getLogger(Reflections.log.getName()).setLevel(Level.OFF);
		return reflections;
	}

	public static boolean isState(Class<?> potential_state)
	{
		return elements.containsKey(potential_state);
	}

	public static ArrayList<Field> getClassFields(Class<?> clas)
	{
		ArrayList<Field> fields = null;
		try
		{
			if (elements.containsKey(clas))
			{
				fields = elements.get(clas);
			} else
			{
				HashMap<Class<?>, ArrayList<Field>> newStates = getClassFieldsMappingOfType(clas);
				for (Class<?> object : newStates.keySet())
				{
					if (!elements.containsKey(object))
					{
						elements.put(object, newStates.get(object));
					}
				}
				System.out.println(XMLParser.serializeObject(elements));
				fields = elements.get(clas);
			}
		} catch (Exception notSearched)
		{
			notSearched.printStackTrace();

		}
		return fields;
	}

	public static <T extends Annotation> Set<Class<?>> getPackageClassesAnnotated(Class<T> clas)
	{
		return reflections.getTypesAnnotatedWith(clas);
	}

	/*
	 * Creates a set containing all classes included within the project packages and dependencies that are a sub type of
	 * the specified input. This is used to find specific types of classes so that their properties, ie fields and
	 * methods, can be determined for later use
	 */
	public static <T> Set<Class<? extends T>> getPackageClassesOfType(Class<T> clas)
	{
		return reflections.getSubTypesOf(clas);
	}

	/*
	 * Creates a mapping containing all declared fields for every state class included in the project and dependencies.
	 * This mapping allows for values to be updated without changing pointers.
	 */
	public static void makeAllFieldsAccessable(HashMap<Class<?>, ArrayList<Field>> elements)
	{

		for (Class<?> clas : elements.keySet())
		{
			for (Field field : elements.get(clas))
			{
				field.setAccessible(true);
			}
		}
		// System.out.println(XMLParser.serializeObject(elements));
	}

	public static <T> HashMap<Class<?>, ArrayList<Field>> initializeClassFieldMap(Set<Class<?>> search_classes)
	{
		HashMap<Class<?>, ArrayList<Field>> elements = new HashMap<Class<?>, ArrayList<Field>>();
		for (Class<?> clas : search_classes)
		{
			elements.put(clas, new ArrayList<Field>());
		}
		return elements;
	}

	/*
	 * Creates a mapping containing all declared fields for every state class included in the project and dependencies.
	 * This mapping allows for values to be updated without changing pointers.
	 */
	public static void getClassFieldMapping(HashMap<Class<?>, ArrayList<Field>> elements, Set<Class<?>> search_classes,
	Class<?> search_class)
	{

		for (Class<?> clas : search_classes)
		{
			for (Field fields : clas.getDeclaredFields())
			{
				if (!fields.getName().contains("$SWITCH_TABLE$") && !fields.getType().equals(search_class)
				&& !skipFields.contains(fields))// &&
				// !skipFields.contains(fields))
				{
					if (!elements.get(clas).contains(fields))
					{
						elements.get(clas).add(fields);
					}
				}
			}
			if (search_classes.contains(clas.getSuperclass()))
			{
				Class<?> superClass = clas.getSuperclass();
				ArrayList<Field> extendedFields = new ArrayList<Field>(Arrays.asList(superClass.getDeclaredFields()));
				while (search_classes.contains(superClass))
				{
					for (Field fi : extendedFields)
					{
						if (!fi.getName().contains("$SWITCH_TABLE$") && !fi.getType().equals(search_class)
						&& !skipFields.contains(fi))// &&
						{
							if (!elements.get(clas).contains(fi))
							{
								elements.get(clas).add(fi);
							}
						}
					}
					clas = superClass;
					superClass = superClass.getSuperclass();
					extendedFields.addAll(Arrays.asList(superClass.getDeclaredFields()));

				}
				// elements.put(clas, extendedFields.toArray(new Field[extendedFields.size()]));
			}

		}
		makeAllFieldsAccessable(elements);

	}

	public static HashMap<Class<?>, ArrayList<Field>> getClassFieldsMappingOfType(Class<?> class_search)
	{
		System.out.println("Init");
		HashMap<Class<?>, ArrayList<Field>> eles = new HashMap<Class<?>, ArrayList<Field>>();

		Set<Class<? extends State>> classes = getPackageClassesOfType(State.class);//
		// getPackageClassesAnnotated(State.class);
		HashSet<Class<?>> classSet = new HashSet<Class<?>>();
		classSet.addAll(classes);
		classSet.add(class_search);
		HashMap<Class<?>, ArrayList<Field>> elems = initializeClassFieldMap(classSet);
		getClassFieldMapping(elems, classSet, class_search);
		while (fields(eles) < fields(elems))
		{
			getClassFieldMapping(elems, classSet, class_search);
			eles = elems;
		}
		System.out.println(XMLParser.serializeObject(elems));
		return elems;

	}

	private static Integer fields(HashMap<Class<?>, ArrayList<Field>> test)
	{
		Integer count = 0;
		for (ArrayList<Field> clas : test.values())
		{
			count += clas.size();
		}
		return count;
	}

}
