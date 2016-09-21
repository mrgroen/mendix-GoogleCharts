DROP INDEX "idx_restservices$changeitem_key";
DROP INDEX "idx_restservices$changeitem_isdeleted";
DROP INDEX "idx_restservices$changeitem__isdirty";
DROP INDEX "idx_restservices$changeitem_sequencenr";
DROP INDEX "idx_restservices$changeitem_key_isdeleted__isdirty";
ALTER TABLE "restservices$changeitem" RENAME TO "180ffd95c3aa411a9aacb7c6d14b6ead";
DROP INDEX "idx_restservices$changeitem_changelog_restservices$changelog_restservices$changeitem";
ALTER TABLE "restservices$changeitem_changelog" RENAME TO "ee7ff29cd05f4104a5d44f9870680f00";
ALTER TABLE "restservices$changelog" RENAME TO "5cbaf7250aed421fae66127bf0ed0e77";
DROP INDEX "idx_restservices$changelog_servicedefinition_restservices$dataservicedefinition_restservices$changelog";
ALTER TABLE "restservices$changelog_servicedefinition" RENAME TO "a80acbd1171d456aa41cd72471720142";
ALTER TABLE "restservices$dataservicedefinition" RENAME TO "36210190597f40c5b73f463a896bc82c";
ALTER TABLE "restservices$datasyncstate" RENAME TO "d32c56de91f24f64af184d51362736c1";
DELETE FROM "mendixsystem$entity" 
 WHERE "id" = '32b30934-eb04-477c-b42f-762bfb24047f';
DELETE FROM "mendixsystem$entityidentifier" 
 WHERE "id" = '32b30934-eb04-477c-b42f-762bfb24047f';
DELETE FROM "mendixsystem$sequence" 
 WHERE "attribute_id" IN (SELECT "id"
 FROM "mendixsystem$attribute"
 WHERE "entity_id" = '32b30934-eb04-477c-b42f-762bfb24047f');
DELETE FROM "mendixsystem$attribute" 
 WHERE "entity_id" = '32b30934-eb04-477c-b42f-762bfb24047f';
DELETE FROM "mendixsystem$index" 
 WHERE "table_id" = '32b30934-eb04-477c-b42f-762bfb24047f';
DELETE FROM "mendixsystem$index_column" 
 WHERE "index_id" IN ('0f5d4ff5-d15b-4633-bde8-331033b0583f', '264cb1b8-9dee-4577-84d1-0818bce78f7d', '277e4b3d-2c3a-4657-ad8d-63761f745018', '55cb168f-a816-49de-8d4a-0b7e1d8d61f3', '591bfac9-bcd4-428e-bbac-44290e19242d');
DELETE FROM "mendixsystem$association" 
 WHERE "id" = '5055a8ea-5acf-40db-88c0-abaac75341d7';
DELETE FROM "mendixsystem$entity" 
 WHERE "id" = '1578d087-5b36-4fd2-bbb3-9ea99ee4518b';
DELETE FROM "mendixsystem$entityidentifier" 
 WHERE "id" = '1578d087-5b36-4fd2-bbb3-9ea99ee4518b';
DELETE FROM "mendixsystem$sequence" 
 WHERE "attribute_id" IN (SELECT "id"
 FROM "mendixsystem$attribute"
 WHERE "entity_id" = '1578d087-5b36-4fd2-bbb3-9ea99ee4518b');
DELETE FROM "mendixsystem$attribute" 
 WHERE "entity_id" = '1578d087-5b36-4fd2-bbb3-9ea99ee4518b';
DELETE FROM "mendixsystem$association" 
 WHERE "id" = '07e4911d-1ccb-4bf9-9c15-4a23ff6ccacc';
DELETE FROM "mendixsystem$entity" 
 WHERE "id" = 'bf83925e-60cc-4c9d-8a50-2c0b8ef66d56';
DELETE FROM "mendixsystem$entityidentifier" 
 WHERE "id" = 'bf83925e-60cc-4c9d-8a50-2c0b8ef66d56';
DELETE FROM "mendixsystem$sequence" 
 WHERE "attribute_id" IN (SELECT "id"
 FROM "mendixsystem$attribute"
 WHERE "entity_id" = 'bf83925e-60cc-4c9d-8a50-2c0b8ef66d56');
DELETE FROM "mendixsystem$attribute" 
 WHERE "entity_id" = 'bf83925e-60cc-4c9d-8a50-2c0b8ef66d56';
DELETE FROM "mendixsystem$entity" 
 WHERE "id" = 'f181c38b-0633-405c-abd0-91d997cf32b9';
DELETE FROM "mendixsystem$entityidentifier" 
 WHERE "id" = 'f181c38b-0633-405c-abd0-91d997cf32b9';
DELETE FROM "mendixsystem$sequence" 
 WHERE "attribute_id" IN (SELECT "id"
 FROM "mendixsystem$attribute"
 WHERE "entity_id" = 'f181c38b-0633-405c-abd0-91d997cf32b9');
DELETE FROM "mendixsystem$attribute" 
 WHERE "entity_id" = 'f181c38b-0633-405c-abd0-91d997cf32b9';
DROP TABLE "180ffd95c3aa411a9aacb7c6d14b6ead";
DROP TABLE "ee7ff29cd05f4104a5d44f9870680f00";
DROP TABLE "5cbaf7250aed421fae66127bf0ed0e77";
DROP TABLE "a80acbd1171d456aa41cd72471720142";
DROP TABLE "36210190597f40c5b73f463a896bc82c";
DROP TABLE "d32c56de91f24f64af184d51362736c1";
UPDATE "mendixsystem$version"
 SET "versionnumber" = '4.0.7', 
"lastsyncdate" = '20160916 11:09:07';
