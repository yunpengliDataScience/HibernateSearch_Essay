package com.library.essay.applications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;

import com.library.essay.persistence.entities.Essay;
import com.library.essay.utils.HibernateUtil;

public class MainApplication {

	public static void main(String[] args) {

		MainApplication application = new MainApplication();

		List<Serializable> idList = new ArrayList<Serializable>();

		for (int i = 0; i < 10; i++) {
			Serializable id = application.saveNewEssay("123-ABC-000" + i,
					"Cliff Lee", "HHH CDF" + i);

			idList.add(id);
		}
		
		application.printEssays(idList);

		// Clean up
		// application.deleteEssays(idList);
	}

	public Essay getEssay(Serializable id) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Essay essay = (Essay) session.get(Essay.class, id);

		return essay;
	}

	public void printEssays(List<Serializable> idList) {
		for (Serializable id : idList) {

			Essay essay = getEssay(id);
			System.out.println(essay);
		}
	}

	public void deleteEssays(List<Serializable> idList) {
		for (Serializable id : idList) {
			deleteEssay(id);
		}
	}

	public void deleteEssay(Serializable id) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		session.beginTransaction();

		Essay essay = (Essay) session.load(Essay.class, id);
		session.delete(essay);
		session.getTransaction().commit();
		session.close();
	}

	public Serializable saveNewEssay(String title, String author, String content) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		session.beginTransaction();

		Essay essay = new Essay();
		essay.setTitle(title);
		essay.setAuthor(author);
		essay.setContent(content);

		Serializable id = session.save(essay);
		session.getTransaction().commit();
		session.close();

		return id;

	}

}
