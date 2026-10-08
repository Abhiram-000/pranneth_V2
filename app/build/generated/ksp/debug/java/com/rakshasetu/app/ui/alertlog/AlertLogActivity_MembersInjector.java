package com.rakshasetu.app.ui.alertlog;

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
public final class AlertLogActivity_MembersInjector implements MembersInjector<AlertLogActivity> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  public AlertLogActivity_MembersInjector(Provider<AlertRepository> alertRepositoryProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
  }

  public static MembersInjector<AlertLogActivity> create(
      Provider<AlertRepository> alertRepositoryProvider) {
    return new AlertLogActivity_MembersInjector(alertRepositoryProvider);
  }

  @Override
  public void injectMembers(AlertLogActivity instance) {
    injectAlertRepository(instance, alertRepositoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.alertlog.AlertLogActivity.alertRepository")
  public static void injectAlertRepository(AlertLogActivity instance,
      AlertRepository alertRepository) {
    instance.alertRepository = alertRepository;
  }
}
