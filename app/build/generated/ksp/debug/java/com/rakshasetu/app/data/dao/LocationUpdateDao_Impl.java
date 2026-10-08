package com.rakshasetu.app.data.dao;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import com.rakshasetu.app.data.entity.LocationUpdate;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Float;
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

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class LocationUpdateDao_Impl implements LocationUpdateDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<LocationUpdate> __insertionAdapterOfLocationUpdate;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLocationsForAlert;

  private final SharedSQLiteStatement __preparedStmtOfDeleteLocationsOlderThan;

  public LocationUpdateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfLocationUpdate = new EntityInsertionAdapter<LocationUpdate>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `location_updates` (`id`,`alertId`,`latitude`,`longitude`,`accuracy`,`source`,`timestamp`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final LocationUpdate entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getAlertId());
        statement.bindDouble(3, entity.getLatitude());
        statement.bindDouble(4, entity.getLongitude());
        if (entity.getAccuracy() == null) {
          statement.bindNull(5);
        } else {
          statement.bindDouble(5, entity.getAccuracy());
        }
        statement.bindString(6, entity.getSource());
        statement.bindLong(7, entity.getTimestamp());
      }
    };
    this.__preparedStmtOfDeleteLocationsForAlert = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM location_updates WHERE alertId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteLocationsOlderThan = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM location_updates WHERE timestamp < ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertLocation(final LocationUpdate update,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfLocationUpdate.insertAndReturnId(update);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLocationsForAlert(final long alertId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLocationsForAlert.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, alertId);
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
          __preparedStmtOfDeleteLocationsForAlert.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteLocationsOlderThan(final long before,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteLocationsOlderThan.acquire();
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
          __preparedStmtOfDeleteLocationsOlderThan.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getLocationsForAlert(final long alertId,
      final Continuation<? super List<LocationUpdate>> $completion) {
    final String _sql = "SELECT * FROM location_updates WHERE alertId = ? ORDER BY timestamp ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, alertId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<LocationUpdate>>() {
      @Override
      @NonNull
      public List<LocationUpdate> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAlertId = CursorUtil.getColumnIndexOrThrow(_cursor, "alertId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final List<LocationUpdate> _result = new ArrayList<LocationUpdate>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final LocationUpdate _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAlertId;
            _tmpAlertId = _cursor.getLong(_cursorIndexOfAlertId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _item = new LocationUpdate(_tmpId,_tmpAlertId,_tmpLatitude,_tmpLongitude,_tmpAccuracy,_tmpSource,_tmpTimestamp);
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
  public Object getLatestLocationForAlert(final long alertId,
      final Continuation<? super LocationUpdate> $completion) {
    final String _sql = "SELECT * FROM location_updates WHERE alertId = ? ORDER BY timestamp DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, alertId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<LocationUpdate>() {
      @Override
      @Nullable
      public LocationUpdate call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfAlertId = CursorUtil.getColumnIndexOrThrow(_cursor, "alertId");
          final int _cursorIndexOfLatitude = CursorUtil.getColumnIndexOrThrow(_cursor, "latitude");
          final int _cursorIndexOfLongitude = CursorUtil.getColumnIndexOrThrow(_cursor, "longitude");
          final int _cursorIndexOfAccuracy = CursorUtil.getColumnIndexOrThrow(_cursor, "accuracy");
          final int _cursorIndexOfSource = CursorUtil.getColumnIndexOrThrow(_cursor, "source");
          final int _cursorIndexOfTimestamp = CursorUtil.getColumnIndexOrThrow(_cursor, "timestamp");
          final LocationUpdate _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final long _tmpAlertId;
            _tmpAlertId = _cursor.getLong(_cursorIndexOfAlertId);
            final double _tmpLatitude;
            _tmpLatitude = _cursor.getDouble(_cursorIndexOfLatitude);
            final double _tmpLongitude;
            _tmpLongitude = _cursor.getDouble(_cursorIndexOfLongitude);
            final Float _tmpAccuracy;
            if (_cursor.isNull(_cursorIndexOfAccuracy)) {
              _tmpAccuracy = null;
            } else {
              _tmpAccuracy = _cursor.getFloat(_cursorIndexOfAccuracy);
            }
            final String _tmpSource;
            _tmpSource = _cursor.getString(_cursorIndexOfSource);
            final long _tmpTimestamp;
            _tmpTimestamp = _cursor.getLong(_cursorIndexOfTimestamp);
            _result = new LocationUpdate(_tmpId,_tmpAlertId,_tmpLatitude,_tmpLongitude,_tmpAccuracy,_tmpSource,_tmpTimestamp);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
