package com.library.essay.utils;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import com.library.essay.persistence.entities.Essay;

public class HibernateUtil {

	private static final SessionFactory sessionFactory = buildSessionFactory();

	private static SessionFactory buildSessionFactory() {
		try {
			//For XML mapping
			//return new Configuration().configure().buildSessionFactory();
			
			//For Annotation
			AnnotationConfiguration config=new AnnotationConfiguration();
			config.addAnnotatedClass(Essay.class);
			//return new AnnotationConfiguration().configure().buildSessionFactory();
			
			return config.configure().buildSessionFactory();
			
		} catch (Throwable ex) {
			
			throw new ExceptionInInitializerError(ex);
		}
	}

	public static SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public static void shutdown() {
		// Close caches and connection pools
		getSessionFactory().close();
	}

}
