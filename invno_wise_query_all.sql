SELECT 
    `Invoices`.`invno` AS `Invoice No.`,
    `Invoices`.`invdate` AS `Date`,
    `Invoices`.`bill_value` AS `Bill Value`,
    `Buyers`.`name` AS `Buyer`,
    `Products`.`name` AS Product,
    `Particulars`.`quantity` AS `Quantity`,
    `Particulars`.`price` AS `Rate`
FROM
    `Invoices`
        INNER JOIN
    `Particulars`
        INNER JOIN
    `Products`
        INNER JOIN
    `Buyers`
WHERE
    `Invoices`.`invoiceID` = `Particulars`.`invoiceID`
        AND `Particulars`.`productID` = `Products`.`productID`
        AND `Invoices`.`buyerID` = `Buyers`.`buyerID`;

   
