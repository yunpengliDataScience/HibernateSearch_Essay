package com.library.essay.applications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import com.library.essay.persistence.entities.Essay;
import com.library.essay.utils.HibernateUtil;

public class MainApplication {

	public static void main(String[] args) {

		MainApplication application = new MainApplication();

		List<Serializable> idList = new ArrayList<Serializable>();

		for (int i = 0; i < 10; i++) {
			Serializable id = application.saveNewEssay("123-456-000" + i,
					"Cliff Lee", "HHH CDF" + i);

			idList.add(id);
		}

		application.printEssays(idList);

		application.search();

		// Clean up
		application.deleteEssays(idList);
	}

	public void search() {

		Session session = HibernateUtil.getSessionFactory().openSession();
		FullTextSession fullTextSession = Search.getFullTextSession(session);

		String field0 = "title";
		String field1 = "content";

		String queryString0 = "456";
		String queryString1 = "CDF8";

		String[] fields = new String[] { field0, field1 };
		String[] queries = new String[] { queryString0, queryString1 };

		Transaction tx = fullTextSession.beginTransaction();

		try {
			Query query = MultiFieldQueryParser.parse(Version.LUCENE_36,
					queries, fields, new StandardAnalyzer(Version.LUCENE_36));

			System.out.println("=====================================");
			System.out.println("Lucene query: " + query.toString());
			System.out.println("=====================================");

			FullTextQuery hibQuery = fullTextSession.createFullTextQuery(query,
					Essay.class);

			List<Essay> results = hibQuery.list();

			System.out.println("=====================================");
			System.out.println("Found matched items:");
			System.out.println("=====================================");

			for (Essay essay : results) {
				System.out.println(essay);
			}

		} catch (ParseException e) {

			e.printStackTrace();
		}

		tx.commit();
		session.close();
	}

	public Essay getEssay(Serializable id) {
		Session session = HibernateUtil.getSessionFactory().openSession();

		Essay essay = (Essay) session.get(Essay.class, id);

		return essay;
	}

	public void printEssays(List<Serializable> idList) {

		System.out.println("=====================================");
		System.out.println("Inserted Essays:");
		System.out.println("=====================================");

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
