package com.rakshasetu.app;

import androidx.hilt.work.HiltWorkerFactory;
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
public final class RakshaSetuApp_MembersInjector implements MembersInjector<RakshaSetuApp> {
  private final Provider<HiltWorkerFactory> workerFactoryProvider;

  public RakshaSetuApp_MembersInjector(Provider<HiltWorkerFactory> workerFactoryProvider) {
    this.workerFactoryProvider = workerFactoryProvider;
  }

  public static MembersInjector<RakshaSetuApp> create(
      Provider<HiltWorkerFactory> workerFactoryProvider) {
    return new RakshaSetuApp_MembersInjector(workerFactoryProvider);
  }

  @Override
  public void injectMembers(RakshaSetuApp instance) {
    injectWorkerFactory(instance, workerFactoryProvider.get());
  }

  @InjectedFieldSignature("com.rakshasetu.app.RakshaSetuApp.workerFactory")
  public static void injectWorkerFactory(RakshaSetuApp instance, HiltWorkerFactory workerFactory) {
    instance.workerFactory = workerFactory;
  }
}
