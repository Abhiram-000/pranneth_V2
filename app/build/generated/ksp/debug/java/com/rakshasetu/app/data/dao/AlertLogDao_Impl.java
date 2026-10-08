package com.rakshasetu.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityDeletionOrUpdateAdapter;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.rakshasetu.app.data.entity.AlertLog;
import java.lang.Class;
import java.lang.Double;
import java.lang.Exception;
import java.lang.Float;
import java.lang.Integer;
import java.lang.Long;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AlertLogDao_Impl implements AlertLogDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<AlertLog> __insertionAdapterOfAlertLog;

  private final EntityDeletionOrUpdateAdapter<AlertLog> __updateAdapterOfAlertLog;

  private final SharedSQLiteStatement __preparedStmtOfEndAlert;

  private final SharedSQLiteStatement __preparedStmtOfIncrementSmsCount;

  private final SharedSQLiteStatement __preparedStmtOfIncrementCallCount;

  private final SharedSQLiteStatement __preparedStmtOfMarkEscalated;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAlertsOlderThan;

  public AlertLogDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfAlertLog = new EntityInsertionAdapter<AlertLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `alert_logs` (`id`,`triggerType`,`latitude`,`longitude`,`accuracy`,`locationSource`,`smsSentCount`,`callsMadeCount`,`isEscalated`,`isCancelled`,`cancelMethod`,`isDuress`,`startedAt`,`endedAt`,`batteryLevel`,`hasDataConnection`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlertLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTriggerType());
        if (entity.getLatitude() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getLongitude());
        }
        if (entity.getAccuracy() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getAccuracy());
        }
        if (entity.getLocationSource() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLocationSource());
        }
        statement.bindLong(7, entity.getSmsSentCount());
        statement.bindLong(8, entity.getCallsMadeCount());
        final int _tmp = entity.isEscalated() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.isCancelled() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        if (entity.getCancelMethod() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getCancelMethod());
        }
        final int _tmp_2 = entity.isDuress() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        statement.bindLong(13, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getEndedAt());
        }
        if (entity.getBatteryLevel() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getBatteryLevel());
        }
        final int _tmp_3 = entity.getHasDataConnection() ? 1 : 0;
        statement.bindLong(16, _tmp_3);
      }
    };
    this.__updateAdapterOfAlertLog = new EntityDeletionOrUpdateAdapter<AlertLog>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `alert_logs` SET `id` = ?,`triggerType` = ?,`latitude` = ?,`longitude` = ?,`accuracy` = ?,`locationSource` = ?,`smsSentCount` = ?,`callsMadeCount` = ?,`isEscalated` = ?,`isCancelled` = ?,`cancelMethod` = ?,`isDuress` = ?,`startedAt` = ?,`endedAt` = ?,`batteryLevel` = ?,`hasDataConnection` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final AlertLog entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getTriggerType());
        if (entity.getLatitude() == null) {
          statement.bindNull(3);
        } else {
          statement.bindDouble(3, entity.getLatitude());
        }
        if (entity.getLongitude() == null) {
          statement.bindNull(4);
        } else {
          statement.bindDouble(4, entity.getLongitude());
        }
        if (entity.getAccuracy() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getAccuracy());
        }
        if (entity.getLocationSource() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getLocationSource());
        }
        statement.bindLong(7, entity.getSmsSentCount());
        statement.bindLong(8, entity.getCallsMadeCount());
        final int _tmp = entity.isEscalated() ? 1 : 0;
        statement.bindLong(9, _tmp);
        final int _tmp_1 = entity.isCancelled() ? 1 : 0;
        statement.bindLong(10, _tmp_1);
        if (entity.getCancelMethod() == null) {
          statement.bindNull(11);
        } else {
          statement.bindString(11, entity.getCancelMethod());
        }
        final int _tmp_2 = entity.isDuress() ? 1 : 0;
        statement.bindLong(12, _tmp_2);
        statement.bindLong(13, entity.getStartedAt());
        if (entity.getEndedAt() == null) {
          statement.bindNull(14);
        } else {
          statement.bindLong(14, entity.getEndedAt());
        }
        if (entity.getBatteryLevel() == null) {
          statement.bindNull(15);
        } else {
          statement.bindLong(15, entity.getBatteryLevel());
        }
        final int _tmp_3 = entity.getHasDataConnection() ? 1 : 0;
        statement.bindLong(16, _tmp_3);
        statement.bindLong(17, entity.getId());
      }
    };
    this.__preparedStmtOfEndAlert = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_logs SET endedAt = ?, isCancelled = ?, cancelMethod = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementSmsCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_logs SET smsSentCount = smsSentCount + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfIncrementCallCount = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_logs SET callsMadeCount = callsMadeCount + 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkEscalated = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE alert_logs SET isEscalated = 1 WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAlertsOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM alert_logs WHERE startedAt < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertAlert(final AlertLog alert, final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfAlertLog.insertAndReturnId(alert);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateAlert(final AlertLog alert, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfAlertLog.handle(alert);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object endAlert(final long id, final long endedAt, final boolean isCancelled,
      final String cancelMethod, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfEndAlert.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, endedAt);
        _argIndex = 2;
        final int _tmp = isCancelled ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 3;
        if (cancelMethod == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, cancelMethod);
        }
        _argIndex = 4;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfEndAlert.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementSmsCount(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementSmsCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementSmsCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object incrementCallCount(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfIncrementCallCount.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfIncrementCallCount.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markEscalated(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkEscalated.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkEscalated.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAlertsOlderThan(final long before,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAlertsOlderThan.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, before);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAlertsOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<AlertLog>> getRecentAlerts(final int limit) {
    final String _sql = "SELECT * FROM alert_logs ORDER BY startedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"alert_logs"}, new Callable<List<AlertLog>>() {
      @Override
      @NonNull
      public List<AlertLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTriggerType = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfLocationSource = CursorUtil.getColumnIndexOrThrow(_cursor, "locationSource");
          final int _cursorIndexOfSmsSentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "smsSentCount");
          final int _cursorIndexOfCallsMadeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "callsMadeCount");
          final int _cursorIndexOfIsEscalated = CursorUtil.getColumnIndexOrThrow(_cursor, "isEscalated");
          final int _cursorIndexOfIsCancelled = CursorUtil.getColumnIndexOrThrow(_cursor, "isCancelled");
          final int _cursorIndexOfCancelMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelMethod");
          final int _cursorIndexOfIsDuress = CursorUtil.getColumnIndexOrThrow(_cursor, "isDuress");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfHasDataConnection = CursorUtil.getColumnIndexOrThrow(_cursor, "hasDataConnection");
          final List<AlertLog> _result = new ArrayList<AlertLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTriggerType;
            _tmpTriggerType = _cursor.getString(_cursorIndexOfTriggerType);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpLocationSource;
            if (_cursor.isNull(_cursorIndexOfLocationSource)) {
              _tmpLocationSource = null;
            } else {
              _tmpLocationSource = _cursor.getString(_cursorIndexOfLocationSource);
            }
            final int _tmpSmsSentCount;
            _tmpSmsSentCount = _cursor.getInt(_cursorIndexOfSmsSentCount);
            final int _tmpCallsMadeCount;
            _tmpCallsMadeCount = _cursor.getInt(_cursorIndexOfCallsMadeCount);
            final boolean _tmpIsEscalated;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEscalated);
            _tmpIsEscalated = _tmp != 0;
            final boolean _tmpIsCancelled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCancelled);
            _tmpIsCancelled = _tmp_1 != 0;
            final String _tmpCancelMethod;
            if (_cursor.isNull(_cursorIndexOfCancelMethod)) {
              _tmpCancelMethod = null;
            } else {
              _tmpCancelMethod = _cursor.getString(_cursorIndexOfCancelMethod);
            }
            final boolean _tmpIsDuress;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDuress);
            _tmpIsDuress = _tmp_2 != 0;
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final boolean _tmpHasDataConnection;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfHasDataConnection);
            _tmpHasDataConnection = _tmp_3 != 0;
            _item = new AlertLog(_tmpId,_tmpTriggerType,_tmpLatitude,_tmpLongitude,_tmpAccuracy,_tmpLocationSource,_tmpSmsSentCount,_tmpCallsMadeCount,_tmpIsEscalated,_tmpIsCancelled,_tmpCancelMethod,_tmpIsDuress,_tmpStartedAt,_tmpEndedAt,_tmpBatteryLevel,_tmpHasDataConnection);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getRecentAlertsList(final int limit,
      final Continuation<? super List<AlertLog>> $completion) {
    final String _sql = "SELECT * FROM alert_logs ORDER BY startedAt DESC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertLog>>() {
      @Override
      @NonNull
      public List<AlertLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTriggerType = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfLocationSource = CursorUtil.getColumnIndexOrThrow(_cursor, "locationSource");
          final int _cursorIndexOfSmsSentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "smsSentCount");
          final int _cursorIndexOfCallsMadeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "callsMadeCount");
          final int _cursorIndexOfIsEscalated = CursorUtil.getColumnIndexOrThrow(_cursor, "isEscalated");
          final int _cursorIndexOfIsCancelled = CursorUtil.getColumnIndexOrThrow(_cursor, "isCancelled");
          final int _cursorIndexOfCancelMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelMethod");
          final int _cursorIndexOfIsDuress = CursorUtil.getColumnIndexOrThrow(_cursor, "isDuress");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfHasDataConnection = CursorUtil.getColumnIndexOrThrow(_cursor, "hasDataConnection");
          final List<AlertLog> _result = new ArrayList<AlertLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTriggerType;
            _tmpTriggerType = _cursor.getString(_cursorIndexOfTriggerType);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpLocationSource;
            if (_cursor.isNull(_cursorIndexOfLocationSource)) {
              _tmpLocationSource = null;
            } else {
              _tmpLocationSource = _cursor.getString(_cursorIndexOfLocationSource);
            }
            final int _tmpSmsSentCount;
            _tmpSmsSentCount = _cursor.getInt(_cursorIndexOfSmsSentCount);
            final int _tmpCallsMadeCount;
            _tmpCallsMadeCount = _cursor.getInt(_cursorIndexOfCallsMadeCount);
            final boolean _tmpIsEscalated;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEscalated);
            _tmpIsEscalated = _tmp != 0;
            final boolean _tmpIsCancelled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCancelled);
            _tmpIsCancelled = _tmp_1 != 0;
            final String _tmpCancelMethod;
            if (_cursor.isNull(_cursorIndexOfCancelMethod)) {
              _tmpCancelMethod = null;
            } else {
              _tmpCancelMethod = _cursor.getString(_cursorIndexOfCancelMethod);
            }
            final boolean _tmpIsDuress;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDuress);
            _tmpIsDuress = _tmp_2 != 0;
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final boolean _tmpHasDataConnection;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfHasDataConnection);
            _tmpHasDataConnection = _tmp_3 != 0;
            _item = new AlertLog(_tmpId,_tmpTriggerType,_tmpLatitude,_tmpLongitude,_tmpAccuracy,_tmpLocationSource,_tmpSmsSentCount,_tmpCallsMadeCount,_tmpIsEscalated,_tmpIsCancelled,_tmpCancelMethod,_tmpIsDuress,_tmpStartedAt,_tmpEndedAt,_tmpBatteryLevel,_tmpHasDataConnection);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAlertById(final long id, final Continuation<? super AlertLog> $completion) {
    final String _sql = "SELECT * FROM alert_logs WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AlertLog>() {
      @Override
      @Nullable
      public AlertLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTriggerType = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfLocationSource = CursorUtil.getColumnIndexOrThrow(_cursor, "locationSource");
          final int _cursorIndexOfSmsSentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "smsSentCount");
          final int _cursorIndexOfCallsMadeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "callsMadeCount");
          final int _cursorIndexOfIsEscalated = CursorUtil.getColumnIndexOrThrow(_cursor, "isEscalated");
          final int _cursorIndexOfIsCancelled = CursorUtil.getColumnIndexOrThrow(_cursor, "isCancelled");
          final int _cursorIndexOfCancelMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelMethod");
          final int _cursorIndexOfIsDuress = CursorUtil.getColumnIndexOrThrow(_cursor, "isDuress");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfHasDataConnection = CursorUtil.getColumnIndexOrThrow(_cursor, "hasDataConnection");
          final AlertLog _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTriggerType;
            _tmpTriggerType = _cursor.getString(_cursorIndexOfTriggerType);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpLocationSource;
            if (_cursor.isNull(_cursorIndexOfLocationSource)) {
              _tmpLocationSource = null;
            } else {
              _tmpLocationSource = _cursor.getString(_cursorIndexOfLocationSource);
            }
            final int _tmpSmsSentCount;
            _tmpSmsSentCount = _cursor.getInt(_cursorIndexOfSmsSentCount);
            final int _tmpCallsMadeCount;
            _tmpCallsMadeCount = _cursor.getInt(_cursorIndexOfCallsMadeCount);
            final boolean _tmpIsEscalated;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEscalated);
            _tmpIsEscalated = _tmp != 0;
            final boolean _tmpIsCancelled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCancelled);
            _tmpIsCancelled = _tmp_1 != 0;
            final String _tmpCancelMethod;
            if (_cursor.isNull(_cursorIndexOfCancelMethod)) {
              _tmpCancelMethod = null;
            } else {
              _tmpCancelMethod = _cursor.getString(_cursorIndexOfCancelMethod);
            }
            final boolean _tmpIsDuress;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDuress);
            _tmpIsDuress = _tmp_2 != 0;
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final boolean _tmpHasDataConnection;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfHasDataConnection);
            _tmpHasDataConnection = _tmp_3 != 0;
            _result = new AlertLog(_tmpId,_tmpTriggerType,_tmpLatitude,_tmpLongitude,_tmpAccuracy,_tmpLocationSource,_tmpSmsSentCount,_tmpCallsMadeCount,_tmpIsEscalated,_tmpIsCancelled,_tmpCancelMethod,_tmpIsDuress,_tmpStartedAt,_tmpEndedAt,_tmpBatteryLevel,_tmpHasDataConnection);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getActiveAlert(final Continuation<? super AlertLog> $completion) {
    final String _sql = "SELECT * FROM alert_logs WHERE endedAt IS NULL ORDER BY startedAt DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<AlertLog>() {
      @Override
      @Nullable
      public AlertLog call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTriggerType = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfLocationSource = CursorUtil.getColumnIndexOrThrow(_cursor, "locationSource");
          final int _cursorIndexOfSmsSentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "smsSentCount");
          final int _cursorIndexOfCallsMadeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "callsMadeCount");
          final int _cursorIndexOfIsEscalated = CursorUtil.getColumnIndexOrThrow(_cursor, "isEscalated");
          final int _cursorIndexOfIsCancelled = CursorUtil.getColumnIndexOrThrow(_cursor, "isCancelled");
          final int _cursorIndexOfCancelMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelMethod");
          final int _cursorIndexOfIsDuress = CursorUtil.getColumnIndexOrThrow(_cursor, "isDuress");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfHasDataConnection = CursorUtil.getColumnIndexOrThrow(_cursor, "hasDataConnection");
          final AlertLog _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTriggerType;
            _tmpTriggerType = _cursor.getString(_cursorIndexOfTriggerType);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpLocationSource;
            if (_cursor.isNull(_cursorIndexOfLocationSource)) {
              _tmpLocationSource = null;
            } else {
              _tmpLocationSource = _cursor.getString(_cursorIndexOfLocationSource);
            }
            final int _tmpSmsSentCount;
            _tmpSmsSentCount = _cursor.getInt(_cursorIndexOfSmsSentCount);
            final int _tmpCallsMadeCount;
            _tmpCallsMadeCount = _cursor.getInt(_cursorIndexOfCallsMadeCount);
            final boolean _tmpIsEscalated;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEscalated);
            _tmpIsEscalated = _tmp != 0;
            final boolean _tmpIsCancelled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCancelled);
            _tmpIsCancelled = _tmp_1 != 0;
            final String _tmpCancelMethod;
            if (_cursor.isNull(_cursorIndexOfCancelMethod)) {
              _tmpCancelMethod = null;
            } else {
              _tmpCancelMethod = _cursor.getString(_cursorIndexOfCancelMethod);
            }
            final boolean _tmpIsDuress;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDuress);
            _tmpIsDuress = _tmp_2 != 0;
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final boolean _tmpHasDataConnection;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfHasDataConnection);
            _tmpHasDataConnection = _tmp_3 != 0;
            _result = new AlertLog(_tmpId,_tmpTriggerType,_tmpLatitude,_tmpLongitude,_tmpAccuracy,_tmpLocationSource,_tmpSmsSentCount,_tmpCallsMadeCount,_tmpIsEscalated,_tmpIsCancelled,_tmpCancelMethod,_tmpIsDuress,_tmpStartedAt,_tmpEndedAt,_tmpBatteryLevel,_tmpHasDataConnection);
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @Override
  public Object getAlertsSince(final long since,
      final Continuation<? super List<AlertLog>> $completion) {
    final String _sql = "SELECT * FROM alert_logs WHERE startedAt > ? ORDER BY startedAt DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, since);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<AlertLog>>() {
      @Override
      @NonNull
      public List<AlertLog> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfTriggerType = CursorUtil.getColumnIndexOrThrow(_cursor, "triggerType");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfLocationSource = CursorUtil.getColumnIndexOrThrow(_cursor, "locationSource");
          final int _cursorIndexOfSmsSentCount = CursorUtil.getColumnIndexOrThrow(_cursor, "smsSentCount");
          final int _cursorIndexOfCallsMadeCount = CursorUtil.getColumnIndexOrThrow(_cursor, "callsMadeCount");
          final int _cursorIndexOfIsEscalated = CursorUtil.getColumnIndexOrThrow(_cursor, "isEscalated");
          final int _cursorIndexOfIsCancelled = CursorUtil.getColumnIndexOrThrow(_cursor, "isCancelled");
          final int _cursorIndexOfCancelMethod = CursorUtil.getColumnIndexOrThrow(_cursor, "cancelMethod");
          final int _cursorIndexOfIsDuress = CursorUtil.getColumnIndexOrThrow(_cursor, "isDuress");
          final int _cursorIndexOfStartedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "startedAt");
          final int _cursorIndexOfEndedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "endedAt");
          final int _cursorIndexOfBatteryLevel = CursorUtil.getColumnIndexOrThrow(_cursor, "batteryLevel");
          final int _cursorIndexOfHasDataConnection = CursorUtil.getColumnIndexOrThrow(_cursor, "hasDataConnection");
          final List<AlertLog> _result = new ArrayList<AlertLog>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final AlertLog _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpTriggerType;
            _tmpTriggerType = _cursor.getString(_cursorIndexOfTriggerType);
            final Double _tmpLatitude;
            if (_cursor.isNull(_cursorIndexOfLatitude)) {
              _tmpLatitude = null;
            } else {
              _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            }
            final Double _tmpLongitude;
            if (_cursor.isNull(_cursorIndexOfLongitude)) {
              _tmpLongitude = null;
            } else {
              _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            }
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpLocationSource;
            if (_cursor.isNull(_cursorIndexOfLocationSource)) {
              _tmpLocationSource = null;
            } else {
              _tmpLocationSource = _cursor.getString(_cursorIndexOfLocationSource);
            }
            final int _tmpSmsSentCount;
            _tmpSmsSentCount = _cursor.getInt(_cursorIndexOfSmsSentCount);
            final int _tmpCallsMadeCount;
            _tmpCallsMadeCount = _cursor.getInt(_cursorIndexOfCallsMadeCount);
            final boolean _tmpIsEscalated;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsEscalated);
            _tmpIsEscalated = _tmp != 0;
            final boolean _tmpIsCancelled;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsCancelled);
            _tmpIsCancelled = _tmp_1 != 0;
            final String _tmpCancelMethod;
            if (_cursor.isNull(_cursorIndexOfCancelMethod)) {
              _tmpCancelMethod = null;
            } else {
              _tmpCancelMethod = _cursor.getString(_cursorIndexOfCancelMethod);
            }
            final boolean _tmpIsDuress;
            final int _tmp_2;
            _tmp_2 = _cursor.getInt(_cursorIndexOfIsDuress);
            _tmpIsDuress = _tmp_2 != 0;
            final long _tmpStartedAt;
            _tmpStartedAt = _cursor.getLong(_cursorIndexOfStartedAt);
            final Long _tmpEndedAt;
            if (_cursor.isNull(_cursorIndexOfEndedAt)) {
              _tmpEndedAt = null;
            } else {
              _tmpEndedAt = _cursor.getLong(_cursorIndexOfEndedAt);
            }
            final Integer _tmpBatteryLevel;
            if (_cursor.isNull(_cursorIndexOfBatteryLevel)) {
              _tmpBatteryLevel = null;
            } else {
              _tmpBatteryLevel = _cursor.getInt(_cursorIndexOfBatteryLevel);
            }
            final boolean _tmpHasDataConnection;
            final int _tmp_3;
            _tmp_3 = _cursor.getInt(_cursorIndexOfHasDataConnection);
            _tmpHasDataConnection = _tmp_3 != 0;
            _item = new AlertLog(_tmpId,_tmpTriggerType,_tmpLatitude,_tmpLongitude,_tmpAccuracy,_tmpLocationSource,_tmpSmsSentCount,_tmpCallsMadeCount,_tmpIsEscalated,_tmpIsCancelled,_tmpCancelMethod,_tmpIsDuress,_tmpStartedAt,_tmpEndedAt,_tmpBatteryLevel,_tmpHasDataConnection);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
