package com.rakshasetu.app.domain.sms;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class SMSDispatcher_Factory implements Factory<SMSDispatcher> {
  private final Provider<Context> contextProvider;

  public SMSDispatcher_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public SMSDispatcher get() {
    return newInstance(contextProvider.get());
  }

  public static SMSDispatcher_Factory create(Provider<Context> contextProvider) {
    return new SMSDispatcher_Factory(contextProvider);
  }

  public static SMSDispatcher newInstance(Context context) {
    return new SMSDispatcher(context);
  }
}
