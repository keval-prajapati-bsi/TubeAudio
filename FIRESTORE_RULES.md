# Firestore Security Rules -- Party feature

Paste this into the Firebase console: **Firestore Database -> Rules** tab, replacing whatever's
there now, then click **Publish**.

Without these rules (or with the Firestore default "locked" rules), every read/write from the app
will fail with a permission-denied error -- the app's own code enforces host-only actions in the
UI (only the host sees Approve/Reject/reorder/skip buttons), but that's not real security since
anyone could bypass the app; these rules are what actually stop a non-host client from writing
host-only fields even if they tried to.

```
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Each user's own profile (display name/email) -- only they can read or write it.
    match /users/{uid} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }

    match /parties/{code} {
      // Any signed-in user can look up a party by code (needed to join).
      allow read: if request.auth != null;
      // Creating a party: the creator must be named as the host.
      allow create: if request.auth != null && request.resource.data.hostUid == request.auth.uid;
      // Only the host can change what's playing, play/pause, rename the party, etc.
      allow update, delete: if request.auth != null && request.auth.uid == resource.data.hostUid;

      match /members/{uid} {
        allow read: if request.auth != null;
        // Joining: you can only write your own membership doc.
        allow create, update: if request.auth != null && request.auth.uid == uid;
        // Leaving is deleting your own doc; being removed by the host is the host deleting yours.
        allow delete: if request.auth != null
          && (request.auth.uid == uid || request.auth.uid == get(/databases/$(database)/documents/parties/$(code)).data.hostUid);
      }

      match /queue/{itemId} {
        allow read: if request.auth != null;
        // Requesting a song: must be tagged with your own uid, and must land as "pending"
        // UNLESS you're the host (the host's own requests/playlist-adds auto-approve).
        allow create: if request.auth != null
          && request.resource.data.addedByUid == request.auth.uid
          && (
            request.resource.data.status == 'pending' ||
            request.auth.uid == get(/databases/$(database)/documents/parties/$(code)).data.hostUid
          );
        // Approve/reject/reorder/consume-on-advance: host only.
        allow update, delete: if request.auth != null
          && request.auth.uid == get(/databases/$(database)/documents/parties/$(code)).data.hostUid;
      }
    }
  }
}
```

## Why no Firestore indexes are needed

`PartyRepository.observeQueue()` deliberately fetches the whole queue subcollection with no
`orderBy` clause and sorts pending/approved lists client-side in Kotlin instead -- ordering by two
different fields depending on status would need a Firestore composite index, which means an extra
manual "create index" step in the console. Skipped on purpose to keep setup to just this one rules
paste.
