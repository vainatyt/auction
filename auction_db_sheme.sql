CREATE TABLE IF NOT EXISTS "lots" (
	"start_auction" timestamp without time zone NOT NULL,
	"end_auction" timestamp without time zone NOT NULL,
	"current_cost" numeric(10,0) NOT NULL,
	"rate_step" numeric(10,0) NOT NULL,
	"id_buyer" bigint,
	"id_owner" bigint NOT NULL,
	"id_lot" serial NOT NULL UNIQUE,
	"name" varchar(255) NOT NULL,
	"description" varchar(255) NOT NULL,
	PRIMARY KEY ("id_lot")
);

CREATE TABLE IF NOT EXISTS "users" (
	"id_user" serial NOT NULL,
	"name" varchar(255) NOT NULL,
	"password" varchar(255) NOT NULL,
	"email" varchar(255) NOT NULL,
	"rating" numeric(10,0) NOT NULL,
	PRIMARY KEY ("id_user")
);

CREATE TABLE IF NOT EXISTS "trackable_items" (
	"id_lot" bigint NOT NULL,
	"id_user" bigint NOT NULL,
	"id" serial NOT NULL UNIQUE,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "photo" (
	"id" serial NOT NULL UNIQUE,
	"id_lot" bigint NOT NULL,
	"uuid" uuid NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "comments" (
	"id_commentator" bigint NOT NULL,
	"id_addressee" bigint NOT NULL,
	"rating" numeric(10,0) NOT NULL,
	"review" varchar(255) NOT NULL,
	"date" timestamp without time zone NOT NULL,
	PRIMARY KEY ("id_commentator", "id_addressee")
);

CREATE TABLE IF NOT EXISTS "mails" (
	"id" serial NOT NULL UNIQUE,
	"message" varchar(255) NOT NULL,
	"title" varchar(255) NOT NULL,
	"id_user" bigint NOT NULL,
	PRIMARY KEY ("id")
);

CREATE TABLE IF NOT EXISTS "buy_lot_requests" (
	"id" serial NOT NULL UNIQUE,
	"id_lot" bigint NOT NULL,
	"req_cost" numeric(10,0) NOT NULL,
	"id_user" bigint NOT NULL,
	"date" timestamp without time zone NOT NULL,
	"status" varchar(255) NOT NULL,
	PRIMARY KEY ("id")
);

ALTER TABLE "lots" ADD CONSTRAINT "lots_fk4" FOREIGN KEY ("id_buyer") REFERENCES "users"("id_user");

ALTER TABLE "lots" ADD CONSTRAINT "lots_fk5" FOREIGN KEY ("id_owner") REFERENCES "users"("id_user");

ALTER TABLE "trackable_items" ADD CONSTRAINT "trackable_items_fk0" FOREIGN KEY ("id_lot") REFERENCES "lots"("id_lot");

ALTER TABLE "trackable_items" ADD CONSTRAINT "trackable_items_fk1" FOREIGN KEY ("id_user") REFERENCES "users"("id_user");
ALTER TABLE "photo" ADD CONSTRAINT "photo_fk1" FOREIGN KEY ("id_lot") REFERENCES "lots"("id_lot");
ALTER TABLE "comments" ADD CONSTRAINT "comments_fk0" FOREIGN KEY ("id_commentator") REFERENCES "users"("id_user");

ALTER TABLE "comments" ADD CONSTRAINT "comments_fk1" FOREIGN KEY ("id_addressee") REFERENCES "users"("id_user");
ALTER TABLE "mails" ADD CONSTRAINT "mails_fk3" FOREIGN KEY ("id_user") REFERENCES "users"("id_user");
ALTER TABLE "buy_lot_requests" ADD CONSTRAINT "buy_lot_requests_fk1" FOREIGN KEY ("id_lot") REFERENCES "lots"("id_lot");

ALTER TABLE "buy_lot_requests" ADD CONSTRAINT "buy_lot_requests_fk3" FOREIGN KEY ("id_user") REFERENCES "users"("id_user");