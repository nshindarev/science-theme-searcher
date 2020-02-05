CREATE DATABASE postgres_sts
    WITH
    OWNER = postgres
    ENCODING = 'UTF8'
    CONNECTION LIMIT = -1;

CREATE SCHEMA IF NOT EXISTS science_theme_searcher;

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Author;

CREATE TABLE postgres_sts.science_theme_searcher.Author (
                          Id SERIAL,
                          Name VARCHAR(255) NOT NULL,
    Patronymic VARCHAR(255) NOT NULL,
    Surname VARCHAR(255) NOT NULL,
    N VARCHAR(1) NOT NULL,
    P VARCHAR(1) NOT NULL,
    PRIMARY KEY (Id)
    );

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Publication;

CREATE TABLE postgres_sts.science_theme_searcher.Publication (
                               Id SERIAL,
                               Name VARCHAR(255) NOT NULL,
    Annotation VARCHAR(1000) NULL,
    DescriptionEng VARCHAR(1000) NULL,
    DescriptionRus VARCHAR(1000) NULL,
    PRIMARY KEY (Id)
    );

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Keyword;

CREATE TABLE postgres_sts.science_theme_searcher.Keyword (
                            Id SERIAL,
                            Keyword VARCHAR(255) NOT NULL,
    PRIMARY KEY (Id)
    );

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Link;

CREATE TABLE postgres_sts.science_theme_searcher.Link (
                        Id SERIAL,
                        URL VARCHAR(255) NOT NULL,
    PRIMARY KEY (Id)
    );

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Cluster;

CREATE TABLE postgres_sts.science_theme_searcher.Cluster (
                           Id SERIAL,
                           PRIMARY KEY (Id)
    );

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.ClusterToAuthor;

CREATE TABLE postgres_sts.science_theme_searcher.ClusterToAuthor (
                                   Id_Cluster INTEGER NOT NULL,
                                   Id_Author INTEGER NOT NULL
    );

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.AuthorToPublication;

CREATE TABLE postgres_sts.science_theme_searcher.AuthorToPublication (
                                       Id_Author INTEGER NOT NULL,
                                       Id_Publication INTEGER NOT NULL
    );

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.LinkToAuthor;

CREATE TABLE postgres_sts.science_theme_searcher.LinkToAuthor (
                                Id_Link INTEGER NOT NULL,
                                Id_Author INTEGER NOT NULL
    );

DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.KeywordToPublication;

CREATE TABLE postgres_sts.science_theme_searcher.KeywordToPublication (
                                        Id_Keyword INTEGER NOT NULL,
                                        Id_Publication INTEGER NOT NULL
    );


DROP TABLE IF EXISTS postgres_sts.science_theme_searcher.Clustertokeyword;

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
