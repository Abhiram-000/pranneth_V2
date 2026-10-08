package com.rakshasetu.app.service;

import com.rakshasetu.app.data.repository.AlertRepository;
import com.rakshasetu.app.data.repository.ContactRepository;
import com.rakshasetu.app.data.repository.PreferencesRepository;
import com.rakshasetu.app.domain.call.CallManager;
import com.rakshasetu.app.domain.escalation.EscalationManager;
import com.rakshasetu.app.domain.location.LocationTracker;
import com.rakshasetu.app.domain.sms.SMSDispatcher;
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
public final class AlertDispatchService_MembersInjector implements MembersInjector<AlertDispatchService> {
  private final Provider<SMSDispatcher> smsDispatcherProvider;

  private final Provider<CallManager> callManagerProvider;

  private final Provider<ContactRepository> contactRepositoryProvider;

  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<LocationTracker> locationTrackerProvider;

  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  private final Provider<EscalationManager> escalationManagerProvider;

  public AlertDispatchService_MembersInjector(Provider<SMSDispatcher> smsDispatcherProvider,
      Provider<CallManager> callManagerProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<LocationTracker> locationTrackerProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<EscalationManager> escalationManagerProvider) {
    this.smsDispatcherProvider = smsDispatcherProvider;
    this.callManagerProvider = callManagerProvider;
    this.contactRepositoryProvider = contactRepositoryProvider;
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.locationTrackerProvider = locationTrackerProvider;
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.escalationManagerProvider = escalationManagerProvider;
  }

  public static MembersInjector<AlertDispatchService> create(
      Provider<SMSDispatcher> smsDispatcherProvider, Provider<CallManager> callManagerProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<LocationTracker> locationTrackerProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<EscalationManager> escalationManagerProvider) {
    return new AlertDispatchService_MembersInjector(smsDispatcherProvider, callManagerProvider, contactRepositoryProvider, alertRepositoryProvider, locationTrackerProvider, preferencesRepositoryProvider, escalationManagerProvider);
  }

  @Override
  public void injectMembers(AlertDispatchService instance) {
    injectSmsDispatcher(instance, smsDispatcherProvider.get());
    injectCallManager(instance, callManagerProvider.get());
    injectContactRepository(instance, contactRepositoryProvider.get());
    injectAlertRepository(instance, alertRepositoryProvider.get());
    injectLocationTracker(instance, locationTrackerProvider.get());
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
    injectEscalationManager(instance, escalationManagerProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.AlertDispatchService.smsDispatcher")
  public static void injectSmsDispatcher(AlertDispatchService instance,
      SMSDispatcher smsDispatcher) {
    instance.smsDispatcher = smsDispatcher;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.AlertDispatchService.callManager")
  public static void injectCallManager(AlertDispatchService instance, CallManager callManager) {
    instance.callManager = callManager;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.AlertDispatchService.contactRepository")
  public static void injectContactRepository(AlertDispatchService instance,
      ContactRepository contactRepository) {
    instance.contactRepository = contactRepository;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.AlertDispatchService.alertRepository")
  public static void injectAlertRepository(AlertDispatchService instance,
      AlertRepository alertRepository) {
    instance.alertRepository = alertRepository;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.AlertDispatchService.locationTracker")
  public static void injectLocationTracker(AlertDispatchService instance,
      LocationTracker locationTracker) {
    instance.locationTracker = locationTracker;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.AlertDispatchService.preferencesRepository")
  public static void injectPreferencesRepository(AlertDispatchService instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.AlertDispatchService.escalationManager")
  public static void injectEscalationManager(AlertDispatchService instance,
      EscalationManager escalationManager) {
    instance.escalationManager = escalationManager;
  }
}
