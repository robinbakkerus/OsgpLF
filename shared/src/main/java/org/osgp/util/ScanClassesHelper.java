package org.osgp.util;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.reflections.Reflections;

public class ScanClassesHelper {

	public static Set<Class<?>> findAnnotatedClasses(final String path, 
			final Class<? extends Annotation> mainAnnotation) {
		Reflections reflections = new Reflections(path);
		return reflections.getTypesAnnotatedWith(mainAnnotation);
	}
	
	
	public static Map<String, RequestHandler> fillRequestHandlersMap(final String pck) {
		Map<String, RequestHandler> result = new HashMap<>();
		Set<Class<?>> annotated = 
				ScanClassesHelper.findAnnotatedClasses(pck, AnnotRequestHandler.class);

		for (Class<?> clz : annotated) {
			RequestHandler reqhandler = null;
			try {
				reqhandler = (RequestHandler) clz.newInstance();
				AnnotRequestHandler reqAnnot = (AnnotRequestHandler) clz.getAnnotation(AnnotRequestHandler.class);
				result.put(reqAnnot.action().getSimpleName(), reqhandler);
			} catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
}
