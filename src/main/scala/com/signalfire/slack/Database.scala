package com.signalfire.slack.server

import java.net.URI
import java.sql.{Connection, DriverManager, Statement, PreparedStatement, ResultSet}
import java.util.Properties

/** Manages database connections */
object Database {

  def getConnection(): Connection  = {
    val dbUri = new URI(System.getenv("DATABASE_URL")) 
  
    val username = dbUri.getUserInfo().split(":")(0)
    val password = dbUri.getUserInfo().split(":")(1)
    val dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() 
    val properties = new Properties()
    properties.setProperty("user", dbUri.getUserInfo().split(":")(0))
    properties.setProperty("password", dbUri.getUserInfo().split(":")(1))
    // Needed for accessing database from outside Heroku
    //properties.setProperty("ssl", "true")
    //properties.setProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory")
    DriverManager.getConnection(dbUrl, properties)
  }

  def findRestaurant(name: String): Option[Restaurant] = {
    val connection = getConnection()
    val sql = s"SELECT id, name, text, image FROM RESTAURANT WHERE name ILIKE ? LIMIT 1"
    val pstmt = connection.prepareStatement(sql)
    pstmt.setString(1, name + '%')
    val rs = pstmt.executeQuery()
    val found = rs.next()
    if (found) {
      Some(Restaurant(rs.getInt(1), rs.getString(2), rs.getString(3), rs.getString(4)))
    } else {
      None
    }
  }

  def populateRestaurants(restaurants: Seq[Restaurant]) {
    val connection = getConnection
    restaurants.foreach { case Restaurant(id, name, text, image) =>
      val query = s"INSERT INTO RESTAURANT (name, text, image) VALUES (?, ?, ?)"
      val pstmt = connection.prepareStatement(query)
      pstmt.setString(1, name)
      pstmt.setString(2, text)
      pstmt.setString(3, image)
      pstmt.execute()
    }
  }
}
