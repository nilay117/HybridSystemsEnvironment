package edu.ucsc.cross.hse.core.exe.access;

//
import bs.commons.objects.access.FieldFinder;
import bs.commons.objects.manipulation.XMLParser;
import com.be3short.data.cloning.ObjectCloner;
import com.be3short.data.file.general.FileSystemInteractor;
import com.jcabi.aspects.Loggable;
import edu.ucsc.cross.hse.core.obj.structure.HybridSystem;
import edu.ucsc.cross.hse.core.obj.structure.State;
import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.util.Utils;

@Loggable(Loggable.TRACE)
public class StateFieldMapper
{

	private static ArrayList<Field> skipFields = getSkipFields();
	public static Reflections reflections;
	public static HashMap<String, ArrayList<Field>> elements = getClassFieldsMappingOfType(State.class);

	private static ArrayList<Field> getSkipFields()
	{
		ArrayList<Field> fieldz = new ArrayList<Field>();
		fieldz.addAll(Arrays.asList(State.class.getDeclaredFields()));
		return fieldz;
	}

	private static void getReflections()
	{
		if (reflections == null)
		{
			// StaticLoggerBinder.getSingleton().setMavenLog(new MvnLog());
			Reflections.log = null;
			// MySimpleLoggerFactory.defaultLogLevel = 100;
			reflections = new Reflections();
			// System.setProperty(org.slf4j.impl.SimpleLogger.DEFAULT_LOG_LEVEL_KEY, "TRACE");

			// MySimpleLogger logger = (MySimpleLogger) reflections.log;
			// logger.setLogLevel(00);
			// log.
			// LogManager.getLogger(Reflections.log.getName()).setLevel(Level.OFF);
		}
	}

	public static boolean isState(Class<?> potential_state)
	{
		String clas = potential_state.getName();
		return elements.containsKey(potential_state);
	}

	public static ArrayList<Field> getClassFields(Class<?> class_to_find)
	{
		String clas = class_to_find.getName();
		ArrayList<Field> fields = null;
		try
		{
			if (elements.containsKey(clas))
			{
				fields = elements.get(clas);
			} else
			{
				getReflections();
				HashMap<String, ArrayList<Field>> newStates = getClassFieldsMappingOfType(class_to_find);
				for (String object : newStates.keySet())
				{
					if (!elements.containsKey(object))
					{
						elements.put(object, newStates.get(object));
					}
				}
				FileSystemInteractor.createOutputFile("build/.hse/fieldfile.xml", XMLParser.serializeObject(elements));
				// System.out.println(XMLParser.serializeObject(elements));
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
	public static void makeAllFieldsAccessable(HashMap<String, ArrayList<Field>> elements)
	{

		for (String clas : elements.keySet())
		{
			for (Field field : elements.get(clas))
			{
				field.setAccessible(true);
			}
		}
		// System.out.println(XMLParser.serializeObject(elements));
	}

	public static <T> HashMap<String, ArrayList<Field>> initializeClassFieldMap(Set<Class<?>> search_classes)
	{
		HashMap<String, ArrayList<Field>> elements = new HashMap<String, ArrayList<Field>>();
		for (Class<?> clas : search_classes)
		{

			elements.put(clas.getName(), new ArrayList<Field>());
		}
		return elements;
	}

	/*
	 * Creates a mapping containing all declared fields for every state class included in the project and dependencies.
	 * This mapping allows for values to be updated without changing pointers.
	 */
	public static void getClassFieldMapping(HashMap<String, ArrayList<Field>> elements, Set<Class<?>> search_classes,
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
					if (!elements.get(clas.getName()).contains(fields))
					{
						elements.get(clas.getName()).add(fields);
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
							if (!elements.get(clas.getName()).contains(fi))
							{
								elements.get(clas.getName()).add(fi);
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

	public static <T> HashMap<String, ArrayList<Field>> getClassFieldsMappingOfType(Class<T> class_search)
	{

		HashMap<String, ArrayList<Field>> eles = new HashMap<String, ArrayList<Field>>();

		try

		{
			Object elefie = XMLParser.getObject(new File("build/.hse/fieldfile.xml"));
			if (elefie != null)
			{
				if (elefie.getClass().equals(HashMap.class))
				{
					HashMap<Object, Object> map = (HashMap<Object, Object>) elefie;

					for (Object classObj : map.keySet())
					{
						String cl = (String) classObj;
						eles.put(cl, (ArrayList<Field>) map.get(classObj));
					}
					return eles;
				}
			}
			throw new Exception();

		} catch (Exception e)
		{
			getReflections();
			Set<Class<? extends T>> classes = getPackageClassesOfType(class_search);//
			// getPackageClassesAnnotated(State.class);
			HashSet<Class<?>> classSet = new HashSet<Class<?>>();
			classSet.addAll(classes);
			classSet.add(class_search);
			HashMap<String, ArrayList<Field>> elems = initializeClassFieldMap(classSet);
			getClassFieldMapping(elems, classSet, class_search);
			while (fields(eles) < fields(elems))
			{
				getClassFieldMapping(elems, classSet, class_search);
				eles = elems;
			}
			FileSystemInteractor.createOutputFile("build/.hse/fieldfile.xml", XMLParser.serializeObject(elems));
			// System.out.println(XMLParser.serializeObject(elems));
			return elems;
		}

	}

	private static Integer fields(HashMap<String, ArrayList<Field>> test)
	{
		Integer count = 0;
		for (ArrayList<Field> clas : test.values())
		{
			count += clas.size();
		}
		return count;
	}

}
