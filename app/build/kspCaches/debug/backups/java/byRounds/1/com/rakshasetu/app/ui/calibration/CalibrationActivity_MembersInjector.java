package com.rakshasetu.app.ui.calibration;

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
public final class CalibrationActivity_MembersInjector implements MembersInjector<CalibrationActivity> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public CalibrationActivity_MembersInjector(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  public static MembersInjector<CalibrationActivity> create(
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new CalibrationActivity_MembersInjector(preferencesRepositoryProvider);
  }

  @Override
  public void injectMembers(CalibrationActivity instance) {
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.calibration.CalibrationActivity.preferencesRepository")
  public static void injectPreferencesRepository(CalibrationActivity instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }
}
