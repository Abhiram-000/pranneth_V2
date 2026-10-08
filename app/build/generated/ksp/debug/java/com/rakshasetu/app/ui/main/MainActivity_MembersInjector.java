package com.rakshasetu.app.ui.main;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<ContactRepository> contactRepositoryProvider;

  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  public MainActivity_MembersInjector(Provider<ContactRepository> contactRepositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    this.contactRepositoryProvider = contactRepositoryProvider;
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider) {
    return new MainActivity_MembersInjector(contactRepositoryProvider, preferencesRepositoryProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectContactRepository(instance, contactRepositoryProvider.get());
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.main.MainActivity.contactRepository")
  public static void injectContactRepository(MainActivity instance,
      ContactRepository contactRepository) {
    instance.contactRepository = contactRepository;
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.main.MainActivity.preferencesRepository")
  public static void injectPreferencesRepository(MainActivity instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }
}
