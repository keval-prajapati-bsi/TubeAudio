package com.vibesync.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class PlaybackStateDao_Impl implements PlaybackStateDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlaybackStateEntity> __insertionAdapterOfPlaybackStateEntity;

  public PlaybackStateDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlaybackStateEntity = new EntityInsertionAdapter<PlaybackStateEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `playback_state` (`id`,`videoId`,`queuePlaylistId`,`queueIndex`,`positionMs`,`updatedAtEpochMillis`) VALUES (?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlaybackStateEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getVideoId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getVideoId());
        }
        statement.bindLong(3, entity.getQueuePlaylistId());
        statement.bindLong(4, entity.getQueueIndex());
        statement.bindLong(5, entity.getPositionMs());
        statement.bindLong(6, entity.getUpdatedAtEpochMillis());
      }
    };
  }

  @Override
  public Object save(final PlaybackStateEntity state,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPlaybackStateEntity.insert(state);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object get(final Continuation<? super PlaybackStateEntity> $completion) {
    final String _sql = "SELECT * FROM playback_state WHERE id = 0";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<PlaybackStateEntity>() {
      @Override
      @Nullable
      public PlaybackStateEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "videoId");
          final int _cursorIndexOfQueuePlaylistId = CursorUtil.getColumnIndexOrThrow(_cursor, "queuePlaylistId");
          final int _cursorIndexOfQueueIndex = CursorUtil.getColumnIndexOrThrow(_cursor, "queueIndex");
          final int _cursorIndexOfPositionMs = CursorUtil.getColumnIndexOrThrow(_cursor, "positionMs");
          final int _cursorIndexOfUpdatedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "updatedAtEpochMillis");
          final PlaybackStateEntity _result;
          if (_cursor.moveToFirst()) {
            final int _tmpId;
            _tmpId = _cursor.getInt(_cursorIndexOfId);
            final String _tmpVideoId;
            if (_cursor.isNull(_cursorIndexOfVideoId)) {
              _tmpVideoId = null;
            } else {
              _tmpVideoId = _cursor.getString(_cursorIndexOfVideoId);
            }
            final long _tmpQueuePlaylistId;
            _tmpQueuePlaylistId = _cursor.getLong(_cursorIndexOfQueuePlaylistId);
            final int _tmpQueueIndex;
            _tmpQueueIndex = _cursor.getInt(_cursorIndexOfQueueIndex);
            final long _tmpPositionMs;
            _tmpPositionMs = _cursor.getLong(_cursorIndexOfPositionMs);
            final long _tmpUpdatedAtEpochMillis;
            _tmpUpdatedAtEpochMillis = _cursor.getLong(_cursorIndexOfUpdatedAtEpochMillis);
            _result = new PlaybackStateEntity(_tmpId,_tmpVideoId,_tmpQueuePlaylistId,_tmpQueueIndex,_tmpPositionMs,_tmpUpdatedAtEpochMillis);
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
