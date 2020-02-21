package com.library.essay.applications;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.Query;
import org.apache.lucene.util.Version;
import org.hibernate.CacheMode;
import org.hibernate.FlushMode;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
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

    List<Serializable> idList = application.saveTestEssays(100);

    application.printEssays(idList);

    application.search();

    // application.buildIndex();

    // application.buildIndexBatch();

    // Clean up
    application.deleteEssays(idList);

    System.exit(0);
  }

  public List<Serializable> saveTestEssays(int initialSize) {
    List<Serializable> idList = new ArrayList<Serializable>();

    String prefix = "123";
    String[] letters = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O",
        "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};

    for (int i = 0; i < initialSize; i++) {

      String letter = letters[i % 26];
      String tag = prefix + "-" + letter + letter + letter + letter + "-000" + i;

      String title = "Test Essay " + i;
      Serializable id = this.saveNewEssay(tag, title, "Cliff Lee", "HHH CDF" + i);

      idList.add(id);
    }

    return idList;
  }

  public void buildIndex() {

    System.out.println("Start building index......");

    Session session = HibernateUtil.getSessionFactory().openSession();

    // Index the hazards
    FullTextSession fullTextSession = Search.getFullTextSession(session);
    fullTextSession.setFlushMode(FlushMode.MANUAL);
    fullTextSession.setCacheMode(CacheMode.IGNORE);
    Transaction transaction = fullTextSession.beginTransaction();

    // Scrollable results will avoid loading too many objects in memory
    ScrollableResults results = fullTextSession.createCriteria(Essay.class).setFetchSize(100)
        .scroll(ScrollMode.FORWARD_ONLY);
    int index = 0;
    while (results.next()) {
      index++;
      fullTextSession.index(results.get(0)); // index each element
      if (index % 100 == 0) {
        fullTextSession.flushToIndexes(); // apply changes to indexes
        fullTextSession.clear(); // free memory since the queue is processed
      }
      System.out.println("in loop " + index);
    }
    transaction.commit();

    session.close();

    System.out.println("End of building index.");
  }

  public void buildIndexBatch() {

    System.out.println("Start building index......");

    Session session = HibernateUtil.getSessionFactory().openSession();

    // Index the hazards
    FullTextSession fullTextSession = Search.getFullTextSession(session);

    try {

      fullTextSession.createIndexer().startAndWait();

    } catch (InterruptedException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    session.close();

    System.out.println("End of building index.");
  }

  public void search() {

    Session session = HibernateUtil.getSessionFactory().openSession();
    FullTextSession fullTextSession = Search.getFullTextSession(session);

    String field0 = "tag";
    String field1 = "content";

    String queryString0 = "123-CCC*";

    String queryString1 = "\"HHH CDF2\"";

    // String queryString1 = "HHH CDF2";

    // String[] fields = new String[] {/*field0,*/ field1};
    // String[] queries = new String[] {/*queryString0,*/ queryString1};

    String[] fields = new String[] {field0, field1};
    String[] queries = new String[] {queryString0, queryString1};

    // String[] fields = new String[] {field0};
    // String[] queries = new String[] {queryString0};

    Transaction tx = fullTextSession.beginTransaction();

    try {

      Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_36);
      // Analyzer analyzer=new WhitespaceAnalyzer(Version.LUCENE_36);

      MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_36, fields, analyzer);

      parser.setAllowLeadingWildcard(true); // TODO

      // parser.setDefaultOperator(Operator.AND);
      BooleanClause.Occur[] flags = {BooleanClause.Occur.MUST, BooleanClause.Occur.MUST};

      Query query =
          MultiFieldQueryParser.parse(Version.LUCENE_36, queries, fields, flags, analyzer);

      // Query query = MultiFieldQueryParser.parse(Version.LUCENE_36, queries, fields,
      // new StandardAnalyzer(Version.LUCENE_36));

      // Query query = MultiFieldQueryParser.parse(Version.LUCENE_36, queries, fields,
      // new WhitespaceAnalyzer(Version.LUCENE_36));

      // Query query = MultiFieldQueryParser.parse(Version.LUCENE_36, queries, fields,
      // new KeywordAnalyzer());

      System.out.println("=====================================");
      System.out.println("Lucene query: " + query.toString());
      System.out.println("=====================================");

      FullTextQuery hibQuery = fullTextSession.createFullTextQuery(query, Essay.class);

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

    System.out.println("=====================================");
    System.out.println("Deleted Essays:");
    System.out.println("=====================================");

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

    System.out.println("Essay " + id + " has been deleted.");
  }

  public Serializable saveNewEssay(String tag, String title, String author, String content) {
    Session session = HibernateUtil.getSessionFactory().openSession();

    session.beginTransaction();

    Essay essay = new Essay();
    essay.setTag(tag);
    essay.setTitle(title);
    essay.setAuthor(author);
    essay.setContent(content);

    Serializable id = session.save(essay);
    session.getTransaction().commit();
    session.close();

    return id;

  }

}
