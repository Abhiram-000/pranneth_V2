package com.rakshasetu.app.hilt;

import com.rakshasetu.app.data.RakshaSetuDatabase;
import com.rakshasetu.app.data.dao.AlertLogDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.Preconditions;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
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
public final class AppModule_ProvideAlertLogDaoFactory implements Factory<AlertLogDao> {
  private final Provider<RakshaSetuDatabase> dbProvider;

  public AppModule_ProvideAlertLogDaoFactory(Provider<RakshaSetuDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public AlertLogDao get() {
    return provideAlertLogDao(dbProvider.get());
  }

  public static AppModule_ProvideAlertLogDaoFactory create(
      Provider<RakshaSetuDatabase> dbProvider) {
    return new AppModule_ProvideAlertLogDaoFactory(dbProvider);
  }

  public static AlertLogDao provideAlertLogDao(RakshaSetuDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideAlertLogDao(db));
  }
}
