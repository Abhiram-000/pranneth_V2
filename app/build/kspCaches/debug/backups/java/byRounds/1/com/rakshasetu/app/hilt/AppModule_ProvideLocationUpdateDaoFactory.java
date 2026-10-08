package com.rakshasetu.app.hilt;

import com.rakshasetu.app.data.RakshaSetuDatabase;
import com.rakshasetu.app.data.dao.LocationUpdateDao;
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
public final class AppModule_ProvideLocationUpdateDaoFactory implements Factory<LocationUpdateDao> {
  private final Provider<RakshaSetuDatabase> dbProvider;

  public AppModule_ProvideLocationUpdateDaoFactory(Provider<RakshaSetuDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public LocationUpdateDao get() {
    return provideLocationUpdateDao(dbProvider.get());
  }

  public static AppModule_ProvideLocationUpdateDaoFactory create(
      Provider<RakshaSetuDatabase> dbProvider) {
    return new AppModule_ProvideLocationUpdateDaoFactory(dbProvider);
  }

  public static LocationUpdateDao provideLocationUpdateDao(RakshaSetuDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideLocationUpdateDao(db));
  }
}
