package com.vibesync.app.data.db;

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
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile TrackDao _trackDao;

  private volatile PlaylistDao _playlistDao;

  private volatile PlaylistTrackDao _playlistTrackDao;

  private volatile PlaybackStateDao _playbackStateDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(4) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `tracks` (`videoId` TEXT NOT NULL, `url` TEXT NOT NULL, `title` TEXT NOT NULL, `thumbnailUrl` TEXT, `durationSeconds` INTEGER NOT NULL, `addedAtEpochMillis` INTEGER NOT NULL, `isDownloaded` INTEGER NOT NULL, `localFilePath` TEXT, `isLiked` INTEGER NOT NULL, PRIMARY KEY(`videoId`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `playlists` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `isDefault` INTEGER NOT NULL, `createdAtEpochMillis` INTEGER NOT NULL, `lastPlayedAtEpochMillis` INTEGER, `description` TEXT, `coverImageUri` TEXT, `themeColorArgb` INTEGER)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `playlist_tracks` (`playlistId` INTEGER NOT NULL, `videoId` TEXT NOT NULL, `position` INTEGER NOT NULL, `addedAtEpochMillis` INTEGER NOT NULL, PRIMARY KEY(`playlistId`, `videoId`), FOREIGN KEY(`playlistId`) REFERENCES `playlists`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE , FOREIGN KEY(`videoId`) REFERENCES `tracks`(`videoId`) ON UPDATE NO ACTION ON DELETE CASCADE )");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_tracks_videoId` ON `playlist_tracks` (`videoId`)");
        db.execSQL("CREATE INDEX IF NOT EXISTS `index_playlist_tracks_playlistId` ON `playlist_tracks` (`playlistId`)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `playback_state` (`id` INTEGER NOT NULL, `videoId` TEXT NOT NULL, `queuePlaylistId` INTEGER NOT NULL, `queueIndex` INTEGER NOT NULL, `positionMs` INTEGER NOT NULL, `updatedAtEpochMillis` INTEGER NOT NULL, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '965d9e26f7a90868044606e0ad58e2ea')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `tracks`");
        db.execSQL("DROP TABLE IF EXISTS `playlists`");
        db.execSQL("DROP TABLE IF EXISTS `playlist_tracks`");
        db.execSQL("DROP TABLE IF EXISTS `playback_state`");
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
        db.execSQL("PRAGMA foreign_keys = ON");
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
        final HashMap<String, TableInfo.Column> _columnsTracks = new HashMap<String, TableInfo.Column>(9);
        _columnsTracks.put("videoId", new TableInfo.Column("videoId", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("url", new TableInfo.Column("url", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("thumbnailUrl", new TableInfo.Column("thumbnailUrl", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("durationSeconds", new TableInfo.Column("durationSeconds", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("addedAtEpochMillis", new TableInfo.Column("addedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("isDownloaded", new TableInfo.Column("isDownloaded", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("localFilePath", new TableInfo.Column("localFilePath", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTracks.put("isLiked", new TableInfo.Column("isLiked", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysTracks = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesTracks = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTracks = new TableInfo("tracks", _columnsTracks, _foreignKeysTracks, _indicesTracks);
        final TableInfo _existingTracks = TableInfo.read(db, "tracks");
        if (!_infoTracks.equals(_existingTracks)) {
          return new RoomOpenHelper.ValidationResult(false, "tracks(com.vibesync.app.data.db.TrackEntity).\n"
                  + " Expected:\n" + _infoTracks + "\n"
                  + " Found:\n" + _existingTracks);
        }
        final HashMap<String, TableInfo.Column> _columnsPlaylists = new HashMap<String, TableInfo.Column>(8);
        _columnsPlaylists.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("isDefault", new TableInfo.Column("isDefault", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("createdAtEpochMillis", new TableInfo.Column("createdAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("lastPlayedAtEpochMillis", new TableInfo.Column("lastPlayedAtEpochMillis", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("description", new TableInfo.Column("description", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("coverImageUri", new TableInfo.Column("coverImageUri", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylists.put("themeColorArgb", new TableInfo.Column("themeColorArgb", "INTEGER", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlaylists = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlaylists = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlaylists = new TableInfo("playlists", _columnsPlaylists, _foreignKeysPlaylists, _indicesPlaylists);
        final TableInfo _existingPlaylists = TableInfo.read(db, "playlists");
        if (!_infoPlaylists.equals(_existingPlaylists)) {
          return new RoomOpenHelper.ValidationResult(false, "playlists(com.vibesync.app.data.db.PlaylistEntity).\n"
                  + " Expected:\n" + _infoPlaylists + "\n"
                  + " Found:\n" + _existingPlaylists);
        }
        final HashMap<String, TableInfo.Column> _columnsPlaylistTracks = new HashMap<String, TableInfo.Column>(4);
        _columnsPlaylistTracks.put("playlistId", new TableInfo.Column("playlistId", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylistTracks.put("videoId", new TableInfo.Column("videoId", "TEXT", true, 2, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylistTracks.put("position", new TableInfo.Column("position", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaylistTracks.put("addedAtEpochMillis", new TableInfo.Column("addedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlaylistTracks = new HashSet<TableInfo.ForeignKey>(2);
        _foreignKeysPlaylistTracks.add(new TableInfo.ForeignKey("playlists", "CASCADE", "NO ACTION", Arrays.asList("playlistId"), Arrays.asList("id")));
        _foreignKeysPlaylistTracks.add(new TableInfo.ForeignKey("tracks", "CASCADE", "NO ACTION", Arrays.asList("videoId"), Arrays.asList("videoId")));
        final HashSet<TableInfo.Index> _indicesPlaylistTracks = new HashSet<TableInfo.Index>(2);
        _indicesPlaylistTracks.add(new TableInfo.Index("index_playlist_tracks_videoId", false, Arrays.asList("videoId"), Arrays.asList("ASC")));
        _indicesPlaylistTracks.add(new TableInfo.Index("index_playlist_tracks_playlistId", false, Arrays.asList("playlistId"), Arrays.asList("ASC")));
        final TableInfo _infoPlaylistTracks = new TableInfo("playlist_tracks", _columnsPlaylistTracks, _foreignKeysPlaylistTracks, _indicesPlaylistTracks);
        final TableInfo _existingPlaylistTracks = TableInfo.read(db, "playlist_tracks");
        if (!_infoPlaylistTracks.equals(_existingPlaylistTracks)) {
          return new RoomOpenHelper.ValidationResult(false, "playlist_tracks(com.vibesync.app.data.db.PlaylistTrackCrossRef).\n"
                  + " Expected:\n" + _infoPlaylistTracks + "\n"
                  + " Found:\n" + _existingPlaylistTracks);
        }
        final HashMap<String, TableInfo.Column> _columnsPlaybackState = new HashMap<String, TableInfo.Column>(6);
        _columnsPlaybackState.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaybackState.put("videoId", new TableInfo.Column("videoId", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaybackState.put("queuePlaylistId", new TableInfo.Column("queuePlaylistId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaybackState.put("queueIndex", new TableInfo.Column("queueIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaybackState.put("positionMs", new TableInfo.Column("positionMs", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsPlaybackState.put("updatedAtEpochMillis", new TableInfo.Column("updatedAtEpochMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysPlaybackState = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesPlaybackState = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoPlaybackState = new TableInfo("playback_state", _columnsPlaybackState, _foreignKeysPlaybackState, _indicesPlaybackState);
        final TableInfo _existingPlaybackState = TableInfo.read(db, "playback_state");
        if (!_infoPlaybackState.equals(_existingPlaybackState)) {
          return new RoomOpenHelper.ValidationResult(false, "playback_state(com.vibesync.app.data.db.PlaybackStateEntity).\n"
                  + " Expected:\n" + _infoPlaybackState + "\n"
                  + " Found:\n" + _existingPlaybackState);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "965d9e26f7a90868044606e0ad58e2ea", "05bf70dd8750456ccc700c885f8be109");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "tracks","playlists","playlist_tracks","playback_state");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    final boolean _supportsDeferForeignKeys = android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP;
    try {
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = FALSE");
      }
      super.beginTransaction();
      if (_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA defer_foreign_keys = TRUE");
      }
      _db.execSQL("DELETE FROM `tracks`");
      _db.execSQL("DELETE FROM `playlists`");
      _db.execSQL("DELETE FROM `playlist_tracks`");
      _db.execSQL("DELETE FROM `playback_state`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      if (!_supportsDeferForeignKeys) {
        _db.execSQL("PRAGMA foreign_keys = TRUE");
      }
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
    _typeConvertersMap.put(TrackDao.class, TrackDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlaylistDao.class, PlaylistDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlaylistTrackDao.class, PlaylistTrackDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(PlaybackStateDao.class, PlaybackStateDao_Impl.getRequiredConverters());
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
  public TrackDao trackDao() {
    if (_trackDao != null) {
      return _trackDao;
    } else {
      synchronized(this) {
        if(_trackDao == null) {
          _trackDao = new TrackDao_Impl(this);
        }
        return _trackDao;
      }
    }
  }

  @Override
  public PlaylistDao playlistDao() {
    if (_playlistDao != null) {
      return _playlistDao;
    } else {
      synchronized(this) {
        if(_playlistDao == null) {
          _playlistDao = new PlaylistDao_Impl(this);
        }
        return _playlistDao;
      }
    }
  }

  @Override
  public PlaylistTrackDao playlistTrackDao() {
    if (_playlistTrackDao != null) {
      return _playlistTrackDao;
    } else {
      synchronized(this) {
        if(_playlistTrackDao == null) {
          _playlistTrackDao = new PlaylistTrackDao_Impl(this);
        }
        return _playlistTrackDao;
      }
    }
  }

  @Override
  public PlaybackStateDao playbackStateDao() {
    if (_playbackStateDao != null) {
      return _playbackStateDao;
    } else {
      synchronized(this) {
        if(_playbackStateDao == null) {
          _playbackStateDao = new PlaybackStateDao_Impl(this);
        }
        return _playbackStateDao;
      }
    }
  }
}
