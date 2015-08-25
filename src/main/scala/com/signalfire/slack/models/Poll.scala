package com.signalfire.slack.models

import java.sql.Timestamp

case class Poll(id: Int, createdBy: String, defaultRestaurantId: Int, createdAt: Timestamp, updatedAt: Timestamp) {

}
