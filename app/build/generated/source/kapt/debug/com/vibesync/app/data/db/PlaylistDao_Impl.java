package com.vibesync.app.data.db;

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
public final class PlaylistDao_Impl implements PlaylistDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlaylistEntity> __insertionAdapterOfPlaylistEntity;

  private final SharedSQLiteStatement __preparedStmtOfRename;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  private final SharedSQLiteStatement __preparedStmtOfTouchLastPlayed;

  public PlaylistDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlaylistEntity = new EntityInsertionAdapter<PlaylistEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `playlists` (`id`,`name`,`isDefault`,`createdAtEpochMillis`,`lastPlayedAtEpochMillis`,`description`,`coverImageUri`,`themeColorArgb`) VALUES (nullif(?, 0),?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlaylistEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getName());
        }
        final int _tmp = entity.isDefault() ? 1 : 0;
        statement.bindLong(3, _tmp);
        statement.bindLong(4, entity.getCreatedAtEpochMillis());
        if (entity.getLastPlayedAtEpochMillis() == null) {
          statement.bindNull(5);
        } else {
          statement.bindLong(5, entity.getLastPlayedAtEpochMillis());
        }
        if (entity.getDescription() == null) {
          statement.bindNull(6);
        } else {
          statement.bindString(6, entity.getDescription());
        }
        if (entity.getCoverImageUri() == null) {
          statement.bindNull(7);
        } else {
          statement.bindString(7, entity.getCoverImageUri());
        }
        if (entity.getThemeColorArgb() == null) {
          statement.bindNull(8);
        } else {
          statement.bindLong(8, entity.getThemeColorArgb());
        }
      }
    };
    this.__preparedStmtOfRename = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE playlists SET name = ? WHERE id = ? AND isDefault = 0";
        return _query;
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM playlists WHERE id = ? AND isDefault = 0";
        return _query;
      }
    };
    this.__preparedStmtOfTouchLastPlayed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE playlists SET lastPlayedAtEpochMillis = ? WHERE id = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PlaylistEntity playlist,
      final Continuation<? super Long> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Long>() {
      @Override
      @NonNull
      public Long call() throws Exception {
        __db.beginTransaction();
        try {
          final Long _result = __insertionAdapterOfPlaylistEntity.insertAndReturnId(playlist);
          __db.setTransactionSuccessful();
          return _result;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object rename(final long id, final String name,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRename.acquire();
        int _argIndex = 1;
        if (name == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, name);
        }
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
          __preparedStmtOfRename.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final long id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object touchLastPlayed(final long id, final long timestamp,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfTouchLastPlayed.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, timestamp);
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
          __preparedStmtOfTouchLastPlayed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<PlaylistEntity>> observePlaylists() {
    final String _sql = "SELECT * FROM playlists ORDER BY isDefault DESC, createdAtEpochMillis ASC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"playlists"}, new Callable<List<PlaylistEntity>>() {
      @Override
      @NonNull
      public List<PlaylistEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "isDefault");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final int _cursorIndexOfLastPlayedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPlayedAtEpochMillis");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "coverImageUri");
          final int _cursorIndexOfThemeColorArgb = CursorUtil.getColumnIndexOrThrow(_cursor, "themeColorArgb");
          final List<PlaylistEntity> _result = new ArrayList<PlaylistEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlaylistEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final boolean _tmpIsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDefault);
            _tmpIsDefault = _tmp != 0;
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            final Long _tmpLastPlayedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAtEpochMillis)) {
              _tmpLastPlayedAtEpochMillis = null;
            } else {
              _tmpLastPlayedAtEpochMillis = _cursor.getLong(_cursorIndexOfLastPlayedAtEpochMillis);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImageUri;
            if (_cursor.isNull(_cursorIndexOfCoverImageUri)) {
              _tmpCoverImageUri = null;
            } else {
              _tmpCoverImageUri = _cursor.getString(_cursorIndexOfCoverImageUri);
            }
            final Integer _tmpThemeColorArgb;
            if (_cursor.isNull(_cursorIndexOfThemeColorArgb)) {
              _tmpThemeColorArgb = null;
            } else {
              _tmpThemeColorArgb = _cursor.getInt(_cursorIndexOfThemeColorArgb);
            }
            _item = new PlaylistEntity(_tmpId,_tmpName,_tmpIsDefault,_tmpCreatedAtEpochMillis,_tmpLastPlayedAtEpochMillis,_tmpDescription,_tmpCoverImageUri,_tmpThemeColorArgb);
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
  public Flow<List<PlaylistEntity>> observeRecentPlaylists() {
    final String _sql = "SELECT * FROM playlists WHERE lastPlayedAtEpochMillis IS NOT NULL ORDER BY lastPlayedAtEpochMillis DESC LIMIT 8";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"playlists"}, new Callable<List<PlaylistEntity>>() {
      @Override
      @NonNull
      public List<PlaylistEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "isDefault");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final int _cursorIndexOfLastPlayedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPlayedAtEpochMillis");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "coverImageUri");
          final int _cursorIndexOfThemeColorArgb = CursorUtil.getColumnIndexOrThrow(_cursor, "themeColorArgb");
          final List<PlaylistEntity> _result = new ArrayList<PlaylistEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final PlaylistEntity _item;
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final boolean _tmpIsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDefault);
            _tmpIsDefault = _tmp != 0;
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            final Long _tmpLastPlayedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAtEpochMillis)) {
              _tmpLastPlayedAtEpochMillis = null;
            } else {
              _tmpLastPlayedAtEpochMillis = _cursor.getLong(_cursorIndexOfLastPlayedAtEpochMillis);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImageUri;
            if (_cursor.isNull(_cursorIndexOfCoverImageUri)) {
              _tmpCoverImageUri = null;
            } else {
              _tmpCoverImageUri = _cursor.getString(_cursorIndexOfCoverImageUri);
            }
            final Integer _tmpThemeColorArgb;
            if (_cursor.isNull(_cursorIndexOfThemeColorArgb)) {
              _tmpThemeColorArgb = null;
            } else {
              _tmpThemeColorArgb = _cursor.getInt(_cursorIndexOfThemeColorArgb);
            }
            _item = new PlaylistEntity(_tmpId,_tmpName,_tmpIsDefault,_tmpCreatedAtEpochMillis,_tmpLastPlayedAtEpochMillis,_tmpDescription,_tmpCoverImageUri,_tmpThemeColorArgb);
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
  public Object getDefaultPlaylist(final Continuation<? super PlaylistEntity> $completion) {
    final String _sql = "SELECT * FROM playlists WHERE isDefault = 1 LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PlaylistEntity>() {
      @Override
      @Nullable
      public PlaylistEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "isDefault");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final int _cursorIndexOfLastPlayedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPlayedAtEpochMillis");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "coverImageUri");
          final int _cursorIndexOfThemeColorArgb = CursorUtil.getColumnIndexOrThrow(_cursor, "themeColorArgb");
          final PlaylistEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final boolean _tmpIsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDefault);
            _tmpIsDefault = _tmp != 0;
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            final Long _tmpLastPlayedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAtEpochMillis)) {
              _tmpLastPlayedAtEpochMillis = null;
            } else {
              _tmpLastPlayedAtEpochMillis = _cursor.getLong(_cursorIndexOfLastPlayedAtEpochMillis);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImageUri;
            if (_cursor.isNull(_cursorIndexOfCoverImageUri)) {
              _tmpCoverImageUri = null;
            } else {
              _tmpCoverImageUri = _cursor.getString(_cursorIndexOfCoverImageUri);
            }
            final Integer _tmpThemeColorArgb;
            if (_cursor.isNull(_cursorIndexOfThemeColorArgb)) {
              _tmpThemeColorArgb = null;
            } else {
              _tmpThemeColorArgb = _cursor.getInt(_cursorIndexOfThemeColorArgb);
            }
            _result = new PlaylistEntity(_tmpId,_tmpName,_tmpIsDefault,_tmpCreatedAtEpochMillis,_tmpLastPlayedAtEpochMillis,_tmpDescription,_tmpCoverImageUri,_tmpThemeColorArgb);
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
  public Object getById(final long id, final Continuation<? super PlaylistEntity> $completion) {
    final String _sql = "SELECT * FROM playlists WHERE id = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, id);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PlaylistEntity>() {
      @Override
      @Nullable
      public PlaylistEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfName = CursorUtil.getColumnIndexOrThrow(_cursor, "name");
          final int _cursorIndexOfIsDefault = CursorUtil.getColumnIndexOrThrow(_cursor, "isDefault");
          final int _cursorIndexOfCreatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtEpochMillis");
          final int _cursorIndexOfLastPlayedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "lastPlayedAtEpochMillis");
          final int _cursorIndexOfDescription = CursorUtil.getColumnIndexOrThrow(_cursor, "description");
          final int _cursorIndexOfCoverImageUri = CursorUtil.getColumnIndexOrThrow(_cursor, "coverImageUri");
          final int _cursorIndexOfThemeColorArgb = CursorUtil.getColumnIndexOrThrow(_cursor, "themeColorArgb");
          final PlaylistEntity _result;
          if (_cursor.moveToFirst()) {
            final long _tmpId;
            _tmpId = _cursor.getLong(_cursorIndexOfId);
            final String _tmpName;
            if (_cursor.isNull(_cursorIndexOfName)) {
              _tmpName = null;
            } else {
              _tmpName = _cursor.getString(_cursorIndexOfName);
            }
            final boolean _tmpIsDefault;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDefault);
            _tmpIsDefault = _tmp != 0;
            final long _tmpCreatedAtEpochMillis;
            _tmpCreatedAtEpochMillis = _cursor.getLong(_cursorIndexOfCreatedAtEpochMillis);
            final Long _tmpLastPlayedAtEpochMillis;
            if (_cursor.isNull(_cursorIndexOfLastPlayedAtEpochMillis)) {
              _tmpLastPlayedAtEpochMillis = null;
            } else {
              _tmpLastPlayedAtEpochMillis = _cursor.getLong(_cursorIndexOfLastPlayedAtEpochMillis);
            }
            final String _tmpDescription;
            if (_cursor.isNull(_cursorIndexOfDescription)) {
              _tmpDescription = null;
            } else {
              _tmpDescription = _cursor.getString(_cursorIndexOfDescription);
            }
            final String _tmpCoverImageUri;
            if (_cursor.isNull(_cursorIndexOfCoverImageUri)) {
              _tmpCoverImageUri = null;
            } else {
              _tmpCoverImageUri = _cursor.getString(_cursorIndexOfCoverImageUri);
            }
            final Integer _tmpThemeColorArgb;
            if (_cursor.isNull(_cursorIndexOfThemeColorArgb)) {
              _tmpThemeColorArgb = null;
            } else {
              _tmpThemeColorArgb = _cursor.getInt(_cursorIndexOfThemeColorArgb);
            }
            _result = new PlaylistEntity(_tmpId,_tmpName,_tmpIsDefault,_tmpCreatedAtEpochMillis,_tmpLastPlayedAtEpochMillis,_tmpDescription,_tmpCoverImageUri,_tmpThemeColorArgb);
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
