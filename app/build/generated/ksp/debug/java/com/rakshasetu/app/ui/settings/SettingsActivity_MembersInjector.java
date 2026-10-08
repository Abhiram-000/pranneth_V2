package com.rakshasetu.app.ui.settings;

import com.rakshasetu.app.data.repository.PreferencesRepository;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class SettingsActivity_MembersInjector implements MembersInjector<SettingsActivity> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public SettingsActivity_MembersInjector(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  public static MembersInjector<SettingsActivity> create(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new SettingsActivity_MembersInjector(preferencesRepositoryProvider);
  }

  @Override
  public void injectMembers(SettingsActivity instance) {
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.settings.SettingsActivity.preferencesRepository")
  public static void injectPreferencesRepository(SettingsActivity instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }
}
