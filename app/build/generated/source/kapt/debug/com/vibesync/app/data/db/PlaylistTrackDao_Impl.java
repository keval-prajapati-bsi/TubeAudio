package com.vibesync.app.data.db;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomDatabaseKt;
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
public final class PlaylistTrackDao_Impl implements PlaylistTrackDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<PlaylistTrackCrossRef> __insertionAdapterOfPlaylistTrackCrossRef;

  private final SharedSQLiteStatement __preparedStmtOfShiftAllUp;

  private final SharedSQLiteStatement __preparedStmtOfRemove;

  private final SharedSQLiteStatement __preparedStmtOfUpdatePosition;

  public PlaylistTrackDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfPlaylistTrackCrossRef = new EntityInsertionAdapter<PlaylistTrackCrossRef>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR IGNORE INTO `playlist_tracks` (`playlistId`,`videoId`,`position`,`addedAtEpochMillis`) VALUES (?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final PlaylistTrackCrossRef entity) {
        statement.bindLong(1, entity.getPlaylistId());
        if (entity.getVideoId() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getVideoId());
        }
        statement.bindLong(3, entity.getPosition());
        statement.bindLong(4, entity.getAddedAtEpochMillis());
      }
    };
    this.__preparedStmtOfShiftAllUp = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE playlist_tracks SET position = position + 1 WHERE playlistId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfRemove = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM playlist_tracks WHERE playlistId = ? AND videoId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfUpdatePosition = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE playlist_tracks SET position = ? WHERE playlistId = ? AND videoId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final PlaylistTrackCrossRef crossRef,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfPlaylistTrackCrossRef.insert(crossRef);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object prependToDefault(final long playlistId, final String videoId,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> PlaylistTrackDao.DefaultImpls.prependToDefault(PlaylistTrackDao_Impl.this, playlistId, videoId, __cont), $completion);
  }

  @Override
  public Object appendToPlaylist(final long playlistId, final String videoId,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> PlaylistTrackDao.DefaultImpls.appendToPlaylist(PlaylistTrackDao_Impl.this, playlistId, videoId, __cont), $completion);
  }

  @Override
  public Object reorder(final long playlistId, final List<String> orderedVideoIds,
      final Continuation<? super Unit> $completion) {
    return RoomDatabaseKt.withTransaction(__db, (__cont) -> PlaylistTrackDao.DefaultImpls.reorder(PlaylistTrackDao_Impl.this, playlistId, orderedVideoIds, __cont), $completion);
  }

  @Override
  public Object shiftAllUp(final long playlistId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfShiftAllUp.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, playlistId);
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
          __preparedStmtOfShiftAllUp.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object remove(final long playlistId, final String videoId,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfRemove.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, playlistId);
        _argIndex = 2;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, videoId);
        }
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
          __preparedStmtOfRemove.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object updatePosition(final long playlistId, final String videoId, final int position,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfUpdatePosition.acquire();
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, position);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, playlistId);
        _argIndex = 3;
        if (videoId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, videoId);
        }
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
          __preparedStmtOfUpdatePosition.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<TrackEntity>> observeTracksForPlaylist(final long playlistId) {
    final String _sql = "\n"
            + "        SELECT tracks.* FROM tracks\n"
            + "        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId\n"
            + "        WHERE playlist_tracks.playlistId = ?\n"
            + "        ORDER BY playlist_tracks.position ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playlistId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks",
        "playlist_tracks"}, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "videoId");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "durationSeconds");
          final int _cursorIndexOfAddedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAtEpochMillis");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfLocalFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "localFilePath");
          final int _cursorIndexOfIsLiked = CursorUtil.getColumnIndexOrThrow(_cursor, "isLiked");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final String _tmpVideoId;
            if (_cursor.isNull(_cursorIndexOfVideoId)) {
              _tmpVideoId = null;
            } else {
              _tmpVideoId = _cursor.getString(_cursorIndexOfVideoId);
            }
            final String _tmpUrl;
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final long _tmpDurationSeconds;
            _tmpDurationSeconds = _cursor.getLong(_cursorIndexOfDurationSeconds);
            final long _tmpAddedAtEpochMillis;
            _tmpAddedAtEpochMillis = _cursor.getLong(_cursorIndexOfAddedAtEpochMillis);
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final String _tmpLocalFilePath;
            if (_cursor.isNull(_cursorIndexOfLocalFilePath)) {
              _tmpLocalFilePath = null;
            } else {
              _tmpLocalFilePath = _cursor.getString(_cursorIndexOfLocalFilePath);
            }
            final boolean _tmpIsLiked;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsLiked);
            _tmpIsLiked = _tmp_1 != 0;
            _item = new TrackEntity(_tmpVideoId,_tmpUrl,_tmpTitle,_tmpThumbnailUrl,_tmpDurationSeconds,_tmpAddedAtEpochMillis,_tmpIsDownloaded,_tmpLocalFilePath,_tmpIsLiked);
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
  public Object getTracksForPlaylistOnce(final long playlistId,
      final Continuation<? super List<TrackEntity>> $completion) {
    final String _sql = "\n"
            + "        SELECT tracks.* FROM tracks\n"
            + "        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId\n"
            + "        WHERE playlist_tracks.playlistId = ?\n"
            + "        ORDER BY playlist_tracks.position ASC\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playlistId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<TrackEntity>>() {
      @Override
      @NonNull
      public List<TrackEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfVideoId = CursorUtil.getColumnIndexOrThrow(_cursor, "videoId");
          final int _cursorIndexOfUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "url");
          final int _cursorIndexOfTitle = CursorUtil.getColumnIndexOrThrow(_cursor, "title");
          final int _cursorIndexOfThumbnailUrl = CursorUtil.getColumnIndexOrThrow(_cursor, "thumbnailUrl");
          final int _cursorIndexOfDurationSeconds = CursorUtil.getColumnIndexOrThrow(_cursor, "durationSeconds");
          final int _cursorIndexOfAddedAtEpochMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "addedAtEpochMillis");
          final int _cursorIndexOfIsDownloaded = CursorUtil.getColumnIndexOrThrow(_cursor, "isDownloaded");
          final int _cursorIndexOfLocalFilePath = CursorUtil.getColumnIndexOrThrow(_cursor, "localFilePath");
          final int _cursorIndexOfIsLiked = CursorUtil.getColumnIndexOrThrow(_cursor, "isLiked");
          final List<TrackEntity> _result = new ArrayList<TrackEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final TrackEntity _item;
            final String _tmpVideoId;
            if (_cursor.isNull(_cursorIndexOfVideoId)) {
              _tmpVideoId = null;
            } else {
              _tmpVideoId = _cursor.getString(_cursorIndexOfVideoId);
            }
            final String _tmpUrl;
            if (_cursor.isNull(_cursorIndexOfUrl)) {
              _tmpUrl = null;
            } else {
              _tmpUrl = _cursor.getString(_cursorIndexOfUrl);
            }
            final String _tmpTitle;
            if (_cursor.isNull(_cursorIndexOfTitle)) {
              _tmpTitle = null;
            } else {
              _tmpTitle = _cursor.getString(_cursorIndexOfTitle);
            }
            final String _tmpThumbnailUrl;
            if (_cursor.isNull(_cursorIndexOfThumbnailUrl)) {
              _tmpThumbnailUrl = null;
            } else {
              _tmpThumbnailUrl = _cursor.getString(_cursorIndexOfThumbnailUrl);
            }
            final long _tmpDurationSeconds;
            _tmpDurationSeconds = _cursor.getLong(_cursorIndexOfDurationSeconds);
            final long _tmpAddedAtEpochMillis;
            _tmpAddedAtEpochMillis = _cursor.getLong(_cursorIndexOfAddedAtEpochMillis);
            final boolean _tmpIsDownloaded;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfIsDownloaded);
            _tmpIsDownloaded = _tmp != 0;
            final String _tmpLocalFilePath;
            if (_cursor.isNull(_cursorIndexOfLocalFilePath)) {
              _tmpLocalFilePath = null;
            } else {
              _tmpLocalFilePath = _cursor.getString(_cursorIndexOfLocalFilePath);
            }
            final boolean _tmpIsLiked;
            final int _tmp_1;
            _tmp_1 = _cursor.getInt(_cursorIndexOfIsLiked);
            _tmpIsLiked = _tmp_1 != 0;
            _item = new TrackEntity(_tmpVideoId,_tmpUrl,_tmpTitle,_tmpThumbnailUrl,_tmpDurationSeconds,_tmpAddedAtEpochMillis,_tmpIsDownloaded,_tmpLocalFilePath,_tmpIsLiked);
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
  public Flow<Integer> observeTrackCount(final long playlistId) {
    final String _sql = "SELECT COUNT(*) FROM playlist_tracks WHERE playlistId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playlistId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"playlist_tracks"}, new Callable<Integer>() {
      @Override
      @NonNull
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
          } else {
            _result = null;
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
  public Flow<String> observeCoverThumbnail(final long playlistId) {
    final String _sql = "\n"
            + "        SELECT tracks.thumbnailUrl FROM tracks\n"
            + "        INNER JOIN playlist_tracks ON tracks.videoId = playlist_tracks.videoId\n"
            + "        WHERE playlist_tracks.playlistId = ?\n"
            + "        ORDER BY playlist_tracks.position ASC\n"
            + "        LIMIT 1\n"
            + "        ";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playlistId);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks",
        "playlist_tracks"}, new Callable<String>() {
      @Override
      @Nullable
      public String call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final String _result;
          if (_cursor.moveToFirst()) {
            if (_cursor.isNull(0)) {
              _result = null;
            } else {
              _result = _cursor.getString(0);
            }
          } else {
            _result = null;
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
  public Object maxPosition(final long playlistId,
      final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT MAX(position) FROM playlist_tracks WHERE playlistId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, playlistId);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<Integer>() {
      @Override
      @Nullable
      public Integer call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final Integer _result;
          if (_cursor.moveToFirst()) {
            final Integer _tmp;
            if (_cursor.isNull(0)) {
              _tmp = null;
            } else {
              _tmp = _cursor.getInt(0);
            }
            _result = _tmp;
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
  public Object playlistIdsForTrack(final String videoId,
      final Continuation<? super List<Long>> $completion) {
    final String _sql = "SELECT playlistId FROM playlist_tracks WHERE videoId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (videoId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, videoId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<Long>>() {
      @Override
      @NonNull
      public List<Long> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final List<Long> _result = new ArrayList<Long>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final Long _item;
            if (_cursor.isNull(0)) {
              _item = null;
            } else {
              _item = _cursor.getLong(0);
            }
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
