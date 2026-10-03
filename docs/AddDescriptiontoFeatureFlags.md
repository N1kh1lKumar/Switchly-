# Session 2 Task — Add  description field on feature flags

**Previous:** [Session 1 — What are we building?](01-what-are-we-building.md)

By the end of this session, you'll create an **organization**, a **project** inside it, and a **feature flag** inside that — and turn the flag on and off — all through your own API.

Everything lives in memory for now. No database yet.

---

## API

| Method | URL | What it does |
| ------ | --- | ------------ |
| `POST` | `/api/v1/orgs` | Create an organization |
| `GET` | `/api/v1/orgs` | List organizations |
| `GET` | `/api/v1/orgs/{orgId}` | Get one organization |
| `POST` | `/api/v1/orgs/{orgId}/projects` | Create a project in an organization |
| `GET` | `/api/v1/orgs/{orgId}/projects` | List an organization's projects |
| `GET` | `/api/v1/projects/{projectId}` | Get one project |
| `POST` | `/api/v1/projects/{projectId}/flags` | Create a flag in a project |
| `GET` | `/api/v1/projects/{projectId}/flags` | List a project's flags |
| `GET` | `/api/v1/flags/{flagId}` | Get one flag |
| `PUT` | `/api/v1/flags/{flagId}/state` | Turn a flag on or off |
| `PATCH` | `/api/v1/flags/{flagId}/description` | Update a flag's description |

---

# Feature Update — Flag Description

This update adds support for a **description** field on feature flags.

A flag can now contain:

- `key` — unique identifier of the flag inside a project
- `name` — human-readable name
- `description` — explanation of what the flag is used for
- `enabled` — whether the flag is currently enabled

The description can be updated independently without changing the flag's state or other properties.

---

## Step 1 — Update the Flag model

### `model/Flag.java`

The `Flag` model was updated to include a `description` field.

The description is stored along with the other flag information.

The model now contains:

```text
Flag
├── id
├── organizationId
├── projectId
├── key
├── name
├── description
└── enabled
```

The existing flag state functionality remains unchanged.

---

## Step 2 — Add the request DTO

### `dto/UpdateFlagDescriptionRequest.java`

A new DTO was created for requests that update the flag description.

```java
package live.switchly.api.dto;

public record UpdateFlagDescriptionRequest(String description) {}
```

The request body contains:

```json
{
  "description": "Enable the new checkout experience"
}
```

The DTO keeps the request body separate from the `Flag` model.

---

## Step 3 — Update the repository

### `repository/FlagRepository.java`

The existing flag repository continues to handle storing and retrieving `Flag` objects.

No separate repository is required for descriptions because the description belongs to the flag itself.

---

### `repository/InMemoryFlagRepository.java`

The in-memory repository continues to store the updated `Flag` object.

Since the project currently uses an in-memory `Map`, updating the flag updates the object stored for that flag.

---

## Step 4 — Update the service

### `service/FlagService.java`

The flag service contains the business logic for updating the description.

The flow is:

```text
Request
   │
   ▼
FlagController
   │
   ▼
FlagService
   │
   ├── Find flag by ID
   │
   ├── Update description
   │
   ▼
FlagRepository
   │
   ▼
Updated Flag
```

The service first finds the flag using its ID.

If the flag does not exist, the existing `NotFoundException` is used and the API returns `404 Not Found`.

---

## Step 5 — Add the new API

### `controller/FlagController.java`

A new endpoint was added:

```http
PATCH /api/v1/flags/{flagId}/description
```

The endpoint accepts the flag ID as a path parameter and the new description in the request body.

### Request

```json
{
  "description": "Enable the new checkout experience"
}
```

### Example

```bash
curl -i -X PATCH \
  http://localhost:8080/api/v1/flags/PASTE_FLAG_ID/description \
  -H "Content-Type: application/json" \
  -d '{"description":"Enable the new checkout experience"}'
```

### Path Parameter

| Parameter | Type | Description |
| --------- | ---- | ----------- |
| `flagId` | UUID | ID of the feature flag |

### Request Body

| Field | Type | Description |
| ----- | ---- | ----------- |
| `description` | String | Description of what the feature flag is used for |

---

## Step 6 — Example API flow

First create a flag:

```bash
curl -X POST http://localhost:8080/api/v1/projects/PASTE_PROJECT_ID/flags \
  -H "Content-Type: application/json" \
  -d '{"key":"new-checkout","name":"New Checkout"}'
```

The flag is initially created with its default description value.

Then update the description:

```bash
curl -X PATCH \
  http://localhost:8080/api/v1/flags/PASTE_FLAG_ID/description \
  -H "Content-Type: application/json" \
  -d '{"description":"Enables the redesigned checkout experience"}'
```

Finally, retrieve the flag:

```bash
curl http://localhost:8080/api/v1/flags/PASTE_FLAG_ID
```

The response now contains the description:

```json
{
  "id": "PASTE_FLAG_ID",
  "organizationId": "PASTE_ORG_ID",
  "projectId": "PASTE_PROJECT_ID",
  "key": "new-checkout",
  "name": "New Checkout",
  "description": "Enables the redesigned checkout experience",
  "enabled": false
}
```

---

## Step 7 — Files created

The following new file was added:

```text
dto/
└── UpdateFlagDescriptionRequest.java
```

This DTO represents the request body used by the new description API.

---

## Step 8 — Files modified

The feature requires changes to the existing flag implementation.

```text
model/
└── Flag.java

service/
└── FlagService.java

controller/
└── FlagController.java

repository/
└── FlagRepository.java
└── InMemoryFlagRepository.java
```

The exact implementation changes are limited to adding support for the description field and updating it through the new API.

---

## Step 9 — Existing functionality

The existing flag APIs continue to work:

### Create flag

```http
POST /api/v1/projects/{projectId}/flags
```

### List project flags

```http
GET /api/v1/projects/{projectId}/flags
```

### Get flag

```http
GET /api/v1/flags/{flagId}
```

### Update flag state

```http
PUT /api/v1/flags/{flagId}/state
```

### Update flag description

```http
PATCH /api/v1/flags/{flagId}/description
```

The new description functionality does not replace or change the existing state API.

---

## Step 10 — Error handling

The existing global exception handling is reused.

### Flag does not exist

```http
PATCH /api/v1/flags/{unknownFlagId}/description
```

Returns:

```http
404 Not Found
```

with the existing error structure:

```json
{
  "error": {
    "code": "NOT_FOUND",
    "message": "Flag <id> not found"
  }
}
```

---

## The whole flow

```text
HTTP PATCH request
       │
       ▼
FlagController
       │
       │  flagId + description
       ▼
FlagService
       │
       │  find flag
       ▼
FlagRepository
       │
       │  update flag
       ▼
Updated Flag
       │
       ▼
HTTP Response
```

The controller handles HTTP.

The service handles the business operation.

The repository handles storing the updated flag.

---

## Final structure

```text
api/src/main/java/live/switchly/api/
├── SwitchlyApiApplication.java
│
├── controller/
│   ├── OrganizationController.java
│   ├── ProjectController.java
│   └── FlagController.java
│
├── dto/
│   ├── CreateOrganizationRequest.java
│   ├── CreateProjectRequest.java
│   ├── CreateFlagRequest.java
│   ├── UpdateFlagStateRequest.java
│   └── UpdateFlagDescriptionRequest.java     ← NEW
│
├── exception/
│   ├── NotFoundException.java
│   ├── ConflictException.java
│   ├── ErrorResponse.java
│   └── GlobalExceptionHandler.java
│
├── model/
│   ├── Organization.java
│   ├── Project.java
│   └── Flag.java                              ← UPDATED
│
├── repository/
│   ├── OrganizationRepository.java
│   ├── InMemoryOrganizationRepository.java
│   ├── ProjectRepository.java
│   ├── InMemoryProjectRepository.java
│   ├── FlagRepository.java                    ← UPDATED
│   └── InMemoryFlagRepository.java            ← UPDATED
│
└── service/
    ├── OrganizationService.java
    ├── ProjectService.java
    └── FlagService.java                       ← UPDATED
```

---

## New API summary

| Method | Endpoint | Purpose |
| ------ | -------- | ------- |
| `PATCH` | `/api/v1/flags/{flagId}/description` | Update the description of a flag |

### Example request

```json
{
  "description": "Enables the redesigned checkout experience"
}
```

### Example response

```json
{
  "id": "762fe117-14c1-46cb-a931-c8989d633774",
  "organizationId": "ORGANIZATION_ID",
  "projectId": "PROJECT_ID",
  "key": "new-checkout",
  "name": "New Checkout",
  "description": "Enables the redesigned checkout experience",
  "enabled": false
}
```

---

## Checkpoint

- ✅ Added `description` to `Flag`
- ✅ Created `UpdateFlagDescriptionRequest`
- ✅ Added an API to update a flag description
- ✅ Updated the flag service to handle description changes
- ✅ Updated the flag persistence layer
- ✅ Existing flag state functionality remains available
- ✅ Unknown flag IDs return `404`
- ✅ The API follows the existing Controller → Service → Repository structure