package com.library.essay.utils;

import java.util.List;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;

public class HibernateSplitInClause {

  private static int PARAMETER_LIMIT = 999;

  // Split long list by 1000 into sublists, and use "Or" to combine criterions.
  // Customize Restrictions.in(String propertyName, List values).
  public static Criterion in(String propertyName, List values) {

    Criterion criterion = null;

    int listSize = values.size();
    for (int i = 0; i < listSize; i += PARAMETER_LIMIT) {
      List subList;
      if (listSize > i + PARAMETER_LIMIT) {
        subList = values.subList(i, (i + PARAMETER_LIMIT));
      } else {
        subList = values.subList(i, listSize);
      }
      if (criterion != null) {
        criterion = Restrictions.or(criterion, Restrictions.in(propertyName, subList));
      } else {
        criterion = Restrictions.in(propertyName, subList);
      }
    }
    return criterion;
  }
}
