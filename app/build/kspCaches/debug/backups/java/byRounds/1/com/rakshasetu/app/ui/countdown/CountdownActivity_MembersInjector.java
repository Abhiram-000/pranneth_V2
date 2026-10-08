package com.rakshasetu.app.ui.countdown;

import com.rakshasetu.app.domain.location.LocationTracker;
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
public final class CountdownActivity_MembersInjector implements MembersInjector<CountdownActivity> {
  private final Provider<LocationTracker> locationTrackerProvider;

  public CountdownActivity_MembersInjector(Provider<LocationTracker> locationTrackerProvider) {
    this.locationTrackerProvider = locationTrackerProvider;
  }

  public static MembersInjector<CountdownActivity> create(
      Provider<LocationTracker> locationTrackerProvider) {
    return new CountdownActivity_MembersInjector(locationTrackerProvider);
  }

  @Override
  public void injectMembers(CountdownActivity instance) {
    injectLocationTracker(instance, locationTrackerProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.ui.countdown.CountdownActivity.locationTracker")
  public static void injectLocationTracker(CountdownActivity instance,
      LocationTracker locationTracker) {
    instance.locationTracker = locationTracker;
  }
}
