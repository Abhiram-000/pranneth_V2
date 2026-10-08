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
import com.rakshasetu.app.data.entity.EmergencyContact;
import java.lang.Class;
import java.lang.Exception;
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
public final class EmergencyContactDao_Impl implements EmergencyContactDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<EmergencyContact> __insertionAdapterOfEmergencyContact;

  private final EntityDeletionOrUpdateAdapter<EmergencyContact> __deletionAdapterOfEmergencyContact;

  private final EntityDeletionOrUpdateAdapter<EmergencyContact> __updateAdapterOfEmergencyContact;

  private final SharedSQLiteStatement __preparedStmtOfDeleteContactById;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePriority;

  private final SharedSQLiteStatement __preparedStmtOfUpdateVerification;

  public EmergencyContactDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfEmergencyContact = new EntityInsertionAdapter<EmergencyContact>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `emergency_contacts` (`id`,`name`,`phoneNumber`,`countryCode`,`relation`,`customRelation`,`priority`,`isVerified`,`lastNotifiedAt`,`createdAt`,`updatedAt`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmergencyContact entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getPhoneNumber());
        statement.bindString(4, entity.getCountryCode());
        statement.bindString(5, entity.getRelation());
        if (entity.getCustomRelation() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCustomRelation());
        }
        statement.bindLong(7, entity.getPriority());
        final int _tmp = entity.isVerified() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getLastNotifiedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getLastNotifiedAt());
        }
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getUpdatedAt());
      }
    };
    this.__deletionAdapterOfEmergencyContact = new EntityDeletionOrUpdateAdapter<EmergencyContact>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `emergency_contacts` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmergencyContact entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfEmergencyContact = new EntityDeletionOrUpdateAdapter<EmergencyContact>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `emergency_contacts` SET `id` = ?,`name` = ?,`phoneNumber` = ?,`countryCode` = ?,`relation` = ?,`customRelation` = ?,`priority` = ?,`isVerified` = ?,`lastNotifiedAt` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final EmergencyContact entity) {
        statement.bindLong(1, entity.getId());
        statement.bindString(2, entity.getName());
        statement.bindString(3, entity.getPhoneNumber());
        statement.bindString(4, entity.getCountryCode());
        statement.bindString(5, entity.getRelation());
        if (entity.getCustomRelation() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getCustomRelation());
        }
        statement.bindLong(7, entity.getPriority());
        final int _tmp = entity.isVerified() ? 1 : 0;
        statement.bindLong(8, _tmp);
        if (entity.getLastNotifiedAt() == null) {
          statement.bindNull(9);
        } else {
          statement.bindLong(9, entity.getLastNotifiedAt());
        }
        statement.bindLong(10, entity.getCreatedAt());
        statement.bindLong(11, entity.getUpdatedAt());
        statement.bindLong(12, entity.getId());
      }
    };
    this.__preparedStmtOfDeleteContactById = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM emergency_contacts WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePriority = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE emergency_contacts SET priority = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdateVerification = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE emergency_contacts SET isVerified = ?, lastNotifiedAt = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insertContact(final EmergencyContact contact,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfEmergencyContact.insertAndReturnId(contact);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteContact(final EmergencyContact contact,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __deletionAdapterOfEmergencyContact.handle(contact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object updateContact(final EmergencyContact contact,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __updateAdapterOfEmergencyContact.handle(contact);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteContactById(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteContactById.acquire();
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
          __preparedStmtOfDeleteContactById.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePriority(final long id, final int priority,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePriority.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, priority);
        _argIndex = 2;
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
          __preparedStmtOfUpdatePriority.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updateVerification(final long id, final boolean isVerified, final Long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdateVerification.acquire();
        int _argIndex = 1;
        final int _tmp = isVerified ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
        _argIndex = 2;
        if (timestamp == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindLong(_argIndex, timestamp);
        }
        _argIndex = 3;
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
          __preparedStmtOfUpdateVerification.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<EmergencyContact>> getAllContacts() {
    final String _sql = "SELECT * FROM emergency_contacts ORDER BY priority ASC, createdAt ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"emergency_contacts"}, new Callable<List<EmergencyContact>>() {
      @Override
      @NonNull
      public List<EmergencyContact> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "countryCode");
          final int _cursorIndexOfRelation = CursorUtil.getColumnIndexOrThrow(_cursor, "relation");
          final int _cursorIndexOfCustomRelation = CursorUtil.getColumnIndexOrThrow(_cursor, "customRelation");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastNotifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastNotifiedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<EmergencyContact> _result = new ArrayList<EmergencyContact>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmergencyContact _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpCountryCode;
            _tmpCountryCode = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpRelation;
            _tmpRelation = _cursor.getString(_cursorIndexOfRelation);
            final String _tmpCustomRelation;
            if (_cursor.isNull(_cursorIndexOfCustomRelation)) {
              _tmpCustomRelation = null;
            } else {
              _tmpCustomRelation = _cursor.getString(_cursorIndexOfCustomRelation);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final Long _tmpLastNotifiedAt;
            if (_cursor.isNull(_cursorIndexOfLastNotifiedAt)) {
              _tmpLastNotifiedAt = null;
            } else {
              _tmpLastNotifiedAt = _cursor.getLong(_cursorIndexOfLastNotifiedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new EmergencyContact(_tmpId,_tmpName,_tmpPhoneNumber,_tmpCountryCode,_tmpRelation,_tmpCustomRelation,_tmpPriority,_tmpIsVerified,_tmpLastNotifiedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getAllContactsList(final Continuation<? super List<EmergencyContact>> $completion) {
    final String _sql = "SELECT * FROM emergency_contacts ORDER BY priority ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<EmergencyContact>>() {
      @Override
      @NonNull
      public List<EmergencyContact> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "countryCode");
          final int _cursorIndexOfRelation = CursorUtil.getColumnIndexOrThrow(_cursor, "relation");
          final int _cursorIndexOfCustomRelation = CursorUtil.getColumnIndexOrThrow(_cursor, "customRelation");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastNotifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastNotifiedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final List<EmergencyContact> _result = new ArrayList<EmergencyContact>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final EmergencyContact _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpCountryCode;
            _tmpCountryCode = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpRelation;
            _tmpRelation = _cursor.getString(_cursorIndexOfRelation);
            final String _tmpCustomRelation;
            if (_cursor.isNull(_cursorIndexOfCustomRelation)) {
              _tmpCustomRelation = null;
            } else {
              _tmpCustomRelation = _cursor.getString(_cursorIndexOfCustomRelation);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final Long _tmpLastNotifiedAt;
            if (_cursor.isNull(_cursorIndexOfLastNotifiedAt)) {
              _tmpLastNotifiedAt = null;
            } else {
              _tmpLastNotifiedAt = _cursor.getLong(_cursorIndexOfLastNotifiedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _item = new EmergencyContact(_tmpId,_tmpName,_tmpPhoneNumber,_tmpCountryCode,_tmpRelation,_tmpCustomRelation,_tmpPriority,_tmpIsVerified,_tmpLastNotifiedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Object getContactById(final long id,
      final Continuation<? super EmergencyContact> $completion) {
    final String _sql = "SELECT * FROM emergency_contacts WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EmergencyContact>() {
      @Override
      @Nullable
      public EmergencyContact call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "countryCode");
          final int _cursorIndexOfRelation = CursorUtil.getColumnIndexOrThrow(_cursor, "relation");
          final int _cursorIndexOfCustomRelation = CursorUtil.getColumnIndexOrThrow(_cursor, "customRelation");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastNotifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastNotifiedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final EmergencyContact _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpCountryCode;
            _tmpCountryCode = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpRelation;
            _tmpRelation = _cursor.getString(_cursorIndexOfRelation);
            final String _tmpCustomRelation;
            if (_cursor.isNull(_cursorIndexOfCustomRelation)) {
              _tmpCustomRelation = null;
            } else {
              _tmpCustomRelation = _cursor.getString(_cursorIndexOfCustomRelation);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final Long _tmpLastNotifiedAt;
            if (_cursor.isNull(_cursorIndexOfLastNotifiedAt)) {
              _tmpLastNotifiedAt = null;
            } else {
              _tmpLastNotifiedAt = _cursor.getLong(_cursorIndexOfLastNotifiedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new EmergencyContact(_tmpId,_tmpName,_tmpPhoneNumber,_tmpCountryCode,_tmpRelation,_tmpCustomRelation,_tmpPriority,_tmpIsVerified,_tmpLastNotifiedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
  public Flow<Integer> getContactCount() {
    final String _sql = "SELECT COUNT(*) FROM emergency_contacts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"emergency_contacts"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getContactCountSync(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM emergency_contacts";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final int _tmp;
            _tmp = _cursor.getInt(0);
            _result = _tmp;
          } else {
            _result = 0;
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
  public Object getContactByPhone(final String phone,
      final Continuation<? super EmergencyContact> $completion) {
    final String _sql = "SELECT * FROM emergency_contacts WHERE phoneNumber = ? LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindString(_argIndex, phone);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<EmergencyContact>() {
      @Override
      @Nullable
      public EmergencyContact call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfPhoneNumber = CursorUtil.getColumnIndexOrThrow(_cursor, "phoneNumber");
          final int _cursorIndexOfCountryCode = CursorUtil.getColumnIndexOrThrow(_cursor, "countryCode");
          final int _cursorIndexOfRelation = CursorUtil.getColumnIndexOrThrow(_cursor, "relation");
          final int _cursorIndexOfCustomRelation = CursorUtil.getColumnIndexOrThrow(_cursor, "customRelation");
          final int _cursorIndexOfPriority = CursorUtil.getColumnIndexOrThrow(_cursor, "priority");
          final int _cursorIndexOfIsVerified = CursorUtil.getColumnIndexOrThrow(_cursor, "isVerified");
          final int _cursorIndexOfLastNotifiedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "lastNotifiedAt");
          final int _cursorIndexOfCreatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAt");
          final int _cursorIndexOfUpdatedAt = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAt");
          final EmergencyContact _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            _tmpName = _cursor.getString(_cursorIndexOfName);
            final String _tmpPhoneNumber;
            _tmpPhoneNumber = _cursor.getString(_cursorIndexOfPhoneNumber);
            final String _tmpCountryCode;
            _tmpCountryCode = _cursor.getString(_cursorIndexOfCountryCode);
            final String _tmpRelation;
            _tmpRelation = _cursor.getString(_cursorIndexOfRelation);
            final String _tmpCustomRelation;
            if (_cursor.isNull(_cursorIndexOfCustomRelation)) {
              _tmpCustomRelation = null;
            } else {
              _tmpCustomRelation = _cursor.getString(_cursorIndexOfCustomRelation);
            }
            final int _tmpPriority;
            _tmpPriority = _cursor.getInt(_cursorIndexOfPriority);
            final boolean _tmpIsVerified;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsVerified);
            _tmpIsVerified = _tmp != 0;
            final Long _tmpLastNotifiedAt;
            if (_cursor.isNull(_cursorIndexOfLastNotifiedAt)) {
              _tmpLastNotifiedAt = null;
            } else {
              _tmpLastNotifiedAt = _cursor.getLong(_cursorIndexOfLastNotifiedAt);
            }
            final long _tmpCreatedAt;
            _tmpCreatedAt = _cursor.getLong(_cursorIndexOfCreatedAt);
            final long _tmpUpdatedAt;
            _tmpUpdatedAt = _cursor.getLong(_cursorIndexOfUpdatedAt);
            _result = new EmergencyContact(_tmpId,_tmpName,_tmpPhoneNumber,_tmpCountryCode,_tmpRelation,_tmpCustomRelation,_tmpPriority,_tmpIsVerified,_tmpLastNotifiedAt,_tmpCreatedAt,_tmpUpdatedAt);
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
