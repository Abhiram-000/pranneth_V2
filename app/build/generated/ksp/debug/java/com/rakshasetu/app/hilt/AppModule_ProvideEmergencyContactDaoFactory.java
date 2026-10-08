package com.rakshasetu.app.hilt;

import com.rakshasetu.app.data.RakshaSetuDatabase;
import com.rakshasetu.app.data.dao.EmergencyContactDao;
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
public final class AppModule_ProvideEmergencyContactDaoFactory implements Factory<EmergencyContactDao> {
  private final Provider<RakshaSetuDatabase> dbProvider;

  public AppModule_ProvideEmergencyContactDaoFactory(Provider<RakshaSetuDatabase> dbProvider) {
    this.dbProvider = dbProvider;
  }

  @Override
  public EmergencyContactDao get() {
    return provideEmergencyContactDao(dbProvider.get());
  }

  public static AppModule_ProvideEmergencyContactDaoFactory create(
      Provider<RakshaSetuDatabase> dbProvider) {
    return new AppModule_ProvideEmergencyContactDaoFactory(dbProvider);
  }

  public static EmergencyContactDao provideEmergencyContactDao(RakshaSetuDatabase db) {
    return Preconditions.checkNotNullFromProvides(AppModule.INSTANCE.provideEmergencyContactDao(db));
  }
}
