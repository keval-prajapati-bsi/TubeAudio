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
public final class TrackDao_Impl implements TrackDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<TrackEntity> __insertionAdapterOfTrackEntity;

  private final SharedSQLiteStatement __preparedStmtOfDelete;

  private final SharedSQLiteStatement __preparedStmtOfMarkDownloaded;

  private final SharedSQLiteStatement __preparedStmtOfClearDownload;

  private final SharedSQLiteStatement __preparedStmtOfSetLiked;

  public TrackDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfTrackEntity = new EntityInsertionAdapter<TrackEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `tracks` (`videoId`,`url`,`title`,`thumbnailUrl`,`durationSeconds`,`addedAtEpochMillis`,`isDownloaded`,`localFilePath`,`isLiked`) VALUES (?,?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final TrackEntity entity) {
        if (entity.getVideoId() == null) {
          statement.bindNull(1);
        } else {
          statement.bindString(1, entity.getVideoId());
        }
        if (entity.getUrl() == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.getUrl());
        }
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.getTitle());
        }
        if (entity.getThumbnailUrl() == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.getThumbnailUrl());
        }
        statement.bindLong(5, entity.getDurationSeconds());
        statement.bindLong(6, entity.getAddedAtEpochMillis());
        final int _tmp = entity.isDownloaded() ? 1 : 0;
        statement.bindLong(7, _tmp);
        if (entity.getLocalFilePath() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLocalFilePath());
        }
        final int _tmp_1 = entity.isLiked() ? 1 : 0;
        statement.bindLong(9, _tmp_1);
      }
    };
    this.__preparedStmtOfDelete = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM tracks WHERE videoId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkDownloaded = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tracks SET isDownloaded = 1, localFilePath = ? WHERE videoId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfClearDownload = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tracks SET isDownloaded = 0, localFilePath = NULL WHERE videoId = ?";
        return _query;
      }
    };
    this.__preparedStmtOfSetLiked = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE tracks SET isLiked = ? WHERE videoId = ?";
        return _query;
      }
    };
  }

  @Override
  public Object upsert(final TrackEntity track, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfTrackEntity.insert(track);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object delete(final String videoId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDelete.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfDelete.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markDownloaded(final String videoId, final String path,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkDownloaded.acquire();
        int _argIndex = 1;
        if (path == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindString(_argIndex, path);
        }
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
          __preparedStmtOfMarkDownloaded.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object clearDownload(final String videoId, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfClearDownload.acquire();
        int _argIndex = 1;
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
          __preparedStmtOfClearDownload.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object setLiked(final String videoId, final boolean liked,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfSetLiked.acquire();
        int _argIndex = 1;
        final int _tmp = liked ? 1 : 0;
        _stmt.bindLong(_argIndex, _tmp);
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
          __preparedStmtOfSetLiked.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object getByVideoId(final String videoId,
      final Continuation<? super TrackEntity> $completion) {
    final String _sql = "SELECT * FROM tracks WHERE videoId = ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (videoId == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, videoId);
    }
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<TrackEntity>() {
      @Override
      @Nullable
      public TrackEntity call() throws Exception {
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
          final TrackEntity _result;
          if (_cursor.moveToFirst()) {
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
            _result = new TrackEntity(_tmpVideoId,_tmpUrl,_tmpTitle,_tmpThumbnailUrl,_tmpDurationSeconds,_tmpAddedAtEpochMillis,_tmpIsDownloaded,_tmpLocalFilePath,_tmpIsLiked);
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
  public Flow<List<TrackEntity>> search(final String query) {
    final String _sql = "SELECT * FROM tracks WHERE title LIKE '%' || ? || '%' ORDER BY addedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    if (query == null) {
      _statement.bindNull(_argIndex);
    } else {
      _statement.bindString(_argIndex, query);
    }
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
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
  public Flow<List<TrackEntity>> observeDownloaded() {
    final String _sql = "SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY addedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
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
  public Object getDownloadedOnce(final Continuation<? super List<TrackEntity>> $completion) {
    final String _sql = "SELECT * FROM tracks WHERE isDownloaded = 1 ORDER BY addedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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
  public Flow<List<TrackEntity>> observeLiked() {
    final String _sql = "SELECT * FROM tracks WHERE isLiked = 1 ORDER BY addedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"tracks"}, new Callable<List<TrackEntity>>() {
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
  public Object getLikedOnce(final Continuation<? super List<TrackEntity>> $completion) {
    final String _sql = "SELECT * FROM tracks WHERE isLiked = 1 ORDER BY addedAtEpochMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
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

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
