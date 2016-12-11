INSERT INTO user(id, name, carnet, telefono, direccion, Salary, type, login, password) VALUES 
(1,'admin',222,333,'direccion',3000,'Admin','admin','admin'),
(2,'admin2',7878,78798,'dir',78,'Admin','admin2','admin2'),
(3,'insumo',789789,789789,'direccion',7878,'Insumo','insumo','insumo'),
(4,'Employee 1',789789,789789,'direccion',890890,'Employee','employee','employee'),
(5,'Store',789789,789789,'Address',678678,'Store','alamcen','store');

INSERT INTO company(id, name) VALUES
('10001','APL'),
('10002','ADEPLEC'),
('10004','ACRHOBOL'),
('10005','APLI'),
('10006','ALVA'),
('10007','AMLECO');

INSERT INTO customer VALUES 
  (1, 'Daniel Campos', 0, 22333, 'Address', '2225', 1, 'Modulo 1', 'Asociacion', 10001, 133, 'acc', 0, 'position', '2016-06-17 23:53:08'),
  (2, 'Juan Cespedez', 0, 22333, 'Address', '2383', 1, 'Module Name', 'Asociacion', 10001, 133, 'acc', 0, 'position', '2016-06-17 23:53:08'),
  (3, 'Vaneza Mamani', 0, 22333, 'Address', '2472', 1, 'Module Name', 'Asociacion', 10001, 133, 'acc', 0, 'position', '2016-06-17 23:53:08'),
  (4, 'Maria Arce', 0, 22333, 'Address', '2952', 1, 'Module Name', 'Asociacion', 10001, 133, 'no acc', 0, 'position', '2016-06-17 23:53:08'),
  (5, 'Silvia Rocha', 0, 22333, 'Address', '10289', 1, 'Module Name', 'Asociacion', 10001, 180, 'acc', 0, 'position', '2016-06-17 23:53:08'),
  (6, 'Hansel Cuchallo', 0, 22333, 'Address', '3302', 1, 'Module Name', 'Asociacion', 10001, 206, 'acc', 0, 'position', '2016-06-17 23:53:08');

UPDATE customer SET companyName = "APL" WHERE companyId = '10001';
UPDATE customer SET companyName = "ADEPLEC" WHERE companyId = '10002';
UPDATE customer SET companyName = "ACRHOBOL" WHERE companyId = '10004';
UPDATE customer SET companyName = "APLI" WHERE companyId = '10005';
UPDATE customer SET companyName = "ALVA" WHERE companyId = '10006';
UPDATE customer SET companyName = "AMLECO" WHERE companyId = '10007';
UPDATE customer SET companyName = "ALVICO" WHERE companyId = '10008';
UPDATE customer SET companyName = "ALDEPA" WHERE companyId = '10009';
UPDATE customer SET companyName = "ADEPLECTRA" WHERE companyId = '100010';
UPDATE customer SET companyName = "APLEVACC" WHERE companyId = '100011';
UPDATE customer SET companyName = "APLIM" WHERE companyId = '100013';
UPDATE customer SET companyName = "INDEPENDIENTES" WHERE companyId = '19999';
UPDATE customer SET companyName = "APLE MAICA" WHERE companyId = '10014';
UPDATE customer SET companyName = "A.L.M." WHERE companyId = '10012';
UPDATE customer SET companyName = "ASO LATTE CLAKH" WHERE companyId = '10015';

INSERT INTO product(vendorName, name, type) VALUES
('AGP', 'Product 1', 'type1'),
('AGP', 'Product 2', 'type1'),
('AGP', 'Product 3', 'type1'),
('AGP', 'Product 4', 'type1'),
('AGP', 'Product 5', 'type1'),
('AGP', 'Product 6', 'type1');

INSERT INTO product(vendorName, name, currentAmount, cost, totalValue, type) VALUES
('BI', 'Product 7', '1', '30.24', '30.24', 'type1'),
('BI', 'Product 8', '2', '50.24', '100.48', 'type1'),
('BI', 'Product 9', '0', '214.8', '0', 'type1'),
('BI', 'Product 10', '12', '117.6', '1411.2', 'type1');

INSERT INTO product(vendorName, name, currentAmount, cost, totalValue, type) VALUES
('AG',	'Product 11',	'0',	'303',	'0', 'type1'),
('AG',	'Product 12',	'3',	'119.3',	'357.9', 'type1'),
('AG',	'Product 13',	'2',	'48.72',	'97.44', 'type1');

INSERT INTO product(vendorName, name, currentAmount, cost, totalValue, type) VALUES

('DI',	'Product 14',	'0',	'0',	'0', 'type1'),
('DI',	'Product 15',	'0',	'0',	'0', 'type1'),
('DI',	'Product 16',	'2',	'0',	'0', 'type1'),
('DI',	'Product 17',	'0',	'0',	'0', 'type1');

INSERT INTO product(vendorName, name, currentAmount, cost, totalValue, type) VALUES
('CLA BELLA',	'Product 18',	'1',	'1250',	'1250', 'type1'),
('CLA BELLA',	'Product 19',	'0',	'1150',	'0', 'type1'),
('CLA BELLA',	'Product 20',	'5',	'900',	'4500', 'type1'),
('CLA BELLA',	'Product 21',	'0',	'0',	'0', 'type1');

INSERT INTO roles(roleName, roleCode) VALUES 
('Unit of Measure', "measure"),
('Create Unidaded de MCreate edida', "measureCreate"),
('List Unidades MList edida', "measureList"),
('view Unit of Measure', "measureShow"),
('Edit Unit of Measure', "measureEdit"),
('Remove Unit of Measure', "measureDelete"),

('Products', "product"),
('Create Product', "productCreate"),
('List Products', "productList"),
('view Product', "productShow"),
('Edit Product', "productEdit"),
('Remove Product', "productDelete"),

('Vendores', "vendor"),
('Create Vendor', "vendorCreate"),
('List Vendor', "vendorList"),
('view Vendor', "vendorShow"),
('Edit Vendor', "vendorEdit"),
('Remove Vendor', "vendorDelete"),

('Modulos', "module"),
('Create Modulo ', "moduleCreate"),
('List Modulo ', "moduleList"),
('view Modulo ', "moduleShow"),
('Edit Modulo ', "moduleEdit"),
('Remove Modulo ', "moduleDelete"),

('Customer', "customer"),
('Create Customer', "customerCreate"),
('List Customer', "customerList"),
('view Customer', "customerShow"),
('Edit Customer', "customerEdit"),
('Remove Customer', "customerDelete"),

('Users', "user"),
('Create Users', "userCreate"),
('List Users', "userList"),
('view Users', "userShow"),
('Edit Users', "userEdit"),
('Remove Users', "userDelete"),

('Accounts', "account"),
('Create Accounts', "accountCreate"),
('List Accounts', "accountList"),
('view Accounts', "accountShow"),
('Edit Accounts', "accountEdit"),
('Remove Accounts', "accountDelete"),

('Transactions', "transaction"),
('Create Transactions', "transactionCreate"),
('List Transactions', "transactionList"),
('view Transactions', "transactionShow"),
('Edit Transactions', "transactionEdit"),
('Remove Transactions', "transactionDelete"),

('Detail of Transaction', "transactionDetail"),
('Create Detail of Transaction', "transactionDetailCreate"),
('List Detail of Transaction', "transactionDetailList"),
('view Detail of Transaction', "transactionDetailShow"),
('Edit Detail of Transaction', "transactionDetailEdit"),
('Remove Detail of Transaction', "transactionDetailDelete"),

('Orders', "productRequest"),
('Create Orders', "productRequestCreate"),
('List Orders', "productRequestList"),
('view Orders', "productRequestShow"),
('Edit Orders', "productRequestEdit"),
('Remove Orders', "productRequestDelete"),
('Enviar Orders', "productRequestSend"),
('Aceptar Orders', "productRequestAccept"),
('Finalizar Orders', "productRequestFinish"),

('Detail of Order', "requestRow"),
('Create Detail of Order', "requestRowCreate"),
('List Detail of Order', "requestRowList"),
('view Detail of Order', "requestRowShow"),
('Edit Detail of Order', "requestRowEdit"),
('Remove Detail of Order', "requestRowDelete"),

('Asignacion de Product a Customer', "requestRowCustomer"),
('Create Asignacion de Product a Customer', "requestRowCustomerCreate"),
('List Asignacion de Product a Customer', "requestRowCustomerList"),
('view Asignacion de Product a Customer', "requestRowCustomerShow"),
('Edit Asignacion de Product a Customer', "requestRowCustomerEdit"),
('Remove Asignacion de Product a Customer', "requestRowCustomerDelete"),

('Report of Discounts', "discountReport"),
('Create Report of Discounts', "discountReportCreate"),
('List Report of Discounts', "discountReportList"),
('view Report of Discounts', "discountReportShow"),
('Edit Report of Discounts', "discountReportEdit"),
('Remove Report of Discounts', "discountReportDelete"),
('Finalizar Report of Discounts', "discountReportFinalize"),

('Detail of Discount', "discountDetail"),
('Create Detail of Discount', "discountDetailCreate"),
('List Detail of Discount', "discountDetailList"),
('view Detail of Discount', "discountDetailShow"),
('Edit Detail of Discount', "discountDetailEdit"),
('Remove Detail of Discount', "discountDetailDelete"),

('Products al Inventario', "productInv"),
('Create Products al Inventario', "productInvCreate"),
('List Products al Inventario', "productInvList"),
('view Products al Inventario', "productInvShow"),
('Edit Products al Inventario', "productInvEdit"),
('Remove Products al Inventario', "productInvDelete"),

('Report', "report"),
('view Balance sheet', "balanceShow"),
('view Journal Book', "diaryShow"),
('view Financial Status', "financeShow"),
('view Libros del Mayor', "mayorShow"),
('view Trial Balance', "sumasSaldosShow"),

('Company Info', "setting"),
('view Company Info', "settingShow"),
('Edit Company Info', "settingEdit");

INSERT INTO `measure`(id, name, quantity, description) VALUES 
(1,'250 ML','250','250 ML'),
(2,'100 ML','100','Description'),
(3,'10gr','10','10 Gramos'),
(4,'80X2mm','2','Description'),
(5,'250cc','250','250cc'),
(6,'20ML','20','Description'),
(7,'500ML','500','500ML'),
(8,'1LITRO','1000','Description'),
(9,'200GRS','200','200GRS'),
(10,'10GRS','10','10GRS'),
(11,'50SOB. X 10GRS.','50','50SOB. X 10GRS.'),
(12,'100 X 1KG','100','100 X 1KG'),
(13,'100 X 25KG','25','100 X 25KG'),
(14,'VALDE X 18KG','1','VALDE X 18KG'),
(15,'VALDE X 4KG','1','VALDE X 4KG'),
(16,'Unidad','1','Unidad'),
(17,'1 ML','1','1 ML'),
(18,'1 GR','1','1 GR');

UPDATE measure SET measureId = 0, measureName = 'Ninguno';

INSERT INTO `account` VALUES (1,'1.0','ACTIVO','ACTIVO',0,'NO','',0,0,0,'2016-07-18 13:19:25'),
(2,'1.1','ACTIVO CORRIENTE','ACTIVO',1,'NO','',0,0,0,'2016-07-18 13:20:01'),
(3,'1.2','ACTIVO NO CORRIENTE','ACTIVO',1,'NO','',0,0,0,'2016-07-18 13:22:30'),
(4,'1.1.1','ACTIVO DISPONIBLE','ACTIVO',2,'NO','',0,0,0,'2016-07-18 13:23:17'),
(5,'1.1.2','ACTIVO EXIGIBLE','ACTIVO',2,'NO','',0,0,0,'2016-07-18 13:24:06'),
(6,'1.1.1.1','CAJA MONEDA NACIONAL','ACTIVO',4,'NO','',0,0,0,'2016-07-18 13:25:19'),
(7,'1.1.1.2','BANCO MONEDA NACIONAL','ACTIVO',4,'NO','',0,0,0,'2016-07-18 13:25:45'),
(8,'1.1.1.1.1','Caja Moneda Nacional','ACTIVO',6,'NO','',1,0,0,'2016-07-18 13:26:28'),
(9,'1.1.1.2.1','Banco Economico M/N','ACTIVO',7,'NO','',1,0,0,'2016-07-18 13:27:09'),
(10,'1.1.2.1','IMPUESTOS POR RECUPERAR','ACTIVO',5,'NO','',0,0,0,'2016-07-18 13:28:52'),
(11,'1.1.2.1.1','IVA Credito Fiscal','ACTIVO',10,'NO','',1,0,0,'2016-07-18 13:29:40'),
(12,'1.2.1','ACTIVO FIJO','ACTIVO',3,'NO','',0,0,0,'2016-07-18 13:31:02'),
(13,'1.2.1.1','MUEBLES Y ENSERES','ACTIVO',12,'NO','',0,0,0,'2016-07-18 13:31:53'),
(14,'1.2.1.1.1','Muebles y Enseres','ACTIVO',13,'NO','',1,0,0,'2016-07-18 13:32:47'),
(15,'1.2.1.1.2','Depreciacion Acum. Muebles Y Enseres','ACTIVO',13,'SI','',1,0,0,'2016-07-18 13:33:32'),
(16,'1.2.1.2','EQUIPO DE COMPUTACION','ACTIVO',12,'NO','',0,0,0,'2016-07-18 13:34:32'),
(17,'1.2.1.2.1','Equipo de Computacion','ACTIVO',16,'NO','',1,0,0,'2016-07-18 13:35:33'),
(18,'1.2.1.2.2','Depreciacion Acumulada Equipo de Computacion','ACTIVO',16,'SI','',1,0,0,'2016-07-18 13:36:27'),
(19,'1.2.1.3','EQUIPO E INSTALACION','ACTIVO',12,'NO','',0,0,0,'2016-07-18 13:37:41'),
(20,'1.2.1.3.1','Equipo e Instalacion','ACTIVO',19,'NO','',1,0,0,'2016-07-18 13:38:49'),
(21,'1.2.1.3.2','Depreciacion Acumulada Equipo e Instalacion','ACTIVO',19,'SI','',1,0,0,'2016-07-18 13:39:28'),
(22,'3.0','PATRIMONIO','PATRIMONIO',0,'NO','',0,0,0,'2016-07-18 13:41:44'),
(23,'3.1','PATRIMONIO','PATRIMONIO',22,'NO','',0,0,0,'2016-07-18 13:43:35'),
(24,'3.2','PERDIDA DE LA GESTION','PATRIMONIO',22,'NO','',0,0,0,'2016-07-18 13:44:35'),
(25,'3.1.1','Capital','PATRIMONIO',23,'NO','',1,0,0,'2016-07-18 13:45:56'),
(26,'3.1.2','Ajuste de Capital','PATRIMONIO',23,'NO','',1,0,0,'2016-07-18 13:46:46'),
(27,'3.1.3','Resultados Acumulados','PATRIMONIO',23,'NO','',1,0,0,'2016-07-18 13:47:19'),
(28,'3.2.1','Perdida de la Gestion','PATRIMONIO',24,'NO','',1,0,0,'2016-07-18 13:47:57'),
(29,'2.0','PASIVO','PASIVO',0,'NO','',1,0,0,'2016-07-18 13:49:22'),
(30,'4.0','INGRESOS','INGRESO',0,'NO','',0,0,0,'2016-07-18 13:59:18'),
(31,'4.1','INGRESOS OPERATIVOS','INGRESO',30,'NO','',0,0,0,'2016-07-18 14:04:49'),
(32,'4.1.1','INGRESOS POR VENTAS','INGRESO',31,'NO','',1,0,0,'2016-07-18 14:07:29'),
(33,'4.1.1.1','Ingreso por Aportes de Socios','INGRESO',33,'NO','',0,0,0,'2016-07-18 14:08:18'),
(34,'4.2','OTROS INGRESOS','INGRESO',30,'NO','',0,0,0,'2016-07-18 14:08:50'),
(35,'4.2.1','INGRESOS NO OPERATIVOS','INGRESO',34,'NO','',0,0,0,'2016-07-18 14:10:23'),
(36,'4.2.1.1','Diferencia de Redondeos','INGRESO',35,'NO','',1,0,0,'2016-07-18 14:10:52'),
(37,'4.2.1.2','Ingresos Reexpresados','INGRESO',33,'NO','',1,0,0,'2016-07-18 14:11:42'),
(38,'4.0','GASTOS','EGRESO',0,'NO','',0,0,0,'2016-07-18 14:15:48'),
(39,'4.1','GASTOS DE OPERATION','EGRESO',38,'NO','',0,0,0,'2016-07-18 14:16:12'),
(40,'4.1.1','COMBUSTIBLE Y LUBRICANTES','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:16:57'),
(41,'4.1.1.1','Combustibles','EGRESO',40,'NO','',1,0,0,'2016-07-18 14:17:23'),
(42,'4.1.2','CORREOS Y COURIER','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:17:54'),
(43,'4.1.2.1','Correos y Courier','EGRESO',42,'NO','',1,0,0,'2016-07-18 14:18:16'),
(44,'4.1.3','DEPRECIACIONES DEL ACTIVO FIJO','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:19:09'),
(45,'4.1.3.1','Depreciacion Equipo de Computacion','EGRESO',44,'NO','',1,0,0,'2016-07-18 14:20:02'),
(46,'4.1.3.2','Depreciacion Equipo de Instalacion','EGRESO',44,'NO','',1,0,0,'2016-07-18 14:20:35'),
(47,'4.1.3.3','Depreciacion Muebles y Enseres','EGRESO',44,'NO','',1,0,0,'2016-07-18 14:21:02'),
(48,'4.1.4','GASTOS GENERALES','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:23:39'),
(49,'4.1.4.1','Gastos de Representacion','EGRESO',48,'NO','',1,0,0,'2016-07-18 14:24:07'),
(50,'4.1.4.2','Gastos Generales','EGRESO',48,'NO','',1,0,0,'2016-07-18 14:24:32'),
(51,'4.1.4.3','Refrigerios al Personal','EGRESO',48,'NO','',1,0,0,'2016-07-18 14:25:03'),
(52,'4.1.5','SERVICIOS PROFECIONALES Y COMERCIALES','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:26:05'),
(53,'4.1.5.1','Imprenta y Papelera','EGRESO',52,'NO','',1,0,0,'2016-07-18 14:26:35'),
(54,'4.1.5.2','Propaganda y Publicidad','EGRESO',52,'NO','',1,0,0,'2016-07-18 14:27:09'),
(55,'4.1.6','MANTENIMIENTO Y REPARACIONES','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:28:34'),
(56,'4.1.6.1','Accesorios y Repuestos','EGRESO',55,'NO','',1,0,0,'2016-07-18 14:29:26'),
(57,'4.1.6.2','Mantenimiento Vehiculo','EGRESO',55,'NO','',1,0,0,'2016-07-18 14:29:46'),
(58,'4.1.7','MATERIALES DE ESCRITORIO Y OTROS MATERIALES','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:30:30'),
(59,'4.1.7.1','Materiales de Escritorio y Oficina','EGRESO',58,'NO','',1,0,0,'2016-07-18 14:31:01'),
(60,'4.1.7.2','Material Electrico','EGRESO',58,'NO','',1,0,0,'2016-07-18 14:31:40'),
(61,'4.1.8','PASAJES Y TRANSPORTES','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:32:16'),
(62,'4.1.8.1','Pasajes','EGRESO',61,'NO','',1,0,0,'2016-07-18 14:32:34'),
(63,'4.1.9','SERVICIOS BASICOS','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:33:26'),
(64,'4.1.9.1','Servicios Telefonicos','EGRESO',63,'NO','',1,0,0,'2016-07-18 14:33:54'),
(65,'4.1.10','TRAMITES LEGALES','EGRESO',39,'NO','',0,0,0,'2016-07-18 14:34:23'),
(66,'4.1.10.1','Gastos legales y de tramites','EGRESO',65,'NO','',1,0,0,'2016-07-18 14:34:46'),
(67,'4.2','OTROS EGRESOS','EGRESO',38,'NO','',0,0,0,'2016-07-18 14:35:28'),
(68,'4.2.1','Diferencia de Redondeos','EGRESO',67,'NO','',1,0,0,'2016-07-18 14:35:57'),
(69,'4.2.2','AITB','EGRESO',67,'NO','',1,0,0,'2016-07-18 14:36:14'),
(70,'4.2.3','Egresos Reexpresados','EGRESO',67,'NO','',1,0,0,'2016-07-18 14:36:39');

UPDATE product SET measureId = 16, measureName="Unidad" where measureId = 0;

UPDATE product SET cost = 10, percent = "0,1", price = 11 where cost = 0;
