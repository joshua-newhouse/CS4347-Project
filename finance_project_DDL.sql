-- MySQL Script generated by MySQL Workbench
-- Mon Dec  4 02:31:10 2017
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema finance
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema finance
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `finance` DEFAULT CHARACTER SET utf8 ;
USE `finance` ;

-- -----------------------------------------------------
-- Table `finance`.`Users`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `finance`.`Users` ;

CREATE TABLE IF NOT EXISTS `finance`.`Users` (
  `SSN` INT UNSIGNED NOT NULL,
  `first_name` VARCHAR(45) NOT NULL,
  `last_name` VARCHAR(45) NOT NULL,
  `username` VARCHAR(45) NOT NULL,
  `password` VARCHAR(45) NOT NULL,
  `date_created` DATE NOT NULL,
  `date_updated` DATE NOT NULL,
  PRIMARY KEY (`SSN`),
  UNIQUE INDEX `SSN_UNIQUE` (`SSN` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `finance`.`User_Accounts`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `finance`.`User_Accounts` ;

CREATE TABLE IF NOT EXISTS `finance`.`User_Accounts` (
  `Account_Number` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `User_SSN` INT NOT NULL,
  `Account_Type` INT NOT NULL,
  `date_created` DATE NOT NULL,
  `date_updated` DATE NOT NULL,
  `current_balance` DECIMAL NOT NULL,
  `Routing_Number` INT NOT NULL,
  `Interest_Rate` FLOAT NULL,
  PRIMARY KEY (`Account_Number`),
  UNIQUE INDEX `Account_Number_UNIQUE` (`Account_Number` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `finance`.`Transaction`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `finance`.`Transaction` ;

CREATE TABLE IF NOT EXISTS `finance`.`Transaction` (
  `Transaction_ID` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `Acct_No` INT UNSIGNED NOT NULL,
  `Point_of_sale` VARCHAR(45) NOT NULL,
  `Amount` DECIMAL NULL,
  `Type` VARCHAR(45) NULL,
  `State` TINYINT(1) NULL,
  `date` DATE NULL,
  PRIMARY KEY (`Transaction_ID`),
  UNIQUE INDEX `Transaction_ID_UNIQUE` (`Transaction_ID` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `finance`.`UserToAccount`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `finance`.`UserToAccount` ;

CREATE TABLE IF NOT EXISTS `finance`.`UserToAccount` (
  `User_SSN` INT UNSIGNED NOT NULL,
  `Account_Number` INT UNSIGNED NOT NULL,
  INDEX `fk_UserToAccount_1_idx` (`User_SSN` ASC),
  INDEX `fk_UserToAccount_2_idx` (`Account_Number` ASC),
  CONSTRAINT `fk_UserToAccount_1`
    FOREIGN KEY (`User_SSN`)
    REFERENCES `finance`.`Users` (`SSN`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_UserToAccount_2`
    FOREIGN KEY (`Account_Number`)
    REFERENCES `finance`.`User_Accounts` (`Account_Number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `finance`.`AcctToTrans`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `finance`.`AcctToTrans` ;

CREATE TABLE IF NOT EXISTS `finance`.`AcctToTrans` (
  `Account_Number` INT UNSIGNED NOT NULL,
  `Transaction_ID` INT UNSIGNED NOT NULL,
  UNIQUE INDEX `Transaction_ID_UNIQUE` (`Transaction_ID` ASC),
  UNIQUE INDEX `Account_Number_UNIQUE` (`Account_Number` ASC),
  CONSTRAINT `fk_AcctToTrans_1`
    FOREIGN KEY (`Transaction_ID`)
    REFERENCES `finance`.`Transaction` (`Transaction_ID`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_AcctToTrans_2`
    FOREIGN KEY (`Account_Number`)
    REFERENCES `finance`.`User_Accounts` (`Account_Number`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
