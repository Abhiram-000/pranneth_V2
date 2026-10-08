package com.rakshasetu.app.service;

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
public final class SmsDeliveredReceiver_MembersInjector implements MembersInjector<SmsDeliveredReceiver> {
  private final Provider<ContactRepository> contactRepositoryProvider;

  public SmsDeliveredReceiver_MembersInjector(
      Provider<ContactRepository> contactRepositoryProvider) {
    this.contactRepositoryProvider = contactRepositoryProvider;
  }

  public static MembersInjector<SmsDeliveredReceiver> create(
      Provider<ContactRepository> contactRepositoryProvider) {
    return new SmsDeliveredReceiver_MembersInjector(contactRepositoryProvider);
  }

  @Override
  public void injectMembers(SmsDeliveredReceiver instance) {
    injectContactRepository(instance, contactRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.SmsDeliveredReceiver.contactRepository")
  public static void injectContactRepository(SmsDeliveredReceiver instance,
      ContactRepository contactRepository) {
    instance.contactRepository = contactRepository;
  }
}
