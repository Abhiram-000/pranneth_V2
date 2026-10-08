package com.rakshasetu.app.data.repository;

import android.content.SharedPreferences;
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
public final class PreferencesRepository_Factory implements Factory<PreferencesRepository> {
  private final Provider<SharedPreferences> prefsProvider;

  public PreferencesRepository_Factory(Provider<SharedPreferences> prefsProvider) {
    this.prefsProvider = prefsProvider;
  }

  @Override
  public PreferencesRepository get() {
    return newInstance(prefsProvider.get());
  }

  public static PreferencesRepository_Factory create(Provider<SharedPreferences> prefsProvider) {
    return new PreferencesRepository_Factory(prefsProvider);
  }

  public static PreferencesRepository newInstance(SharedPreferences prefs) {
    return new PreferencesRepository(prefs);
  }
}
