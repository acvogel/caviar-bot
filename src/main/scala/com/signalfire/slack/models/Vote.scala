package com.signalfire.slack.models

import java.sql.Timestamp

case class Vote(id: Int, pollId: Int, restaurantId: Int, createdBy: String, createdAt: Timestamp, updatedAt: Timestamp)
