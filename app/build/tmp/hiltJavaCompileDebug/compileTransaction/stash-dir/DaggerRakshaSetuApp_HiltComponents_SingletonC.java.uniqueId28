package com.rakshasetu.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import androidx.fragment.app.Fragment;
import androidx.hilt.work.HiltWorkerFactory;
import androidx.hilt.work.WorkerAssistedFactory;
import androidx.hilt.work.WorkerFactoryModule_ProvideFactoryFactory;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;
import androidx.work.ListenableWorker;
import androidx.work.WorkerParameters;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.rakshasetu.app.data.RakshaSetuDatabase;
import com.rakshasetu.app.data.dao.AlertLogDao;
import com.rakshasetu.app.data.dao.EmergencyContactDao;
import com.rakshasetu.app.data.dao.LocationUpdateDao;
import com.rakshasetu.app.data.repository.AlertRepository;
import com.rakshasetu.app.data.repository.ContactRepository;
import com.rakshasetu.app.data.repository.PreferencesRepository;
import com.rakshasetu.app.domain.call.CallManager;
import com.rakshasetu.app.domain.escalation.EscalationManager;
import com.rakshasetu.app.domain.location.LocationTracker;
import com.rakshasetu.app.domain.sms.SMSDispatcher;
import com.rakshasetu.app.hilt.AppModule_ProvideAlertLogDaoFactory;
import com.rakshasetu.app.hilt.AppModule_ProvideDatabaseFactory;
import com.rakshasetu.app.hilt.AppModule_ProvideEmergencyContactDaoFactory;
import com.rakshasetu.app.hilt.AppModule_ProvideLocationUpdateDaoFactory;
import com.rakshasetu.app.hilt.AppModule_ProvideSharedPreferencesFactory;
import com.rakshasetu.app.service.AirplaneModeReceiver;
import com.rakshasetu.app.service.AirplaneModeReceiver_MembersInjector;
import com.rakshasetu.app.service.AlertDispatchService;
import com.rakshasetu.app.service.AlertDispatchService_MembersInjector;
import com.rakshasetu.app.service.LocationTrackingService;
import com.rakshasetu.app.service.LocationTrackingService_MembersInjector;
import com.rakshasetu.app.service.ShakeDetectionService;
import com.rakshasetu.app.service.SmsDeliveredReceiver;
import com.rakshasetu.app.service.SmsDeliveredReceiver_MembersInjector;
import com.rakshasetu.app.service.SmsSentReceiver;
import com.rakshasetu.app.service.SmsSentReceiver_MembersInjector;
import com.rakshasetu.app.ui.alertlog.AlertLogActivity;
import com.rakshasetu.app.ui.alertlog.AlertLogActivity_MembersInjector;
import com.rakshasetu.app.ui.calibration.CalibrationActivity;
import com.rakshasetu.app.ui.calibration.CalibrationActivity_MembersInjector;
import com.rakshasetu.app.ui.contacts.AddContactActivity;
import com.rakshasetu.app.ui.contacts.AddContactActivity_MembersInjector;
import com.rakshasetu.app.ui.contacts.ContactListActivity;
import com.rakshasetu.app.ui.contacts.ContactListActivity_MembersInjector;
import com.rakshasetu.app.ui.countdown.CountdownActivity;
import com.rakshasetu.app.ui.countdown.CountdownActivity_MembersInjector;
import com.rakshasetu.app.ui.main.MainActivity;
import com.rakshasetu.app.ui.main.MainActivity_MembersInjector;
import com.rakshasetu.app.ui.onboarding.OnboardingActivity;
import com.rakshasetu.app.ui.onboarding.OnboardingActivity_MembersInjector;
import com.rakshasetu.app.ui.settings.SettingsActivity;
import com.rakshasetu.app.ui.settings.SettingsActivity_MembersInjector;
import com.rakshasetu.app.worker.ServiceWatchdogWorker;
import com.rakshasetu.app.worker.ServiceWatchdogWorker_AssistedFactory;
import dagger.hilt.android.ActivityRetainedLifecycle;
import dagger.hilt.android.ViewModelLifecycle;
import dagger.hilt.android.internal.builders.ActivityComponentBuilder;
import dagger.hilt.android.internal.builders.ActivityRetainedComponentBuilder;
import dagger.hilt.android.internal.builders.FragmentComponentBuilder;
import dagger.hilt.android.internal.builders.ServiceComponentBuilder;
import dagger.hilt.android.internal.builders.ViewComponentBuilder;
import dagger.hilt.android.internal.builders.ViewModelComponentBuilder;
import dagger.hilt.android.internal.builders.ViewWithFragmentComponentBuilder;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories;
import dagger.hilt.android.internal.lifecycle.DefaultViewModelFactories_InternalFactoryFactory_Factory;
import dagger.hilt.android.internal.managers.ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory;
import dagger.hilt.android.internal.managers.SavedStateHandleHolder;
import dagger.hilt.android.internal.modules.ApplicationContextModule;
import dagger.hilt.android.internal.modules.ApplicationContextModule_ProvideContextFactory;
import dagger.internal.DaggerGenerated;
import dagger.internal.DoubleCheck;
import dagger.internal.Preconditions;
import dagger.internal.SingleCheck;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class DaggerRakshaSetuApp_HiltComponents_SingletonC {
  private DaggerRakshaSetuApp_HiltComponents_SingletonC() {
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private ApplicationContextModule applicationContextModule;

    private Builder() {
    }

    public Builder applicationContextModule(ApplicationContextModule applicationContextModule) {
      this.applicationContextModule = Preconditions.checkNotNull(applicationContextModule);
      return this;
    }

    public RakshaSetuApp_HiltComponents.SingletonC build() {
      Preconditions.checkBuilderRequirement(applicationContextModule, ApplicationContextModule.class);
      return new SingletonCImpl(applicationContextModule);
    }
  }

  private static final class ActivityRetainedCBuilder implements RakshaSetuApp_HiltComponents.ActivityRetainedC.Builder {
    private final SingletonCImpl singletonCImpl;

    private SavedStateHandleHolder savedStateHandleHolder;

    private ActivityRetainedCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ActivityRetainedCBuilder savedStateHandleHolder(
        SavedStateHandleHolder savedStateHandleHolder) {
      this.savedStateHandleHolder = Preconditions.checkNotNull(savedStateHandleHolder);
      return this;
    }

    @Override
    public RakshaSetuApp_HiltComponents.ActivityRetainedC build() {
      Preconditions.checkBuilderRequirement(savedStateHandleHolder, SavedStateHandleHolder.class);
      return new ActivityRetainedCImpl(singletonCImpl, savedStateHandleHolder);
    }
  }

  private static final class ActivityCBuilder implements RakshaSetuApp_HiltComponents.ActivityC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private Activity activity;

    private ActivityCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ActivityCBuilder activity(Activity activity) {
      this.activity = Preconditions.checkNotNull(activity);
      return this;
    }

    @Override
    public RakshaSetuApp_HiltComponents.ActivityC build() {
      Preconditions.checkBuilderRequirement(activity, Activity.class);
      return new ActivityCImpl(singletonCImpl, activityRetainedCImpl, activity);
    }
  }

  private static final class FragmentCBuilder implements RakshaSetuApp_HiltComponents.FragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private Fragment fragment;

    private FragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public FragmentCBuilder fragment(Fragment fragment) {
      this.fragment = Preconditions.checkNotNull(fragment);
      return this;
    }

    @Override
    public RakshaSetuApp_HiltComponents.FragmentC build() {
      Preconditions.checkBuilderRequirement(fragment, Fragment.class);
      return new FragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragment);
    }
  }

  private static final class ViewWithFragmentCBuilder implements RakshaSetuApp_HiltComponents.ViewWithFragmentC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private View view;

    private ViewWithFragmentCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;
    }

    @Override
    public ViewWithFragmentCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public RakshaSetuApp_HiltComponents.ViewWithFragmentC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewWithFragmentCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl, view);
    }
  }

  private static final class ViewCBuilder implements RakshaSetuApp_HiltComponents.ViewC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private View view;

    private ViewCBuilder(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
    }

    @Override
    public ViewCBuilder view(View view) {
      this.view = Preconditions.checkNotNull(view);
      return this;
    }

    @Override
    public RakshaSetuApp_HiltComponents.ViewC build() {
      Preconditions.checkBuilderRequirement(view, View.class);
      return new ViewCImpl(singletonCImpl, activityRetainedCImpl, activityCImpl, view);
    }
  }

  private static final class ViewModelCBuilder implements RakshaSetuApp_HiltComponents.ViewModelC.Builder {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private SavedStateHandle savedStateHandle;

    private ViewModelLifecycle viewModelLifecycle;

    private ViewModelCBuilder(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
    }

    @Override
    public ViewModelCBuilder savedStateHandle(SavedStateHandle handle) {
      this.savedStateHandle = Preconditions.checkNotNull(handle);
      return this;
    }

    @Override
    public ViewModelCBuilder viewModelLifecycle(ViewModelLifecycle viewModelLifecycle) {
      this.viewModelLifecycle = Preconditions.checkNotNull(viewModelLifecycle);
      return this;
    }

    @Override
    public RakshaSetuApp_HiltComponents.ViewModelC build() {
      Preconditions.checkBuilderRequirement(savedStateHandle, SavedStateHandle.class);
      Preconditions.checkBuilderRequirement(viewModelLifecycle, ViewModelLifecycle.class);
      return new ViewModelCImpl(singletonCImpl, activityRetainedCImpl, savedStateHandle, viewModelLifecycle);
    }
  }

  private static final class ServiceCBuilder implements RakshaSetuApp_HiltComponents.ServiceC.Builder {
    private final SingletonCImpl singletonCImpl;

    private Service service;

    private ServiceCBuilder(SingletonCImpl singletonCImpl) {
      this.singletonCImpl = singletonCImpl;
    }

    @Override
    public ServiceCBuilder service(Service service) {
      this.service = Preconditions.checkNotNull(service);
      return this;
    }

    @Override
    public RakshaSetuApp_HiltComponents.ServiceC build() {
      Preconditions.checkBuilderRequirement(service, Service.class);
      return new ServiceCImpl(singletonCImpl, service);
    }
  }

  private static final class ViewWithFragmentCImpl extends RakshaSetuApp_HiltComponents.ViewWithFragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl;

    private final ViewWithFragmentCImpl viewWithFragmentCImpl = this;

    private ViewWithFragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        FragmentCImpl fragmentCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;
      this.fragmentCImpl = fragmentCImpl;


    }
  }

  private static final class FragmentCImpl extends RakshaSetuApp_HiltComponents.FragmentC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final FragmentCImpl fragmentCImpl = this;

    private FragmentCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, ActivityCImpl activityCImpl,
        Fragment fragmentParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return activityCImpl.getHiltInternalFactoryFactory();
    }

    @Override
    public ViewWithFragmentComponentBuilder viewWithFragmentComponentBuilder() {
      return new ViewWithFragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl, fragmentCImpl);
    }
  }

  private static final class ViewCImpl extends RakshaSetuApp_HiltComponents.ViewC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl;

    private final ViewCImpl viewCImpl = this;

    private ViewCImpl(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
        ActivityCImpl activityCImpl, View viewParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;
      this.activityCImpl = activityCImpl;


    }
  }

  private static final class ActivityCImpl extends RakshaSetuApp_HiltComponents.ActivityC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ActivityCImpl activityCImpl = this;

    private ActivityCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, Activity activityParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public void injectAlertLogActivity(AlertLogActivity alertLogActivity) {
      injectAlertLogActivity2(alertLogActivity);
    }

    @Override
    public void injectCalibrationActivity(CalibrationActivity calibrationActivity) {
      injectCalibrationActivity2(calibrationActivity);
    }

    @Override
    public void injectAddContactActivity(AddContactActivity addContactActivity) {
      injectAddContactActivity2(addContactActivity);
    }

    @Override
    public void injectContactListActivity(ContactListActivity contactListActivity) {
      injectContactListActivity2(contactListActivity);
    }

    @Override
    public void injectCountdownActivity(CountdownActivity countdownActivity) {
      injectCountdownActivity2(countdownActivity);
    }

    @Override
    public void injectMainActivity(MainActivity mainActivity) {
      injectMainActivity2(mainActivity);
    }

    @Override
    public void injectOnboardingActivity(OnboardingActivity onboardingActivity) {
      injectOnboardingActivity2(onboardingActivity);
    }

    @Override
    public void injectSettingsActivity(SettingsActivity settingsActivity) {
      injectSettingsActivity2(settingsActivity);
    }

    @Override
    public DefaultViewModelFactories.InternalFactoryFactory getHiltInternalFactoryFactory() {
      return DefaultViewModelFactories_InternalFactoryFactory_Factory.newInstance(Collections.<Class<?>, Boolean>emptyMap(), new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl));
    }

    @Override
    public Map<Class<?>, Boolean> getViewModelKeys() {
      return Collections.<Class<?>, Boolean>emptyMap();
    }

    @Override
    public ViewModelComponentBuilder getViewModelComponentBuilder() {
      return new ViewModelCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public FragmentComponentBuilder fragmentComponentBuilder() {
      return new FragmentCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @Override
    public ViewComponentBuilder viewComponentBuilder() {
      return new ViewCBuilder(singletonCImpl, activityRetainedCImpl, activityCImpl);
    }

    @CanIgnoreReturnValue
    private AlertLogActivity injectAlertLogActivity2(AlertLogActivity instance) {
      AlertLogActivity_MembersInjector.injectAlertRepository(instance, singletonCImpl.alertRepositoryProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private CalibrationActivity injectCalibrationActivity2(CalibrationActivity instance2) {
      CalibrationActivity_MembersInjector.injectPreferencesRepository(instance2, singletonCImpl.preferencesRepositoryProvider.get());
      return instance2;
    }

    @CanIgnoreReturnValue
    private AddContactActivity injectAddContactActivity2(AddContactActivity instance3) {
      AddContactActivity_MembersInjector.injectContactRepository(instance3, singletonCImpl.contactRepositoryProvider.get());
      return instance3;
    }

    @CanIgnoreReturnValue
    private ContactListActivity injectContactListActivity2(ContactListActivity instance4) {
      ContactListActivity_MembersInjector.injectContactRepository(instance4, singletonCImpl.contactRepositoryProvider.get());
      return instance4;
    }

    @CanIgnoreReturnValue
    private CountdownActivity injectCountdownActivity2(CountdownActivity instance5) {
      CountdownActivity_MembersInjector.injectLocationTracker(instance5, singletonCImpl.locationTrackerProvider.get());
      return instance5;
    }

    @CanIgnoreReturnValue
    private MainActivity injectMainActivity2(MainActivity instance6) {
      MainActivity_MembersInjector.injectContactRepository(instance6, singletonCImpl.contactRepositoryProvider.get());
      MainActivity_MembersInjector.injectPreferencesRepository(instance6, singletonCImpl.preferencesRepositoryProvider.get());
      return instance6;
    }

    @CanIgnoreReturnValue
    private OnboardingActivity injectOnboardingActivity2(OnboardingActivity instance7) {
      OnboardingActivity_MembersInjector.injectPreferencesRepository(instance7, singletonCImpl.preferencesRepositoryProvider.get());
      OnboardingActivity_MembersInjector.injectContactRepository(instance7, singletonCImpl.contactRepositoryProvider.get());
      return instance7;
    }

    @CanIgnoreReturnValue
    private SettingsActivity injectSettingsActivity2(SettingsActivity instance8) {
      SettingsActivity_MembersInjector.injectPreferencesRepository(instance8, singletonCImpl.preferencesRepositoryProvider.get());
      return instance8;
    }
  }

  private static final class ViewModelCImpl extends RakshaSetuApp_HiltComponents.ViewModelC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl;

    private final ViewModelCImpl viewModelCImpl = this;

    private ViewModelCImpl(SingletonCImpl singletonCImpl,
        ActivityRetainedCImpl activityRetainedCImpl, SavedStateHandle savedStateHandleParam,
        ViewModelLifecycle viewModelLifecycleParam) {
      this.singletonCImpl = singletonCImpl;
      this.activityRetainedCImpl = activityRetainedCImpl;


    }

    @Override
    public Map<Class<?>, Provider<ViewModel>> getHiltViewModelMap() {
      return Collections.<Class<?>, Provider<ViewModel>>emptyMap();
    }

    @Override
    public Map<Class<?>, Object> getHiltViewModelAssistedMap() {
      return Collections.<Class<?>, Object>emptyMap();
    }
  }

  private static final class ActivityRetainedCImpl extends RakshaSetuApp_HiltComponents.ActivityRetainedC {
    private final SingletonCImpl singletonCImpl;

    private final ActivityRetainedCImpl activityRetainedCImpl = this;

    private dagger.internal.Provider<ActivityRetainedLifecycle> provideActivityRetainedLifecycleProvider;

    private ActivityRetainedCImpl(SingletonCImpl singletonCImpl,
        SavedStateHandleHolder savedStateHandleHolderParam) {
      this.singletonCImpl = singletonCImpl;

      initialize(savedStateHandleHolderParam);

    }

    @SuppressWarnings("unchecked")
    private void initialize(final SavedStateHandleHolder savedStateHandleHolderParam) {
      this.provideActivityRetainedLifecycleProvider = DoubleCheck.provider(new SwitchingProvider<ActivityRetainedLifecycle>(singletonCImpl, activityRetainedCImpl, 0));
    }

    @Override
    public ActivityComponentBuilder activityComponentBuilder() {
      return new ActivityCBuilder(singletonCImpl, activityRetainedCImpl);
    }

    @Override
    public ActivityRetainedLifecycle getActivityRetainedLifecycle() {
      return provideActivityRetainedLifecycleProvider.get();
    }

    private static final class SwitchingProvider<T> implements dagger.internal.Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final ActivityRetainedCImpl activityRetainedCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, ActivityRetainedCImpl activityRetainedCImpl,
          int id) {
        this.singletonCImpl = singletonCImpl;
        this.activityRetainedCImpl = activityRetainedCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // dagger.hilt.android.ActivityRetainedLifecycle 
          return (T) ActivityRetainedComponentManager_LifecycleModule_ProvideActivityRetainedLifecycleFactory.provideActivityRetainedLifecycle();

          default: throw new AssertionError(id);
        }
      }
    }
  }

  private static final class ServiceCImpl extends RakshaSetuApp_HiltComponents.ServiceC {
    private final SingletonCImpl singletonCImpl;

    private final ServiceCImpl serviceCImpl = this;

    private ServiceCImpl(SingletonCImpl singletonCImpl, Service serviceParam) {
      this.singletonCImpl = singletonCImpl;


    }

    @Override
    public void injectAlertDispatchService(AlertDispatchService alertDispatchService) {
      injectAlertDispatchService2(alertDispatchService);
    }

    @Override
    public void injectLocationTrackingService(LocationTrackingService locationTrackingService) {
      injectLocationTrackingService2(locationTrackingService);
    }

    @Override
    public void injectShakeDetectionService(ShakeDetectionService shakeDetectionService) {
    }

    @CanIgnoreReturnValue
    private AlertDispatchService injectAlertDispatchService2(AlertDispatchService instance) {
      AlertDispatchService_MembersInjector.injectSmsDispatcher(instance, singletonCImpl.sMSDispatcherProvider.get());
      AlertDispatchService_MembersInjector.injectCallManager(instance, singletonCImpl.callManagerProvider.get());
      AlertDispatchService_MembersInjector.injectContactRepository(instance, singletonCImpl.contactRepositoryProvider.get());
      AlertDispatchService_MembersInjector.injectAlertRepository(instance, singletonCImpl.alertRepositoryProvider.get());
      AlertDispatchService_MembersInjector.injectLocationTracker(instance, singletonCImpl.locationTrackerProvider.get());
      AlertDispatchService_MembersInjector.injectPreferencesRepository(instance, singletonCImpl.preferencesRepositoryProvider.get());
      AlertDispatchService_MembersInjector.injectEscalationManager(instance, singletonCImpl.escalationManagerProvider.get());
      return instance;
    }

    @CanIgnoreReturnValue
    private LocationTrackingService injectLocationTrackingService2(
        LocationTrackingService instance2) {
      LocationTrackingService_MembersInjector.injectAlertRepository(instance2, singletonCImpl.alertRepositoryProvider.get());
      LocationTrackingService_MembersInjector.injectPreferencesRepository(instance2, singletonCImpl.preferencesRepositoryProvider.get());
      LocationTrackingService_MembersInjector.injectLocationTracker(instance2, singletonCImpl.locationTrackerProvider.get());
      LocationTrackingService_MembersInjector.injectLocationUpdateDao(instance2, singletonCImpl.locationUpdateDao());
      LocationTrackingService_MembersInjector.injectContactRepository(instance2, singletonCImpl.contactRepositoryProvider.get());
      LocationTrackingService_MembersInjector.injectSmsDispatcher(instance2, singletonCImpl.sMSDispatcherProvider.get());
      return instance2;
    }
  }

  private static final class SingletonCImpl extends RakshaSetuApp_HiltComponents.SingletonC {
    private final ApplicationContextModule applicationContextModule;

    private final SingletonCImpl singletonCImpl = this;

    private dagger.internal.Provider<ServiceWatchdogWorker_AssistedFactory> serviceWatchdogWorker_AssistedFactoryProvider;

    private dagger.internal.Provider<RakshaSetuDatabase> provideDatabaseProvider;

    private dagger.internal.Provider<AlertRepository> alertRepositoryProvider;

    private dagger.internal.Provider<ContactRepository> contactRepositoryProvider;

    private dagger.internal.Provider<SharedPreferences> provideSharedPreferencesProvider;

    private dagger.internal.Provider<PreferencesRepository> preferencesRepositoryProvider;

    private dagger.internal.Provider<LocationTracker> locationTrackerProvider;

    private dagger.internal.Provider<SMSDispatcher> sMSDispatcherProvider;

    private dagger.internal.Provider<CallManager> callManagerProvider;

    private dagger.internal.Provider<EscalationManager> escalationManagerProvider;

    private SingletonCImpl(ApplicationContextModule applicationContextModuleParam) {
      this.applicationContextModule = applicationContextModuleParam;
      initialize(applicationContextModuleParam);

    }

    private Map<String, Provider<WorkerAssistedFactory<? extends ListenableWorker>>> mapOfStringAndProviderOfWorkerAssistedFactoryOf(
        ) {
      return Collections.<String, Provider<WorkerAssistedFactory<? extends ListenableWorker>>>singletonMap("com.rakshasetu.app.worker.ServiceWatchdogWorker", ((dagger.internal.Provider) serviceWatchdogWorker_AssistedFactoryProvider));
    }

    private HiltWorkerFactory hiltWorkerFactory() {
      return WorkerFactoryModule_ProvideFactoryFactory.provideFactory(mapOfStringAndProviderOfWorkerAssistedFactoryOf());
    }

    private AlertLogDao alertLogDao() {
      return AppModule_ProvideAlertLogDaoFactory.provideAlertLogDao(provideDatabaseProvider.get());
    }

    private EmergencyContactDao emergencyContactDao() {
      return AppModule_ProvideEmergencyContactDaoFactory.provideEmergencyContactDao(provideDatabaseProvider.get());
    }

    private LocationUpdateDao locationUpdateDao() {
      return AppModule_ProvideLocationUpdateDaoFactory.provideLocationUpdateDao(provideDatabaseProvider.get());
    }

    @SuppressWarnings("unchecked")
    private void initialize(final ApplicationContextModule applicationContextModuleParam) {
      this.serviceWatchdogWorker_AssistedFactoryProvider = SingleCheck.provider(new SwitchingProvider<ServiceWatchdogWorker_AssistedFactory>(singletonCImpl, 0));
      this.provideDatabaseProvider = DoubleCheck.provider(new SwitchingProvider<RakshaSetuDatabase>(singletonCImpl, 2));
      this.alertRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<AlertRepository>(singletonCImpl, 1));
      this.contactRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<ContactRepository>(singletonCImpl, 3));
      this.provideSharedPreferencesProvider = DoubleCheck.provider(new SwitchingProvider<SharedPreferences>(singletonCImpl, 5));
      this.preferencesRepositoryProvider = DoubleCheck.provider(new SwitchingProvider<PreferencesRepository>(singletonCImpl, 4));
      this.locationTrackerProvider = DoubleCheck.provider(new SwitchingProvider<LocationTracker>(singletonCImpl, 6));
      this.sMSDispatcherProvider = DoubleCheck.provider(new SwitchingProvider<SMSDispatcher>(singletonCImpl, 7));
      this.callManagerProvider = DoubleCheck.provider(new SwitchingProvider<CallManager>(singletonCImpl, 8));
      this.escalationManagerProvider = DoubleCheck.provider(new SwitchingProvider<EscalationManager>(singletonCImpl, 9));
    }

    @Override
    public void injectRakshaSetuApp(RakshaSetuApp rakshaSetuApp) {
      injectRakshaSetuApp2(rakshaSetuApp);
    }

    @Override
    public void injectAirplaneModeReceiver(AirplaneModeReceiver airplaneModeReceiver) {
      injectAirplaneModeReceiver2(airplaneModeReceiver);
    }

    @Override
    public void injectSmsDeliveredReceiver(SmsDeliveredReceiver smsDeliveredReceiver) {
      injectSmsDeliveredReceiver2(smsDeliveredReceiver);
    }

    @Override
    public void injectSmsSentReceiver(SmsSentReceiver smsSentReceiver) {
      injectSmsSentReceiver2(smsSentReceiver);
    }

    @Override
    public Set<Boolean> getDisableFragmentGetContextFix() {
      return Collections.<Boolean>emptySet();
    }

    @Override
    public ActivityRetainedComponentBuilder retainedComponentBuilder() {
      return new ActivityRetainedCBuilder(singletonCImpl);
    }

    @Override
    public ServiceComponentBuilder serviceComponentBuilder() {
      return new ServiceCBuilder(singletonCImpl);
    }

    @CanIgnoreReturnValue
    private RakshaSetuApp injectRakshaSetuApp2(RakshaSetuApp instance) {
      RakshaSetuApp_MembersInjector.injectWorkerFactory(instance, hiltWorkerFactory());
      return instance;
    }

    @CanIgnoreReturnValue
    private AirplaneModeReceiver injectAirplaneModeReceiver2(AirplaneModeReceiver instance2) {
      AirplaneModeReceiver_MembersInjector.injectAlertRepository(instance2, alertRepositoryProvider.get());
      return instance2;
    }

    @CanIgnoreReturnValue
    private SmsDeliveredReceiver injectSmsDeliveredReceiver2(SmsDeliveredReceiver instance3) {
      SmsDeliveredReceiver_MembersInjector.injectContactRepository(instance3, contactRepositoryProvider.get());
      return instance3;
    }

    @CanIgnoreReturnValue
    private SmsSentReceiver injectSmsSentReceiver2(SmsSentReceiver instance4) {
      SmsSentReceiver_MembersInjector.injectAlertRepository(instance4, alertRepositoryProvider.get());
      return instance4;
    }

    private static final class SwitchingProvider<T> implements dagger.internal.Provider<T> {
      private final SingletonCImpl singletonCImpl;

      private final int id;

      SwitchingProvider(SingletonCImpl singletonCImpl, int id) {
        this.singletonCImpl = singletonCImpl;
        this.id = id;
      }

      @SuppressWarnings("unchecked")
      @Override
      public T get() {
        switch (id) {
          case 0: // com.rakshasetu.app.worker.ServiceWatchdogWorker_AssistedFactory 
          return (T) new ServiceWatchdogWorker_AssistedFactory() {
            @Override
            public ServiceWatchdogWorker create(Context appContext, WorkerParameters workerParams) {
              return new ServiceWatchdogWorker(appContext, workerParams);
            }
          };

          case 1: // com.rakshasetu.app.data.repository.AlertRepository 
          return (T) new AlertRepository(singletonCImpl.alertLogDao());

          case 2: // com.rakshasetu.app.data.RakshaSetuDatabase 
          return (T) AppModule_ProvideDatabaseFactory.provideDatabase(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 3: // com.rakshasetu.app.data.repository.ContactRepository 
          return (T) new ContactRepository(singletonCImpl.emergencyContactDao());

          case 4: // com.rakshasetu.app.data.repository.PreferencesRepository 
          return (T) new PreferencesRepository(singletonCImpl.provideSharedPreferencesProvider.get());

          case 5: // android.content.SharedPreferences 
          return (T) AppModule_ProvideSharedPreferencesFactory.provideSharedPreferences(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 6: // com.rakshasetu.app.domain.location.LocationTracker 
          return (T) new LocationTracker(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 7: // com.rakshasetu.app.domain.sms.SMSDispatcher 
          return (T) new SMSDispatcher(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 8: // com.rakshasetu.app.domain.call.CallManager 
          return (T) new CallManager(ApplicationContextModule_ProvideContextFactory.provideContext(singletonCImpl.applicationContextModule));

          case 9: // com.rakshasetu.app.domain.escalation.EscalationManager 
          return (T) new EscalationManager();

          default: throw new AssertionError(id);
        }
      }
    }
  }
}
