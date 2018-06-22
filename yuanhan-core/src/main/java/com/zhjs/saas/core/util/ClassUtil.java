/**
 * 
 */
package com.yuanhan.yuanhan.core.util;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.Vector;

import org.springframework.util.ClassUtils;

/**
 * @author:		Jackie Wong 
 * @since:		Sep 22, 2009
 * @modified:	Sep 22, 2009
 * @version:	  
 */
@SuppressWarnings({"unchecked","rawtypes"})
public abstract class ClassUtil extends ClassUtils {


	/**
	 * Private constructor to prevent instantiation.
	 */
	private ClassUtil(){}
	
	/**
	 * Map with collection wrapper type as key and corresponding class
	 * type as value, for example: Integer.class -> int.class.
	 */
	private static final Set collectionTypeMap = new HashSet(8);
	static {
		collectionTypeMap.add(Collection.class);
		collectionTypeMap.add(List.class);
		collectionTypeMap.add(ArrayList.class);
		collectionTypeMap.add(LinkedList.class);
		collectionTypeMap.add(Set.class);
		collectionTypeMap.add(HashSet.class);
		collectionTypeMap.add(TreeSet.class);
		collectionTypeMap.add(Vector.class);
	}
	
	/**
	 * validate if the input type is collection type
	 * @param type
	 * @return
	 */
	public static boolean isCollectionType(Class type)
	{
		return collectionTypeMap.contains(type);
	}
	
	/**
	 * <p>this method works only for spring aop framework.
	 * used to get class from aop proxy.</p>
	 * 
	 */
	public static Class getProxyTargetClass(Object proxy)
	{
		return AopUtil.getFinalTargetClass(proxy);
	}
	
	
	/**
	 * get all classes which referring to the interface
	 * 
	 * @param interface.class
	 * @return List<Class>
	 */
	public static List<Class> getAllClassByInterface(Class c)
	{
		List<Class> returnClassList = new ArrayList<Class>();
		if (c.isInterface())
		{
			String packageName = c.getPackage().getName();
			try
			{
				List<Class> allClass = getClasses(packageName);
				for (int i = 0; i < allClass.size(); i++)
				{
					if (c.isAssignableFrom(allClass.get(i)))
					{
						// don't add the interface.class itself
						if (!c.equals(allClass.get(i)))
						{
							returnClassList.add(allClass.get(i));
						}
					}
				}
			}
			catch (ClassNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		return returnClassList;
	}
	
	
	/**
	 * get all classes in a package
	 * 
	 * @param packageName
	 * @return List<Class>
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static List<Class> getClasses(String packageName)
			throws ClassNotFoundException, IOException {
		ClassLoader classLoader = Thread.currentThread()
				.getContextClassLoader();
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}
		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClasses(directory, packageName));
		}
		return classes;
	}

	/**
	 * get all classes in a package, these classes located on a physical directory
	 * 
	 * @param directory
	 * @param packageName
	 * @return List<Class>
	 * @throws ClassNotFoundException
	 */
	private static List<Class> findClasses(File directory, String packageName)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClasses(file, packageName + "."
						+ file.getName()));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName
						+ '.'
						+ file.getName().substring(0,
								file.getName().length() - 6)));
			}
		}
		return classes;
	}   	
	
	public static void main(String...args)
	{
		System.out.print(ClassUtil.isCollectionType(Vector.class));
	}

}
