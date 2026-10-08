package com.rakshasetu.app.worker;

import android.content.Context;
import androidx.work.WorkerParameters;
import dagger.internal.DaggerGenerated;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
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
public final class ServiceWatchdogWorker_Factory {
  public ServiceWatchdogWorker get(Context appContext, WorkerParameters workerParams) {
    return newInstance(appContext, workerParams);
  }

  public static ServiceWatchdogWorker_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static ServiceWatchdogWorker newInstance(Context appContext,
      WorkerParameters workerParams) {
    return new ServiceWatchdogWorker(appContext, workerParams);
  }

  private static final class InstanceHolder {
    private static final ServiceWatchdogWorker_Factory INSTANCE = new ServiceWatchdogWorker_Factory();
  }
}
