package com.rakshasetu.app.data.repository;

import com.rakshasetu.app.data.dao.AlertLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast",
    "deprecation",
    "nullness:initialization.field.uninitialized"
})
public final class AlertRepository_Factory implements Factory<AlertRepository> {
  private final Provider<AlertLogDao> alertLogDaoProvider;

  public AlertRepository_Factory(Provider<AlertLogDao> alertLogDaoProvider) {
    this.alertLogDaoProvider = alertLogDaoProvider;
  }

  @Override
  public AlertRepository get() {
    return newInstance(alertLogDaoProvider.get());
  }

  public static AlertRepository_Factory create(Provider<AlertLogDao> alertLogDaoProvider) {
    return new AlertRepository_Factory(alertLogDaoProvider);
  }

  public static AlertRepository newInstance(AlertLogDao alertLogDao) {
    return new AlertRepository(alertLogDao);
  }
}
