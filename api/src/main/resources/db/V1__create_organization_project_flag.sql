CREATE TABLE organization (
    id    UUID PRIMARY KEY,
    name  TEXT NOT NULL
);

CREATE TABLE project (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organization(id),
    name             TEXT NOT NULL
);

CREATE TABLE flag (
    id               UUID PRIMARY KEY,
    organization_id  UUID NOT NULL REFERENCES organization(id),
    project_id       UUID NOT NULL REFERENCES project(id),
    key              TEXT NOT NULL,
    name             TEXT NOT NULL,
    enabled          BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT uq_flag_key_per_project UNIQUE (project_id, key)
);