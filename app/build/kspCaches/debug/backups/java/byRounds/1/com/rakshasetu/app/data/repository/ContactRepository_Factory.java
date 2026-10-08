package com.rakshasetu.app.data.repository;

import com.rakshasetu.app.data.dao.EmergencyContactDao;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class ContactRepository_Factory implements Factory<ContactRepository> {
  private final Provider<EmergencyContactDao> contactDaoProvider;

  public ContactRepository_Factory(Provider<EmergencyContactDao> contactDaoProvider) {
    this.contactDaoProvider = contactDaoProvider;
  }

  @Override
  public ContactRepository get() {
    return newInstance(contactDaoProvider.get());
  }

  public static ContactRepository_Factory create(Provider<EmergencyContactDao> contactDaoProvider) {
    return new ContactRepository_Factory(contactDaoProvider);
  }

  public static ContactRepository newInstance(EmergencyContactDao contactDao) {
    return new ContactRepository(contactDao);
  }
}
