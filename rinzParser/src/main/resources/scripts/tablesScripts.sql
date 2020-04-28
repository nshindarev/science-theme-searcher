CREATE DATABASE postgres_sts
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

CREATE SCHEMA IF NOT EXISTS science_theme_searcher;

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.ClusterToAuthor;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Clustertokeyword;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.KeywordToPublication;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.LinkToAuthor;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.AuthorToPublication;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.AuthorToAuthor;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Author;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Publication;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Keyword;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Link;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Cluster;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.ClusterToKeyword;

CREATE TABLE postgres_sts.science_theme_searcher.Author (
                          Id integer,
                          Name VARCHAR(255) NULL,
    Patronymic VARCHAR(255) NULL,
    Surname VARCHAR(255) NOT NULL,
    N VARCHAR(1) NULL,
    P VARCHAR(1) NULL,
    Revision integer DEFAULT 0,
    PRIMARY KEY (Id)
    );

CREATE TABLE postgres_sts.science_theme_searcher.Publication (
                               Id SERIAL,
                               Name VARCHAR(255) NOT NULL,
    Annotation VARCHAR(1000) NULL,
    DescriptionEng VARCHAR(1000) NULL,
    DescriptionRus VARCHAR(1000) NULL,
    Metric INTEGER NULL,
    PRIMARY KEY (Id)
    );

CREATE TABLE postgres_sts.science_theme_searcher.Keyword (
                            Id SERIAL,
                            Keyword VARCHAR(255) NOT NULL,
    PRIMARY KEY (Id)
    );

CREATE TABLE postgres_sts.science_theme_searcher.Link (
                        Id SERIAL,
                        URL VARCHAR(255) NOT NULL,
    PRIMARY KEY (Id)
    );

CREATE TABLE postgres_sts.science_theme_searcher.Cluster (
                           Id SERIAL,
                           PRIMARY KEY (Id)
    );

CREATE TABLE postgres_sts.science_theme_searcher.ClusterToAuthor (
                                   Id_Cluster INTEGER NOT NULL,
                                   Id_Author INTEGER NOT NULL
    );

CREATE TABLE postgres_sts.science_theme_searcher.AuthorToPublication (
                                       Id_Author INTEGER NOT NULL,
                                       Id_Publication INTEGER NOT NULL
    );

CREATE TABLE postgres_sts.science_theme_searcher.LinkToAuthor (
                                Id_Link INTEGER NOT NULL,
                                Id_Author INTEGER NOT NULL
    );

CREATE TABLE postgres_sts.science_theme_searcher.KeywordToPublication (
                                        Id_Keyword INTEGER NOT NULL,
                                        Id_Publication INTEGER NOT NULL
    );


CREATE TABLE postgres_sts.science_theme_searcher.ClusterToKeyword (
                                     Id_Cluster INTEGER NOT NULL,
                                     Id_Keyword INTEGER NOT NULL
    );


CREATE TABLE postgres_sts.science_theme_searcher.AuthorToAuthor (
                                     Id VARCHAR(255) NOT NULL,
                                     Id_First INTEGER NOT NULL,
                                     Id_Second INTEGER NOT NULL,
                                     Weight INTEGER NOT NULL,
    PRIMARY KEY (Id)
    );


ALTER TABLE postgres_sts.science_theme_searcher.ClusterToAuthor ADD FOREIGN KEY (Id_Cluster) REFERENCES postgres_sts.science_theme_searcher.Cluster (Id);
ALTER TABLE postgres_sts.science_theme_searcher.ClusterToAuthor ADD FOREIGN KEY (Id_Author) REFERENCES postgres_sts.science_theme_searcher.Author (Id);
ALTER TABLE postgres_sts.science_theme_searcher.AuthorToPublication ADD FOREIGN KEY (Id_Author) REFERENCES postgres_sts.science_theme_searcher.Author (Id);
ALTER TABLE postgres_sts.science_theme_searcher.AuthorToPublication ADD FOREIGN KEY (Id_Publication) REFERENCES postgres_sts.science_theme_searcher.Publication (Id);
ALTER TABLE postgres_sts.science_theme_searcher.LinkToAuthor ADD FOREIGN KEY (Id_Link) REFERENCES postgres_sts.science_theme_searcher.Link (Id);
ALTER TABLE postgres_sts.science_theme_searcher.LinkToAuthor ADD FOREIGN KEY (Id_Author) REFERENCES postgres_sts.science_theme_searcher.Author (Id);
ALTER TABLE postgres_sts.science_theme_searcher.KeywordToPublication ADD FOREIGN KEY (Id_Keyword) REFERENCES postgres_sts.science_theme_searcher.Keyword (Id);
ALTER TABLE postgres_sts.science_theme_searcher.KeywordToPublication ADD FOREIGN KEY (Id_Publication) REFERENCES postgres_sts.science_theme_searcher.Publication (Id);
ALTER TABLE postgres_sts.science_theme_searcher.Clustertokeyword ADD FOREIGN KEY (Id_Cluster) REFERENCES postgres_sts.science_theme_searcher.Cluster (Id);
ALTER TABLE postgres_sts.science_theme_searcher.Clustertokeyword ADD FOREIGN KEY (Id_Keyword) REFERENCES postgres_sts.science_theme_searcher.Keyword (Id);
ALTER TABLE postgres_sts.science_theme_searcher.AuthorToAuthor ADD FOREIGN KEY (Id_First) REFERENCES postgres_sts.science_theme_searcher.Author (Id);
ALTER TABLE postgres_sts.science_theme_searcher.AuthorToAuthor ADD FOREIGN KEY (Id_Second) REFERENCES postgres_sts.science_theme_searcher.Author (Id);


-- DROP FUNCTION IF EXISTS a2aHandleConflicts;
--  CREATE FUNCTION a2aHandleConflicts() RETURNS trigger AS $emp_stamp$
--      BEGIN
--          IF NEW.id_first = OLD.id_second AND OLD.id_first = NEW.id_second THEN
--              NEW.weight = 1 + (NEW.weight + OLD.weight - 1);
--          END IF;
--  		DELETE FROM postgres_sts.science_theme_searcher.AuthorToAuthor WHERE id_first = OLD.id_first AND id_second = OLD.id_second;
--           RETURN NEW;
--      END;
--  $emp_stamp$ LANGUAGE plpgsql;

--  CREATE TRIGGER emp_stamp AFTER INSERT ON postgres_sts.science_theme_searcher.AuthorToAuthor
--      FOR EACH ROW EXECUTE PROCEDURE a2aHandleConflicts();