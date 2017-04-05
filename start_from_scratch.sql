#DROP DATABASE InvoiceDB ;

CREATE DATABASE InvoiceDB;
USE InvoiceDB;
CREATE TABLE IF NOT EXISTS Buyers (
    buyerID INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(80) NOT NULL UNIQUE,
    address VARCHAR(200),
    TINNo VARCHAR(25),
    CSTNo VARCHAR(25),
    email VARCHAR(60)
)  ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS Products (
    productID INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL UNIQUE,
    unit VARCHAR(20),
    type VARCHAR(15)
)  ENGINE=INNODB;

CREATE TABLE IF NOT EXISTS Invoices (
    invoiceID INTEGER PRIMARY KEY AUTO_INCREMENT,
    invno INTEGER,
    invdate DATE NOT NULL,
    buyerID INTEGER,
    cstrate DECIMAL(5 , 2 ),
    scrate DECIMAL(5 , 2 ),
    other INTEGER,
    bill_value INTEGER,
    tax_type VARCHAR(6),
    form_c CHAR(1),
    remarks VARCHAR(50),
    FOREIGN KEY (buyerID)
        REFERENCES Buyers (buyerID)
)  ENGINE=INNODB;
            

CREATE TABLE IF NOT EXISTS Particulars (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    invoiceID INTEGER NOT NULL,
    productID INTEGER NOT NULL,
    quantity INTEGER NOT NULL,
    price DECIMAL(10 , 2 ),
    FOREIGN KEY (invoiceID)
        REFERENCES Invoices (invoiceID)
        ON DELETE CASCADE ON UPDATE CASCADE,
    FOREIGN KEY (productID)
        REFERENCES Products (productID)
)  ENGINE=INNODB;


INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('0.5 HP Single Phase Openwell Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('0.5 HP Single Phase Monoblock Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('0.5 HP Single Phase Vertical Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('0.5 HP TEFC Motor', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('0.5 HP Self-Priming Monoblock Pumpset', 'Nos');

INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1 HP Single Phase Openwell Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1 HP Single Phase Monoblock Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1 HP Single Phase Vertical Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1 HP TEFC Motor', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1 HP Self-Priming Monoblock Pumpset', 'Nos');

INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('0.75 HP Single Phase Openwell Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('0.75 HP Single Phase Monoblock Pumpset', 'Nos');

INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1.5 HP Single Phase Openwell Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1.5 HP Single Phase Monoblock Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1.5 HP Single Phase Vertical Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1.5 HP TEFC Motor', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('1.5 HP Self-Priming Monoblock Pumpset', 'Nos');

INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('2 HP Single Phase Openwell Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('2 HP Single Phase Monoblock Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('2 HP Single Phase Vertical Submersible Pumpset', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('2 HP TEFC Motor', 'Nos');
INSERT INTO `InvoiceDB`.`Products` (`name`, `unit`) VALUES ('2 HP Self-Priming Monoblock Pumpset', 'Nos');

#INSERT INTO `InvoiceDB`.`Buyers` (`name`, `address`, `TINNo`, `CSTNo`) VALUES ('Western Trading1', 'One Steet Road,\n Two  Road,\n Third Lane,\n City ', '12233', '12223');

#UPDATE `InvoiceDB`.`Buyers` SET `address`='Murikkal Road Junction2,\r Parappanangadi,\r Malappuram Distt., Kerala' WHERE `buyerID`='1';
INSERT INTO `InvoiceDB`.`Buyers` (`name`, `address`, `TINNo`, `CSTNo`) VALUES ('Tex Marketing Agencies','V.P.B. Complex,Ram Mohan Road,\nNear Chintavalap,Calicut-673 004,\nKerala', '32110817736', '32110817736/C');

INSERT INTO `InvoiceDB`.`Buyers` (`name`, `address`, `TINNo`, `CSTNo`) VALUES ('Sathya Enterprises','22nd Main Road,Mysore Road,\nHosaguddadahalli,\nBangalore-520 026', '29500109541', ' ');
#UPDATE `InvoiceDB`.`Buyers` SET `address`='Murikkal Road Junction2,\r Parappanangadi,\r Malappuram Distt., Kerala' WHERE `buyerID`='1';
INSERT INTO `InvoiceDB`.`Buyers` (`name`, `address`, `TINNo`, `CSTNo`) VALUES ('Shri Marikomba Enterprises','Ganesh Nagar,Near Kanada School,\nSirsi - 581 402,\nKarnataka', '29400665212', ' ');

delimiter $$
CREATE TRIGGER one_invno_per_finyear
      BEFORE INSERT ON Invoices FOR EACH ROW
      BEGIN 
          IF EXISTS(SELECT 1 FROM Invoices WHERE `invno`=NEW.`invno` AND YEAR(DATE_SUB(`invdate`,INTERVAL 3 MONTH))=YEAR(DATE_SUB(NEW.`invdate`,INTERVAL 3 MONTH)))
             THEN
                 SIGNAL  SQLSTATE '45000'
                 SET MESSAGE_TEXT = 'Cannot add this invoice no : You can use one invoice no only once in financial year';
          END IF;
      END;$$
