package com.signalfire.slack.server

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;

import org.openqa.selenium.remote.RemoteWebDriver


import org.joda.time.DateTime


object Selenium {
  def login = "acvogel@gmail.com"
  val password = "sl0NSmFlJX0YoaO;XxKl"

  def main(args: Array[String]) {
    val driver = new FirefoxDriver()
    signIn(login, password, driver)
    setDateTime(new DateTime(2015, 8, 26, 0, 0), driver)
  }

  def signIn(login: String, password: String, driver: WebDriver) {
    driver.get("http://www.trycaviar.com/san-francisco")
    val signInButton = driver.findElement(By.className("global-header_account_sign-in-link"))
    signInButton.click()

    waitFor(driver, 10, _.getTitle.equals("Caviar | Sign In"))
    
    val loginForm = driver.findElement(By.name("user[email]"))
    loginForm.sendKeys(login)

    val passwordForm = driver.findElement(By.name("user[password]"))
    passwordForm.sendKeys(password)

    val submitButton = driver.findElement(By.name("button"))
    submitButton.click()
  }

  // <span class="filter-option pull-left">ASAP</span>
  def setDateTime(date: DateTime, driver: RemoteWebDriver) {
    // idea: do lookup based on mm/dd
    //val dateSpan = driver.findElement(By.className("filter-option pull-left"))
    val dateSpan = driver.findElement(By.className("btn"))
    dateSpan.click()
    // TODO WAIT HERE FOR JS

    val dateStr = s"""${date.getMonthOfYear}/${date.getDayOfMonth}""" // XXX use dateformat
    val dateElement = driver.findElementByPartialLinkText(dateStr)
    dateElement.click()


    // TODO find time span element, click to get dropdown, then lookup correct time
    // possible bug: if the day is TODAY, then the button to click for time dropdown might be different
    //val timeSpan = driver.findElement(By.

    val timeStr = "12:00 PM – 01:00 PM" // XXX note the padding on the hour
    val timeElement = driver.findElementByLinkText(timeStr)
    timeElement.click()

    // WAIT HERE
  }

  def waitFor(driver: WebDriver, timeOut: Int, f: (WebDriver) => Boolean) = {
    new WebDriverWait(driver, timeOut).until(
     new ExpectedCondition[Boolean] {
       override def apply(d: WebDriver) = f(d)
     })
  }
}
