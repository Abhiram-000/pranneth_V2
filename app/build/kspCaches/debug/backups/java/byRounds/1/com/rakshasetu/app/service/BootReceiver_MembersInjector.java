package com.rakshasetu.app.service;

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
public final class BootReceiver_MembersInjector implements MembersInjector<BootReceiver> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public BootReceiver_MembersInjector(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  public static MembersInjector<BootReceiver> create(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new BootReceiver_MembersInjector(preferencesRepositoryProvider);
  }

  @Override
  public void injectMembers(BootReceiver instance) {
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.BootReceiver.preferencesRepository")
  public static void injectPreferencesRepository(BootReceiver instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }
}
