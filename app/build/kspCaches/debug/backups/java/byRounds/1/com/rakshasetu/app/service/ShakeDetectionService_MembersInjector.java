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
public final class ShakeDetectionService_MembersInjector implements MembersInjector<ShakeDetectionService> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public ShakeDetectionService_MembersInjector(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  public static MembersInjector<ShakeDetectionService> create(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new ShakeDetectionService_MembersInjector(preferencesRepositoryProvider);
  }

  @Override
  public void injectMembers(ShakeDetectionService instance) {
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.ShakeDetectionService.preferencesRepository")
  public static void injectPreferencesRepository(ShakeDetectionService instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }
}
