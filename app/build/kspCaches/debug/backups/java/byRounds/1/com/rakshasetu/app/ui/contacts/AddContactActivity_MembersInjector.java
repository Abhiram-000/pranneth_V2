package com.rakshasetu.app.ui.contacts;

import com.rakshasetu.app.data.repository.ContactRepository;
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
public final class AddContactActivity_MembersInjector implements MembersInjector<AddContactActivity> {
  private final Provider<ContactRepository> contactRepositoryProvider;

  public AddContactActivity_MembersInjector(Provider<ContactRepository> contactRepositoryProvider) {
    this.contactRepositoryProvider = contactRepositoryProvider;
  }

  public static MembersInjector<AddContactActivity> create(
      Provider<ContactRepository> contactRepositoryProvider) {
    return new AddContactActivity_MembersInjector(contactRepositoryProvider);
  }

  @Override
  public void injectMembers(AddContactActivity instance) {
    injectContactRepository(instance, contactRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.contacts.AddContactActivity.contactRepository")
  public static void injectContactRepository(AddContactActivity instance,
      ContactRepository contactRepository) {
    instance.contactRepository = contactRepository;
  }
}
