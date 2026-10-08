package com.rakshasetu.app.service;

import com.rakshasetu.app.data.repository.AlertRepository;
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
public final class SmsSentReceiver_MembersInjector implements MembersInjector<SmsSentReceiver> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  public SmsSentReceiver_MembersInjector(Provider<AlertRepository> alertRepositoryProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  public static MembersInjector<SmsSentReceiver> create(
      Provider<AlertRepository> alertRepositoryProvider) {
    return new SmsSentReceiver_MembersInjector(alertRepositoryProvider);
  }

  @Override
  public void injectMembers(SmsSentReceiver instance) {
    injectAlertRepository(instance, alertRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.SmsSentReceiver.alertRepository")
  public static void injectAlertRepository(SmsSentReceiver instance,
      AlertRepository alertRepository) {
    instance.alertRepository = alertRepository;
  }
}
