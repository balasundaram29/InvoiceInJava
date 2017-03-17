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
