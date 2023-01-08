--b)
SELECT Aufnr, Dauer, MitStundensatz, (MitStundensatz* Dauer) AS Lohnkosten FROM Mitarbeiter m LEFT JOIN Auftrag a  on m.MitID = a.MitID WHERE Dauer is not Null
--398 Datensatze
--c)
SELECT KunName, KunOrt, Anfahrt FROM Auftrag a JOIN Kunde k ON a.KunNr=k.KunNr WHERE Anfahrt>50
--d)
SELECT a.KunNr, EtBezeichnung, ErlDat FROM Auftrag a Join Montage m on a.Aufnr=m.Aufnr JOIN Ersatzteil e ON e.EtID=m.EtID JOIN Kunde k ON k.KunNr=a.KunNr  WHERE e.EtID LIKE 'H0230' AND ErlDat < DATEADD(month, -2, GETDATE())
--e)
SELECT a.Aufnr, SUM(Anfahrt*2.5) AS Fahrtpreis, SUM(MitStundensatz* Dauer) AS Lohnkosten, Anzahl*EtPreis AS MaterialKosten 
From Auftrag a JOIN  Mitarbeiter m ON a.MitID=m.MitID JOIN
Montage o on a.AufNr=o.AufNr JOIN Ersatzteil e ON o.EtID = e.EtID
WHERE Anfahrt is not NULL GROUP BY a.Aufnr, Anzahl, EtPreis
--f)
SELECT MitName, ErlDat FROM Auftrag a JOIN Mitarbeiter m ON a.MitID=m.MitID WHERE ErlDat > DATEADD(month, -1, GETDATE()) AND ErlDat < DATEADD(month, 0, GETDATE())
--g)
SELECT e.EtBezeichnung, SUM(Anzahl) FROM Auftrag a JOIN Montage m ON a.Aufnr=m.Aufnr JOIN Ersatzteil e ON e.EtID=m.EtID JOIN Kunde k ON k.KunNr=a.KunNr WHERE ErlDat > DATEADD(month, -1, GETDATE()) AND ErlDat < DATEADD(month, 0, GETDATE()) GROUP BY EtBezeichnung
--2.6)
--a)
SELECT m.MitID
FROM Mitarbeiter m
WHERE m.MitID NOT IN (SELECT a.MitID 
					  FROM Auftrag a JOIN Mitarbeiter m 
					  ON a.MitID = m.MitID WHERE DATEDIFF(mm,a.Aufdat, GETDATE()) =
					  )
--b)
SELECT (MitStundensatz* Dauer) AS Lohnkosten, SUM(Anfahrt*2.5) AS Fahrtpreis, Aufnr
FROM Mitarbeiter m JOIN Auftrag a ON m.MitID=a.MitID
WHERE  a.Aufnr not in (SELECT a.Aufnr 
		From Auftrag a JOIN  Mitarbeiter m ON a.MitID=m.MitID JOIN
		Montage o on a.AufNr=o.AufNr JOIN Ersatzteil e ON o.EtID = e.EtID
		WHERE Anfahrt is not NULL GROUP BY a.Aufnr, Anzahl, EtPreis
		) AND Dauer is not null
		GROUP BY MitStundensatz* Dauer, Aufnr
--c)
SELECT AufDat
From Auftrag
WHERE AufDat = (
SELECT MIN(ErlDat)  
FROM Auftrag 
WHERE Dauer is Null AND ErlDat is not Null
)
--d)
SELECT Aufdat, KunName
FROM Auftrag a JOIN Kunde k ON a.KunNr=k.KunNr
WHERE AufDat IN (
				SELECT AufDat
				FROM Auftrag
				WHERE DATEPART(mm, AufDat)= 3
				)
--194 Ergebnisse
--e)
SELECT MitID, Aufnr, Max(a.Dauer) AS maxDauer
FROM Auftrag a JOIN (
		SELECT MAX(Dauer) AS MaxDauer
		FROM Mitarbeiter m JOIN Auftrag a ON m.MitID=a.MitID 
		GROUP BY MitName) UA
		ON a.Dauer=UA.MaxDauer
GROUP BY MitID, Aufnr
SELECT Aufnr
FROM Auftrag
WHERE Dauer Join (SELECT MAX(Dauer)
		FROM Mitarbeiter m JOIN Auftrag a ON m.MitID=a.MitID 
		GROUP BY MitName) UA
		on 
		--k.A. digga
--2.7)
--HAVING ist eine Bedingung, die auf aggregierte Werte angewendet werden kann. Die WHERE Bedingung kann zum Beispiel auf gruppierte Werte (GROUP BY) nicht angewendet werden, daf�r muss man HAVING verwenden
--a)
SELECT MitID, SUM(Anfahrt) AS GesamtKilo
FROM Auftrag
WHEDATEPART(mm, ErlDat)= 4
GROUP BY MitID
HAVING SUM(Anfahrt)>500
--3 Datensatze
--b)
SELECT e.EtID, EtAnzLager
FROM Ersatzteil e JOIN Montage m 
ON e.EtID=m.EtID
GROUP BY EtAnzLager, e.EtID
HAVING EtAnzLager > Sum(Anzahl)
--2.8)
--a
SELECT * FROM Kunde
INSERT INTO Kunde VALUES (1501,'Lukas','Dresden',01277,'Reichenbac2')
--b
UPDATE Mitarbeiter SET MitStundensatz =(SELECT MIN(MitStundensatz)
										FROM Mitarbeiter
										WHERE MitJob LIKE 'Monteur') WHERE MitJob LIKE 'Azubi'
UPDATE Mitarbeiter SET MitJob = 'Monteur' WHERE MitJob LIKE 'Azubi'
SELECT * FROM Mitarbeiter
--c
DELETE Montage WHERE EtID IN (SELECT EtID
							 FROM Ersatzteil
							 WHERE EtHersteller LIKE 'Mosch')
DELETE Ersatzteil WHERE EtHersteller LIKE 'Mosch'
DELETE Ersatzteil WHERE EtID = (
								SELECT 
--d
DELETE Auftrag WHERE  DATEPART(mm, ErlDat) = 3
DELETE Montage WHERE AufNr IN (SELECT AufNr 
							   FROM Auftrag
							   WHERE DATEPART(mm, ErlDat) = 3)
DELETE Kunde WHERE KunNr IN (SELECT KunNr
							 FROM Auftrag
							 WHERE DATEPART(mm, ErlDat) = 3) AND KunNr NOT IN (SELECT KunNr
																			   FROM Auftrag
																			   WHERE DATEPART(mm, ErlDat) != 3)
--2.10)
CREATE VIEW Auftragswert
AS 
SELECT a.AufNr AS A, a.ErlDat AS B, k.KunOrt AS C, SUM(m.Anzahl* e.EtPreis) AS Materialwert, a.Anfahrt*2.5 AS Anfahrtskosten, z.MitStundensatz* a.Dauer AS Lohnkosten , z.MitStundensatz* a.Dauer + a.Anfahrt*2.5 + SUM(m.Anzahl* ePreis)  AS 'Gesamtwert'
FROM Auftrag a JOIN Kunde k ON a.KunNr=k.KunNr JOIN Montage m ON a.AufNr=m.AufNr JOIN Ersatzteil e ON e.EtID=m.EtID JOIN Mitarbeiter z ON z.MitID=a.MitID
GROUP BY a.AufNr, a.ErlDat, k.KunOrt, a.Anfahrt, z.MitStundensatz, a.Dauer

SELECT SUM(Gesamtwert)
FROM Auftragswert
--2.11)
SELECT Anfahrt FROM Auftrag

SELECT Aufnr, Anfahrt,
  CASE
	WHEN Anfahrt<12 THEN 30
	WHEN Anfahrt>12 THEN Anfahrt*2.5
	ELSE				0
  END AS Anfahrtspreis
FROM AuftragitJob LIKE 'Azubi'
UPDATE Mitarbeiter SET MitJob = 'Monteur' WHERE MitJob LIKE 'Azubi'
SELECT * FROM Mitarbeiter
--c
DELETE Montage WHERE EtID IN (SELECT EtID
							 FROM Ersatzteil
							 WHERE EtHersteller LIKE 'Mosch')
DELETE Ersatzteil WHERE EtHersteller LIKE 'Mosch'
DELETE Ersatzteil WHERE EtID = (
								SELECT 
--d
DELETE Auftrag WHERE  DATEPART(mm, ErlDat) = 3
DELETE Montage WHERE AufNr IN (SELECT AufNr 
							   FROM Auftrag
							   WHERE DATEPART(mm, ErlDat) = 3)
DELETE Kunde WHERE KunNr IN (SELECT KunNr
							 FROM Auftrag
							 WHERE DATEPART(mm, ErlDat) = 3) AND KunNr NOT IN (SELECT KunNr
																			   FROM Auftrag
																			   WHERE DATEPART(mm, ErlDat) != 3)
--2.10)
CREATE VIEW Auftragswert
AS 
SELECT a.AufNr AS A, a.ErlDat AS B, k.KunOrt AS C, SUM(m.Anzahl* e.EtPreis) AS Materialwert, a.Anfahrt*2.5 AS Anfahrtskosten, z.MitStundensatz* a.Dauer AS Lohnkosten , z.MitStundensatz* a.Dauer + a.Anfahrt*2.5 + SUM(m.Anzahl*