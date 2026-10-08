package com.rakshasetu.app.ui.onboarding;

import com.rakshasetu.app.data.repository.ContactRepository;
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
public final class OnboardingActivity_MembersInjector implements MembersInjector<OnboardingActivity> {
  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  private final Provider<ContactRepository> contactRepositoryProvider;

  public OnboardingActivity_MembersInjector(
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider) {
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.contactRepositoryProvider = contactRepositoryProvider;
  }

  public static MembersInjector<OnboardingActivity> create(
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<ContactRepository> contactRepositoryProvider) {
    return new OnboardingActivity_MembersInjector(preferencesRepositoryProvider, contactRepositoryProvider);
  }

  @Override
  public void injectMembers(OnboardingActivity instance) {
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
    injectContactRepository(instance, contactRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.onboarding.OnboardingActivity.preferencesRepository")
  public static void injectPreferencesRepository(OnboardingActivity instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.onboarding.OnboardingActivity.contactRepository")
  public static void injectContactRepository(OnboardingActivity instance,
      ContactRepository contactRepository) {
    instance.contactRepository = contactRepository;
  }
}
