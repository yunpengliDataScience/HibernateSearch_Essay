package com.library.essay.persistence.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import org.apache.solr.analysis.KeywordTokenizerFactory;
import org.apache.solr.analysis.LowerCaseFilterFactory;
import org.hibernate.search.annotations.Analyzer;
import org.hibernate.search.annotations.AnalyzerDef;
import org.hibernate.search.annotations.DocumentId;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Index;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;
import org.hibernate.search.annotations.TokenFilterDef;
import org.hibernate.search.annotations.TokenizerDef;

@Entity
@Table(name = "Essay")
@Indexed
// @Analyzer(impl = org.apache.lucene.analysis.standard.StandardAnalyzer.class)

@AnalyzerDef(name = "lowCaseKeywordAnalyzer",
    tokenizer = @TokenizerDef(factory = KeywordTokenizerFactory.class),
    filters = {@TokenFilterDef(factory = LowerCaseFilterFactory.class)})

public class Essay {

  @Id
  @Column(name = "ESSAY_ID")
  @DocumentId
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SEQ")
  @SequenceGenerator(name = "SEQ", sequenceName = "ESSAY_SEQ", allocationSize = 1)
  private Long id;

  @Column(name = "TAG")
  // @Field(index=Index.YES, store=Store.YES, analyze=Analyze.NO)
  @Analyzer(definition = "lowCaseKeywordAnalyzer")
  @Field(index = Index.YES, store = Store.YES)
  private String tag;

  @Column(name = "TITLE")
  @Field(index = Index.YES, store = Store.YES)
  private String title;

  @Column(name = "AUTHOR")
  @Field
  private String author;

  @Lob
  @Column(name = "CONTENT")
  @Field
  private String content;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getAuthor() {
    return author;
  }

  public void setAuthor(String author) {
    this.author = author;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  @Override
  public String toString() {
    return "Title: " + title + ", Tag: " + tag + ", Author: " + author + ", Content: " + content;

  }
}
