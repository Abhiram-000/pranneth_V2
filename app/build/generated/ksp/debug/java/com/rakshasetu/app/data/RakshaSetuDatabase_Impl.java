package com.rakshasetu.app.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import com.rakshasetu.app.data.dao.AlertLogDao;
import com.rakshasetu.app.data.dao.AlertLogDao_Impl;
import com.rakshasetu.app.data.dao.EmergencyContactDao;
import com.rakshasetu.app.data.dao.EmergencyContactDao_Impl;
import com.rakshasetu.app.data.dao.LocationUpdateDao;
import com.rakshasetu.app.data.dao.LocationUpdateDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class RakshaSetuDatabase_Impl extends RakshaSetuDatabase {
  private volatile EmergencyContactDao _emergencyContactDao;

  private volatile AlertLogDao _alertLogDao;

  private volatile LocationUpdateDao _locationUpdateDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(1) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `emergency_contacts` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `phoneNumber` TEXT NOT NULL, `countryCode` TEXT NOT NULL, `relation` TEXT NOT NULL, `customRelation` TEXT, `priority` INTEGER NOT NULL, `isVerified` INTEGER NOT NULL, `lastNotifiedAt` INTEGER, `createdAt` INTEGER NOT NULL, `updatedAt` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `alert_logs` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `triggerType` TEXT NOT NULL, `latitude` REAL, `longitude` REAL, `accuracy` REAL, `locationSource` TEXT, `smsSentCount` INTEGER NOT NULL, `callsMadeCount` INTEGER NOT NULL, `isEscalated` INTEGER NOT NULL, `isCancelled` INTEGER NOT NULL, `cancelMethod` TEXT, `isDuress` INTEGER NOT NULL, `startedAt` INTEGER NOT NULL, `endedAt` INTEGER, `batteryLevel` INTEGER, `hasDataConnection` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `location_updates` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `alertId` INTEGER NOT NULL, `latitude` REAL NOT NULL, `longitude` REAL NOT NULL, `accuracy` REAL, `source` TEXT NOT NULL, `timestamp` INTEGER NOT NULL)");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '64e7264285bc7d3174d2d60755d14fe2')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `emergency_contacts`");
        db.execSQL("DROP TABLE IF EXISTS `alert_logs`");
        db.execSQL("DROP TABLE IF EXISTS `location_updates`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsEmergencyContacts = new HashMap<String, TableInfo.Column>(11);
        _columnsEmergencyContacts.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("phoneNumber", new TableInfo.Column("phoneNumber", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("countryCode", new TableInfo.Column("countryCode", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("relation", new TableInfo.Column("relation", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("customRelation", new TableInfo.Column("customRelation", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("priority", new TableInfo.Column("priority", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("isVerified", new TableInfo.Column("isVerified", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("lastNotifiedAt", new TableInfo.Column("lastNotifiedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsEmergencyContacts.put("updatedAt", new TableInfo.Column("updatedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysEmergencyContacts = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesEmergencyContacts = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoEmergencyContacts = new TableInfo("emergency_contacts", _columnsEmergencyContacts, _foreignKeysEmergencyContacts, _indicesEmergencyContacts);
        final TableInfo _existingEmergencyContacts = TableInfo.read(db, "emergency_contacts");
        if (!_infoEmergencyContacts.equals(_existingEmergencyContacts)) {
          return new RoomOpenHelper.ValidationResult(false, "emergency_contacts(com.rakshasetu.app.data.entity.EmergencyContact).\n"
                  + " Expected:\n" + _infoEmergencyContacts + "\n"
                  + " Found:\n" + _existingEmergencyContacts);
        }
        final HashMap<String, TableInfo.Column> _columnsAlertLogs = new HashMap<String, TableInfo.Column>(16);
        _columnsAlertLogs.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("triggerType", new TableInfo.Column("triggerType", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("accuracy", new TableInfo.Column("accuracy", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("locationSource", new TableInfo.Column("locationSource", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("smsSentCount", new TableInfo.Column("smsSentCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("callsMadeCount", new TableInfo.Column("callsMadeCount", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("isEscalated", new TableInfo.Column("isEscalated", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("isCancelled", new TableInfo.Column("isCancelled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("cancelMethod", new TableInfo.Column("cancelMethod", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("isDuress", new TableInfo.Column("isDuress", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("startedAt", new TableInfo.Column("startedAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("endedAt", new TableInfo.Column("endedAt", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("batteryLevel", new TableInfo.Column("batteryLevel", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsAlertLogs.put("hasDataConnection", new TableInfo.Column("hasDataConnection", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysAlertLogs = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesAlertLogs = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoAlertLogs = new TableInfo("alert_logs", _columnsAlertLogs, _foreignKeysAlertLogs, _indicesAlertLogs);
        final TableInfo _existingAlertLogs = TableInfo.read(db, "alert_logs");
        if (!_infoAlertLogs.equals(_existingAlertLogs)) {
          return new RoomOpenHelper.ValidationResult(false, "alert_logs(com.rakshasetu.app.data.entity.AlertLog).\n"
                  + " Expected:\n" + _infoAlertLogs + "\n"
                  + " Found:\n" + _existingAlertLogs);
        }
        final HashMap<String, TableInfo.Column> _columnsLocationUpdates = new HashMap<String, TableInfo.Column>(7);
        _columnsLocationUpdates.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationUpdates.put("alertId", new TableInfo.Column("alertId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationUpdates.put("latitude", new TableInfo.Column("latitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationUpdates.put("longitude", new TableInfo.Column("longitude", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationUpdates.put("accuracy", new TableInfo.Column("accuracy", "REAL", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationUpdates.put("source", new TableInfo.Column("source", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLocationUpdates.put("timestamp", new TableInfo.Column("timestamp", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLocationUpdates = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLocationUpdates = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLocationUpdates = new TableInfo("location_updates", _columnsLocationUpdates, _foreignKeysLocationUpdates, _indicesLocationUpdates);
        final TableInfo _existingLocationUpdates = TableInfo.read(db, "location_updates");
        if (!_infoLocationUpdates.equals(_existingLocationUpdates)) {
          return new RoomOpenHelper.ValidationResult(false, "location_updates(com.rakshasetu.app.data.entity.LocationUpdate).\n"
                  + " Expected:\n" + _infoLocationUpdates + "\n"
                  + " Found:\n" + _existingLocationUpdates);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "64e7264285bc7d3174d2d60755d14fe2", "92aa2511953fcc2bd6ae93914fc734de");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "emergency_contacts","alert_logs","location_updates");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `emergency_contacts`");
      _db.execSQL("DELETE FROM `alert_logs`");
      _db.execSQL("DELETE FROM `location_updates`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(EmergencyContactDao.class, EmergencyContactDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(AlertLogDao.class, AlertLogDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LocationUpdateDao.class, LocationUpdateDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public EmergencyContactDao emergencyContactDao() {
    if (_emergencyContactDao != null) {
      return _emergencyContactDao;
    } else {
      synchronized(this) {
        if(_emergencyContactDao == null) {
          _emergencyContactDao = new EmergencyContactDao_Impl(this);
        }
        return _emergencyContactDao;
      }
    }
  }

  @Override
  public AlertLogDao alertLogDao() {
    if (_alertLogDao != null) {
      return _alertLogDao;
    } else {
      synchronized(this) {
        if(_alertLogDao == null) {
          _alertLogDao = new AlertLogDao_Impl(this);
        }
        return _alertLogDao;
      }
    }
  }

  @Override
  public LocationUpdateDao locationUpdateDao() {
    if (_locationUpdateDao != null) {
      return _locationUpdateDao;
    } else {
      synchronized(this) {
        if(_locationUpdateDao == null) {
          _locationUpdateDao = new LocationUpdateDao_Impl(this);
        }
        return _locationUpdateDao;
      }
    }
  }
}
