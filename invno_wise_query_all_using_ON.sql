SELECT 
    `Invoices`.`invno` AS `Invoice No.`,
    `Invoices`.`invdate` AS `Date`,
    `Invoices`.`bill_value` AS `Bill Value`,
    `Buyers`.`name` AS `Buyer`,
    `Products`.`name` AS `Product`,
    `Particulars`.`quantity` AS `Quantity`,
    `Particulars`.`price` AS `Rate`,
    `Invoices`.`remarks` AS `Remarks`
FROM
    `Invoices`
        INNER JOIN
    `Particulars` ON `Invoices`.`invoiceID` = `Particulars`.`invoiceID`
        INNER JOIN
    `Products` ON `Particulars`.`productID` = `Products`.`productID`
        INNER JOIN
    `Buyers` ON `Invoices`.`buyerID` = `Buyers`.`buyerID`
ORDER BY `Invoices`.`invno`;

