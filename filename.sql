delimiter $$
CREATE TRIGGER one_invno_per_finyear
BEFORE INSERT ON Invoices FOR EACH ROW
BEGIN 
IF EXISTS(SELECT 1 FROM Invoices WHERE `invno`=NEW.`invno` AND YEAR(DATE_SUB(`invdate`,INTERVAL 3 MONTH))=YEAR(DATE_SUB(NEW.`invdate`,INTERVAL 3 MONTH)))
 THEN SIGNAL SQLSTATE '45000' 
SET MESSAGE_TEXT = 'Cannot add this invoice no : You can use one invoice no only once in financial year';
END IF; 
END;$$
