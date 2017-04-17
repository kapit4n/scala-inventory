# --- !Downs
drop table IF EXISTS bancos;
drop table IF EXISTS user;
drop table IF EXISTS company;
drop table IF EXISTS category;
drop table IF EXISTS account;
drop table IF EXISTS customer;
drop table IF EXISTS transaction;
drop table IF EXISTS transactionDetail;
drop table IF EXISTS vendor;
drop table IF EXISTS vendorContract;
drop table IF EXISTS vendorContractItem;
drop table IF EXISTS reportes;
drop table IF EXISTS product;
drop table IF EXISTS productInv;
drop table IF EXISTS discountReport;
drop table IF EXISTS discountDetail;
drop table IF EXISTS requestRow;
drop table IF EXISTS productRequest;
drop table IF EXISTS requestRowCustomer;
drop table IF EXISTS logEntry;
drop table IF EXISTS measure;
drop table IF EXISTS setting;
drop table IF EXISTS roles;
drop table IF EXISTS userRole;
drop table IF EXISTS productVendor;

## --- !Ups
create table company (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) not null
);

create table category (
  name VARCHAR(100) NOT NULL PRIMARY KEY,
  description VARCHAR(100)
);

create table measure (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  quantity double,
  description VARCHAR(250),
  measureId INT,
  measureName VARCHAR(100)
);

create table account (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  code VARCHAR(30),
  name VARCHAR(100),
  type VARCHAR(30),
  parent INT(6),
  negativo VARCHAR(30),
  description VARCHAR(250),
  child boolean,
  debit double,
  credit double,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table transaction (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  date VARCHAR(30),
  type VARCHAR(30),
  description VARCHAR(250),
  createdBy INT,
  createdByName VARCHAR(100),
  autorizedBy INT,
  autorizedByName VARCHAR(100), 
  receivedBy INT,
  receivedByName VARCHAR(100), 
  updatedBy INT(6),
  updatedByName VARCHAR(100),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table setting (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100),
  president VARCHAR(50),
  language VARCHAR(50),
  description VARCHAR(2505)
);

create table logEntry (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  action VARCHAR(100),
  tableName1 VARCHAR(100),
  userId INT(6),
  userName VARCHAR(255),
  description VARCHAR(255),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table transactionDetail (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  transaction INT,
  account INT,
  debit double,
  credit double,
  transactionDate VARCHAR(30),
  accountCode VARCHAR(30),
  accountName VARCHAR(100),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INT,
  createdByName VARCHAR(100)
);

create table bancos (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) not null,
  tipo VARCHAR(30) not null,
  currentMoney VARCHAR(30),
  typeMoney VARCHAR(30)
);

create table product (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) not null,
  cost double DEFAULT 0,
  totalValue double DEFAULT 0,
  percent double DEFAULT 0,
  price double DEFAULT 0,
  description VARCHAR(250) DEFAULT '',
  measureId INT DEFAULT 0,
  measureName VARCHAR(100) DEFAULT '',
  currentAmount INT  DEFAULT 0,
  stockLimit INT  DEFAULT 0,
  vendorId INT DEFAULT 0,
  vendorName VARCHAR(100) DEFAULT '',
  vendorCode VARCHAR(50) DEFAULT '',
  category VARCHAR(50) DEFAULT '',
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table customer (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) not null,
  carnet INT not null,
  phone INT,
  address VARCHAR(100),
  account VARCHAR(30),
  companyName VARCHAR(100),
  status VARCHAR(30),
  totalDebt double,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table vendor (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) not null,
  phone INT,
  address VARCHAR(100),
  contact VARCHAR(100),
  account INT,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table vendorContract (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  vendorId VARCHAR(100) not null,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  startDate DATE,
  dueDate DATE
);

create table vendorContractItem (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  contractId INT not null,
  productId INT not null,
  startDate DATE,
  dueDate DATE,
  cost INT,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table reportes (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  monto INT not null,
  account INT not null,
  cliente INT not null,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table user (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(100) not null,
  carnet INT not null,
  phone INT,
  address VARCHAR(30),
  Salary INT,
  type VARCHAR(30),
  login VARCHAR(30),
  password VARCHAR(30),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table userRole (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  userId INT,
  roleName VARCHAR(100),
  roleCode VARCHAR(50)
);

create table productVendor (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  productId INT,
  vendorId INT
);

create table roles (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  roleName VARCHAR(100),
  roleCode VARCHAR(50)
);

create table productRequest (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  date VARCHAR(30),
  employee INT,
  employeename VARCHAR(100),
  storekeeper INT,
  storekeeperName VARCHAR(100),
  user INT,
  userName VARCHAR(100),
  module INT,
  moduleName VARCHAR(100),
  totalPrice double,
  paid double,
  credit double,
  paidDriver double,
  creditDriver double,
  status VARCHAR(30),
  detail VARCHAR(250),
  type VARCHAR(30),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INT,
  createdByName VARCHAR(100),
  acceptedBy INT,
  acceptedByName VARCHAR(100),
  acceptedAt Date
);

create table requestRow (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  requestId INT,
  productId INT,
  productName VARCHAR(100),
  customerId INT,
  customerName VARCHAR(100),
  quantity INT,
  price double,
  totalPrice double,
  paid double,
  credit double,
  paidDriver double,
  creditDriver double,
  measureId INT,
  measureName VARCHAR(100),
  status VARCHAR(30),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INT,
  createdByName VARCHAR(100)
);

create table requestRowCustomer (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  requestRowId INT,
  productId INT,
  productName VARCHAR(100),
  customerId INT,
  customerName VARCHAR(100),
  measureId INT,
  measureName VARCHAR(100),
  quantity INT,
  price double,
  totalPrice double,
  paid double,
  credit double,
  status VARCHAR(30),
  type VARCHAR(30),
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  createdBy INT,
  createdByName VARCHAR(100),
  payType VARCHAR(20)
);

create table productInv (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  productId INT,
  vendorId INT,
  measureId INT,
  productName VARCHAR(60),
  vendorName VARCHAR(60),
  measureName VARCHAR(60),
  amount INT  DEFAULT 0,
  amountLeft INT  DEFAULT 0,
  cost_unit double  DEFAULT 0,
  total_cost double  DEFAULT 0,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table discountReport (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  startDate VARCHAR(30),
  endDate VARCHAR(30),
  status VARCHAR(30),
  total double,
  user_id INT,
  createdAt TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

create table discountDetail (
  id INT(6) UNSIGNED AUTO_INCREMENT PRIMARY KEY,
  discountReport INT,
  requestRow INT,
  customerId INT,
  customerName VARCHAR(100),
  status VARCHAR(30),
  discount double
);
