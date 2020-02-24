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
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Author;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Publication;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Keyword;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Link;
DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Cluster;

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
