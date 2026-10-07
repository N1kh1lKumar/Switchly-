# Delete Flag by ID API

## Overview

Added an API to delete an existing feature flag using its unique `flagId`.

The implementation follows the existing Switchly layered architecture:

```text
FlagController
      ↓
FlagService
      ↓
FlagRepository
      ↓
InMemoryFlagRepository
```

The flag is first checked for existence. If the flag exists, it is removed from the in-memory storage. If it does not exist, the existing `NotFoundException` handling is used.

---

## API

### Delete Flag by ID

**HTTP Method:**

```http
DELETE
```

**Endpoint:**

```http
/api/v1/flags/{flagId}
```

### Example Request

```http
DELETE /api/v1/flags/550e8400-e29b-41d4-a716-446655440000
```

The `flagId` must be the UUID of the flag that needs to be deleted.

---

## Response

### Successful Deletion

If the flag exists and is successfully deleted:

```http
204 No Content
```

No response body is returned.

### Flag Not Found

If the provided `flagId` does not exist:

```http
404 Not Found
```

The existing `NotFoundException` mechanism handles this case.

---

# Files Changed

The following files were updated to implement this feature.

## 1. `FlagController.java`

### Change

Added a new `DELETE` endpoint:

```java
@DeleteMapping("/flags/{flagId}")
@ResponseStatus(HttpStatus.NO_CONTENT)
public void deleteById(@PathVariable UUID flagId) {
    flagService.deleteById(flagId);
}
```

### Responsibility

The controller:

- Receives the `flagId` from the request path.
- Passes the ID to `FlagService`.
- Returns `204 No Content` after successful deletion.

---

## 2. `FlagService.java`

### Change

Added the `deleteById()` method:

```java
public void deleteById(UUID id) {
    getById(id);
    flagRepository.deleteById(id);
}
```

### Responsibility

The service layer:

1. Checks whether the flag exists using the existing `getById()` method.
2. Throws `NotFoundException` if the flag does not exist.
3. Calls the repository to delete the flag if it exists.

This keeps the deletion logic inside the service layer.

---

## 3. `FlagRepository.java`

### Change

Added the delete operation to the repository interface:

```java
void deleteById(UUID id);
```

### Responsibility

This defines the contract that repository implementations must follow for deleting a flag by its ID.

---

## 4. `InMemoryFlagRepository.java`

### Change

Implemented the `deleteById()` method:

```java
@Override
public void deleteById(UUID id) {
    store.remove(id);
}
```

### Responsibility

The project currently uses in-memory storage. The method removes the flag associated with the provided UUID from the internal `ConcurrentHashMap`.

---

# Request Flow

When a client sends a delete request:

```text
DELETE /api/v1/flags/{flagId}
              │
              ▼
       FlagController
              │
              ▼
        FlagService
              │
        ┌─────┴─────┐
        │           │
   Flag exists?    No
        │           │
       Yes          ▼
        │      NotFoundException
        ▼           │
FlagRepository      ▼
        │       404 Not Found
        ▼
InMemoryFlagRepository
        │
        ▼
   Remove Flag
        │
        ▼
  204 No Content
```

---

# Implementation Summary

| File | Change |
|---|---|
| `FlagController.java` | Added `DELETE /api/v1/flags/{flagId}` endpoint |
| `FlagService.java` | Added flag existence check and delete logic |
| `FlagRepository.java` | Added `deleteById(UUID id)` method |
| `InMemoryFlagRepository.java` | Implemented deletion from in-memory storage |

---

# API Behavior

| Scenario | Response |
|---|---|
| Flag exists | `204 No Content` |
| Flag does not exist | `404 Not Found` |
| Successful deletion | Flag removed from in-memory storage |

---

## Feature Added

The Switchly API now supports deleting a feature flag by its unique ID through:

```http
DELETE /api/v1/flags/{flagId}
```

The implementation maintains the existing layered architecture and reuses the project's existing exception-handling mechanism for missing flags.