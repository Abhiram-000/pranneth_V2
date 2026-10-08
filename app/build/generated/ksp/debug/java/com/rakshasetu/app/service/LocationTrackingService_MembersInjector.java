package com.rakshasetu.app.service;

import com.rakshasetu.app.data.dao.LocationUpdateDao;
import com.rakshasetu.app.data.repository.AlertRepository;
import com.rakshasetu.app.data.repository.ContactRepository;
import com.rakshasetu.app.data.repository.PreferencesRepository;
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
public final class LocationTrackingService_MembersInjector implements MembersInjector<LocationTrackingService> {
  private final Provider<AlertRepository> alertRepositoryProvider;

  private final Provider<PreferencesRepository> preferencesRepositoryProvider;

  private final Provider<LocationTracker> locationTrackerProvider;

  private final Provider<LocationUpdateDao> locationUpdateDaoProvider;

  private final Provider<ContactRepository> contactRepositoryProvider;

  private final Provider<SMSDispatcher> smsDispatcherProvider;

  public LocationTrackingService_MembersInjector(Provider<AlertRepository> alertRepositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<LocationTracker> locationTrackerProvider,
      Provider<LocationUpdateDao> locationUpdateDaoProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<SMSDispatcher> smsDispatcherProvider) {
    this.alertRepositoryProvider = alertRepositoryProvider;
    this.preferencesRepositoryProvider = preferencesRepositoryProvider;
    this.locationTrackerProvider = locationTrackerProvider;
    this.locationUpdateDaoProvider = locationUpdateDaoProvider;
    this.contactRepositoryProvider = contactRepositoryProvider;
    this.smsDispatcherProvider = smsDispatcherProvider;
  }

  public static MembersInjector<LocationTrackingService> create(
      Provider<AlertRepository> alertRepositoryProvider,
      Provider<PreferencesRepository> preferencesRepositoryProvider,
      Provider<LocationTracker> locationTrackerProvider,
      Provider<LocationUpdateDao> locationUpdateDaoProvider,
      Provider<ContactRepository> contactRepositoryProvider,
      Provider<SMSDispatcher> smsDispatcherProvider) {
    return new LocationTrackingService_MembersInjector(alertRepositoryProvider, preferencesRepositoryProvider, locationTrackerProvider, locationUpdateDaoProvider, contactRepositoryProvider, smsDispatcherProvider);
  }

  @Override
  public void injectMembers(LocationTrackingService instance) {
    injectAlertRepository(instance, alertRepositoryProvider.get());
    injectPreferencesRepository(instance, preferencesRepositoryProvider.get());
    injectLocationTracker(instance, locationTrackerProvider.get());
    injectLocationUpdateDao(instance, locationUpdateDaoProvider.get());
    injectContactRepository(instance, contactRepositoryProvider.get());
    injectSmsDispatcher(instance, smsDispatcherProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.LocationTrackingService.alertRepository")
  public static void injectAlertRepository(LocationTrackingService instance,
      AlertRepository alertRepository) {
    instance.alertRepository = alertRepository;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.LocationTrackingService.preferencesRepository")
  public static void injectPreferencesRepository(LocationTrackingService instance,
      PreferencesRepository preferencesRepository) {
    instance.preferencesRepository = preferencesRepository;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.LocationTrackingService.locationTracker")
  public static void injectLocationTracker(LocationTrackingService instance,
      LocationTracker locationTracker) {
    instance.locationTracker = locationTracker;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.LocationTrackingService.locationUpdateDao")
  public static void injectLocationUpdateDao(LocationTrackingService instance,
      LocationUpdateDao locationUpdateDao) {
    instance.locationUpdateDao = locationUpdateDao;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.LocationTrackingService.contactRepository")
  public static void injectContactRepository(LocationTrackingService instance,
      ContactRepository contactRepository) {
    instance.contactRepository = contactRepository;
  }

  @InjectedFieldSignature("com.rakshasetu.app.service.LocationTrackingService.smsDispatcher")
  public static void injectSmsDispatcher(LocationTrackingService instance,
      SMSDispatcher smsDispatcher) {
    instance.smsDispatcher = smsDispatcher;
  }
}
